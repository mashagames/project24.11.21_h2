package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


public class WrongFormatException extends ResponseStatusException {
    public WrongFormatException() {
        super(HttpStatus.BAD_REQUEST, "Неверный формат имени пользователя");  // 400
    }
}
