package com.telus.credit.exceptions;

import java.time.format.DateTimeParseException;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.telus.credit.common.ErrorCode;
import com.telus.credit.model.BaseResponse;

@ControllerAdvice
public class ExceptionHandlers {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlers.class);

    @ExceptionHandler(value = DataAccessException.class)
    public ResponseEntity<BaseResponse> handleDataAccessException(DataAccessException de) {
        LOGGER.error("{}: Data Access Exception. Message:{} . {} ", ExceptionConstants.STACKDRIVER_METRIC,de.getMessage(),ExceptionHelper.getStackTrace(de));
        ResponseEntity<BaseResponse> errorResponse = ExceptionHelper.createErrorResponse(new CreditException(HttpStatus.INTERNAL_SERVER_ERROR,
                ExceptionConstants.ERR_CODE_8000, ExceptionConstants.ERR_CODE_8000_MSG, de.getMessage()));
        return errorResponse;
    }

    @ExceptionHandler(value = CreditException.class)
    public ResponseEntity<BaseResponse> handleCreditException(CreditException ce) {
    	int exceptionHttpStatusVal = (ce.getHttpStatus()!=null)?ce.getHttpStatus().value():0;
    	if(	HttpStatus.BAD_REQUEST.value() !=exceptionHttpStatusVal && HttpStatus.NOT_FOUND.value() !=exceptionHttpStatusVal) {
    		LOGGER.error("{}: CreditException: {} . {}", ExceptionConstants.STACKDRIVER_METRIC,ce.toString(), ExceptionHelper.getStackTrace(ce));
    	}else {
    		LOGGER.warn("{} CreditException: {} . {}", ExceptionConstants.STACKDRIVER_WARNING_METRIC,ce.toString(), ExceptionHelper.getStackTrace(ce));
    		
    	}
        ResponseEntity<BaseResponse> errorResponse = ExceptionHelper.createErrorResponse(ce);
        return errorResponse;
    }

    @ExceptionHandler(value = DateTimeParseException.class)
    public ResponseEntity<BaseResponse> handleDateTimeParseException(DateTimeParseException de) {
        LOGGER.warn("DateTimeParse exception. {}",  ExceptionHelper.getStackTrace(de));
        return ExceptionHelper.createErrorResponse(new CreditException(HttpStatus.BAD_REQUEST,
                ErrorCode.C_1119.code(), ErrorCode.C_1119.getMessage(), de.getMessage()));
    }

    @ExceptionHandler(value = NumberFormatException.class)
    public ResponseEntity<BaseResponse> handleNumberFormatException(NumberFormatException ne) {
        LOGGER.warn("{} NumberFormatException exception. {}", ExceptionConstants.STACKDRIVER_METRIC,  ExceptionHelper.getStackTrace(ne));
        return ExceptionHelper.createErrorResponse(new CreditException(HttpStatus.BAD_REQUEST,
                ExceptionConstants.ERR_CODE_1000, ExceptionConstants.ERR_CODE_1000_MSG, "Invalid number " + ne.getMessage().replaceFirst("For", "for")));
    }
    
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<BaseResponse> handleConstraintViolationException(ConstraintViolationException cve) {
    	LOGGER.warn("{} , ConstraintViolationException. ",  ExceptionConstants.STACKDRIVER_WARNING_METRIC);
    	String code[] = StringUtils.split(cve.getMessage(), ":");
    	if(ObjectUtils.isNotEmpty(code) && code.length > 1 && ObjectUtils.isNotEmpty(ErrorCode.from(StringUtils.trim(code[1])))) {
    		return ExceptionHelper.createErrorResponse(new CreditException(HttpStatus.BAD_REQUEST,
    				StringUtils.trim(code[1]), ErrorCode.from(StringUtils.trim(code[1])).getMessage(),code[0]));
    	}
    	return ExceptionHelper.createErrorResponse(new CreditException(HttpStatus.BAD_REQUEST,
    			ExceptionConstants.ERR_CODE_1000, ExceptionConstants.ERR_CODE_1000_MSG, cve.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<BaseResponse> handleCreditException(Exception exception) {
        LOGGER.error("{}: Exception. {}", ExceptionConstants.STACKDRIVER_METRIC, ExceptionHelper.getStackTrace(exception));
        ResponseEntity<BaseResponse> errorResponse = ExceptionHelper.createErrorResponse(exception);
        return errorResponse;
    }
}
