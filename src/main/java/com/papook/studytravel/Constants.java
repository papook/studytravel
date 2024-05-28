package com.papook.studytravel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class Constants {
    @Autowired
    public static Environment env;

    public static final String PORT = env.getProperty("server.port");
    public static final String API_BASE = "/api";
    public static final String BASE_URI = "localhost:" + PORT + API_BASE;

    public static final String UNIVERSITY_BASE = "/universities";
}
