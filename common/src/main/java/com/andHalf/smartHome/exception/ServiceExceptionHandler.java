package com.andHalf.smartHome.exception;

import com.andHalf.smartHome.constants.ErrorEnum;
import com.andHalf.smartHome.dtos.ErrorInfo;
import com.andHalf.smartHome.dtos.ResponseDto;
import com.andHalf.smartHome.utils.ResponseDtoUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDto> handleUserNotFoundException(
            RuntimeException ex, WebRequest request) {
        ResponseDto responseDto = ResponseDtoUtil.getErrorResponseDto(ErrorInfo.builder().
                errorCode(HttpStatus.NOT_FOUND.value())
                .errorLevel(ErrorEnum.ERROR)
                .errorMessage(ex.getMessage()).build());
        return new ResponseEntity<>(responseDto, getHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDto> handleAccessDeniedException(
            RuntimeException ex, WebRequest request) {
        ResponseDto responseDto = ResponseDtoUtil.getErrorResponseDto(ErrorInfo.builder().
                errorCode(HttpStatus.FORBIDDEN.value())
                .errorLevel(ErrorEnum.ERROR)
                .errorMessage(ex.getMessage()).build());
        return new ResponseEntity<>(responseDto, getHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServiceParameterException.class)
    public ResponseEntity<ResponseDto> handleServiceParameterException(
            RuntimeException ex, WebRequest request) {
        ResponseDto responseDto = ResponseDtoUtil.getErrorResponseDto(ErrorInfo.builder().
                errorCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .errorLevel(ErrorEnum.ERROR)
                .errorMessage(ex.getMessage()).build());
        return new ResponseEntity<>(responseDto, getHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ResponseDto> handleInvalidRequestException(
            RuntimeException ex, WebRequest request) {
        ResponseDto responseDto = ResponseDtoUtil.getErrorResponseDto(ErrorInfo.builder().
                errorCode(HttpStatus.BAD_REQUEST.value())
                .errorLevel(ErrorEnum.ERROR)
                .errorMessage(ex.getMessage()).build());
        return new ResponseEntity<>(responseDto, getHeaders(), HttpStatus.NOT_FOUND);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
