package com.papook.studytravel.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SuppressWarnings("unused")
public class Client {
    public final String DISPATCHER_URI;

    public Client(final String dispatcherUri) {
        this.DISPATCHER_URI = dispatcherUri;
        client = HttpClient.newHttpClient();
    }

    public String getUniversitiesCollectionUri;
    public String getStudyModulesCollectionUri;
    public String postCreateUniversityUri;
    public String postCreateStudyModuleUri;

    public Map<Long, String> universityLinksOnCurrentPage = new HashMap<>();
    public Map<Long, String> studyModuleLinksOnCurrentPage = new HashMap<>();

    public HttpClient client;
    public HttpRequest request;
    public HttpResponse<String> response;

    /**
     * Setup the client by fetching the links from the dispatcher.
     * This method calls getDispatcher() and fetchLinksFromDispatcher().
     * 
     * @see #getDispatcher
     * @see #fetchLinksFromDispatcher
     */
    public void setup() {
        getDispatcher();
        fetchLinksFromDispatcher();
    }

    public void getDispatcher() {
        request = HttpRequest.newBuilder()
                .uri(URI.create(DISPATCHER_URI))
                .GET()
                .build();

        log.info("Sending request to dispatcher at " + DISPATCHER_URI);
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
        } catch (IOException e) {
            log.error("Error sending request to dispatcher. Make sure the server is running.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }
    }

    public void fetchLinksFromDispatcher() {
        log.info("Fetching links from dispatcher response headers.");

        log.info("Fetching getUniversitiesCollection.");
        getUniversitiesCollectionUri = getLinkFromResponseHeaders("getUniversitiesCollection");

        log.info("Fetching getStudyModulesCollection.");
        getStudyModulesCollectionUri = getLinkFromResponseHeaders("getStudyModulesCollection");

        log.info("Fetching postCreateUniversity.");
        postCreateUniversityUri = getLinkFromResponseHeaders("postCreateUniversity");

        log.info("Fetching postCreateStudyModule.");
        postCreateStudyModuleUri = getLinkFromResponseHeaders("postCreateStudyModule");

        log.info("Links fetched.");
    }

    public HttpResponse<String> getUniversitiesCollection() {
        request = HttpRequest.newBuilder()
                .uri(URI.create(getUniversitiesCollectionUri))
                .GET()
                .build();

        log.info("[GET]: " + getUniversitiesCollectionUri);
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
        } catch (IOException e) {
            log.error("Error sending request to get universities collection.");
            return null;
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
            return null;
        }

        return response;
    }

    private String getLinkFromResponseHeaders(String rel) {

        List<String> linkHeaders = response.headers().allValues("Link");

        if (linkHeaders.size() == 0) {
            return null;
        }

        // Iterate through each "Link" header
        for (String linkHeader : linkHeaders) {
            String[] parts = linkHeader.split(";");
            if (parts.length <= 1) {
                continue;
            }

            // Extract the URL part and the rel part
            String urlPart = parts[0].trim();
            String relPart = parts[1].trim();

            // Check if the rel part matches the desired relation type
            if (relPart.equals("rel=\"" + rel + "\"")) {
                // Remove the angle brackets from the URL part and return it
                return urlPart.substring(1, urlPart.length() - 1);
            }

        }

        return null;
    }

    private static String replacePartInUriTemplate(String uriTemplate, String placeholder, Object replacement) {
        return uriTemplate.replace("{" + placeholder + "}", String.valueOf(replacement));
    }
}
