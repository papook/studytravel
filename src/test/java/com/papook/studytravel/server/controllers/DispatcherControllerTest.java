package com.papook.studytravel.server.controllers;

import static com.papook.studytravel.server.ServerConfiguration.BASE_URI;
import static com.papook.studytravel.server.ServerConfiguration.UNIVERSITY_ENDPOINT;
import static com.papook.studytravel.server.ServerConfiguration.MODULE_ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(DispatcherController.class)
public class DispatcherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHypermediaLinks() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/")).andReturn();

        String getUniversitiesCollection = String.format(
                "<%s%s>;rel=\"getUniversitiesCollection\"",
                BASE_URI, UNIVERSITY_ENDPOINT);
        String getModulesCollection = String.format(
                "<%s%s>;rel=\"getModulesCollection\"",
                BASE_URI, MODULE_ENDPOINT);
        String postCreateUniversity = String.format(
                "<%s%s>;rel=\"postCreateUniversity\"",
                BASE_URI, UNIVERSITY_ENDPOINT);
        String postCreateModule = String.format(
                "<%s%s>;rel=\"postCreateModule\"",
                BASE_URI, MODULE_ENDPOINT);

        List<String> expectedLinkHeaders = List.of(
                getUniversitiesCollection,
                getModulesCollection,
                postCreateUniversity,
                postCreateModule);

        List<String> linkHeaders = mvcResult.getResponse().getHeaders("Link");

        assertThat(linkHeaders).hasSize(expectedLinkHeaders.size());
        assertThat(linkHeaders).isEqualTo(expectedLinkHeaders);
    }

    @Test
    public void testReturnEmptyBody() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/")).andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        assertThat(content).isEmpty();
    }

    @Test
    public void testReturnStatusOk() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/")).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);
    }
}
