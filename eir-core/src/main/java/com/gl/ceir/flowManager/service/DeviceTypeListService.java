package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.configuration.AppConfiguration;
import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.contstants.ApplicationType;
import com.gl.ceir.flowManager.contstants.DeviceType;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.entity.DeviceTypeList;
import com.gl.ceir.flowManager.repository.DeviceTypeListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeviceTypeListService implements IDeviceTypeListService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<String, GlobalDeviceTypeListValue> deviceTypeList = new ConcurrentHashMap<>();
    ;

    @Autowired
    AppConfiguration appConfiguration;

    @PostConstruct
    public void myInit() {
        if (appConfiguration.getApplicationType() == ApplicationType.CACHE) loadDeviceTypeList();
    }


    @Autowired
    private DeviceTypeListRepository deviceTypeListRepository;

    @Override
    public int loadDeviceTypeList() {

        deviceTypeList = new ConcurrentHashMap<>();

        List<DeviceTypeList> fullDeviceTypeList = deviceTypeListRepository.findAll();
        for (DeviceTypeList deviceTypeListElement : fullDeviceTypeList) {
            GlobalDeviceTypeListValue value = new GlobalDeviceTypeListValue(deviceTypeListElement.getTac(), DeviceType.findByName(deviceTypeListElement.getDeviceType()));
            deviceTypeList.put(deviceTypeListElement.getTac(), value);
        }
        logger.info("Device Type List data load count : {}", deviceTypeList.size());
        return deviceTypeList.size();
    }

    @Override
    public ApiStatusMessage addToListDB(GlobalDeviceTypeListValue value) {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            try {
                Optional<DeviceTypeList> deviceTypeListPresent = deviceTypeListRepository.getByTac(value.getTac());
                if (deviceTypeListPresent.isPresent()) {
                    logger.info("Add GSMA Data: Found entity : {}", deviceTypeListPresent.get());
                    return ApiStatusMessage.duplicate;
                } else {
                    DeviceTypeList entity = new DeviceTypeList();
                    entity.setTac(value.getTac());
                    entity.setDeviceType(value.getDevice_type().getName());
                    DeviceTypeList savedEntity = deviceTypeListRepository.save(entity);
                    logger.info("Add GSMA Data: Added entity to {}: {}", appConfiguration.getApplicationType(), savedEntity);
                    return ApiStatusMessage.added;
                }
            } catch (Exception e) {
                logger.error("Add GSMA Data:Exception while adding data in db Request:{} ErrorMessage:{}", value, e.getMessage());
                return ApiStatusMessage.notAdded;
            }
        } else {
            GlobalDeviceTypeListValue deviceTypeListValue = deviceTypeList.get(value.getTac());
            if (deviceTypeListValue == null) {
                logger.info("Add GSMA Data: Added entity to {} existing:{} Override DeviceType:{} ", appConfiguration.getApplicationType(), deviceTypeListValue, value.getDevice_type());
                deviceTypeList.put(value.getTac(), value);
                return ApiStatusMessage.added;
            } else {
                return ApiStatusMessage.duplicate;
            }
        }
    }


    @Override
    public ApiStatusMessage removeFromListDB(GlobalDeviceTypeListValue value) {
        try {
            if (appConfiguration.getApplicationType() == ApplicationType.DB) {
                Optional<DeviceTypeList> deviceTypeListPresent = deviceTypeListRepository.getByTac(value.getTac());
                if (deviceTypeListPresent.isPresent()) {
                    logger.info("Remove GSMA Data: Found entity : {} ", deviceTypeListPresent.get());
                    deviceTypeListRepository.delete(deviceTypeListPresent.get());
                    return ApiStatusMessage.deleted;
                } else {
                    logger.info("Remove GSMA Data: No entry found for {}", value);
                    return ApiStatusMessage.notFound;
                }
            } else {
                if (deviceTypeList.get(value.getTac()) == null) {
                    return ApiStatusMessage.notFound;
                } else {
                    GlobalDeviceTypeListValue deviceTypeListValue = deviceTypeList.remove(value.getTac());
                    logger.info("Add GSMA Data: Removed entity from {}: deviceTypeListValue:{}  Request :{} ", appConfiguration.getApplicationType(), deviceTypeListValue, value.getTac());
                    return ApiStatusMessage.deleted;
                }
            }
        } catch (Exception e) {
            logger.error("Remove GSMA Data: Exception while deleting data from DB Request:{} ErrorMessage:{}", value, e.getMessage());
            return ApiStatusMessage.notDeleted;
        }
    }

    @Override
    public DeviceTypeListResponseData getDeviceTypeData(String tac) {
        logger.info("Search: Checking in [Device Type List] : ", tac);
        DeviceTypeListResponseData resp = new DeviceTypeListResponseData();
        GlobalDeviceTypeListValue data = deviceTypeList.get(tac);
        if (data == null) {
            resp.setDeviceType(null);
        } else {
            resp.setDeviceType(data.getDevice_type());
        }
        logger.info("Search: device type tac : {} The device type : {}", tac, resp);
        return resp;
    }

    @Override
    public Long getCount() {
        if (appConfiguration.getApplicationType() == ApplicationType.DB) {
            return deviceTypeListRepository.count();
        } else {
            return (long) deviceTypeList.size();
        }
    }
}