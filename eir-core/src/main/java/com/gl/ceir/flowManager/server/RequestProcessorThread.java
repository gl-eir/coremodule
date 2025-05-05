package com.gl.ceir.flowManager.server;

import com.gl.ceir.flowManager.configuration.InfoMapperUdpServerConfig;
import com.gl.ceir.flowManager.contstants.MsisdnFilledStatus;
import com.gl.ceir.flowManager.dto.InformationMappingRequestDto;
import com.gl.ceir.flowManager.dto.MappingType;
import com.gl.ceir.flowManager.dto.VerificationRequest;
import com.gl.ceir.flowManager.executor.UdpThreadPoolExecutor;
import com.gl.ceir.flowManager.orchestrator.RequestProcessOrchestrator;
import com.gl.ceir.flowManager.service.ModuleAlertService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Service
@Slf4j
public class RequestProcessorThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RequestProcessOrchestrator requestProcessOrchestrator;

    @Autowired
    Gson gson;

    @Autowired
    @Qualifier("receivingQueue")
    BlockingQueue<String> receivingQueue;

    @Autowired
    UdpThreadPoolExecutor executor;

    @Autowired
    InfoMapperUdpServerConfig infoMapperUdpServerConfig;

    @Autowired
    private IUdpSender udpSender;

    @Autowired
    private ModuleAlertService alertService;

    public void run() {
        while (true) {
            try {
                String requestString = receivingQueue.take();
                VerificationRequest request = gson.fromJson(requestString, VerificationRequest.class);
                request.setMsisdnFilled(StringUtils.isBlank(request.getMsisdn()) ? MsisdnFilledStatus.MISSING : MsisdnFilledStatus.FILLED);
                logger.info("VerificationRequest : {} Queue:{}", request, executor.getQueue().size());
                executor.execute(() -> requestProcessOrchestrator.processUdpRequest(request));
                alertService.sendUdpQueueSizeAlert(executor.getQueue().size());
            } catch (Exception e) {
                logger.error("Some exception", e);
            }
        }
    }

}

