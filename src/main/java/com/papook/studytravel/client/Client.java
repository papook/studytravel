package com.papook.studytravel.client;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class Client {
    public final String DISPATCHER_URI;

    public Client(final String dispatcherUri) {
        this.DISPATCHER_URI = dispatcherUri;
    }

    public String getUniversitiesCollectionUri;
    public String getStudyModulesCollectionUri;

    public Map<Long, String> universityLinksOnCurrentPage = new HashMap<>();
    public Map<Long, String> studyModuleLinksOnCurrentPage = new HashMap<>();

    public HttpClient client;
    public HttpRequest request;
    public HttpResponse<String> response;

}
