package com.gl.ceir.flowManager.controller;

import com.gl.ceir.flowManager.configuration.EdrUdpConfig;
import com.gl.ceir.flowManager.contstants.MsisdnFilledStatus;
import com.gl.ceir.flowManager.contstants.ReasonCode;
import com.gl.ceir.flowManager.contstants.StatusValue;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.orchestrator.RequestProcessOrchestrator;
import com.gl.ceir.flowManager.server.IUdpSender;
import com.gl.ceir.flowManager.traces.EdrDto;
import com.gl.ceir.flowManager.traces.TracesService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
public class SearchController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RequestProcessOrchestrator requestProcessOrchestrator;

    @Autowired
    EdrUdpConfig edrUdpConfig;

    @Autowired
    IUdpSender udpSender;

    @Autowired
    TracesService tracesService;

    @PostMapping("/search")
    public ResponseEntity<VerificationResponse> search(@RequestBody VerificationRequest request) {
        try {
            VerificationResponse verificationResponse = requestProcessOrchestrator.checkRules(request);
            return new ResponseEntity<>(verificationResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/v1/equipment-status")
    public ResponseEntity<EquipmentStatusResponseDto> searchGet(@RequestParam(name = "pei", required = true) String imei, @RequestParam(name = "reqID", required = true) String requestId,
                                                                @RequestParam(name = "gpsi", required = false) String msisdn, @RequestParam(name = "supi", required = false) String imsi,
                                                                @RequestParam(name = "nwID", required = true) String callingGt, HttpServletRequest httpServletRequest) {
        log.info("Search request received : imei : {} , requestId : {}, msisdn : {} , imsi : {} , callingGt : {}", imei, requestId, msisdn, imsi, callingGt);
        VerificationRequest request = VerificationRequest.builder().tid(requestId).imei(imei).imsi(imsi).msisdn(msisdn).msisdnFilled(StringUtils.isBlank(msisdn) ? MsisdnFilledStatus.MISSING : MsisdnFilledStatus.FILLED).
                build();
        long start = System.currentTimeMillis();
        LocalDateTime requestDate = LocalDateTime.now();
        String server = httpServletRequest.getRemoteAddr();
        VerificationResponse verificationResponse = requestProcessOrchestrator.checkRules(request);
        if (verificationResponse.getStatus() == 7) {
            EquipmentStatusResponseDto response = EquipmentStatusResponseDto.builder().equipmentStatus(null)
                    .problemDetail(ProblemDetailsDto.builder().description("Device no found/exist")
                            .properties(ProblemDetailsPropertiesDto.builder()
                                    .detail(String.valueOf(verificationResponse.getDeviceType()))
                                    .status(100).title(ReasonCode.getName(verificationResponse.getReasonCode()).name()).build()).build()).build();
            writeEDR(verificationResponse, (System.currentTimeMillis() - start), requestDate, server, callingGt);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            EquipmentStatusResponseDto response = EquipmentStatusResponseDto.builder().equipmentStatus(verificationResponse.getValue()).build();
            writeEDR(verificationResponse, (System.currentTimeMillis() - start), requestDate, server, callingGt);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    public void writeEDR(VerificationResponse responseDto, long timeTaken, LocalDateTime requestDate, String server, String callingGt) {
        try {
            String hostname = InetAddress.getLocalHost().getHostAddress();
            String timeStamp = requestDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            EdrDto edrDto = new EdrDto("SS7", responseDto.getTid(), callingGt, responseDto);
            edrDto.setTimeStamp(timeStamp);
            edrDto.setTimeTaken(String.valueOf(timeTaken));
            edrDto.setHostname(hostname);
            edrDto.setServer(server);
            udpSender.send(edrUdpConfig.getEdrServerIp(), edrUdpConfig.getEdrServerPort(), edrDto.toEdr());
            if (responseDto.getStatus() == StatusValue.blacklist.getCode()) {
                tracesService.sendBlackListTrace(edrDto);
            } else if (responseDto.getStatus() == StatusValue.greylist.getCode()) {
                tracesService.sendGreyListTrace(edrDto);
            }
        } catch (Exception e) {
            logger.error("Error while sending EDR ERROR:{}", e.getMessage(), e);
        }
    }
}
