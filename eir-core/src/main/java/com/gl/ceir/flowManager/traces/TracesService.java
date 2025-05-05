package com.gl.ceir.flowManager.traces;

import com.gl.ceir.flowManager.contstants.AlertIds;
import com.gl.ceir.flowManager.contstants.AlertMessagePlaceholders;

import java.util.Map;

public interface TracesService {

    void sendBlackListTrace(EdrDto edrDto);

    void sendGreyListTrace(EdrDto edrDto);
}
