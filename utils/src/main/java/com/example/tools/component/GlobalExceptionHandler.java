package com.example.tools.component;

import com.example.tools.entity.CustomException;
import com.example.tools.entity.ResponseData;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
//import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理Validated校验异常
     * <p>
     * 注: 常见的ConstraintViolationException异常， 也属于ValidationException异常
     *
     * @param e 捕获到的异常
     * @return 返回给前端的data
     */
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
    public ResponseData handleParameterVerificationException(Exception e) {
        log.error(ExceptionUtils.getStackTrace(e));
        String msg = null;
        /// BindException
        if (e instanceof BindException) {
            // getFieldError获取的是第一个不合法的参数(P.S.如果有多个参数不合法的话)
            FieldError fieldError = ((BindException) e).getFieldError();
            if (fieldError != null) {
                msg = fieldError.getDefaultMessage();
            }
            /// MethodArgumentNotValidException
        } else if (e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            // getFieldError获取的是第一个不合法的参数(P.S.如果有多个参数不合法的话)
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                msg = fieldError.getDefaultMessage();
            }
            /// ValidationException 的子类异常ConstraintViolationException
        } else if (e instanceof ConstraintViolationException || e instanceof ConstraintDeclarationException) {
            /*
             * ConstraintViolationException的e.getMessage()形如
             *     {方法名}.{参数名}: {message}
             *  这里只需要取后面的message即可
             */
            msg = e.getMessage();
            if (msg != null) {
                int lastIndex = msg.lastIndexOf(':');
                if (lastIndex >= 0) {
                    msg = msg.substring(lastIndex + 1).trim();
                }
            }
            /// ValidationException 的其它子类异常
        } else {
            msg = "处理参数时异常";
        }

        ResponseData responseData = new ResponseData();
        responseData.setCode(400);
        responseData.setMessage(msg);
        responseData.setException(ExceptionUtils.getStackTrace(e));
        return responseData;
    }

    //    @ResponseStatus(code = HttpStatus.FORBIDDEN)
//    @ExceptionHandler(value = {AccessDeniedException.class})
//    public ResponseData handleAccessDeniedException(Exception e) {
//        ResponseData responseData = new ResponseData();
//        responseData.forbidden();
//        return responseData;
//    }
    @ExceptionHandler(value = CustomException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseData handleCustomException(Exception ex) {
        log.error(ExceptionUtils.getStackTrace(ex));
        ResponseData responseData = new ResponseData();
        responseData.setCode(((CustomException) ex).getCode());
        responseData.setMessage(ex.getMessage());
        responseData.setException(ExceptionUtils.getStackTrace(ex));
        return responseData;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseData handleException(Exception ex) {
        log.error(ExceptionUtils.getStackTrace(ex));
        ResponseData responseData = new ResponseData();
        if (ex instanceof DataIntegrityViolationException) { // 数据库操作异常
            if (ex.toString().contains("a foreign key constraint fails")) { //外键关联问题，具体前端可以根据发送的请求判断
                responseData.setCode(5001);
                responseData.setMessage("a foreign key constraint fails");
            } else {
                responseData.internalError();
            }
        } else {
            responseData.internalError();
        }
        responseData.setException(ExceptionUtils.getStackTrace(ex));
        return responseData;
    }
}
