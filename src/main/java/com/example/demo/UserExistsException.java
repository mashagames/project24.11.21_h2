package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserExistsException extends ResponseStatusException {
     public UserExistsException() {
         super(HttpStatus.CONFLICT, "Пользователь уже существует"); // 409
     }
}
