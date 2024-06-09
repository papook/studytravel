package com.papook.studytravel.server.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents an error message that can be sent to the client
 * in case of an exception. The message is human-readable and
 * provides a message and a status code.
 * 
 * @author papook
 */
@Data
@AllArgsConstructor
public class ErrorMessage {
    private int status;
    private String message;
}