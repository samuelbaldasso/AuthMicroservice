package com.sbaldasso.mybank.auth.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserNotFoundException extends RuntimeException {
    private String message;
}
