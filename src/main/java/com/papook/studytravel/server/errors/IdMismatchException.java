package com.papook.studytravel.server.errors;

/**
 * Exception thrown when the ID in the request path does not match the ID in the
 * request body during a PUT request.
 * 
 * @author papook
 */
public class IdMismatchException extends RuntimeException {
    private static final long serialVersionUID = 1L;
}
