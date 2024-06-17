package com.papook.studytravel;

import static com.papook.studytravel.server.ServerConfiguration.PAGE_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.papook.studytravel.client.utils.LocalDateAdapter;
import com.papook.studytravel.server.models.StudyModule;
import com.papook.studytravel.server.models.University;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class StudyTravelApplicationIntegrationTests {

    private final static String DISPATCHER_URL = "http://localhost:8080";

    // Initialize the Gson object with a custom LocalDate adapter
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    // Set up the HTTP client and request/response objects
    static HttpClient client = HttpClient.newHttpClient();
    static HttpRequest request;
    static HttpResponse<String> response;

    // Declare the URIs for the different endpoints
    static String createUniversityUri;
    static String universitiesCollectionUri;
    static String createStudyModuleUri;
    static String studyModulesCollectionUrl;
    static String deleteAllUniversitiesUri;
    static String deleteAllModulesUri;

    static String selfLink;
    static String updateLink;
    static String deleteLink;

    @Test
    @Order(1)
    void getDispatcher() {
        request = HttpRequest.newBuilder()
                .uri(URI.create(DISPATCHER_URL))
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            createUniversityUri = getLinkFromResponseHeaders("postCreateUniversity");
            universitiesCollectionUri = getLinkFromResponseHeaders("getUniversitiesCollection");
            createStudyModuleUri = getLinkFromResponseHeaders("postCreateStudyModule");
            studyModulesCollectionUrl = getLinkFromResponseHeaders("getStudyModulesCollection");
            deleteAllUniversitiesUri = getLinkFromResponseHeaders("deleteAllUniversities");
            deleteAllModulesUri = getLinkFromResponseHeaders("deleteAllStudyModules");

            assertThat(response.statusCode()).isEqualTo(200);

            assertThat(createUniversityUri).isNotNull();
            assertThat(universitiesCollectionUri).isNotNull();
            assertThat(createStudyModuleUri).isNotNull();
            assertThat(studyModulesCollectionUrl).isNotNull();
        } catch (IOException e) {
            fail("Error sending request to dispatcher. Make sure the server is running.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(2)
    void createUniversityWithPostRequest() {
        University university = generateSampleUniversityObject(1);
        String universityJson = gson.toJson(university);

        request = HttpRequest.newBuilder()
                .uri(URI.create(createUniversityUri))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(universityJson))
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(201);
            assertThat(response.headers().firstValue("Location")).isPresent();
        } catch (IOException e) {
            fail("Error creating university with POST request.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(3)
    void getCreatedUniversity() {
        String location = response.headers().firstValue("Location").get();
        request = HttpRequest.newBuilder()
                .uri(URI.create(location))
                .GET()
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            selfLink = gson.fromJson(response.body(), University.class).getSelf().toString();
            updateLink = getLinkFromResponseHeaders("putUpdateUniversity");
            deleteLink = getLinkFromResponseHeaders("delUniversity");

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).contains("University 1");
            assertThat(updateLink).isNotNull();
            assertThat(deleteLink).isNotNull();
        } catch (IOException e) {
            fail("Error getting created university.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(4)
    void updateUniversity() {
        University currentUniversity = gson.fromJson(response.body(), University.class);
        String updatedUniversityName = "Updated University";

        University modifiedUniversity = University.builder()
                .id(currentUniversity.getId())
                .name(updatedUniversityName)
                .country(currentUniversity.getCountry())
                .department(currentUniversity.getDepartment())
                .contactPersonName(currentUniversity.getContactPersonName())
                .outgoingStudentNumber(currentUniversity.getOutgoingStudentNumber())
                .incomingStudentNumber(currentUniversity.getIncomingStudentNumber())
                .springSemesterStart(currentUniversity.getSpringSemesterStart())
                .fallSemesterStart(currentUniversity.getFallSemesterStart())
                .build();

        String updatedUniversityJson = gson.toJson(modifiedUniversity);
        request = HttpRequest.newBuilder()
                .uri(URI.create(updateLink))
                .header("Content-Type", "application/json")
                .PUT(BodyPublishers.ofString(updatedUniversityJson))
                .build();

        try {
            // Send the update request
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(204);

            String getSelfUri = getLinkFromResponseHeaders("getSelf");
            request = HttpRequest.newBuilder()
                    .uri(URI.create(getSelfUri))
                    .GET()
                    .build();

            // Send the get request to check if the university was updated
            response = client.send(request, BodyHandlers.ofString());

            University updatedUniversity = gson.fromJson(response.body(), University.class);
            assertThat(updatedUniversity.getName()).isEqualTo(updatedUniversityName);

        } catch (IOException e) {
            fail("Error updating university.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(5)
    void deleteUniversity() {
        // Send the delete request
        request = HttpRequest.newBuilder()
                .uri(URI.create(deleteLink))
                .DELETE()
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(204);

            // Check if the university was deleted
            request = HttpRequest.newBuilder()
                    .uri(URI.create(selfLink))
                    .build();

            response = client.send(request, BodyHandlers.ofString());

            assertThat(response.statusCode()).isEqualTo(404);

        } catch (IOException e) {
            fail("Error deleting university.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(6)
    void createUniversityWithPutRequest() {
        String universityJson = generateSampleUniversityJsonWithId(1);

        request = HttpRequest.newBuilder()
                .uri(URI.create(selfLink))
                .header("Content-Type", "application/json")
                .PUT(BodyPublishers.ofString(universityJson))
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(201);
            Optional<String> locationHeaderOptional = response.headers().firstValue("Location");
            assertThat(locationHeaderOptional).isPresent();

            request = HttpRequest.newBuilder()
                    .uri(URI.create(locationHeaderOptional.get()))
                    .GET()
                    .build();

            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).contains("University 1");
        } catch (IOException e) {
            fail("Error creating university with PUT request.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(7)
    void createStudyModuleWithPostRequest() {
        String studyModuleJson = generateSampleStudyModuleJsonWithoutId(1);

        request = HttpRequest.newBuilder()
                .uri(URI.create(createStudyModuleUri))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(studyModuleJson))
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(201);
            assertThat(response.headers().firstValue("Location")).isPresent();
        } catch (IOException e) {
            fail("Error creating study module with POST request.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(8)
    void getCreatedStudyModule() {
        String location = response.headers().firstValue("Location").get();
        request = HttpRequest.newBuilder()
                .uri(URI.create(location))
                .GET()
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            selfLink = gson.fromJson(response.body(), StudyModule.class).getSelf().toString();
            updateLink = getLinkFromResponseHeaders("putUpdateModule");
            deleteLink = getLinkFromResponseHeaders("delModule");

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).contains("Study Module 1");
            assertThat(updateLink).isNotNull();
            assertThat(deleteLink).isNotNull();
        } catch (IOException e) {
            fail("Error getting created study module.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(9)
    void updateStudyModule() {
        StudyModule currentStudyModule = gson.fromJson(response.body(), StudyModule.class);
        String updatedStudyModuleName = "Updated StudyModule";

        StudyModule modifiedStudyModule = StudyModule.builder()
                .id(currentStudyModule.getId())
                .name(updatedStudyModuleName)
                .semester(currentStudyModule.getSemester())
                .creditPoints(currentStudyModule.getCreditPoints())
                .build();

        String updatedStudyModuleJson = gson.toJson(modifiedStudyModule);
        request = HttpRequest.newBuilder()
                .uri(URI.create(updateLink))
                .header("Content-Type", "application/json")
                .PUT(BodyPublishers.ofString(updatedStudyModuleJson))
                .build();

        try {
            // Send the update request
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(204);

            String getSelfUri = getLinkFromResponseHeaders("getSelf");
            request = HttpRequest.newBuilder()
                    .uri(URI.create(getSelfUri))
                    .GET()
                    .build();

            // Send the get request to check if the study module was updated
            response = client.send(request, BodyHandlers.ofString());

            StudyModule updatedStudyModule = gson.fromJson(response.body(), StudyModule.class);
            assertThat(updatedStudyModule.getName()).isEqualTo(updatedStudyModuleName);

        } catch (IOException e) {
            fail("Error updating study module.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(10)
    void deleteStudyModule() {
        // Send the delete request
        request = HttpRequest.newBuilder()
                .uri(URI.create(deleteLink))
                .DELETE()
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(204);

            // Check if the study module was deleted
            request = HttpRequest.newBuilder()
                    .uri(URI.create(selfLink))
                    .build();

            response = client.send(request, BodyHandlers.ofString());

            assertThat(response.statusCode()).isEqualTo(404);

        } catch (IOException e) {
            fail("Error deleting study module.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(11)
    void createStudyModuleWithPutRequest() {
        String studyModuleJson = generateSampleStudyModuleJsonWithId(1);

        request = HttpRequest.newBuilder()
                .uri(URI.create(selfLink))
                .header("Content-Type", "application/json")
                .PUT(BodyPublishers.ofString(studyModuleJson))
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(201);
            Optional<String> locationHeaderOptional = response.headers().firstValue("Location");
            assertThat(locationHeaderOptional).isPresent();

            request = HttpRequest.newBuilder()
                    .uri(URI.create(locationHeaderOptional.get()))
                    .GET()
                    .build();

            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).contains("Study Module 1");
        } catch (IOException e) {
            fail("Error creating study module with PUT request.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(12)
    void linkFirstUniversityToFirstModule() {
        // Get the first university
        String linkToModuleUriTemplate = null;

        request = HttpRequest.newBuilder()
                .uri(URI.create(universitiesCollectionUri))
                .GET()
                .build();
        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);

            University[] responseBodyObjects = gson.fromJson(response.body(), University[].class);
            String university1Uri = responseBodyObjects[0].getSelf().toString();

            request = HttpRequest.newBuilder()
                    .uri(URI.create(university1Uri))
                    .GET()
                    .build();

            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);

            linkToModuleUriTemplate = getLinkFromResponseHeaders("putLinkModule");
            assertThat(linkToModuleUriTemplate).isNotNull();

            assertThat(response.statusCode()).isEqualTo(200);
        } catch (IOException e) {
            fail("Error getting uri template for linking a module to university.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }

        // Link the first university to the first study module
        String linkToModule1Uri = replacePartInUriTemplate(
                linkToModuleUriTemplate,
                "moduleId",
                1);

        request = HttpRequest.newBuilder()
                .uri(URI.create(linkToModule1Uri))
                .PUT(BodyPublishers.noBody())
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(204);
        } catch (IOException e) {
            fail("Error linking the first university to the first study module.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(13)
    void getStudyModulesOfUniversity() {
        String getModulesOfUniversityUri = getLinkFromResponseHeaders("getModulesOfUniversity");
        assertThat(getModulesOfUniversityUri).isNotNull();

        request = HttpRequest.newBuilder()
                .uri(URI.create(getModulesOfUniversityUri))
                .GET()
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            StudyModule[] studyModules = gson.fromJson(response.body(), StudyModule[].class);
            assertThat(studyModules.length).isEqualTo(1);
        } catch (IOException e) {
            fail("Error getting study modules of university.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(14)
    void unlinkFirstUniversityFromFirstModule() {
        String unlinkFromModuleUriTemplate = null;

        request = HttpRequest.newBuilder()
                .uri(URI.create(universitiesCollectionUri))
                .GET()
                .build();
        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);

            University[] responseBodyObjects = gson.fromJson(response.body(), University[].class);
            String university1Uri = responseBodyObjects[0].getSelf().toString();

            request = HttpRequest.newBuilder()
                    .uri(URI.create(university1Uri))
                    .GET()
                    .build();

            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);

            unlinkFromModuleUriTemplate = getLinkFromResponseHeaders("delUnlinkModule");
            assertThat(unlinkFromModuleUriTemplate).isNotNull();

            assertThat(response.statusCode()).isEqualTo(200);
        } catch (IOException e) {
            fail("Error getting uri template for unlinking a module from university.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }

        String unlinkFromModule1Uri = replacePartInUriTemplate(
                unlinkFromModuleUriTemplate,
                "moduleId",
                1);

        request = HttpRequest.newBuilder()
                .uri(URI.create(unlinkFromModule1Uri))
                .DELETE()
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(204);
        } catch (IOException e) {
            fail("Error unlinking the first university from the first study module.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(15)
    void getStudyModulesOfUniversityAfterUnlinking() {
        String getModulesOfUniversityUri = getLinkFromResponseHeaders("getModulesOfUniversity");
        assertThat(getModulesOfUniversityUri).isNotNull();

        request = HttpRequest.newBuilder()
                .uri(URI.create(getModulesOfUniversityUri))
                .GET()
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            StudyModule[] studyModules = gson.fromJson(response.body(), StudyModule[].class);
            assertThat(studyModules.length).isEqualTo(0);
        } catch (IOException e) {
            fail("Error getting study modules of university after unlinking.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(16)
    void deleteUniversityWhichHasLinkedModules() {
        // Create a university
        String universityJson = generateSampleUniversityJsonWithoutId(2);
        String delUniversityUri = null;

        request = HttpRequest.newBuilder()
                .uri(URI.create(createUniversityUri))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(universityJson))
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(201);
            Optional<String> locationHeaderOptional = response.headers().firstValue("Location");
            assertThat(locationHeaderOptional).isPresent();

            request = HttpRequest.newBuilder()
                    .uri(URI.create(locationHeaderOptional.get()))
                    .GET()
                    .build();

            response = client.send(request, BodyHandlers.ofString());

            delUniversityUri = getLinkFromResponseHeaders("delUniversity");
        } catch (IOException e) {
            fail("Error creating university with POST request.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }

        // Create a study module
        String studyModuleJson = generateSampleStudyModuleJsonWithoutId(2);

        request = HttpRequest.newBuilder()
                .uri(URI.create(createStudyModuleUri))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(studyModuleJson))
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(201);
            Optional<String> firstValue2 = response.headers().firstValue("Location");
            assertThat(firstValue2).isPresent();
        } catch (IOException e) {
            fail("Error creating study module with POST request.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }

        // Link the second university to the second study module
        String linkToModuleUriTemplate = null;

        request = HttpRequest.newBuilder()
                .uri(URI.create(universitiesCollectionUri))
                .GET()
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);

            University[] responseBodyObjects = gson.fromJson(response.body(), University[].class);
            String university2Uri = responseBodyObjects[1].getSelf().toString();

            request = HttpRequest.newBuilder()
                    .uri(URI.create(university2Uri))
                    .GET()
                    .build();

            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);

            linkToModuleUriTemplate = getLinkFromResponseHeaders("putLinkModule");
            assertThat(linkToModuleUriTemplate).isNotNull();

            request = HttpRequest.newBuilder()
                    .uri(URI.create(replacePartInUriTemplate(linkToModuleUriTemplate, "moduleId", 2)))
                    .PUT(BodyPublishers.noBody())
                    .build();

            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(204);

        } catch (IOException e) {
            fail("Error getting uri template for linking a module to university.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }

        // Try to delete the second university
        request = HttpRequest.newBuilder()
                .uri(URI.create(delUniversityUri))
                .DELETE()
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(204);
        } catch (IOException e) {
            fail("Error deleting university with linked modules.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(17)
    void verifyThatLinkedModuleIsDeleted() {
        request = HttpRequest.newBuilder()
                .uri(URI.create(studyModulesCollectionUrl))
                .GET()
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            StudyModule[] studyModules = gson.fromJson(response.body(), StudyModule[].class);
            assertThat(studyModules.length).isLessThan(PAGE_SIZE);
            assertThat(response.body()).doesNotContain("Study Module 2");
        } catch (IOException e) {
            fail("Error getting study modules of university after unlinking.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(18)
    void generateBunchOfUniversitiesAndTestPaging() {
        // Verify that next page does not exists
        request = HttpRequest.newBuilder()
                .uri(URI.create(universitiesCollectionUri))
                .GET()
                .build();
        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            University[] universities = gson.fromJson(response.body(), University[].class);
            assertThat(universities.length).isLessThan(PAGE_SIZE);

            String nextPageUri = getLinkFromResponseHeaders("next");
            assertThat(nextPageUri).isNull();
        } catch (IOException e) {
            fail("Error getting universities collection.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }

        // Create 5 pages of universities
        for (int i = 1; i <= PAGE_SIZE * 5; i++) {
            String universityJson = generateSampleUniversityJsonWithoutId(i);

            request = HttpRequest.newBuilder()
                    .uri(URI.create(createUniversityUri))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(universityJson))
                    .build();

            try {
                response = client.send(request, BodyHandlers.ofString());
                assertThat(response.statusCode()).isEqualTo(201);
            } catch (IOException e) {
                fail("Error creating university with POST request.");
            } catch (InterruptedException e) {
                fail("The request was interrupted.");
            }
        }

        // Verify that next page exists
        request = HttpRequest.newBuilder()
                .uri(URI.create(universitiesCollectionUri))
                .GET()
                .build();
        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            University[] universities = gson.fromJson(response.body(), University[].class);
            assertThat(universities.length).isEqualTo(PAGE_SIZE);

            String nextPageUri = getLinkFromResponseHeaders("next");
            assertThat(nextPageUri).isNotNull();

            // Get the next page
            request = HttpRequest.newBuilder()
                    .uri(URI.create(nextPageUri))
                    .GET()
                    .build();

            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            universities = gson.fromJson(response.body(), University[].class);
            assertThat(universities.length).isEqualTo(PAGE_SIZE);

            String prevPageUri = getLinkFromResponseHeaders("prev");
            assertThat(prevPageUri).isNotNull();

            // Get the previous page
            request = HttpRequest.newBuilder()
                    .uri(URI.create(prevPageUri))
                    .GET()
                    .build();

            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            universities = gson.fromJson(response.body(), University[].class);
            assertThat(universities.length).isEqualTo(PAGE_SIZE);

        } catch (IOException e) {
            fail("Error getting universities collection.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(19)
    void generateBunchOfStudyModulesAndTestPaging() {
        // Verify that next page does not exists
        request = HttpRequest.newBuilder()
                .uri(URI.create(studyModulesCollectionUrl))
                .GET()
                .build();
        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            StudyModule[] studyModules = gson.fromJson(response.body(), StudyModule[].class);
            assertThat(studyModules.length).isLessThan(PAGE_SIZE);

            String nextPageUri = getLinkFromResponseHeaders("next");
            assertThat(nextPageUri).isNull();
        } catch (IOException e) {
            fail("Error getting study modules collection.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }

        // Create 5 pages of study modules
        for (int i = 1; i <= PAGE_SIZE * 5; i++) {
            String studyModuleJson = generateSampleStudyModuleJsonWithoutId(i);

            request = HttpRequest.newBuilder()
                    .uri(URI.create(createStudyModuleUri))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(studyModuleJson))
                    .build();

            try {
                response = client.send(request, BodyHandlers.ofString());
                assertThat(response.statusCode()).isEqualTo(201);
            } catch (IOException e) {
                fail("Error creating study module with POST request.");
            } catch (InterruptedException e) {
                fail("The request was interrupted.");
            }
        }

        // Verify that next page exists
        request = HttpRequest.newBuilder()
                .uri(URI.create(studyModulesCollectionUrl))
                .GET()
                .build();
        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            StudyModule[] studyModules = gson.fromJson(response.body(), StudyModule[].class);
            assertThat(studyModules.length).isEqualTo(PAGE_SIZE);

            String nextPageUri = getLinkFromResponseHeaders("next");
            assertThat(nextPageUri).isNotNull();

            // Get the next page
            request = HttpRequest.newBuilder()
                    .uri(URI.create(nextPageUri))
                    .GET()
                    .build();

            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            studyModules = gson.fromJson(response.body(), StudyModule[].class);
            assertThat(studyModules.length).isEqualTo(PAGE_SIZE);

            String prevPageUri = getLinkFromResponseHeaders("prev");
            assertThat(prevPageUri).isNotNull();

            // Get the previous page
            request = HttpRequest.newBuilder()
                    .uri(URI.create(prevPageUri))
                    .GET()
                    .build();

            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            studyModules = gson.fromJson(response.body(), StudyModule[].class);
            assertThat(studyModules.length).isEqualTo(PAGE_SIZE);
        } catch (IOException e) {
            fail("Error getting study modules collection.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(20)
    void deleteAllResources() {
        request = HttpRequest.newBuilder()
                .uri(URI.create(deleteAllUniversitiesUri))
                .DELETE()
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(204);
        } catch (IOException e) {
            fail("Error deleting all universities.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }

        request = HttpRequest.newBuilder()
                .uri(URI.create(deleteAllModulesUri))
                .DELETE()
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(204);
        } catch (IOException e) {
            fail("Error deleting all study modules.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    @Test
    @Order(21)
    void verifyThatAllResourcesAreDeleted() {
        request = HttpRequest.newBuilder()
                .uri(URI.create(universitiesCollectionUri))
                .GET()
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            University[] universities = gson.fromJson(response.body(), University[].class);
            assertThat(universities.length).isEqualTo(0);
        } catch (IOException e) {
            fail("Error getting universities collection after deleting all resources.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }

        request = HttpRequest.newBuilder()
                .uri(URI.create(studyModulesCollectionUrl))
                .GET()
                .build();

        try {
            response = client.send(request, BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            StudyModule[] studyModules = gson.fromJson(response.body(), StudyModule[].class);
            assertThat(studyModules.length).isEqualTo(0);
        } catch (IOException e) {
            fail("Error getting study modules collection after deleting all resources.");
        } catch (InterruptedException e) {
            fail("The request was interrupted.");
        }
    }

    private String generateSampleStudyModuleJsonWithId(int id) {
        String semester = id % 2 == 0 ? "spring" : "fall";

        return "{" +
                "\"id\":" + id + "," +
                "\"name\":\"Study Module " + id + "\"," +
                "\"semester\": \"" + semester + "\"," +
                "\"creditPoints\": 5" +
                "}";
    }

    private String generateSampleStudyModuleJsonWithoutId(int number) {
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

    private static University generateSampleUniversityObject(int id) {
        return University.builder()
                .name("University " + id)
                .country("Country " + id)
                .department("Department " + id)
                .contactPersonName("Contact Person " + id)
                .outgoingStudentNumber(id * 10)
                .incomingStudentNumber(id * 15)
                .springSemesterStart(LocalDate.of(2022, 3, 1))
                .fallSemesterStart(LocalDate.of(2021, 10, 1))
                .build();
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
