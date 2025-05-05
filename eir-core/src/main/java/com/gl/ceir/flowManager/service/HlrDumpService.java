package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.configuration.AppConfiguration;
import com.gl.ceir.flowManager.configuration.InfoMapperUdpServerConfig;
import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.contstants.ApplicationType;
import com.gl.ceir.flowManager.entity.HlrData;
import com.gl.ceir.flowManager.exception.DataNotFoundException;
import com.gl.ceir.flowManager.repository.HlrDataRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HlrDumpService implements IHlrDumpService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<String, String> hlrDumpCache = new ConcurrentHashMap<>();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HlrDataRepository hlrDataRepository;

    @Autowired
    AppConfiguration appConfiguration;

    @Autowired
    InfoMapperUdpServerConfig infoMapperUdpServerConfig;

    @PostConstruct
    public void myInit() {
        if (appConfiguration.getApplicationType() == ApplicationType.CACHE && infoMapperUdpServerConfig.getInfoMapperIsEnabled())
            loadHlrDump();
    }


    public int loadHlrDump() {
        try {
            String query = "select imsi , msisdn from hlr_data";
            Statement statement = jdbcTemplate.getDataSource().getConnection().createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String imsi = rs.getString("imsi");
                String msisdn = rs.getString("msisdn");
                hlrDumpCache.put(imsi, msisdn);
            }
            rs.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("HlrDumpCache data load count : {}", hlrDumpCache.size());
        return hlrDumpCache.size();
    }

    @Override
    public String getMsisdn(String imsi) throws DataNotFoundException {
        String msisdn = hlrDumpCache.get(imsi);
        if (msisdn == null) throw new DataNotFoundException("Data Not Found in Bocked TAC List tac:" + msisdn);
        logger.info("Search: Found in HLR Dump List : msisdn:{} imsi:{}", msisdn, imsi);
        return msisdn;

    }

    @Override
    public ApiStatusMessage removeFromList(String imsie, String msisdn) {
        try {
            if (appConfiguration.getApplicationType() == ApplicationType.DB) {
                Optional<HlrData> hlrDataOptional = hlrDataRepository.getByImsiAndMsisnd(imsie, msisdn);
                if (hlrDataOptional.isPresent()) {
                    logger.info("Remove Hlr Data: Found entity : {} for imsie:{} msisdn:{}", hlrDataOptional.get(), imsie, msisdn);
                    hlrDataRepository.delete(hlrDataOptional.get());
                    return ApiStatusMessage.deleted;
                } else {
                    logger.info("Remove Hlr Data: No entry found for imsie:{} msisdn:{} ", imsie, msisdn);
                    return ApiStatusMessage.notFound;
                }
            } else {
                if (hlrDumpCache.get(imsie) == null) {
                    return ApiStatusMessage.notFound;
                } else {
                    String value = hlrDumpCache.remove(imsie);
                    logger.info("Add HLR Data: Removed entity from {}: imsi:{} existing msisdn:{}  Request Msisdn:{} ", appConfiguration.getApplicationType(), imsie, value, msisdn);
                    return ApiStatusMessage.deleted;
                }
            }
        } catch (Exception e) {
            logger.error("Remove Hlr Data: Exception while deleting data from DB: imsie:{} msisdn:{} ErrorMessage:{}", imsie, msisdn, e.getMessage());
            return ApiStatusMessage.notDeleted;
        }
    }

    @Override
    public ApiStatusMessage addToList(String imsie, String msisdn) {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            try {
                Optional<HlrData> hlrDataOptional = hlrDataRepository.getByImsiAndMsisnd(imsie, msisdn);
                if (hlrDataOptional.isPresent()) {
                    logger.info("Add HLR Data: Found entity : {}", hlrDataOptional.get());
                    return ApiStatusMessage.duplicate;
                } else {
                    HlrData entity = new HlrData();
                    entity.setImsi(imsie);
                    entity.setMsisdn(msisdn);
                    HlrData savedEntity = hlrDataRepository.save(entity);
                    logger.info("Add HLR Data: Added entity to {}: {}", appConfiguration.getApplicationType(), savedEntity);
                    return ApiStatusMessage.added;
                }
            } catch (Exception e) {
                logger.error("Add Hlr Data:Exception while adding data in db imsie:{} msisdn:{} ErrorMessage:{}", imsie, msisdn, e.getMessage());
                return ApiStatusMessage.notAdded;
            }
        } else {
            String value = hlrDumpCache.get(imsie);
            logger.info("Add HLR Data: Added entity to {}: imsi:{} existing msisdn:{} Override Msisdn:{} ", appConfiguration.getApplicationType(), imsie, value, msisdn);
            hlrDumpCache.put(imsie, msisdn);
            return ApiStatusMessage.added;
        }
    }

    @Override
    public Long getCount() {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            return hlrDataRepository.count();
        } else {
            return (long) hlrDumpCache.size();
        }
    }
}