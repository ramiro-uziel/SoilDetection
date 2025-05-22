package com.springboot.SoilDetection.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Map<String, Object> errorDetails = new HashMap<>();

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            errorDetails.put("status", statusCode);
            errorDetails.put("error", HttpStatus.valueOf(statusCode).getReasonPhrase());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorDetails.put("message", "The requested resource was not found");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorDetails.put("message", "An internal server error occurred");
            } else {
                errorDetails.put("message", "An unexpected error occurred");
            }
        } else {
            errorDetails.put("status", 500);
            errorDetails.put("error", "Internal Server Error");
            errorDetails.put("message", "An unexpected error occurred");
        }

        return new ResponseEntity<>(errorDetails, HttpStatus.valueOf((Integer) errorDetails.get("status")));
    }
}
