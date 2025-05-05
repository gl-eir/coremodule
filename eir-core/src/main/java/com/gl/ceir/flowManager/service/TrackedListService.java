package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.configuration.AppConfiguration;
import com.gl.ceir.flowManager.contstants.*;
import com.gl.ceir.flowManager.dto.ListResponseData;
import com.gl.ceir.flowManager.dto.TrackedListKey;
import com.gl.ceir.flowManager.dto.TrackedListValue;
import com.gl.ceir.flowManager.dto.VerificationRequest;
import com.gl.ceir.flowManager.entity.TrackedList;
import com.gl.ceir.flowManager.exception.DataNotFoundException;
import com.gl.ceir.flowManager.repository.TrackedListRepository;
import com.gl.ceir.flowManager.util.DateUtil;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TrackedListService implements ITrackedListService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<TrackedListKey, String> trackedListCache = new ConcurrentHashMap<>();

    @Autowired
    AppConfiguration appConfiguration;

    @PostConstruct
    public void myInit() {
        if (appConfiguration.getApplicationType() == ApplicationType.CACHE) loadTrackedList();
    }

    @Autowired
    TrackedListRepository trackedListRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private FileDownloadStatus fileStatus = FileDownloadStatus.COMPLETED;

    public FileDownloadStatus getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(FileDownloadStatus fileStatus) {
        this.fileStatus = fileStatus;
    }

    String query = "select imei , imsi , msisdn , created_on from grey_list";

    @Override
    public int loadTrackedList() {
        try {

            Statement statement = jdbcTemplate.getDataSource().getConnection().createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String imei = StringUtils.isBlank(rs.getString("imei")) ? null : rs.getString("imei");
                String imsi = StringUtils.isBlank(rs.getString("imsi")) ? null : rs.getString("imsi");
                String msisdn = StringUtils.isBlank(rs.getString("msisdn")) ? null : rs.getString("msisdn");
                String createdDate = rs.getString("created_on") == null ? "" : rs.getString("created_on");
                createdDate = createdDate == null ? "" : createdDate;
                TrackedListKey key = new TrackedListKey(imei, imsi, null);
                trackedListCache.put(key, createdDate);
            }
            rs.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Tracked List data load cache:{}", trackedListCache.size());
        return trackedListCache.size();
    }

    public void writeFile() {
        logger.info("Going to Write file TrackedList status:{}", fileStatus);
        PrintWriter fileCloseWriter = null;
        try {
            PrintWriter writer = new PrintWriter(appConfiguration.getFilePath() + "/" + ListType.TRACKED_LIST.getFilename());
            fileCloseWriter = writer;
            writer.println("IMEI,IMSI,MSISDN,CREATED_DATE");
            logger.info("File Writing Started for TrackedList status:{} from:{}", fileStatus, appConfiguration.getApplicationType());
            final AtomicLong count = new AtomicLong(0);
            if (appConfiguration.getApplicationType() == ApplicationType.DB) {
                jdbcTemplate.query(query, new RowCallbackHandler() {
                    public void processRow(ResultSet rs) throws SQLException {
                        String imei = rs.getString("imei") == null ? "" : rs.getString("imei");
                        String imsi = rs.getString("imsi") == null ? "" : rs.getString("imsi");
                        String msisdn = rs.getString("msisdn") == null ? "" : rs.getString("msisdn");
                        String createdDate = rs.getString("created_on") == null ? "" : rs.getString("created_on");
                        writer.println(imei + "," + imsi + "," + msisdn + "," + createdDate);
                        count.set(count.get() + 1);
                        if (count.get() % 1000 == 0) {
                            logger.info("TrackList Records:{} are written in file ", count.get());
                        }
                    }
                });
            } else {
                for (TrackedListKey key : trackedListCache.keySet()) {
                    writer.println((key.getImei() == null ? "" : key.getImei()) + "," + (key.getImsi() == null ? "" : key.getImsi()) + "," + (key.getMsisdn() == null ? "" : key.getMsisdn()) + "," + trackedListCache.get(key));
                    count.set(count.get() + 1);
                    if (count.get() % 1000 == 0) {
                        logger.info("TrackList Records:{} are written in file ", count.get());
                    }
                }
            }
            fileStatus = FileDownloadStatus.COMPLETED;
        } catch (Exception e) {
            logger.error("Error While TrackedList writing file Error{}", e.getMessage());
            fileStatus = FileDownloadStatus.COMPLETED_ERROR;
        } finally {
            fileCloseWriter.close();
        }
    }

    @Override
    public ApiStatusMessage addToList(TrackedListValue value) {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            Optional<TrackedList> blackListPresent = trackedListRepository.findByImeiAndImsiAndMsisdn(value.getImei(), value.getImsi(), value.getMsisdn());
            if (blackListPresent.isPresent()) {
                logger.info("Add Tracked List:Duplicate entry found for req : {}", value);
                return ApiStatusMessage.duplicate;
            } else {
                try {
                    TrackedList entity = new TrackedList();
                    entity.setCreated_on(value.getCreated_on());
                    entity.setImei(value.getImei());
                    entity.setImsi(value.getImsi());
                    entity.setMsisdn(value.getMsisdn());
                    entity.setRequestDate(value.getRequestDate());
                    entity.setActualImei(value.getActualImei());
                    trackedListRepository.save(entity);
                    return ApiStatusMessage.added;
                } catch (Exception e) {
                    logger.info("Add Tracked List:Exception while adding data in db : {} ErrorMessage:{}", value, e.getMessage());
                    return ApiStatusMessage.notAdded;
                }
            }
        } else {
            TrackedListKey key = new TrackedListKey(value.getImei(), value.getImsi(), value.getMsisdn());
            if (trackedListCache.get(key) == null) {
                trackedListCache.put(new TrackedListKey(value.getImei(), value.getImsi(), value.getMsisdn()), value.getCreated_on().format(DateUtil.defaultDateFormat));
                return ApiStatusMessage.added;
            } else {
                return ApiStatusMessage.duplicate;
            }
        }
    }


    @Override
    public ApiStatusMessage removeFromList(TrackedListValue value) {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            Optional<TrackedList> blackListPresent = trackedListRepository.findByImeiAndImsiAndMsisdn(value.getImei(), value.getImsi(), value.getMsisdn());
            if (blackListPresent.isPresent()) {
                logger.info("Remove Tracked List: Found entity : {} for req : {}", blackListPresent.get(), value);
                try {
                    trackedListRepository.delete(blackListPresent.get());
                    logger.info("Remove Tracked List: Data deleted from {}: {}", appConfiguration.getApplicationType(), value);
                    return ApiStatusMessage.deleted;
                } catch (Exception e) {
                    logger.error("Remove Tracked List: Exception while deleting data from DB: {} ErrorMessage:{}", value, e.getMessage());
                    return ApiStatusMessage.notDeleted;
                }
            } else {
                logger.info("Remove Tracked List: No entry found for req : {}", value);
                return ApiStatusMessage.notFound;
            }
        } else {
            TrackedListKey key = new TrackedListKey(value.getImei(), value.getImsi(), value.getMsisdn());
            if (trackedListCache.get(key) == null) {
                return ApiStatusMessage.notFound;
            } else {
                trackedListCache.remove(key);
                return ApiStatusMessage.deleted;
            }
        }
    }

    @Override
    public ListResponseData isPresent(VerificationRequest request) throws DataNotFoundException {
        String creationDate = trackedListCache.get(new TrackedListKey(request.getImei(), null, null));

        if (creationDate == null)
            creationDate = trackedListCache.get(new TrackedListKey(appConfiguration.getImeiNullPattern(), request.getImsi(), null));
        else
            return new ListResponseData(creationDate, ReasonCode.GreylistWithIMEI);


        if (creationDate == null)
            creationDate = trackedListCache.get(new TrackedListKey(appConfiguration.getImeiNullPattern(), null, request.getMsisdn()));
        else
            return new ListResponseData(creationDate, ReasonCode.GreylistWithIMSI);

        if (creationDate == null)
            creationDate = trackedListCache.get(new TrackedListKey(request.getImei(), request.getImsi(), null));
        else
            return new ListResponseData(creationDate, ReasonCode.GreylistWithMSISDN);

        if (creationDate == null)
            creationDate = trackedListCache.get(new TrackedListKey(request.getImei(), null, request.getMsisdn()));
        else
            return new ListResponseData(creationDate, ReasonCode.GreylistWithIMEI_IMSI);

        if (creationDate == null)
            creationDate = trackedListCache.get(new TrackedListKey(appConfiguration.getImeiNullPattern(), request.getImsi(), request.getMsisdn()));
        else
            return new ListResponseData(creationDate, ReasonCode.GreylistWithIMEI_MSISDN);

        if (creationDate == null)
            creationDate = trackedListCache.get(new TrackedListKey(request.getImei(), request.getImsi(), request.getMsisdn()));
        else
            return new ListResponseData(creationDate, ReasonCode.GreylistWithIMSI_MSISDN);

        if (creationDate == null)
            throw new DataNotFoundException("Not found");
        else
            return new ListResponseData(creationDate, ReasonCode.GreylistWithIMEI_IMSI_MSISDN);
    }

    public List<TrackedListKey> get() {
        List<TrackedListKey> list = null;
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            Pageable firstElements = PageRequest.of(0, 10);
            Page<TrackedList> page = trackedListRepository.findAll(firstElements);
            list = page.get().map(p -> new TrackedListKey(p.getImei(), p.getImsi(), p.getMsisdn())).collect(Collectors.toList());
        } else if (appConfiguration.getApplicationType() == ApplicationType.CACHE) {
            return trackedListCache.keySet().stream().limit(10).collect(Collectors.toList());
        }
        return list;
    }


    @Override
    public Long getCount() {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            return trackedListRepository.count();
        } else {
            return (long) trackedListCache.size();
        }
    }
}
