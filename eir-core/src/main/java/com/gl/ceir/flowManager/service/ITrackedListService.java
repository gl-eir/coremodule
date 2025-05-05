package com.gl.ceir.flowManager.service;

import com.gl.ceir.flowManager.contstants.ApiStatusMessage;
import com.gl.ceir.flowManager.dto.*;
import com.gl.ceir.flowManager.exception.DataNotFoundException;

import javax.sound.midi.Track;
import java.util.List;
import java.util.Optional;

public interface ITrackedListService {

    int loadTrackedList();

    ApiStatusMessage removeFromList(TrackedListValue values);

    ApiStatusMessage addToList(TrackedListValue value);

    ListResponseData isPresent(VerificationRequest request) throws DataNotFoundException;

    List<TrackedListKey> get();

    Long getCount();
}
