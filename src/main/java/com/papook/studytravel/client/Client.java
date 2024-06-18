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

import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.papook.studytravel.client.utils.LocalDateAdapter;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Client {
    public final String DISPATCHER_URI;

    public Client(final String dispatcherUri) {
        this.DISPATCHER_URI = dispatcherUri;
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    private String getUniversitiesCollectionUri;
    private String getStudyModulesCollectionUri;
    private String postCreateUniversityUri;
    private String postCreateStudyModuleUri;
    private String deleteAllUniversitiesUri;
    private String deleteAllStudyModulesUri;

    private String getSelfUri;
    private String updateUri;
    private String deleteUri;

    public Map<Long, String> resourceLinksOnLastFetchedPage = new HashMap<>();

    private HttpClient client;
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

        log.info("Fetching deleteAllUniversities.");
        deleteAllUniversitiesUri = getLinkFromResponseHeaders("deleteAllUniversities");

        log.info("Fetching deleteAllStudyModules.");
        deleteAllStudyModulesUri = getLinkFromResponseHeaders("deleteAllStudyModules");

        log.info("Links fetched.");
    }

    /**
     * Sends a GET request to the universities collection without any filtering or
     * sorting.
     */
    public void getAllUniversities() {
        newGetUniversitiesRequest()
                .send();
    }

    /**
     * Sends a GET request to the study modules collection without any filtering
     * sorting.
     */
    public void getAllStudyModules() {
        newGetStudyModulesRequest()
                .allModules()
                .send();
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
    public void updateResource(String json) {
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
     * Sends a GET request to the resource with the given ID. The URI is fetched
     * from the getSelfUri field. The response is deserialized into a Map.
     * 
     * @return The received resource as a Map.
     * 
     * @see #getSelfUri
     */
    public Map<String, String> getUpdatedResource() {
        request = HttpRequest.newBuilder()
                .uri(URI.create(getSelfUri))
                .GET()
                .build();

        log.info("[GET]: " + getSelfUri);
        try {
            response = client.send(request, BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
            updateUri = getLinkFromResponseHeaders("putUpdate");
            deleteUri = getLinkFromResponseHeaders("delete");
        } catch (IOException e) {
            log.error("Error getting the updated resource.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }

        Map<String, String> deserializedResource = gson.fromJson(response.body(),
                new TypeToken<Map<String, String>>() {
                }.getType());
        return deserializedResource;
    }

    /**
     * Sends a DELETE request to the resource with the given ID. The URI is fetched
     * from the deleteUri field.
     * 
     * @param id The ID of the resource to delete.
     * 
     * @see #deleteUri
     */
    public void deleteResource() {
        if (deleteUri == null) {
            log.error("No delete URI found.");
            return;
        }

        request = HttpRequest.newBuilder()
                .uri(URI.create(deleteUri))
                .DELETE()
                .build();

        log.info("[DELETE]: " + deleteUri);
        try {
            response = client.send(request, BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
            deleteUri = null;
            updateUri = null;
        } catch (IOException e) {
            log.error("Error sending request to delete resource.");
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
     * Sends a PUT request to link the module with the given ID to the current
     * university. The URI is fetched from the putLinkModule field in the response
     * of the GET request to the universities collection.
     * 
     * @param moduleId The ID of the module to link to the current university.
     */
    public void linkModuleToUniversity(Long moduleId) {
        String linkModuleUri = getLinkFromResponseHeaders("putLinkModule");
        linkModuleUri = replacePartInUriTemplate(linkModuleUri, "moduleId", moduleId);

        request = HttpRequest.newBuilder()
                .uri(URI.create(linkModuleUri))
                .PUT(BodyPublishers.noBody())
                .build();

        log.info("[PUT]: " + linkModuleUri);
        try {
            response = client.send(request, BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
        } catch (IOException e) {
            log.error("Error linking module to university.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }
    }

    /**
     * Sends a DELETE request to unlink the module with the given ID from the
     * current university. The URI is fetched from the delUnlinkModule field in the
     * response of the GET request to the universities collection.
     * 
     * @param moduleId The ID of the module to unlink from the current university.
     */
    public void unlinkModuleFromUniversity(Long moduleId) {
        String unlinkModuleUri = getLinkFromResponseHeaders("delUnlinkModule");
        unlinkModuleUri = replacePartInUriTemplate(unlinkModuleUri, "moduleId", moduleId);

        request = HttpRequest.newBuilder()
                .uri(URI.create(unlinkModuleUri))
                .DELETE()
                .build();

        log.info("[DELETE]: " + unlinkModuleUri);
        try {
            response = client.send(request, BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
        } catch (IOException e) {
            log.error("Error unlinking module from university.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }
    }

    /**
     * Sends a GET request to get the modules of the current university without any
     * filtering or sorting.
     */
    public void getAllModulesOfUniversity() {
        newGetStudyModulesRequest()
                .ofCurrentUniversity()
                .send();
    }

    /**
     * Sends a GET request to get a module that belongs to the current university.
     * The URI is fetched from the getModuleOfUniversity field in the response of
     * the GET request to the universities collection.
     * 
     * @return A map of module IDs and links to the respective module.
     */
    public Map<String, String> getModuleOfUniversity(Long id) {
        String getModuleOfUniversityUri = getLinkFromResponseHeaders("getModuleOfUniversity");
        getModuleOfUniversityUri = replacePartInUriTemplate(getModuleOfUniversityUri, "moduleId", id);

        request = HttpRequest.newBuilder()
                .uri(URI.create(getModuleOfUniversityUri))
                .GET()
                .build();

        log.info("[GET]: " + getModuleOfUniversityUri);
        try {
            response = client.send(request, BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
        } catch (IOException e) {
            log.error("Error getting module of university.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }

        Map<String, String> deserializedResource = gson.fromJson(response.body(),
                new TypeToken<Map<String, String>>() {
                }.getType());

        return deserializedResource;
    }

    /**
     * Sends a DELETE request to delete all universities.
     */
    public void deleteAllUniversities() {
        request = HttpRequest.newBuilder()
                .uri(URI.create(deleteAllUniversitiesUri))
                .DELETE()
                .build();

        log.info("[DELETE]: " + deleteAllUniversitiesUri);
        try {
            response = client.send(request, BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
        } catch (IOException e) {
            log.error("Error sending request to delete all universities.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }
    }

    /**
     * Sends a DELETE request to delete all study modules.
     */
    public void deleteAllStudyModules() {
        request = HttpRequest.newBuilder()
                .uri(URI.create(deleteAllStudyModulesUri))
                .DELETE()
                .build();

        log.info("[DELETE]: " + deleteAllStudyModulesUri);
        try {
            response = client.send(request, BodyHandlers.ofString());
            log.info("Code: " + response.statusCode());
        } catch (IOException e) {
            log.error("Error sending request to delete all study modules.");
        } catch (InterruptedException e) {
            log.error("The request was interrupted.");
        }
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

    /**
     * A class to create a GET request to the universities collection with custom
     * filters and sorting.
     * 
     * @see #newGetUniversitiesRequest
     */
    public class GetUniversitiesRequest {
        private String uri = getUniversitiesCollectionUri;
        private String filterByName;
        private String filterByCountry;
        private String sort;

        private GetUniversitiesRequest() {
            this.uri = getUniversitiesCollectionUri;
        }

        public GetUniversitiesRequest filterByName(String name) {
            this.filterByName = name;
            return this;
        }

        public GetUniversitiesRequest dontFilterByName() {
            this.filterByName = null;
            return this;
        }

        public GetUniversitiesRequest filterByCountry(String country) {
            this.filterByCountry = country;
            return this;
        }

        public GetUniversitiesRequest dontFilterByCountry() {
            this.filterByCountry = null;
            return this;
        }

        public GetUniversitiesRequest sort(String sortBy, String order) {
            this.sort = sortBy + "_" + order;
            return this;
        }

        public GetUniversitiesRequest dontSort() {
            this.sort = null;
            return this;
        }

        public void send() {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uri);
            if (filterByName != null) {
                uriBuilder.queryParam("name", filterByName);
            }

            if (filterByCountry != null) {
                uriBuilder.queryParam("country", filterByCountry);
            }

            if (sort != null) {
                uriBuilder.queryParam("sort", sort);
            }

            URI requestUri = uriBuilder.build().toUri();

            request = HttpRequest.newBuilder()
                    .uri(requestUri)
                    .GET()
                    .build();

            log.info("[GET]: " + requestUri);
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
    }

    /**
     * A class to create a GET request to the study modules collection with custom
     * filters and sorting.
     * 
     * @see #newGetStudyModulesRequest
     */
    public class GetStudyModulesRequest {
        private String uri = getStudyModulesCollectionUri;
        private String filterByName;
        private String filterBySemester;
        private String sort;

        private GetStudyModulesRequest() {
            this.uri = getStudyModulesCollectionUri;
        }

        /**
         * Fetch the study modules of the current university.
         */
        public GetStudyModulesRequest ofCurrentUniversity() {
            Map<String, String> currentUniversity = gson.fromJson(response.body(),
                    new TypeToken<Map<String, String>>() {
                    }.getType());

            String modulesUri = currentUniversity.get("modules");
            uri = modulesUri;
            return this;
        }

        /**
         * Fetch all study modules.
         */
        public GetStudyModulesRequest allModules() {
            uri = getStudyModulesCollectionUri;
            return this;
        }

        public GetStudyModulesRequest filterByName(String name) {
            this.filterByName = name;
            return this;
        }

        public GetStudyModulesRequest dontFilterByName() {
            this.filterByName = null;
            return this;
        }

        public GetStudyModulesRequest filterBySemester(String semester) {
            this.filterBySemester = semester;
            return this;
        }

        public GetStudyModulesRequest dontFilterBySemester() {
            this.filterBySemester = null;
            return this;
        }

        public GetStudyModulesRequest sort(String sortBy, String order) {
            this.sort = sortBy + "_" + order;
            return this;
        }

        public GetStudyModulesRequest dontSort() {
            this.sort = null;
            return this;
        }

        public void send() {
            if (uri == null) {
                log.error("Cannot get study modules collection.");
                return;
            }

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uri);
            if (filterByName != null) {
                uriBuilder.queryParam("name", filterByName);
            }

            if (filterBySemester != null) {
                uriBuilder.queryParam("semester", filterBySemester);
            }

            if (sort != null) {
                uriBuilder.queryParam("sort", sort);
            }

            URI requestUri = uriBuilder.build().toUri();

            request = HttpRequest.newBuilder()
                    .uri(requestUri)
                    .GET()
                    .build();

            log.info("[GET]: " + requestUri);
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
    }

    /**
     * Creates a new GetUniversitiesRequest object which can be used to send a GET
     * request to the universities collection with custom filters and sorting.
     * 
     * @return A new GetUniversitiesRequest object.
     * 
     * @see GetUniversitiesRequest
     * 
     */
    public GetUniversitiesRequest newGetUniversitiesRequest() {
        return new GetUniversitiesRequest();
    }

    /**
     * Creates a new GetStudyModulesRequest object which can be used to send a GET
     * request to the study modules collection with custom filters and sorting.
     * 
     * @return A new GetStudyModulesRequest object.
     * 
     * @see GetStudyModulesRequest
     */
    public GetStudyModulesRequest newGetStudyModulesRequest() {
        return new GetStudyModulesRequest();
    }

}
