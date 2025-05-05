package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.configuration.AppConfiguration;
import com.gl.ceir.flowManager.contstants.*;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.entity.ExceptionList;
import com.gl.ceir.flowManager.exception.DataNotFoundException;
import com.gl.ceir.flowManager.repository.ExceptionListRepository;
import com.gl.ceir.flowManager.util.DateUtil;
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

import jakarta.annotation.PostConstruct;

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
public class ExceptionListService implements IExceptionListService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<ExceptionListKey, String> exceptionListCache = new ConcurrentHashMap<>();

    @Autowired
    AppConfiguration appConfiguration;

    private FileDownloadStatus fileStatus = FileDownloadStatus.COMPLETED;

    public FileDownloadStatus getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(FileDownloadStatus fileStatus) {
        this.fileStatus = fileStatus;
    }

    @PostConstruct
    public void myInit() {
        if (appConfiguration.getApplicationType() == ApplicationType.CACHE) loadExceptionList();
    }

    @Autowired
    ExceptionListRepository exceptionListRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    String query = "select imei , imsi , msisdn , created_on from vip_list";

    @Override
    public int loadExceptionList() {
        try {

            Statement statement = jdbcTemplate.getDataSource().getConnection().createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String imei = StringUtils.isBlank(rs.getString("imei")) ? null : rs.getString("imei");
                String imsi = StringUtils.isBlank(rs.getString("imsi")) ? null : rs.getString("imsi");
                String msisdn = StringUtils.isBlank(rs.getString("msisdn")) ? null : rs.getString("msisdn");
                String createdDate = rs.getString("created_on") == null ? "" : rs.getString("created_on");
                ExceptionListKey key = new ExceptionListKey(imei, imsi, null);
                exceptionListCache.put(key, createdDate);
            }
            rs.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("New Exception List data load cache:{}", exceptionListCache.size());
        return exceptionListCache.size();
    }

    public void writeFile() {
        logger.info("Going to Write file ExceptionList status:{}", fileStatus);
        PrintWriter fileCloseWriter = null;
        try {
            PrintWriter writer = new PrintWriter(appConfiguration.getFilePath() + "/" + ListType.EXCEPTION_LIST.getFilename());
            fileCloseWriter = writer;
            writer.println("IMEI,IMSI,MSISDN,CREATED_DATE");
            logger.info("File Writing Started for ExceptionList status:{} from:{}", fileStatus, appConfiguration.getApplicationType());
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
                            logger.info("ExceptionList Records:{} are written in file ", count.get());
                        }
                    }
                });
            } else {
                for (ExceptionListKey key : exceptionListCache.keySet()) {
                    writer.println((key.getImei() == null ? "" : key.getImei()) + "," + (key.getImsi() == null ? "" : key.getImsi()) + "," + (key.getMsisdn() == null ? "" : key.getMsisdn()) + "," + exceptionListCache.get(key));
                    count.set(count.get() + 1);
                    if (count.get() % 1000 == 0) {
                        logger.info("ExceptionList Records:{} are written in file ", count.get());
                    }
                }
            }
            fileStatus = FileDownloadStatus.COMPLETED;
        } catch (Exception e) {
            fileStatus = FileDownloadStatus.COMPLETED_ERROR;
            logger.error("Error While ExceptionList writing file Error{}", e.getMessage(), e);
        } finally {
            fileCloseWriter.close();
        }

    }

    @Override
    public ApiStatusMessage addToList(ExceptionListValue value) {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            Optional<ExceptionList> exceptionListPresent = exceptionListRepository.findByImeiAndImsiAndMsisdn(value.getImei(), value.getImsi(), value.getMsisdn());
            if (exceptionListPresent.isPresent()) {
                logger.info("Add Exception List:Duplicate entry found for req : {}", value);
                return ApiStatusMessage.duplicate;
            } else {
                try {
                    ExceptionList entity = new ExceptionList();
                    entity.setCreated_on(value.getCreated_on());
                    entity.setImei(value.getImei());
                    entity.setImsi(value.getImsi());
                    entity.setMsisdn(value.getMsisdn());
                    entity.setRequestDate(value.getRequestDate());
                    entity.setActualImei(value.getActualImei());
                    ExceptionList savedEntity = exceptionListRepository.save(entity);
                    return ApiStatusMessage.added;
                } catch (Exception e) {
                    logger.info("Add Exception List:Exception while adding data in db : {} ErrorMessage:{}", value, e.getMessage());
                    return ApiStatusMessage.notAdded;
                }
            }
        } else {
            ExceptionListKey key = new ExceptionListKey(value.getImei(), value.getImsi(), value.getMsisdn());
            if (exceptionListCache.get(key) == null) {
                exceptionListCache.put(key, value.getCreated_on().format(DateUtil.defaultDateFormat));
                return ApiStatusMessage.added;
            } else {
                return ApiStatusMessage.duplicate;
            }
        }
    }


    @Override
    public ApiStatusMessage removeFromList(ExceptionListValue value) {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            Optional<ExceptionList> exceptionListPresent = exceptionListRepository.findByImeiAndImsiAndMsisdn(value.getImei(), value.getImsi(), value.getMsisdn());
            if (exceptionListPresent.isPresent()) {
                logger.info("Remove Exception List: Found entity : {} for req : {}", exceptionListPresent.get(), value);
                try {
                    exceptionListRepository.delete(exceptionListPresent.get());
                    logger.info("Remove Exception List: Data deleted from {}: {}", appConfiguration.getApplicationType(), value);
                    return ApiStatusMessage.deleted;
                } catch (Exception e) {
                    logger.error("Remove Exception List: Exception while deleting data from DB: {} ErrorMessage:{}", value, e.getMessage());
                    return ApiStatusMessage.notDeleted;
                }
            } else {
                logger.info("Remove Exception List: No entry found for req : {}", value);
                return ApiStatusMessage.notFound;
            }
        } else {
            ExceptionListKey key = new ExceptionListKey(value.getImei(), value.getImsi(), value.getMsisdn());
            if (exceptionListCache.get(key) == null) {
                return ApiStatusMessage.notFound;
            } else {
                exceptionListCache.remove(key);
                return ApiStatusMessage.deleted;
            }
        }
    }

    @Override
    public ListResponseData isPresent(VerificationRequest request) throws DataNotFoundException {

        String creationDate = exceptionListCache.get(new ExceptionListKey(request.getImei(), null, null));

        if (creationDate == null)
            creationDate = exceptionListCache.get(new ExceptionListKey(appConfiguration.getImeiNullPattern(), request.getImsi(), null));
        else return new ListResponseData(creationDate, ReasonCode.WhitelistWithIMEI);


        if (creationDate == null)
            creationDate = exceptionListCache.get(new ExceptionListKey(appConfiguration.getImeiNullPattern(), null, request.getMsisdn()));
        else return new ListResponseData(creationDate, ReasonCode.WhitelistWithIMSI);

        if (creationDate == null)
            creationDate = exceptionListCache.get(new ExceptionListKey(request.getImei(), request.getImsi(), null));
        else return new ListResponseData(creationDate, ReasonCode.WhitelistWithMSISDN);

        if (creationDate == null)
            creationDate = exceptionListCache.get(new ExceptionListKey(request.getImei(), null, request.getMsisdn()));
        else return new ListResponseData(creationDate, ReasonCode.WhitelistWithIMEI_IMSI);

        if (creationDate == null)
            creationDate = exceptionListCache.get(new ExceptionListKey(appConfiguration.getImeiNullPattern(), request.getImsi(), request.getMsisdn()));
        else return new ListResponseData(creationDate, ReasonCode.WhitelistWithIMEI_MSISDN);

        if (creationDate == null)
            creationDate = exceptionListCache.get(new ExceptionListKey(request.getImei(), request.getImsi(), request.getMsisdn()));
        else return new ListResponseData(creationDate, ReasonCode.WhitelistWithIMSI_MSISDN);

        if (creationDate == null) throw new DataNotFoundException("Not found");
        else return new ListResponseData(creationDate, ReasonCode.WhitelistWithIMEI_IMSI_MSISDN);
    }

    public List<ExceptionListKey> get() {
        List<ExceptionListKey> list = null;
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            Pageable firstElements = PageRequest.of(0, 10);
            Page<ExceptionList> page = exceptionListRepository.findAll(firstElements);
            list = page.get().map(p -> new ExceptionListKey(p.getImei(), p.getImsi(), p.getMsisdn())).collect(Collectors.toList());
        } else if (appConfiguration.getApplicationType() == ApplicationType.CACHE) {
            return exceptionListCache.keySet().stream().limit(10).collect(Collectors.toList());
        }
        return list;
    }


    @Override
    public Long getCount() {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            return exceptionListRepository.count();
        } else {
            return (long) exceptionListCache.size();
        }
    }
}
