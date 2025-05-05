package com.gl.ceir.flowManager.dto;

import com.gl.ceir.flowManager.contstants.MsisdnFilledStatus;
import com.gl.ceir.flowManager.exception.ImeiNullException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationRequest {

    private String tid;
    private String imei;
    private String imsi;
    private String msisdn;
    private String responseIp;
    private int responsePort;
    private String tac;
    private MsisdnFilledStatus msisdnFilled;
    private String imeiReceived;

    public void formatImei() throws ImeiNullException {
        this.imeiReceived = this.imei;
        if (imei == null)
            throw new ImeiNullException(tid);
        else {
            if (imei.length() > 14)
                this.imei = imei.substring(0, 14);

            if (imei.length() > 8)
                this.tac = imei.substring(0, 8);
            else
                this.tac = "";
        }
    }
}
