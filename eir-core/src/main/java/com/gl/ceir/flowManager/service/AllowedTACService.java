package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.configuration.AppConfiguration;
import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.contstants.ApplicationType;
import com.gl.ceir.flowManager.contstants.FileDownloadStatus;
import com.gl.ceir.flowManager.contstants.ListType;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.entity.AllowedTAC;
import com.gl.ceir.flowManager.exception.DataNotFoundException;
import com.gl.ceir.flowManager.repository.AllowedTACListRepository;
import com.gl.ceir.flowManager.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
public class AllowedTACService implements IAllowedTACListService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private Map<AllowedTacKey, String> allowedTACList = new ConcurrentHashMap<>();

    // This method will be called by spring when process comes up.

    @Autowired
    AllowedTACListRepository allowedTACListRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    AppConfiguration appConfiguration;

    String query = "select tac , created_on from allowed_tac";

    private FileDownloadStatus fileStatus = FileDownloadStatus.COMPLETED;

    public FileDownloadStatus getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(FileDownloadStatus fileStatus) {
        this.fileStatus = fileStatus;
    }


    @PostConstruct
    public void myInit() {
        if (appConfiguration.getApplicationType() == ApplicationType.CACHE)
            loadAllowedTACList();
    }

    @Override
    public int loadAllowedTACList() {
        try {

            Statement statement = jdbcTemplate.getDataSource().getConnection().createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String tac = rs.getString("tac");
                String createdDate = rs.getString("created_on");
                AllowedTacKey key = new AllowedTacKey(tac);
                allowedTACList.put(key, createdDate == null ? "" : createdDate);
            }
            rs.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Allowed TAC List data load count : {}", allowedTACList.size());
        return allowedTACList.size();
    }


    public void writeFile() {
        logger.info("Going to Write file AllowedTac status:{}", fileStatus);
        PrintWriter fileCloseWriter = null;
        try {
            PrintWriter writer = new PrintWriter(appConfiguration.getFilePath() + "/" + ListType.ALLOWED_TAC.getFilename());
            fileCloseWriter = writer;
            writer.println("TAC,CREATED_DATE");
            logger.info("File Writing Started for AllowedTac status:{} from:{}", fileStatus, appConfiguration.getApplicationType());
            final AtomicLong count = new AtomicLong(0);
            if (appConfiguration.getApplicationType() == ApplicationType.DB) {
                jdbcTemplate.query(query, new RowCallbackHandler() {
                    public void processRow(ResultSet rs) throws SQLException {
                        String tac = rs.getString("tac") == null ? "" : rs.getString("tac");
                        String createdDate = rs.getString("created_on") == null ? "" : rs.getString("created_on");
                        writer.println(tac + "," + createdDate);
                        count.set(count.get() + 1);
                        if (count.get() % 1000 == 0) {
                            logger.info("AllowedTac Records:{} are written in file ", count.get());
                        }
                    }
                });
            } else {
                for (AllowedTacKey key : allowedTACList.keySet()) {
                    writer.println(key.getTac() + "," + allowedTACList.get(key));
                    count.set(count.get() + 1);
                    if (count.get() % 1000 == 0) {
                        logger.info("AllowedTac Records:{} are written in file ", count.get());
                    }
                }
            }
            fileStatus = FileDownloadStatus.COMPLETED;
        } catch (Exception e) {
            fileStatus = FileDownloadStatus.COMPLETED_ERROR;
            logger.error("Error While AllowedTac writing file Error{}", e.getMessage(), e);
        } finally {
            fileCloseWriter.close();
        }

    }

    @Override
    public ApiStatusMessage removeFromList(AllowedTACListValue value) {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            Optional<AllowedTAC> blackListPresent = allowedTACListRepository.getByTac(value.getTac());
            if (blackListPresent.isPresent()) {
                logger.info("Remove Allowed Tac: Found entity : {} for req : {}", blackListPresent.get(), value);
                try {
                    if (appConfiguration.getApplicationType() == ApplicationType.DB)
                        allowedTACListRepository.delete(blackListPresent.get());
                    else if (appConfiguration.getApplicationType() == ApplicationType.CACHE)
                        allowedTACList.remove(new AllowedTacKey(value.getTac()));
                    logger.info("Remove Allowed Tac: Data deleted from {}: {}", appConfiguration.getApplicationType(), value);
                    return ApiStatusMessage.deleted;
                } catch (Exception e) {
                    logger.error("Remove Allowed Tac: Exception while deleting data from DB: {}", value);
                    return ApiStatusMessage.notDeleted;
                }
            } else {
                logger.info("Remove Allowed Tac: No entry found for req : {}", value);
                return ApiStatusMessage.notFound;
            }
        } else {
            AllowedTacKey key = new AllowedTacKey(value.getTac());
            if (allowedTACList.get(key) == null) {
                return ApiStatusMessage.notFound;
            } else {
                allowedTACList.remove(key);
                return ApiStatusMessage.deleted;
            }
        }
    }


    @Override
    public ApiStatusMessage addToList(AllowedTACListValue values) {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            Optional<AllowedTAC> blackListPresent = allowedTACListRepository.getByTac(values.getTac());
            if (blackListPresent.isPresent()) {
                logger.info("Add Allowed Tac: Duplicate entry : {} for req : {}", blackListPresent.get(), values);
                return ApiStatusMessage.duplicate;
            } else {
                try {
                    AllowedTAC entity = new AllowedTAC();
                    entity.setTac(values.getTac());
                    entity.setCreated_on(values.getCreated_on());
                    entity.setRequestDate(values.getRequestDate());
                    allowedTACListRepository.save(entity);
                    logger.info("Added Allowed Tac: Data Added to {}: {}", appConfiguration.getApplicationType(), values);
                    return ApiStatusMessage.added;
                } catch (Exception e) {
                    logger.info("Add Allowed Tac:Exception while adding data in db : {}", values);
                    return ApiStatusMessage.notAdded;
                }
            }
        } else {
            AllowedTacKey key = new AllowedTacKey(values.getTac());
            if (allowedTACList.get(key) == null) {
                allowedTACList.put(key, values.getCreated_on().format(DateUtil.defaultDateFormat));
                return ApiStatusMessage.added;
            } else {
                return ApiStatusMessage.duplicate;
            }
        }
    }


    @Override
    public ListResponseData isPresent(VerificationRequest request) throws DataNotFoundException {
        ListResponseData resp = new ListResponseData();

        String data = allowedTACList.get(new AllowedTacKey(request.getTac()));
        if (data == null)
            throw new DataNotFoundException("Data Not Found in Allowed Tac:" + request.getTac());

        resp.setCreated_on(data);
        logger.info("Search: Found in Allowed Tac : tac:{} data:{}", request.getTac(), data);
        return resp;
    }

    public List<AllowedTacKey> get() {
        List<AllowedTacKey> list = null;
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            Pageable firstElements = PageRequest.of(0, 10);
            Page<AllowedTAC> page = allowedTACListRepository.findAll(firstElements);
            list = page.get().map(p -> new AllowedTacKey(p.getTac())).collect(Collectors.toList());
        } else if (appConfiguration.getApplicationType() == ApplicationType.CACHE) {
            return allowedTACList.keySet().stream().limit(10).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public Long getCount() {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            return allowedTACListRepository.count();
        } else {
            return (long) allowedTACList.size();
        }
    }

}
