package com.papook.studytravel.client;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j2;

@Log4j2
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

    public void getDispatcher() {
        request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(DISPATCHER_URI))
                .GET()
                .build();

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.error("Error sending request to dispatcher. Make sure the server is running.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }
    }

    
}
