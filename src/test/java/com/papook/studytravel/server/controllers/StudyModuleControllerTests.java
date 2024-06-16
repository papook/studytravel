package com.papook.studytravel.server.controllers;

import static com.papook.studytravel.server.ServerConfiguration.BASE_URI;
import static com.papook.studytravel.server.ServerConfiguration.MODULE_ENDPOINT;
import static com.papook.studytravel.server.ServerConfiguration.UNIVERSITY_ENDPOINT;
import static com.papook.studytravel.server.utils.HypermediaGenerator.formatLinkHeader;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.papook.studytravel.server.models.StudyModule;
import com.papook.studytravel.server.services.StudyModuleService;
import com.papook.studytravel.server.utils.HypermediaGenerator;

@WebMvcTest(StudyModuleController.class)
public class StudyModuleControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StudyModuleService studyModuleService;

	@MockBean
	private HypermediaGenerator hypermediaGenerator;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void testGetCollection() throws Exception {
		int studyModuleCount = 5;
		List<StudyModule> studyModuleList = generateStudyModuleList(studyModuleCount);
		Page<StudyModule> studyModulesPage = new PageImpl<>(studyModuleList);

		when(studyModuleService.getModules("", "", 0)).thenReturn(studyModulesPage);

		String expectedJSON = objectMapper.writeValueAsString(studyModuleList);
		mockMvc.perform(get(MODULE_ENDPOINT)).andExpectAll(
				status().isOk(),
				jsonPath("$.length()").value(studyModuleCount),
				content().contentType("application/json"),
				content().json(expectedJSON));
	}

	@Test
	public void testGetOne() throws Exception {
		String formattedEndpoint = String.format("%s/1", MODULE_ENDPOINT);

		StudyModule studyModule = generateStudyModuleObject(1);
		when(studyModuleService.getModuleById(1L)).thenReturn(studyModule);

		String putUpdateStudyModuleLinkHeader = formatLinkHeader(formattedEndpoint,
				"putUpdateModule");
		String delStudyModuleLinkHeader = formatLinkHeader(formattedEndpoint, "delModule");

		String expectedJSON = objectMapper.writeValueAsString(studyModule);

		mockMvc.perform(get(MODULE_ENDPOINT + "/1"))
				.andExpectAll(
						status().isOk(),
						header().stringValues("Link",
								putUpdateStudyModuleLinkHeader,
								delStudyModuleLinkHeader),
						content().contentType(MediaType.APPLICATION_JSON),
						content().json(expectedJSON));
	}

	@Test
	public void testGetCollectionOfUniversity() throws Exception {
		int studyModuleCount = 5;
		List<StudyModule> studyModuleList = generateStudyModuleList(studyModuleCount);
		Page<StudyModule> studyModulesPage = new PageImpl<>(studyModuleList);

		when(studyModuleService.getModulesForUniversity(1L, "", "", 0)).thenReturn(studyModulesPage);

		String expectedJSON = objectMapper.writeValueAsString(studyModuleList);
		mockMvc.perform(get(UNIVERSITY_ENDPOINT + "/1" + MODULE_ENDPOINT))
				.andExpectAll(
						status().isOk(),
						jsonPath("$.length()").value(studyModuleCount),
						content().contentType("application/json"),
						content().json(expectedJSON));
	}

	@Test
	public void testGetOneOfUniversity() throws Exception {
		String formattedEndpoint = String.format("%s/1", MODULE_ENDPOINT);

		StudyModule studyModule = generateStudyModuleObject(1);
		when(studyModuleService.getModuleForUniversity(1L, 1L)).thenReturn(studyModule);

		String putUpdateStudyModuleLinkHeader = formatLinkHeader(formattedEndpoint,
				"putUpdateModule");
		String delStudyModuleLinkHeader = formatLinkHeader(formattedEndpoint, "delModule");

		String expectedJSON = objectMapper.writeValueAsString(studyModule);

		mockMvc.perform(get(UNIVERSITY_ENDPOINT + "/1" + MODULE_ENDPOINT + "/1").accept(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isOk(),
						header().stringValues("Link",
								putUpdateStudyModuleLinkHeader,
								delStudyModuleLinkHeader),
						content().contentType(MediaType.APPLICATION_JSON),
						content().json(expectedJSON));
	}

	@Test
	public void testCreate() throws Exception {
		String studyModuleJson = generateStudyModuleJson(1);
		StudyModule studyModule = generateStudyModuleObject(1);

		URI location = URI.create(BASE_URI + MODULE_ENDPOINT + "/1");
		when(studyModuleService.createModule(studyModule)).thenReturn(location);

		String expectedJSON = objectMapper.writeValueAsString(studyModule);

		mockMvc.perform(
				post(MODULE_ENDPOINT)
						.content(studyModuleJson)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isCreated(),
						header().string("Location", location.toString()),
						header().doesNotExist("Link"),
						content().contentType(MediaType.APPLICATION_JSON),
						content().json(expectedJSON));
	}

	@Test
	public void testUpdate() throws Exception {
		String studyModuleJson = generateStudyModuleJson(1);
		StudyModule studyModule = generateStudyModuleObject(1);

		// Test creation of a new study module with a PUT request
		Optional<URI> locationOptional = Optional.of(URI.create(BASE_URI + MODULE_ENDPOINT + "/1"));
		when(studyModuleService.updateModule(1L, studyModule)).thenReturn(locationOptional);

		String expectedJSON = objectMapper.writeValueAsString(studyModule);

		mockMvc.perform(
				put(MODULE_ENDPOINT + "/1")
						.content(studyModuleJson)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isCreated(),
						header().string("Location", locationOptional.get().toString()),
						header().doesNotExist("Link"),
						content().contentType(MediaType.APPLICATION_JSON),
						content().json(expectedJSON));

		// Test updating an existing study module with a PUT request
		when(studyModuleService.updateModule(1L, studyModule)).thenReturn(Optional.empty());

		String getSelfLinkHeader = formatLinkHeader(MODULE_ENDPOINT + "/1", "getSelf");
		mockMvc.perform(
				put(MODULE_ENDPOINT + "/1")
						.content(studyModuleJson)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().isNoContent(),
						header().string("Link", getSelfLinkHeader),
						header().doesNotExist("Location"),
						jsonPath("$").doesNotExist());
	}

	@Test
	public void testDelete() throws Exception {
		String getModulesCollectionLink = formatLinkHeader(MODULE_ENDPOINT, "getModulesCollection");

		mockMvc.perform(delete(MODULE_ENDPOINT + "/1"))
				.andExpectAll(
						status().isNoContent(),
						header().string("Link", getModulesCollectionLink),
						jsonPath("$").doesNotExist());
	}

	@Test
	public void testLinkToUniversity() throws Exception {
		String formattedEndpoint = String.format("%s/%d%s", UNIVERSITY_ENDPOINT, 1, MODULE_ENDPOINT);
		String getModulesOfUniversityLink = formatLinkHeader(formattedEndpoint, "getModulesOfUniversity");

		mockMvc.perform(
				put(UNIVERSITY_ENDPOINT + "/1" + MODULE_ENDPOINT + "/1"))
				.andExpectAll(
						status().isNoContent(),
						header().string("Link", getModulesOfUniversityLink),
						content().string(""),
						jsonPath("$").doesNotExist());
	}

	@Test
	public void testUnlinkFromUniversity() throws Exception {
		String formattedEndpoint = String.format("%s/%d%s", UNIVERSITY_ENDPOINT, 1, MODULE_ENDPOINT);
		String getModulesOfUniversityLink = formatLinkHeader(formattedEndpoint, "getModulesOfUniversity");

		mockMvc.perform(
				delete(UNIVERSITY_ENDPOINT + "/1" + MODULE_ENDPOINT + "/1"))
				.andExpectAll(
						status().isNoContent(),
						header().string("Link", getModulesOfUniversityLink),
						content().string(""),
						jsonPath("$").doesNotExist());
	}

	@Test
	public void testDeleteAll() throws Exception {
		String getModulesCollectionLink = formatLinkHeader(MODULE_ENDPOINT, "getModulesCollection");

		mockMvc.perform(delete(MODULE_ENDPOINT))
				.andExpectAll(
						status().isNoContent(),
						header().string("Link", getModulesCollectionLink),
						jsonPath("$").doesNotExist());
	}

	private static String generateStudyModuleJson(int id) {
		String semester = id % 2 == 0 ? "SPRING" : "FALL";

		return "{" +
				"\"id\":" + id + "," +
				"\"name\":\"Study Module " + id + "\"," +
				"\"semester\":\"" + semester + "\"," +
				"\"creditPoints\":\"" + 5 + "\"" +
				"}";
	}

	private StudyModule generateStudyModuleObject(int id) {
		return new StudyModule(
				(long) id,
				"Study Module " + id,
				id % 2 == 0 ? "SPRING" : "FALL",
				5,
				URI.create(BASE_URI + MODULE_ENDPOINT + "/" + id),

				null);
	}

	private List<StudyModule> generateStudyModuleList(int studyModuleCount) {
		List<StudyModule> studyModuleList = new ArrayList<>();
		for (int i = 0; i < studyModuleCount; i++) {
			studyModuleList.add(generateStudyModuleObject(i));
		}
		return studyModuleList;
	}
}
