package xyz.rkgn.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import xyz.rkgn.common.Result;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ExceptionHandlerExceptionResolver {
    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleException(DuplicateKeyException e) {
        e.printStackTrace();
        return Result.fail(e.getLocalizedMessage());
    }

    @ExceptionHandler(DeleteNotAllowedException.class)
    public Result handleDeleteNotAllowedException(DeleteNotAllowedException e) {
        e.printStackTrace();
        return Result.fail(e.getLocalizedMessage());
    }

    @ExceptionHandler(IllegalRequestParamException.class)
    public Result handleIllegalRequestParamException(IllegalRequestParamException e) {
        e.printStackTrace();
        return Result.fail(e.getLocalizedMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleJsonException(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body("JSON数据格式错误: " + ex.getMessage());
    }
}
