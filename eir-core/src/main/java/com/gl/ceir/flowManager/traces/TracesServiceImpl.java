package com.gl.ceir.flowManager.traces;

import com.gl.ceir.flowManager.alert.AlertConfig;
import com.gl.ceir.flowManager.alert.AlertConfigDto;
import com.gl.ceir.flowManager.alert.AlertDto;
import com.gl.ceir.flowManager.alert.AlertService;
import com.gl.ceir.flowManager.contstants.AlertIds;
import com.gl.ceir.flowManager.contstants.AlertMessagePlaceholders;
import com.gl.ceir.flowManager.server.UdpSender;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class TracesServiceImpl implements TracesService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TracesConfig tracesConfig;

    @Autowired
    UdpSender udpSender;

    @Override
    public void sendBlackListTrace(EdrDto edrDto) {
        if (tracesConfig.getTraceBlackFlag())
            sendUdp(edrDto);
    }

    @Override
    public void sendGreyListTrace(EdrDto edrDto) {
        if (tracesConfig.getTraceGreyFlag())
            sendUdp(edrDto);
    }

    private void sendUdp(EdrDto edrDto) {
        String data = edrDto.toEdr();
        log.error("Sending Traces UDP to  IP:{} PORT:{} Request:{}", tracesConfig.getIp(), tracesConfig.getPort(), data);
        udpSender.send(tracesConfig.getIp(), tracesConfig.getPort(), data);
    }

}
