package com.papook.studytravel.server.controllers;

import static com.papook.studytravel.server.ServerConfiguration.MODULE_ENDPOINT;
import static com.papook.studytravel.server.ServerConfiguration.UNIVERSITY_ENDPOINT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.papook.studytravel.server.utils.HypermediaGenerator;

@WebMvcTest(DispatcherController.class)
public class DispatcherControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testDispatcher() throws Exception {
        String getUniversitiesCollection = HypermediaGenerator.formatLinkHeader(
                UNIVERSITY_ENDPOINT, "getUniversitiesCollection");
        String getModulesCollection = HypermediaGenerator.formatLinkHeader(
                MODULE_ENDPOINT, "getStudyModulesCollection");
        String postCreateUniversity = HypermediaGenerator.formatLinkHeader(
                UNIVERSITY_ENDPOINT, "postCreateUniversity");
        String postCreateModule = HypermediaGenerator.formatLinkHeader(
                MODULE_ENDPOINT, "postCreateStudyModule");
        String deleteAllUniversities = HypermediaGenerator.formatLinkHeader(
                UNIVERSITY_ENDPOINT, "deleteAllUniversities");
        String deleteAllModules = HypermediaGenerator.formatLinkHeader(
                MODULE_ENDPOINT, "deleteAllStudyModules");

        mockMvc.perform(get("/")).andExpectAll(
                status().isOk(),
                header().stringValues("Link",
                        getUniversitiesCollection,
                        getModulesCollection,
                        postCreateUniversity,
                        postCreateModule,
                        deleteAllUniversities,
                        deleteAllModules),
                jsonPath("$").doesNotExist(),
                content().string(""));
    }
}
