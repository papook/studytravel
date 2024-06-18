package com.papook.studytravel.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.web.util.UriComponentsBuilder;

@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientTests {

    Client client;
    UriComponentsBuilder uriBuilder;

    private String createUniversityRel = "postCreateUniversity";
    private String createStudyModuleRel = "postCreateStudyModule";
    private String getUniversitiesRel = "getUniversitiesCollection";
    private String getStudyModulesRel = "getStudyModulesCollection";

    @BeforeAll
    public void setup() {
        String dispatcherUri = "http://localhost:8080";

        uriBuilder = UriComponentsBuilder.fromHttpUrl(dispatcherUri);
        client = new Client(dispatcherUri);
        client.setup();

    }

    @BeforeEach
    public void goToDispatcherState() {
        client.getDispatcher();
    }

    @AfterEach
    public void tearDown() {
        client.deleteAllUniversities();
        client.deleteAllStudyModules();
    }

    @Test
    @Order(1)
    public void testGetDispatcher() {
        URI expectedUri = uriBuilder.build().toUri();
        client.getDispatcher();
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        // Check if the dispatcher was retrieved successfully
        assertThat(client.response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(2)
    public void testCreateUniversity() {
        client.getDispatcher();
        String json = generateSampleUniversityJsonWithoutId(1);

        URI expectedUri = URI.create(getLinkFromResponseHeaders(createUniversityRel));
        client.createUniversity(json);
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        // Check if the university was created successfully
        assertThat(client.response.statusCode()).isEqualTo(201);
    }

    @Test
    @Order(3)
    public void testGetCreatedUniversity() {
        String json = generateSampleUniversityJsonWithoutId(1);
        client.createUniversity(json);

        // Check if the university was created successfully
        assertThat(client.response.statusCode()).isEqualTo(201);

        URI expectedUri = URI.create(client.response.headers().firstValue("Location").get());
        client.getCreatedResource();
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        // Check if the university was retrieved successfully
        assertThat(client.response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(4)
    public void testUpdateUniversity() {
        String json = generateSampleUniversityJsonWithoutId(1);
        client.createUniversity(json);
        client.getCreatedResource();

        URI expectedUri = URI.create(getLinkFromResponseHeaders("putUpdate"));
        client.updateResource(generateSampleUniversityJsonWithId(1));
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        // Check if the university was updated successfully
        assertThat(client.response.statusCode()).isEqualTo(204);
    }

    @Test
    @Order(5)
    public void testDeleteUniversity() {
        String json = generateSampleUniversityJsonWithoutId(1);
        client.createUniversity(json);

        // Check if the university was created successfully
        assertThat(client.response.statusCode()).isEqualTo(201);

        client.getCreatedResource();

        // Check if the university was retrieved successfully
        assertThat(client.response.statusCode()).isEqualTo(200);

        URI expectedUri = URI.create(getLinkFromResponseHeaders("delete"));
        client.deleteResource();
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        // Check if the university was deleted successfully
        assertThat(client.response.statusCode()).isEqualTo(204);
    }

    @Test
    @Order(6)
    public void testCreateStudyModule() {
        String json = generateSampleStudyModuleJsonWithoutId(1);
        URI expectedUri = URI.create(getLinkFromResponseHeaders(createStudyModuleRel));
        client.createStudyModule(json);
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        // Check if the study module was created successfully
        assertThat(client.response.statusCode()).isEqualTo(201);
    }

    @Test
    @Order(7)
    public void testGetCreatedStudyModule() {
        String json = generateSampleStudyModuleJsonWithoutId(1);
        client.createStudyModule(json);

        URI expectedUri = URI.create(client.response.headers().firstValue("Location").get());
        client.getCreatedResource();
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        // Check if the study module was retrieved successfully
        assertThat(client.response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(8)
    public void testUpdateStudyModule() {
        String json = generateSampleStudyModuleJsonWithoutId(1);
        client.createStudyModule(json);
        client.getCreatedResource();

        URI expectedUri = URI.create(getLinkFromResponseHeaders("putUpdate"));
        client.updateResource(generateSampleStudyModuleJsonWithId(1));
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        // Check if the study module was updated successfully
        assertThat(client.response.statusCode()).isEqualTo(204);
    }

    @Test
    @Order(9)
    public void testDeleteStudyModule() {
        String json = generateSampleStudyModuleJsonWithoutId(1);
        client.createStudyModule(json);
        client.getCreatedResource();

        URI expectedUri = URI.create(getLinkFromResponseHeaders("delete"));
        client.deleteResource();
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        // Check if the study module was deleted successfully
        assertThat(client.response.statusCode()).isEqualTo(204);
    }

    @Test
    @Order(10)
    public void testLinkModuleToUniversity() {
        int number = 1;
        String studyModuleJson = generateSampleStudyModuleJsonWithoutId(number);
        String universityJson = generateSampleUniversityJsonWithoutId(number);
        client.createStudyModule(studyModuleJson);
        client.createUniversity(universityJson);
        client.getCreatedResource();

        String uri = replacePartInUriTemplate(getLinkFromResponseHeaders("putLinkModule"), "moduleId", number);
        URI expectedUri = URI.create(uri);
        client.linkModuleToUniversity(1L);
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        assertThat(client.response.statusCode()).isEqualTo(204);
    }

    @Test
    @Order(11)
    public void testUnlinkModuleFromUniversity() {
        String studyModuleJson = generateSampleStudyModuleJsonWithoutId(1);
        String universityJson = generateSampleUniversityJsonWithoutId(1);
        client.createStudyModule(studyModuleJson);
        client.createUniversity(universityJson);
        client.getCreatedResource();
        client.linkModuleToUniversity(1L);
        client.getAllUniversities();
        client.getOneResource(1L);

        String uri = replacePartInUriTemplate(getLinkFromResponseHeaders("delUnlinkModule"), "moduleId", 1);
        URI expectedUri = URI.create(uri);
        client.unlinkModuleFromUniversity(1L);
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        // Check if the study module was unlinked from the university successfully
        assertThat(client.response.statusCode()).isEqualTo(204);
    }

    @Test
    @Order(12)
    public void testGetUniversitiesCollection() {
        URI expectedUri = URI.create(getLinkFromResponseHeaders(getUniversitiesRel));
        client.getAllUniversities();
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        assertThat(client.response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(13)
    public void testUniversitySorting() {
        URI expectedUri = UriComponentsBuilder
                .fromUriString(getLinkFromResponseHeaders(getUniversitiesRel))
                .queryParam("sort", "id_asc")
                .build()
                .toUri();
        client.newGetUniversitiesRequest().sort("id", "asc").send();
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        assertThat(client.response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(14)
    public void testUniversityFiltering() {
        URI expectedUri = UriComponentsBuilder
                .fromUriString(getLinkFromResponseHeaders(getUniversitiesRel))
                .queryParam("name", "University 1")
                .queryParam("country", "Country 1")
                .build()
                .toUri();
        client.newGetUniversitiesRequest()
                .filterByName("University 1")
                .filterByCountry("Country 1")
                .send();
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        assertThat(client.response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(15)
    public void testGetStudyModulesCollection() {
        URI expectedUri = URI.create(getLinkFromResponseHeaders(getStudyModulesRel));
        client.getAllStudyModules();
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        assertThat(client.response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(16)
    public void testStudyModuleSorting() {
        URI expectedUri = UriComponentsBuilder
                .fromUriString(getLinkFromResponseHeaders(getStudyModulesRel))
                .queryParam("sort", "id_desc")
                .build()
                .toUri();
        client.newGetStudyModulesRequest().sort("id", "desc").send();
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        assertThat(client.response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(17)
    public void testStudyModuleFiltering() {
        URI expectedUri = UriComponentsBuilder
                .fromUriString(getLinkFromResponseHeaders(getStudyModulesRel))
                .queryParam("name", "Study Module 1")
                .queryParam("semester", "spring")
                .build()
                .toUri();
        client.newGetStudyModulesRequest()
                .filterByName("Study Module 1")
                .filterBySemester("spring")
                .send();
        URI actualUri = client.request.uri();

        // Compare the actual URI with the expected URI
        assertThat(actualUri).isEqualTo(expectedUri);

        assertThat(client.response.statusCode()).isEqualTo(200);
    }

    private static String generateSampleStudyModuleJsonWithId(int id) {
        String semester = id % 2 == 0 ? "spring" : "fall";

        return "{" +
                "\"id\":" + id + "," +
                "\"name\":\"Study Module " + id + "\"," +
                "\"semester\": \"" + semester + "\"," +
                "\"creditPoints\": 5" +
                "}";
    }

    private static String generateSampleStudyModuleJsonWithoutId(int number) {
        String semester = number % 2 == 0 ? "spring" : "fall";

        return "{" +
                "\"name\":\"Study Module " + number + "\"," +
                "\"semester\": \"" + semester + "\"," +
                "\"creditPoints\": 5" +
                "}";
    }

    private static String generateSampleUniversityJsonWithId(int id) {
        return "{" +
                "\"id\":" + id + "," +
                "\"name\":\"University " + id + "\"," +
                "\"country\":\"Country " + id + "\"," +
                "\"department\":\"Department " + id + "\"," +
                "\"contactPersonName\":\"Contact Person " + id + "\"," +
                "\"outgoingStudentNumber\":" + id * 10 + "," +
                "\"incomingStudentNumber\":" + id * 15 + "," +
                "\"springSemesterStart\":\"2022-03-01\"," +
                "\"fallSemesterStart\":\"2021-10-01\"" +
                "}";
    }

    private static String generateSampleUniversityJsonWithoutId(int number) {
        return "{" +
                "\"name\":\"University " + number + "\"," +
                "\"country\":\"Country " + number + "\"," +
                "\"department\":\"Department " + number + "\"," +
                "\"contactPersonName\":\"Contact Person " + number + "\"," +
                "\"outgoingStudentNumber\":" + number * 10 + "," +
                "\"incomingStudentNumber\":" + number * 15 + "," +
                "\"springSemesterStart\":\"2022-03-01\"," +
                "\"fallSemesterStart\":\"2021-10-01\"" +
                "}";
    }

    private String getLinkFromResponseHeaders(String rel) {

        List<String> linkHeaders = client.response.headers().allValues("Link");

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

    private String replacePartInUriTemplate(String uriTemplate, String placeholder, Object replacement) {
        return uriTemplate.replace("{" + placeholder + "}", String.valueOf(replacement));
    }
}
