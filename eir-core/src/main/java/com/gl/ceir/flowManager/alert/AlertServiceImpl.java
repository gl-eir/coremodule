package com.gl.ceir.flowManager.alert;

import com.gl.ceir.flowManager.contstants.AlertIds;
import com.gl.ceir.flowManager.contstants.AlertMessagePlaceholders;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.BooleanUtils;
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
public class AlertServiceImpl implements AlertService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private RestTemplate restTemplate = null;

    private BlockingQueue<AlertDto> queue = null;

    @Autowired
    AlertConfig alertConfig;

    @PostConstruct
    public void init() {
        if (alertConfig.getPostUrl() == null) {
            log.info("Alert Service is not enabled");
        } else {
            SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
            clientHttpRequestFactory.setConnectTimeout(1000);
            clientHttpRequestFactory.setReadTimeout(1000);
            restTemplate = new RestTemplate(clientHttpRequestFactory);
            queue = new LinkedBlockingQueue();
            new Thread(() -> sendAlertConsumer(), "sendAlertsConsumerThread").start();
        }
    }


    @Override
    public void sendAlert(AlertIds alertIds, Map<AlertMessagePlaceholders, String> placeHolderMap) {
        AlertConfigDto configDto = alertConfig.getAlertsMapping().get(alertIds);
        if (configDto == null) {
            log.warn("Message not configured for AlertId:{}", alertIds);
        } else {
            String alertId = configDto.getAlertId();
            String message = configDto.getMessage();
            if (BooleanUtils.isTrue(configDto.getEnable())) {
                putToQueue(AlertDto.builder().alertId(alertId)
                        .alertMessage(message).placeHolderMap(placeHolderMap)
                        .alertProcess(alertConfig.getProcessId())
                        .userId("0").build());
            } else {
                log.warn("Message not enabled for AlertId:{}", alertIds);
            }
        }
    }

    private void putToQueue(AlertDto alertDto) {
        try {
            queue.put(alertDto);
            log.info("Alert Added Request:{} QueueSize:{}", alertDto, queue.size());
        } catch (InterruptedException e) {
            log.error("Error:{} while adding Alert to Queue Request:{}", e.getMessage(), alertDto, e);
        }

    }

    private void sendAlertConsumer() {
        log.info("Alert Consumer Started Thread:{}", Thread.currentThread().getName());
        while (true) {
            try {
                AlertDto alertDto = queue.take();
                if (alertDto.getPlaceHolderMap() != null)
                    for (AlertMessagePlaceholders key : alertDto.getPlaceHolderMap().keySet())
                        alertDto.setAlertMessage(alertDto.getAlertMessage().replaceAll(key.getPlaceholder(), alertDto.getPlaceHolderMap().get(key)));
                log.info("Alert taken from Queue Request:{} QueueSize:{}", alertDto, queue.size());
                callAlertUrl(alertDto);
            } catch (InterruptedException e) {
                log.error("Error while Taking Request from Queue Error:{} ", e.getMessage(), e);
            }
        }
    }

    public void callAlertUrl(AlertDto alertDto) {
        long start = System.currentTimeMillis();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AlertDto> request = new HttpEntity<AlertDto>(alertDto, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(alertConfig.getPostUrl(), request, String.class);
            log.info("Alert Sent Request:{}, TimeTaken:{} Response:{}", alertDto, responseEntity, (System.currentTimeMillis() - start));
        } catch (org.springframework.web.client.ResourceAccessException resourceAccessException) {
            log.error("Error while Sending Alert resourceAccessException:{} Request:{}", resourceAccessException.getMessage(), alertDto, resourceAccessException);
        } catch (Exception e) {
            log.error("Error while Sending Alert Error:{} Request:{}", e.getMessage(), alertDto, e);
        }
    }
}
