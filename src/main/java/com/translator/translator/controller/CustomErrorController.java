package com.translator.translator.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<String> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            return switch (statusCode) {
                case 404 -> ResponseEntity.status(HttpStatus.FOUND)  // ✅ Redirect Status (302)
                         .header("Location", "/404.html")
                         .build();

                case 400 -> ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Custom 400: 400 Bad Request");
                case 500 -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Custom 500: Internal Server Error (custom SKLS) you have been scammed");
                default -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Something went wrong!");
            };
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected error occurred");
    }
}
