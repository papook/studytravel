package com.papook.studytravel.server.errors;

/**
 * Exception thrown when a module of a university is attempted to be accessed
 * without belonging to that university.
 * 
 * @author papook
 */
public class ModuleNotLinkedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
}
