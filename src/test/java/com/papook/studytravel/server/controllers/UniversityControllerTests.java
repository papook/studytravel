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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.papook.studytravel.server.models.University;
import com.papook.studytravel.server.services.UniversityService;
import com.papook.studytravel.server.utils.HypermediaGenerator;

@WebMvcTest(UniversityController.class)
public class UniversityControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UniversityService universityService;

	@MockBean
	private HypermediaGenerator hypermediaGenerator;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void testGetCollection() throws Exception {
		int universityCount = 5;
		List<University> universityList = generateUniversityList(universityCount);
		Page<University> universitiesPage = new PageImpl<>(universityList);

		// Set up the mock objects
		when(universityService.getUniversities("", "", 0, "id_asc")).thenReturn(universitiesPage);

		String expectedJSON = objectMapper.writeValueAsString(universityList);
		mockMvc.perform(get(UNIVERSITY_ENDPOINT))
				.andExpectAll(
						// Test the status code, content type, and JSON object count
						status().isOk(),
						content().contentType("application/json"),
						jsonPath("$.length()").value(universityCount),
						content().json(expectedJSON));
	}

	@Test
	public void testGetOne() throws Exception {
		String formattedEndpoint = String.format("%s/1", UNIVERSITY_ENDPOINT);
		String moduleUriTemplate = String.format("%s/1%s/{moduleId}",
				UNIVERSITY_ENDPOINT, MODULE_ENDPOINT);

		University university = generateUniversityObject(1);
		when(universityService.getUniversityById(1L)).thenReturn(university);

		String updateLinkHeader = formatLinkHeader(formattedEndpoint,
				"update");
		String deleteLinkHeader = formatLinkHeader(formattedEndpoint, "delete");
		String putLinkModuleLinkHeader = formatLinkHeader(moduleUriTemplate, "putLinkModule");
		String delUnlinkModuleLinkHeader = formatLinkHeader(moduleUriTemplate, "delUnlinkModule");
		String getModuleOfUniversityLinkHeader = formatLinkHeader(moduleUriTemplate,
				"getModuleOfUniversity");

		String expectedJSON = objectMapper.writeValueAsString(university);

		mockMvc.perform(
				get(UNIVERSITY_ENDPOINT + "/1"))
				.andExpectAll(
						// Test the status code, content type, and Link headers
						header().stringValues("Link",
								updateLinkHeader,
								deleteLinkHeader,
								putLinkModuleLinkHeader,
								delUnlinkModuleLinkHeader,
								getModuleOfUniversityLinkHeader),
						status().isOk(),
						content().contentType("application/json"),
						jsonPath("$").exists(),
						content().json(expectedJSON));
	}

	@Test
	public void testCreate() throws Exception {
		String universityJson = generateUniversityJsonWithID(1);

		University university = objectMapper.readValue(universityJson, University.class);
		URI location = URI.create(BASE_URI + UNIVERSITY_ENDPOINT + "/1");
		when(universityService.createUniversity(university)).thenReturn(location);

		String expectedJSON = objectMapper.writeValueAsString(university);

		mockMvc.perform(
				post(UNIVERSITY_ENDPOINT)
						.content(universityJson)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(
						// Test the status code, Location header, Content-Type, and JSON object
						status().isCreated(),
						header().string("Location", location.toString()),
						header().doesNotExist("Link"),
						content().contentType("application/json"),
						jsonPath("$").exists(),
						content().json(expectedJSON));
	}

	@Test
	public void testUpdate() throws Exception {
		String universityJson1 = generateUniversityJsonWithID(1);
		University universityObject = objectMapper.readValue(universityJson1, University.class);

		// Test creating a new University with a PUT request
		Optional<URI> locationOptional = Optional.of(URI.create(BASE_URI + UNIVERSITY_ENDPOINT + "/1"));
		when(universityService.updateUniversity(1L, universityObject))
				.thenReturn(locationOptional);

		String expectedJSON = objectMapper.writeValueAsString(universityObject);
		mockMvc.perform(
				put(UNIVERSITY_ENDPOINT + "/1")
						.content(universityJson1)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(
						// Test the status code, Location header, Content-Type, and JSON object
						status().isCreated(),
						header().string("Location", locationOptional.get().toString()),
						header().doesNotExist("Link"),
						content().contentType("application/json"),
						jsonPath("$").exists(),
						content().json(expectedJSON));

		// Test updating an existing University with a PUT request
		when(universityService.updateUniversity(1L, universityObject))
				.thenReturn(Optional.empty());

		String getSelfHeaderLink = formatLinkHeader(UNIVERSITY_ENDPOINT + "/1", "getSelf");
		mockMvc.perform(
				put(UNIVERSITY_ENDPOINT + "/1")
						.content(universityJson1)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(
						// Test the status code, Location header, and JSON object
						status().isNoContent(),
						header().doesNotExist("Location"),
						header().string("Link", getSelfHeaderLink),
						jsonPath("$").doesNotExist());
	}

	@Test
	public void testDelete() throws Exception {
		String getUniversitiesCollectionHeaderLink = formatLinkHeader(UNIVERSITY_ENDPOINT, "getUniversitiesCollection");

		mockMvc.perform(delete(UNIVERSITY_ENDPOINT + "/1"))
				.andExpectAll(
						// Test the status code, Content-Length, and Link header
						status().isNoContent(),
						header().string("Link", getUniversitiesCollectionHeaderLink),
						jsonPath("$").doesNotExist());
	}

	private static String generateUniversityJsonWithID(int id) {
		return "{" +
				"\"id\":" + id + "," +
				"\"name\":\"University " + id + "\"," +
				"\"country\":\"Country " + id + "\"," +
				"\"department\":\"Department " + id + "\"," +
				"\"contactPersonName\":\"Contact Person " + id + "\"," +
				"\"outgoingStudentNumber\":" + (10 + id) + "," +
				"\"incomingStudentNumber\":" + (20 + id) + "," +
				"\"springSemesterStart\":\"2024-06-15\"," +
				"\"fallSemesterStart\":\"2024-06-15\"" +
				"}";
	}

	private static University generateUniversityObject(int id) {
		return new University(
				(long) id,
				"University " + id,
				"Country " + id,
				"Department " + id,
				"Contact Person " + id,
				10 + id,
				20 + id,
				LocalDate.now(),
				LocalDate.now(),
				URI.create(BASE_URI + UNIVERSITY_ENDPOINT + "/" + id + MODULE_ENDPOINT),
				URI.create(BASE_URI + UNIVERSITY_ENDPOINT + "/" + id),
				Set.of());
	}

	@Test
	public void testDeleteAll() throws Exception {
		String getUniversitiesCollectionHeaderLink = formatLinkHeader(UNIVERSITY_ENDPOINT, "getUniversitiesCollection");

		mockMvc.perform(delete(UNIVERSITY_ENDPOINT))
				.andExpectAll(
						// Test the status code, Content-Length, and Link header
						status().isNoContent(),
						header().string("Link", getUniversitiesCollectionHeaderLink),
						jsonPath("$").doesNotExist());
	}

	private static List<University> generateUniversityList(int count) {
		List<University> list = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			list.add(generateUniversityObject(i));
		}

		return list;
	}
}
