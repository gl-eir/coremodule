package com.gl.ceir.flowManager.contstants;

public enum ReasonCode {

    expiryFromDiameterClient(0), //Ok
    blockedByBlockedTacList(1), // Ok
    notFoundInAllowedTacList(2), //Ok
    blockedDevicetype(3), // Ok
    invalidDevice(4), // Ok
    eirDeviceTypeGlobalFlagIsOff(5), // Ok
    eirWorkFlowGlobalFlagIsOff(6), // Ok
    allCheckPassed(7), //Ok
    foreignImsi(8), // Ok
    imeiNull(9), // Ok
    imeiNotNumeric(10), // Ok
    imeiAllZero(11), //Ok
    imeiLength(12), // Ok
    imsiNull(13), // Ok
    WhitelistWithIMEI(14), // Ok
    WhitelistWithIMSI(15), // Ok
    WhitelistWithMSISDN(16), // Ok
    WhitelistWithIMEI_IMSI(17), // Ok
    WhitelistWithIMEI_MSISDN(18), // Ok
    WhitelistWithIMSI_MSISDN(19), // Ok
    WhitelistWithIMEI_IMSI_MSISDN(20), // Ok
    BlacklistWithIMEI(21), // Ok
    BlacklistWithIMSI(22), // Ok
    BlacklistWithMSISDN(23), // Ok
    BlacklistWithIMEI_IMSI(24), // Ok
    BlacklistWithIMEI_MSISDN(25), // Ok
    BlacklistWithIMSI_MSISDN(26), // Ok
    BlacklistWithIMEI_IMSI_MSISDN(27), // Ok
    GreylistWithIMEI(28), // Ok
    GreylistWithIMSI(29), // Ok
    GreylistWithMSISDN(30), // Ok
    GreylistWithIMEI_IMSI(31), // Ok
    GreylistWithIMEI_MSISDN(32), // Ok
    GreylistWithIMSI_MSISDN(33), // Ok
    GreylistWithIMEI_IMSI_MSISDN(34); // Ok

    int code;

    ReasonCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static ReasonCode getName(Integer code) {
        for (ReasonCode reasonCode : ReasonCode.values())
            if (code == reasonCode.getCode())
                return reasonCode;
        return null;
    }
}
