package com.papook.studytravel.server.controllers;

import static com.papook.studytravel.server.ServerConfiguration.BASE_URI;
import static com.papook.studytravel.server.ServerConfiguration.MODULE_ENDPOINT;
import static com.papook.studytravel.server.ServerConfiguration.UNIVERSITY_ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.papook.studytravel.server.models.University;
import com.papook.studytravel.server.services.UniversityService;
import com.papook.studytravel.server.utils.HypermediaGenerator;

@WebMvcTest(UniversityController.class)
public class UniversityControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UniversityService universityService;

	@MockBean
	private HypermediaGenerator hypermediaGenerator;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void testgetCollection() throws Exception {
		int universityCount = 5;
		List<University> universityList = generateUniversityList(universityCount);
		Page<University> universitiesPage = new PageImpl<>(universityList);

		// Set up the mock objects
		when(universityService.getUniversities("", "", 0)).thenReturn(universitiesPage);
		when(hypermediaGenerator.buildPagingLinksHeaders(universitiesPage)).thenReturn(new HttpHeaders());

		// Get the response from the controller
		var response = mockMvc.perform(get(UNIVERSITY_ENDPOINT))
				.andExpect( // Test the content length of the response
						jsonPath("$.length()").value(universityCount))
				.andReturn()
				.getResponse();

		// Test the status code, content type, and JSON content of the response
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentType()).isEqualTo("application/json");

		String expectedJSON = objectMapper.writeValueAsString(universityList);
		String actualJSON = response.getContentAsString();
		assertThat(actualJSON).isEqualTo(expectedJSON);
	}

	@Test
	public void testGetOne() throws Exception {
		String formattedEndpoint = String.format("%s/1", UNIVERSITY_ENDPOINT);
		String moduleUriTemplate = String.format("%s/1%s/{moduleId}",
				UNIVERSITY_ENDPOINT, MODULE_ENDPOINT);

		LocalDate todaysDate = LocalDate.now();
		University university = new University(
				1L,
				"University 1",
				"Country 1",
				"Department 1",
				"Contact Person 1",
				11,
				21,
				todaysDate,
				todaysDate,
				URI.create("http://localhost:8080/universities/1/modules"),
				URI.create("http://localhost:8080/universities/1"),
				Set.of());
		when(universityService.getUniversityById(1L)).thenReturn(university);

		var response = mockMvc.perform(get(UNIVERSITY_ENDPOINT + "/1"))
				.andReturn()
				.getResponse();

		String putUpdateUniversityLinkHeader = HypermediaGenerator.formatLinkHeader(formattedEndpoint,
				"putUpdateUniversity");
		String delUniversityLinkHeader = HypermediaGenerator.formatLinkHeader(formattedEndpoint, "delUniversity");
		String putLinkModuleLinkHeader = HypermediaGenerator.formatLinkHeader(moduleUriTemplate, "putLinkModule");
		String delUnlinkModuleLinkHeader = HypermediaGenerator.formatLinkHeader(moduleUriTemplate, "delUnlinkModule");
		String getModuleOfUniversityLinkHeader = HypermediaGenerator.formatLinkHeader(moduleUriTemplate,
				"getModuleOfUniversity");

		// Test headers and status code
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentType()).isEqualTo("application/json");
		assertThat(response.getHeaders("Link")).contains(
				putUpdateUniversityLinkHeader,
				delUniversityLinkHeader,
				putLinkModuleLinkHeader,
				delUnlinkModuleLinkHeader,
				getModuleOfUniversityLinkHeader);

		// Test the JSON content of the response
		String expectedJSON = objectMapper.writeValueAsString(university);
		String actualJSON = response.getContentAsString();

		assertThat(actualJSON).isEqualTo(expectedJSON);
	}

	@Test
	public void testCreate() throws Exception {
		String universityJson = generateUniversityJsonWithID(1);

		University university = objectMapper.readValue(universityJson, University.class);
		URI location = URI.create("http://localhost:8080/universities/1");
		when(universityService.createUniversity(university)).thenReturn(location);

		var response = mockMvc
				.perform(post(UNIVERSITY_ENDPOINT)
						.content(universityJson)
						.contentType(MediaType.APPLICATION_JSON))
				.andReturn()
				.getResponse();

		// Test headers and status code
		assertThat(response.getStatus()).isEqualTo(201);
		assertThat(response.getHeader("Location")).isEqualTo(location.toString());

		// Test the JSON content of the response
		String expectedJSON = objectMapper.writeValueAsString(university);
		String actualJSON = response.getContentAsString();

		assertThat(actualJSON).isEqualTo(expectedJSON);
	}

	@Test
	public void testUpdate() throws Exception {
		String universityJson1 = generateUniversityJsonWithID(1);
		University universityObject = objectMapper.readValue(universityJson1, University.class);

		// Test creating a new University with a PUT request
		when(universityService.updateUniversity(1L, universityObject))
				.thenReturn(Optional.of(URI.create("http://localhost:8080/universities/1")));

		var createResponse = mockMvc.perform(
				put(UNIVERSITY_ENDPOINT + "/1")
						.content(universityJson1)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").exists())
				.andReturn()
				.getResponse();

		assertThat(createResponse.getStatus()).isEqualTo(201);
		assertThat(createResponse.getHeader("Location")).isEqualTo("http://localhost:8080/universities/1");

		String expectedJSON = objectMapper.writeValueAsString(universityObject);
		String actualJSON = createResponse.getContentAsString();

		assertThat(actualJSON).isEqualTo(expectedJSON);

		// Test updating an existing University with a PUT request
		when(universityService.updateUniversity(1L, universityObject))
				.thenReturn(Optional.empty());

		var updateResponse = mockMvc.perform(
				put(UNIVERSITY_ENDPOINT + "/1")
						.content(universityJson1)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").doesNotExist())
				.andReturn()
				.getResponse();

		String getSelfHeaderLink = String.format("<%s%s>; rel=\"%s\"", BASE_URI, UNIVERSITY_ENDPOINT + "/1", "getSelf");

		assertThat(updateResponse.getStatus()).isEqualTo(204);
		assertThat(updateResponse.getContentLength()).isEqualTo(0);
		assertThat(updateResponse.getHeader("Link")).isEqualTo(getSelfHeaderLink);

	}

	@Test
	public void testDelete() throws Exception {
		var response = mockMvc.perform(
				delete(UNIVERSITY_ENDPOINT + "/1"))
				.andExpect(jsonPath("$").doesNotExist())
				.andReturn()
				.getResponse();

		String getUniversitiesCollectionHeaderLink = String.format("<%s%s>; rel=\"%s\"",
				BASE_URI, UNIVERSITY_ENDPOINT,
				"getUniversitiesCollection");

		assertThat(response.getStatus()).isEqualTo(204);
		assertThat(response.getContentLength()).isEqualTo(0);
		assertThat(response.getHeader("Link")).isEqualTo(getUniversitiesCollectionHeaderLink);
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

	private static List<University> generateUniversityList(int count) {
		List<University> list = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			list.add(new University(
					(long) i + 1,
					"University " + i,
					"Country " + i,
					"Department " + i,
					"Contact Person " + i,
					10 + i,
					20 + i,
					LocalDate.now(),
					LocalDate.now(),
					URI.create("http://localhost:8080/" + i + "/modules"),
					URI.create("http://localhost:8080/universities/" + i),
					Set.of()));
		}

		return list;
	}
}
