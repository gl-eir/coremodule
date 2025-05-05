package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.configuration.AppConfiguration;
import com.gl.ceir.flowManager.contstants.*;
import com.gl.ceir.flowManager.dto.BlackListKey;
import com.gl.ceir.flowManager.dto.BlackListValue;
import com.gl.ceir.flowManager.dto.ListResponseData;
import com.gl.ceir.flowManager.dto.VerificationRequest;
import com.gl.ceir.flowManager.entity.BlackList;
import com.gl.ceir.flowManager.exception.DataNotFoundException;
import com.gl.ceir.flowManager.repository.BlackListRepository;
import com.gl.ceir.flowManager.util.DateUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BlackListService implements IBlackListService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<BlackListKey, String> blackListCache = new ConcurrentHashMap<>();
    @Autowired
    AppConfiguration appConfiguration;

    @PostConstruct
    public void myInit() {
        if (appConfiguration.getApplicationType() == ApplicationType.CACHE) loadBlackList();
    }

    @Autowired
    private BlackListRepository blackListRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    String query = "select imei , imsi , msisdn , created_on from black_list";


    private FileDownloadStatus fileStatus = FileDownloadStatus.COMPLETED;
    ;

    public FileDownloadStatus getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(FileDownloadStatus fileStatus) {
        this.fileStatus = fileStatus;
    }

    @Override
    public int loadBlackList() {

        try {
            Statement statement = jdbcTemplate.getDataSource().getConnection().createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String imei = StringUtils.isBlank(rs.getString("imei")) ? null : rs.getString("imei");
                String imsi = StringUtils.isBlank(rs.getString("imsi")) ? null : rs.getString("imsi");
                String msisdn = StringUtils.isBlank(rs.getString("msisdn")) ? null : rs.getString("msisdn");
                String createdDate = rs.getString("created_on") == null ? "" : rs.getString("created_on");
                BlackListKey key = new BlackListKey(imei, imsi, null);
                blackListCache.put(key, createdDate);
            }
            rs.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("Black List data load  blackListCache:{}", blackListCache.size());
        return blackListCache.size();
    }

    public void writeFile() {
        logger.info("Going to Write file BlackList status:{}", fileStatus);
        PrintWriter fileCloseWriter = null;
        try {
            PrintWriter writer = new PrintWriter(appConfiguration.getFilePath() + "/" + ListType.BLOCKED_LIST.getFilename());
            fileCloseWriter = writer;
            writer.println("IMEI,IMSI,MSISDN,CREATED_DATE");
            logger.info("File Writing Started for BlackList status:{} from:{}", fileStatus, appConfiguration.getApplicationType());
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
                            logger.info("BlackList Records:{} are written in file ", count.get());
                        }
                    }
                });
            } else {
                for (BlackListKey key : blackListCache.keySet()) {
                    writer.println((key.getImei() == null ? "" : key.getImei()) + "," + (key.getImsi() == null ? "" : key.getImsi()) + "," + (key.getMsisdn() == null ? "" : key.getMsisdn()) + "," + blackListCache.get(key));
                    count.set(count.get() + 1);
                    if (count.get() % 1000 == 0) {
                        logger.info("BlackList Records:{} are written in file ", count.get());
                    }
                }
            }
            fileStatus = FileDownloadStatus.COMPLETED;
        } catch (Exception e) {
            fileStatus = FileDownloadStatus.COMPLETED_ERROR;
            logger.error("Error While BlackList writing file Error{}", e.getMessage(), e);
        } finally {
            fileCloseWriter.close();
        }

    }

    @Override
    public ApiStatusMessage addToList(BlackListValue value) {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            Optional<BlackList> blackListPresent = blackListRepository.findByImeiAndImsiAndMsisdn(value.getImei(), value.getImsi(), value.getMsisdn());
            if (blackListPresent.isPresent()) {
                logger.info("Add Black List: Duplicate entry found for req : {}", value);
                return ApiStatusMessage.duplicate;
            } else {
                try {
                    BlackList entity = new BlackList();
                    entity.setCreated_on(value.getCreated_on());
                    entity.setImei(value.getImei());
                    entity.setImsi(value.getImsi());
                    entity.setMsisdn(value.getMsisdn());
                    entity.setRequestDate(value.getRequestDate());
                    entity.setActualImei(value.getActualImei());
                    blackListRepository.save(entity);
                    return ApiStatusMessage.added;
                } catch (Exception e) {
                    logger.info("Add Black List:Exception while adding data in db : {} ErrorMessage:{}", value, e.getMessage());
                    return ApiStatusMessage.notAdded;
                }
            }
        } else {
            BlackListKey key = new BlackListKey(value.getImei(), value.getImsi(), value.getMsisdn());
            if (blackListCache.get(key) == null) {
                blackListCache.put(new BlackListKey(value.getImei(), value.getImsi(), value.getMsisdn()), value.getCreated_on().format(DateUtil.defaultDateFormat));
                return ApiStatusMessage.added;
            } else {
                return ApiStatusMessage.duplicate;
            }
        }
    }


    @Override
    public ApiStatusMessage removeFromList(BlackListValue value) {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            Optional<BlackList> blackListPresent = blackListRepository.findByImeiAndImsiAndMsisdn(value.getImei(), value.getImsi(), value.getMsisdn());
            if (blackListPresent.isPresent()) {
                logger.info("Remove Black List: Found entity : {} for req : {}", blackListPresent.get(), value);
                try {
                    blackListRepository.delete(blackListPresent.get());
                    logger.info("Remove Black List: Data deleted from {}: {}", appConfiguration.getApplicationType(), value);
                    return ApiStatusMessage.deleted;
                } catch (Exception e) {
                    logger.error("Remove Black List: Exception while deleting data from DB: {} ErrorMessage:{}", value, e.getMessage());
                    return ApiStatusMessage.notDeleted;
                }
            } else {
                logger.info("Remove Black List: No entry found for req : {}", value);
                return ApiStatusMessage.notFound;
            }
        } else {
            BlackListKey key = new BlackListKey(value.getImei(), value.getImsi(), value.getMsisdn());
            if (blackListCache.get(key) == null) return ApiStatusMessage.notFound;
            else {
                blackListCache.remove(new BlackListKey(value.getImei(), value.getImsi(), value.getMsisdn()));
                return ApiStatusMessage.deleted;
            }
        }
    }

    @Override
    public ListResponseData isPresent(VerificationRequest request) throws DataNotFoundException {
        String creationDate = blackListCache.get(new BlackListKey(request.getImei(), null, null));

        if (creationDate == null)
            creationDate = blackListCache.get(new BlackListKey(appConfiguration.getImeiNullPattern(), request.getImsi(), null));
        else
            return new ListResponseData(creationDate, ReasonCode.BlacklistWithIMEI);


        if (creationDate == null)
            creationDate = blackListCache.get(new BlackListKey(appConfiguration.getImeiNullPattern(), null, request.getMsisdn()));
        else
            return new ListResponseData(creationDate, ReasonCode.BlacklistWithIMSI);

        if (creationDate == null)
            creationDate = blackListCache.get(new BlackListKey(request.getImei(), request.getImsi(), null));
        else
            return new ListResponseData(creationDate, ReasonCode.BlacklistWithMSISDN);

        if (creationDate == null)
            creationDate = blackListCache.get(new BlackListKey(request.getImei(), null, request.getMsisdn()));
        else
            return new ListResponseData(creationDate, ReasonCode.BlacklistWithIMEI_IMSI);

        if (creationDate == null)
            creationDate = blackListCache.get(new BlackListKey(appConfiguration.getImeiNullPattern(), request.getImsi(), request.getMsisdn()));
        else
            return new ListResponseData(creationDate, ReasonCode.BlacklistWithIMEI_MSISDN);

        if (creationDate == null)
            creationDate = blackListCache.get(new BlackListKey(request.getImei(), request.getImsi(), request.getMsisdn()));
        else
            return new ListResponseData(creationDate, ReasonCode.BlacklistWithIMSI_MSISDN);

        if (creationDate == null)
            throw new DataNotFoundException("Not found");
        else
            return new ListResponseData(creationDate, ReasonCode.BlacklistWithIMEI_IMSI_MSISDN);

    }

    public List<BlackListKey> get() {
        List<BlackListKey> list = null;
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            Pageable firstElements = PageRequest.of(0, 10);
            Page<BlackList> page = blackListRepository.findAll(firstElements);
            list = page.get().map(p -> new BlackListKey(p.getImei(), p.getImsi(), p.getMsisdn())).collect(Collectors.toList());
        } else if (appConfiguration.getApplicationType() == ApplicationType.CACHE) {
            return blackListCache.keySet().stream().limit(10).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public Long getCount() {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            return blackListRepository.count();
        } else {
            return (long) blackListCache.size();
        }
    }
}