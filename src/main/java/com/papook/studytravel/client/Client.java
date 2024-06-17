package com.papook.studytravel.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.papook.studytravel.client.utils.LocalDateAdapter;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SuppressWarnings("unused") // TODO: Remove this suppression
public class Client {
    public final String DISPATCHER_URI;

    public Client(final String dispatcherUri) {
        this.DISPATCHER_URI = dispatcherUri;
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    public String getUniversitiesCollectionUri;
    public String getStudyModulesCollectionUri;
    public String postCreateUniversityUri;
    public String postCreateStudyModuleUri;

    public String getSelfUri;
    public String updateUri;
    public String deleteUri;

    public Map<Long, String> resourceLinksOnLastFetchedPage = new HashMap<>();

    public HttpClient client;
    public HttpRequest request;
    public HttpResponse<String> response;

    Gson gson;

    /**
     * Setup the client by fetching the links from the dispatcher.
     * This method calls getDispatcher() and fetchLinksFromDispatcher().
     * 
     * @see #getDispatcher
     * @see #fetchLinksFromDispatcher
     */
    public void setup() {
        log.info("Setting up client.");
        getDispatcher();
        fetchLinksFromDispatcher();
        log.info("Client setup complete.");
    }

    /**
     * Sends a GET request to the dispatcher.
     */
    public void getDispatcher() {
        request = HttpRequest.newBuilder()
                .uri(URI.create(DISPATCHER_URI))
                .GET()
                .build();

        log.info("Sending request to dispatcher at " + DISPATCHER_URI);
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
            resourceLinksOnLastFetchedPage = fetchLinksOnCurrentPage();
        } catch (IOException e) {
            log.error("Error sending request to dispatcher. Make sure the server is running.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }
    }

    /**
     * Fetches the links from the dispatcher response headers and stores them in the
     * respective fields.
     * 
     * @see #getUniversitiesCollectionUri
     * @see #getStudyModulesCollectionUri
     * @see #postCreateUniversityUri
     * @see #postCreateStudyModuleUri
     */
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

    /**
     * Sends a GET request to the universities collection and stores the links to
     * the individual universities in the universityLinksOnLastFetchedPage field.
     * 
     * @return The response from the server.
     * 
     * @see #resourceLinksOnLastFetchedPage
     */
    public void getUniversitiesCollection() {
        request = HttpRequest.newBuilder()
                .uri(URI.create(getUniversitiesCollectionUri))
                .GET()
                .build();

        log.info("[GET]: " + getUniversitiesCollectionUri);
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
            resourceLinksOnLastFetchedPage = fetchLinksOnCurrentPage();
        } catch (IOException e) {
            log.error("Error sending request to get universities collection.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }

    }

    /**
     * Sends a GET request to the study modules collection and stores the links to
     * the individual study modules in the studyModuleLinksOnLastFetchedPage field.
     * 
     * @return The response from the server.
     * 
     * @see #resourceLinksOnLastFetchedPage
     */
    public void getStudyModulesCollection() {
        request = HttpRequest.newBuilder()
                .uri(URI.create(getStudyModulesCollectionUri))
                .GET()
                .build();

        log.info("[GET]: " + getStudyModulesCollectionUri);
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
            resourceLinksOnLastFetchedPage = fetchLinksOnCurrentPage();
        } catch (IOException e) {
            log.error("Error sending request to get study modules collection.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }
    }

    /**
     * Sends a POST request to the create university endpoint.
     * 
     * @param body The body of the request. This should be a JSON string
     *             representing the university.
     * 
     * @return The response from the server.
     */
    public void createUniversity(String body) {
        request = HttpRequest.newBuilder()
                .uri(URI.create(postCreateUniversityUri))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(body))
                .build();

        log.info("[POST]: " + postCreateUniversityUri);
        try {
            response = client.send(request, BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
        } catch (IOException e) {
            log.error("Error sending request to create university.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }
    }

    /**
     * Sends a POST request to the create study module endpoint.
     * 
     * @param json The body of the request. This should be a JSON string
     *             representing the study module.
     * 
     * @return The response from the server.
     */
    public void createStudyModule(String json) {
        request = HttpRequest.newBuilder()
                .uri(URI.create(postCreateStudyModuleUri))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(json))
                .build();

        log.info("[POST]: " + postCreateStudyModuleUri);
        try {
            response = client.send(request, BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
        } catch (IOException e) {
            log.error("Error sending request to create study module.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }
    }

    /**
     * Sends a PUT request to the resource with the given ID. The URI is fetched
     * from the updateUri field. The request body is the given JSON string. The
     * response is checked for a 204 status code and the getSelfUri field is updated
     * if the PUT request was successful.
     * 
     * @param id   The ID of the resource to update.
     * @param json The JSON string representing the updated resource.
     * 
     * @see #updateUri
     * @see #deleteUri
     * @see #getSelfUri
     * 
     */
    public void updateResource(Long id, String json) {
        if (updateUri == null) {
            log.error("No update URI found.");
            return;
        }

        request = HttpRequest.newBuilder()
                .uri(URI.create(updateUri))
                .header("Content-Type", "application/json")
                .PUT(BodyPublishers.ofString(json))
                .build();

        log.info("[PUT]: " + updateUri);
        try {
            response = client.send(request, BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
            updateUri = null;
            deleteUri = null;

            log.info("Resource updated successfully.");

            if (response.statusCode() == 204) {
                getSelfUri = getLinkFromResponseHeaders("getSelf");
            }
        } catch (IOException e) {
            log.error("Error sending request to update resource.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }

    }

    /**
     * Sends a GET request to the created resource. The URI is fetched from the
     * Location header of the response from the create resource request. The
     * response is deserialized into a Map.
     * 
     * @return The received resource as a Map.
     * 
     * @see Map
     */
    public Map<String, String> getCreatedResource() {
        String location = response.headers().firstValue("Location").get();

        request = HttpRequest.newBuilder()
                .uri(URI.create(location))
                .GET()
                .build();

        log.info("[GET]: " + location);
        try {
            response = client.send(request, BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
            updateUri = getLinkFromResponseHeaders("putUpdate");
            deleteUri = getLinkFromResponseHeaders("delete");
        } catch (IOException e) {
            log.error("Error getting the created resource.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }

        Map<String, String> deserializedResource = gson.fromJson(response.body(),
                new TypeToken<Map<String, String>>() {
                }.getType());
        return deserializedResource;
    }

    /**
     * Sends a GET request to the resource with the given ID. The URI is fetched
     * from the resourceLinksOnLastFetchedPage field. The response is deserialized
     * into a Map.
     * 
     * @param id The ID of the resource to fetch. It must be present in the
     *           {@link #resourceLinksOnLastFetchedPage} field.
     * 
     * @return The received resource as a Map or null if the ID is not present in
     *         the {@link #resourceLinksOnLastFetchedPage} field.
     */
    public Map<String, String> getOneResource(Long id) {
        if (!resourceLinksOnLastFetchedPage.containsKey(id)) {
            log.error("The ID " + id + " is not present in the resourceLinksOnLastFetchedPage.");
            return null;
        }

        String uri = resourceLinksOnLastFetchedPage.get(id);

        request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        log.info("[GET]: " + uri);
        try {
            response = client.send(request, BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
            updateUri = getLinkFromResponseHeaders("putUpdate");
            deleteUri = getLinkFromResponseHeaders("delete");
        } catch (IOException e) {
            log.error("Error getting the resource.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }

        Map<String, String> deserializedResource = gson.fromJson(response.body(),
                new TypeToken<Map<String, String>>() {
                }.getType());

        return deserializedResource;
    }

    /**
     * Processes the response body and creates a map of IDs and links to the
     * respective resource.
     * 
     * @return A map of IDs and links to the respective resource.
     *
     * @see #resourceLinksOnLastFetchedPage
     */
    private Map<Long, String> fetchLinksOnCurrentPage() {
        Map<Long, String> result = new HashMap<>();

        List<Map<String, String>> objectList = gson.fromJson(
                response.body(),
                new TypeToken<List<Map<String, String>>>() {
                }.getType());

        for (Map<String, String> object : objectList) {
            Long id = Long.valueOf(object.get("id"));
            String self = object.get("self");

            result.put(id, self);
        }

        return result;
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
