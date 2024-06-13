package com.papook.studytravel.server.errors;

/**
 * Exception thrown when an already linked module is attempted to be linked to a
 * different university.
 * 
 * @author papook
 */
public class ModuleTakenException extends RuntimeException {
    private static final long serialVersionUID = 1L;
}