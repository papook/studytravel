package com.papook.studytravel.server;

/**
 * The ServerConfiguration class represents
 * the configuration settings for the server.
 */
public class ServerConfiguration {

    /**
     * The port number on which the server is running.
     */
    public static final Integer PORT = 8080;

    /**
     * The host name of the server.
     */
    public static final String HOST = "localhost";

    /**
     * The base URI of the server
     * constructed from the host and port.
     */
    public static final String BASE_URI = "http://" + HOST + ":" + PORT;

    /**
     * The base path for university-related endpoints.
     */
    public static final String UNIVERSITY_ENDPOINT = "/universities";

    /**
     * The base path for module-related endpoints.
     */
    public static final String MODULE_ENDPOINT = "/modules";

}
