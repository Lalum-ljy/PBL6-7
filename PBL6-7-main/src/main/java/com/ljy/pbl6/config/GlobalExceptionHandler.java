package com.ljy.pbl6.config;

import com.ljy.pbl6.common.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<String>> handleException(Exception e) {
        logger.error("全局异常处理: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(500, "服务器内部错误: " + e.getMessage()));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Response<String>> handleSQLException(SQLException e) {
        logger.error("SQL异常: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(500, "数据库操作失败: " + e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error("参数异常: ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error(400, "参数错误: " + e.getMessage()));
    }
}