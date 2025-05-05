package com.gl.ceir.flowManager.traces;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gl.ceir.flowManager.dto.VerificationResponse;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class EdrDto {

    private String timeStamp;
    private String imsi;
    private String imei;
    private String msisdn;
    private String status;
    // EDR Parameters
    private String tac;
    private String deviceType;
    private String appliedListName;
    private Integer reasonCode;
    private String protocol;
    private String timeTaken;
    private String hostname;
    private String server;
    private String originHost;
    private String sessionId;

    public EdrDto() {

    }

    public EdrDto(String protocol, VerificationResponse responseDto) {
        this.protocol = protocol;
        this.setImei(responseDto.getImei());
        this.setImsi(responseDto.getImsi());
        this.setMsisdn(responseDto.getMsisdn());
        this.setTac(responseDto.getTac());
        this.setDeviceType(String.valueOf(responseDto.getDeviceType()));
        this.setStatus(String.valueOf(responseDto.getStatus()));
        this.setAppliedListName(String.valueOf(responseDto.getAppliedListName()));
        this.setReasonCode(responseDto.getReasonCode());
    }

    public EdrDto(String protocol, String sessionId, String originHost, VerificationResponse responseDto) {
        this.protocol = protocol;
        this.sessionId = sessionId;
        this.originHost = originHost;
        this.setImei(responseDto.getImei());
        this.setImsi(responseDto.getImsi());
        this.setMsisdn(responseDto.getMsisdn());
        this.setTac(responseDto.getTac());
        this.setDeviceType(String.valueOf(responseDto.getDeviceType()));
        this.setStatus(String.valueOf(responseDto.getStatus()));
        this.setAppliedListName(String.valueOf(responseDto.getAppliedListName()));
        this.setReasonCode(responseDto.getReasonCode());
    }

    public String toEdr() {
        StringBuilder sb = new StringBuilder();
        String seprator = ",";
        sb.append(timeStamp).append(seprator).append(getImei() == null ? "" : getImei()).append(seprator).append(getImsi() == null ? "" : getImsi()).append(seprator).append(getMsisdn() == null ? "" : getMsisdn())
                .append(seprator).append(getTac() == null ? "" : getTac()).append(seprator).append(getDeviceType() == null ? "" : getDeviceType()).append(seprator).append(getStatus())
                .append(seprator).append(getAppliedListName() == null ? "" : getAppliedListName()).append(seprator).append(getReasonCode()).append(seprator).append(protocol)
                .append(seprator).append(timeTaken).append(seprator).append(hostname == null ? "" : hostname).append(seprator).append(server == null ? "" : server)
                .append(seprator).append(this.getSessionId()).append(seprator).append(this.getOriginHost());
        return sb.toString();
    }

}
