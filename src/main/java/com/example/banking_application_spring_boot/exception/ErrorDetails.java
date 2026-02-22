package com.example.banking_application_spring_boot.exception;

import java.time.LocalDateTime;

public record ErrorDetails(LocalDateTime timestamp, String message, String Details, String errorCode) {

}
