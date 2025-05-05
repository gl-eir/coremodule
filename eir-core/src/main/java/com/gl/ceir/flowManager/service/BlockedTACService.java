package com.gl.ceir.flowManager.service;


import com.gl.ceir.flowManager.configuration.AppConfiguration;
import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.contstants.ApplicationType;
import com.gl.ceir.flowManager.contstants.FileDownloadStatus;
import com.gl.ceir.flowManager.contstants.ListType;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.entity.AllowedTAC;
import com.gl.ceir.flowManager.entity.BlockedTAC;
import com.gl.ceir.flowManager.exception.DataNotFoundException;
import com.gl.ceir.flowManager.repository.BlockedTACListRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class BlockedTACService implements IBlockedTACListService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private Map<BlockedTacKey, String> blockedTACList = new ConcurrentHashMap<>();

    String query = "select tac , created_on from blocked_tac";

    private FileDownloadStatus fileStatus = FileDownloadStatus.COMPLETED;

    public FileDownloadStatus getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(FileDownloadStatus fileStatus) {
        this.fileStatus = fileStatus;
    }

    // This method will be called by spring when process comes up.
    @PostConstruct
    public void myInit() {
        if (appConfiguration.getApplicationType() == ApplicationType.CACHE)
            loadBlockedTACList();
    }

    @Autowired
    BlockedTACListRepository blockedTACListRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    AppConfiguration appConfiguration;

    @Override
    public int loadBlockedTACList() {
        try {
            Statement statement = jdbcTemplate.getDataSource().getConnection().createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String tac = rs.getString("tac") == null ? "" : rs.getString("tac");
                String createdDate = rs.getString("created_on") == null ? "" : rs.getString("created_on");
                BlockedTacKey key = new BlockedTacKey(tac);
                blockedTACList.put(key, createdDate == null ? "" : createdDate);
            }
            rs.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Blocked TAC data load count : {}", blockedTACList.size());
        return blockedTACList.size();
    }

    public void writeFile() {
        logger.info("Going to Write file BlockedTac status:{}", fileStatus);
        PrintWriter fileCloseWriter = null;
        try {
            PrintWriter writer = new PrintWriter(appConfiguration.getFilePath() + "/" + ListType.BLOCKED_TAC.getFilename());
            fileCloseWriter = writer;
            writer.println("TAC,CREATED_DATE");
            logger.info("File Writing Started for BlockedTac status:{} from:{}", fileStatus, appConfiguration.getApplicationType());
            final AtomicLong count = new AtomicLong(0);
            if (appConfiguration.getApplicationType() == ApplicationType.DB) {
                jdbcTemplate.query(query, new RowCallbackHandler() {
                    public void processRow(ResultSet rs) throws SQLException {
                        String tac = rs.getString("tac");
                        String createdDate = rs.getString("created_on");
                        writer.println(tac + "," + createdDate);
                        count.set(count.get() + 1);
                        if (count.get() % 1000 == 0) {
                            logger.info("BlockedTac Records:{} are written in file ", count.get());
                        }
                    }
                });
            } else {
                for (BlockedTacKey key : blockedTACList.keySet()) {
                    writer.println(key.getTac() + "," + blockedTACList.get(key));
                    count.set(count.get() + 1);
                    if (count.get() % 1000 == 0) {
                        logger.info("BlockedTac Records:{} are written in file ", count.get());
                    }
                }
            }
            fileStatus = FileDownloadStatus.COMPLETED;
        } catch (Exception e) {
            fileStatus = FileDownloadStatus.COMPLETED_ERROR;
            logger.error("Error While BlockedTac writing file Error{}", e.getMessage(), e);
        } finally {
            fileCloseWriter.close();
        }

    }

    @Override
    public ApiStatusMessage removeFromList(BlockedTACListValue value) {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            Optional<BlockedTAC> blackListPresent = blockedTACListRepository.getByTac(value.getTac());
            if (blackListPresent.isPresent()) {
                logger.info("Remove Blocked Tac: Found entity : {} for req : {}", blackListPresent.get(), value);
                try {
                    blockedTACListRepository.delete(blackListPresent.get());
                    logger.info("Remove Blocked Tac: Data deleted from {}: {}", appConfiguration.getApplicationType(), value);
                    return ApiStatusMessage.deleted;
                } catch (Exception e) {
                    logger.error("Remove Blocked Tac: Exception while deleting data from DB: {} ErrorMessage:{}", value, e.getMessage());
                    return ApiStatusMessage.notDeleted;
                }
            } else {
                logger.info("Remove Blocked Tac: No entry found for req : {}", value);
                return ApiStatusMessage.notFound;
            }
        } else {
            BlockedTacKey key = new BlockedTacKey(value.getTac());
            if (blockedTACList.get(key) == null) {
                return ApiStatusMessage.notFound;
            } else {
                blockedTACList.remove(key);
                return ApiStatusMessage.deleted;
            }
        }
    }

    @Override
    public ApiStatusMessage addToList(BlockedTACListValue values) {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            Optional<BlockedTAC> blackListPresent = blockedTACListRepository.getByTac(values.getTac());
            if (blackListPresent.isPresent()) {
                logger.info("Add Blocked Tac: Duplicate entry : {} for req : {}", blackListPresent.get(), values);
                return ApiStatusMessage.duplicate;
            } else {
                try {
                    BlockedTAC entity = new BlockedTAC();
                    entity.setTac(values.getTac());
                    entity.setCreated_on(values.getCreated_on());
                    entity.setRequestDate(values.getRequestDate());
                    blockedTACListRepository.save(entity);
                    return ApiStatusMessage.added;
                } catch (Exception e) {
                    logger.info("Add Blocked Tac:Exception while adding data in db : {} ErrorMessage:{}", values, e.getMessage());
                    return ApiStatusMessage.notAdded;
                }
            }
        } else {
            BlockedTacKey key = new BlockedTacKey(values.getTac());
            if (blockedTACList.get(key) == null) {
                blockedTACList.put(key, values.getCreated_on().format(DateUtil.defaultDateFormat));
                return ApiStatusMessage.added;
            } else
                return ApiStatusMessage.duplicate;
        }
    }


    @Override
    public ListResponseData isPresent(VerificationRequest request) throws DataNotFoundException {
        ListResponseData resp = new ListResponseData();

        String data = blockedTACList.get(new BlockedTacKey(request.getTac()));
        if (data == null)
            throw new DataNotFoundException("Data Not Found in Blocked Tac:" + request.getTac());

        resp.setCreated_on(data);
        logger.info("Search: Found in Blocked Tac: tac:{} data:{}", request.getTac(), data);
        return resp;
    }

    public List<BlockedTacKey> get() {
        List<BlockedTacKey> list = null;
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            Pageable firstElements = PageRequest.of(0, 10);
            Page<BlockedTAC> page = blockedTACListRepository.findAll(firstElements);
            list = page.get().map(p -> new BlockedTacKey(p.getTac())).collect(Collectors.toList());
        } else if (appConfiguration.getApplicationType() == ApplicationType.CACHE) {
            return blockedTACList.keySet().stream().limit(10).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public Long getCount() {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            return blockedTACListRepository.count();
        } else {
            return (long) blockedTACList.size();
        }
    }
}
