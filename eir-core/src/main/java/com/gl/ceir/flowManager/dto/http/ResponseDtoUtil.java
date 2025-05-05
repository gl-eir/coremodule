package com.gl.ceir.flowManager.dto.http;


public class ResponseDtoUtil {

    public static final String success = "Success";

    public static final String failure = "Failure";

    public static ResponseDto getSuccessResponseWithData(Object result, String successMessage) {
        return ResponseDto.builder()
                .errorInfo(ErrorInfo.builder().errorCode(0).errorLevel(ErrorEnum.OK).errorMessage(success).build())
                .status(successMessage)
                .result(result)
                .build();
    }

    public static ResponseDto getSuccessResponseWithData(Object result) {
        return getSuccessResponseWithData(result, success);
    }

    public static ResponseDto getFailureResponseWithData(Object result) {
        return ResponseDto.builder()
                .errorInfo(ErrorInfo.builder().errorCode(3).errorLevel(ErrorEnum.ERROR).errorMessage(failure).build())
                .status(failure)
                .result(result)
                .build();
    }

    public static ResponseDto getSuccessResponseDto(String successMessage) {
        return ResponseDto.builder()
                .errorInfo(ErrorInfo.builder().errorCode(0).errorLevel(ErrorEnum.OK).errorMessage(successMessage).build())
                .status(success)
                .build();
    }

    public static ResponseDto getErrorResponseDto(String errorMessage) {
        return ResponseDto.builder()
                .errorInfo(ErrorInfo.builder().errorCode(3).errorLevel(ErrorEnum.ERROR).errorMessage(errorMessage).build())
                .status(failure)
                .build();
    }

}
