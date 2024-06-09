package com.papook.studytravel.server.errors;

import jakarta.persistence.Entity;
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
@Entity
public class ErrorMessage {
    private String message;
    private int status;
}