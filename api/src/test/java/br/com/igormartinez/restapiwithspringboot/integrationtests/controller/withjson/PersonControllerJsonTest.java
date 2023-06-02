package br.com.igormartinez.restapiwithspringboot.integrationtests.controller.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.igormartinez.restapiwithspringboot.configs.TestConfigs;
import br.com.igormartinez.restapiwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.AccountCredentialsVO;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.PersonVO;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	private static PersonVO personVO;

	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		personVO = new PersonVO();
	}

	@Test
	@Order(0)
	void authorization() {
		AccountCredentialsVO user = new AccountCredentialsVO("igormartinez", "admin1234");

		String accessToken = 
			given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.body(user)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class)
								.getAccessToken();

		specification = new RequestSpecBuilder()
			.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
			.setBasePath("/api/person/v1")
			.setPort(TestConfigs.SERVER_PORT)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
	}

	@Test
	@Order(1)
	void testCreate() throws JsonMappingException, JsonProcessingException {
		mockPersonVO();

		String content = 
			given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
					.body(personVO)
				.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();

		PersonVO createdPersonVO = objectMapper.readValue(content, PersonVO.class);
		personVO = createdPersonVO;
		
		assertNotNull(createdPersonVO);

		assertNotNull(createdPersonVO.getId());
		assertNotNull(createdPersonVO.getFirstName());
		assertNotNull(createdPersonVO.getLastName());
		assertNotNull(createdPersonVO.getAddress());
		assertNotNull(createdPersonVO.getGender());
		assertNotNull(createdPersonVO.getEnabled());

		assertTrue(createdPersonVO.getId() > 0);
		assertEquals("Lorem", createdPersonVO.getFirstName());
		assertEquals("Ipsum", createdPersonVO.getLastName());
		assertEquals("Brasil", createdPersonVO.getAddress());
		assertEquals("M", createdPersonVO.getGender());
		assertTrue(createdPersonVO.getEnabled());
	}

	@Test
	@Order(2)
	void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {

		String content = 
			given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_NOTALLOWED)
					.body(personVO)
				.when()
					.post()
				.then()
					.statusCode(403)
				.extract()
					.body()
						.asString();
		
		assertNotNull(content);
		assertEquals("Invalid CORS request", content);
	}

	@Test
	@Order(3)
	void testFindById() throws JsonMappingException, JsonProcessingException {

		String content = 
			given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
					.pathParam("id", personVO.getId())
				.when()
					.get("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();

		PersonVO persistedPersonVO = objectMapper.readValue(content, PersonVO.class);
		
		assertNotNull(persistedPersonVO);

		assertNotNull(persistedPersonVO.getId());
		assertNotNull(persistedPersonVO.getFirstName());
		assertNotNull(persistedPersonVO.getLastName());
		assertNotNull(persistedPersonVO.getAddress());
		assertNotNull(persistedPersonVO.getGender());
		assertNotNull(persistedPersonVO.getEnabled());
		
		assertEquals(personVO.getId(), persistedPersonVO.getId());
		assertEquals("Lorem", persistedPersonVO.getFirstName());
		assertEquals("Ipsum", persistedPersonVO.getLastName());
		assertEquals("Brasil", persistedPersonVO.getAddress());
		assertEquals("M", persistedPersonVO.getGender());
		assertTrue(persistedPersonVO.getEnabled());
	}

	@Test
	@Order(4)
	void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {

		String content = 
			given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_NOTALLOWED)
					.pathParam("id", personVO.getId())
				.when()
					.get("{id}")
				.then()
					.statusCode(403)
				.extract()
					.body()
						.asString();

		assertNotNull(content);
		assertEquals("Invalid CORS request", content);
	}

	@Test
	@Order(5)
	void testDisablePerson() throws JsonMappingException, JsonProcessingException {

		String content = 
			given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
					.pathParam("id", personVO.getId())
				.when()
					.patch("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();

		PersonVO persistedPersonVO = objectMapper.readValue(content, PersonVO.class);
		
		assertNotNull(persistedPersonVO);

		assertNotNull(persistedPersonVO.getId());
		assertNotNull(persistedPersonVO.getFirstName());
		assertNotNull(persistedPersonVO.getLastName());
		assertNotNull(persistedPersonVO.getAddress());
		assertNotNull(persistedPersonVO.getGender());
		assertNotNull(persistedPersonVO.getEnabled());

		assertEquals(personVO.getId(), persistedPersonVO.getId());
		assertEquals("Lorem", persistedPersonVO.getFirstName());
		assertEquals("Ipsum", persistedPersonVO.getLastName());
		assertEquals("Brasil", persistedPersonVO.getAddress());
		assertEquals("M", persistedPersonVO.getGender());
		assertFalse(persistedPersonVO.getEnabled());
	}

	@Test
	@Order(6)
	void testUpdate() throws JsonMappingException, JsonProcessingException {
		personVO.setFirstName("Lorem Ipsum");
		personVO.setLastName("Dolor Sit");
		personVO.setAddress("Argentina");
		personVO.setGender("F");

		String content = 
			given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
					.body(personVO)
				.when()
					.put()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();

		PersonVO updatedPersonVO = objectMapper.readValue(content, PersonVO.class);
		
		assertNotNull(updatedPersonVO);

		assertNotNull(updatedPersonVO.getId());
		assertNotNull(updatedPersonVO.getFirstName());
		assertNotNull(updatedPersonVO.getLastName());
		assertNotNull(updatedPersonVO.getAddress());
		assertNotNull(updatedPersonVO.getGender());
		assertNotNull(updatedPersonVO.getEnabled());

		assertEquals(personVO.getId(), updatedPersonVO.getId());
		assertEquals("Lorem Ipsum", updatedPersonVO.getFirstName());
		assertEquals("Dolor Sit", updatedPersonVO.getLastName());
		assertEquals("Argentina", updatedPersonVO.getAddress());
		assertEquals("F", updatedPersonVO.getGender());
		assertFalse(updatedPersonVO.getEnabled());

		personVO = updatedPersonVO;
	}

	@Test
	@Order(7)
	void testDelete() throws JsonMappingException, JsonProcessingException {

		given()
			.spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
				.pathParam("id", personVO.getId())
			.when()
				.delete("{id}")
			.then()
				.statusCode(204)
			.extract()
				.body()
					.asString();
	}

	@Test
	@Order(8)
	void testFindAll() throws JsonMappingException, JsonProcessingException {

		String content = 
			given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
				.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();

		List<PersonVO> listPersonVO = objectMapper.readValue(content, new TypeReference<List<PersonVO>>() {});
		
		PersonVO foundPerson1 = listPersonVO.get(0);
		
		assertNotNull(foundPerson1);
		assertNotNull(foundPerson1.getId());
		assertNotNull(foundPerson1.getFirstName());
		assertNotNull(foundPerson1.getLastName());
		assertNotNull(foundPerson1.getAddress());
		assertNotNull(foundPerson1.getGender());
		assertNotNull(foundPerson1.getEnabled());

		assertEquals(1, foundPerson1.getId());
		assertEquals("Lionel", foundPerson1.getFirstName());
		assertEquals("Messi", foundPerson1.getLastName());
		assertEquals("Argentina", foundPerson1.getAddress());
		assertEquals("M", foundPerson1.getGender());
		assertTrue(foundPerson1.getEnabled());

		PersonVO foundPerson3 = listPersonVO.get(2);
		
		assertNotNull(foundPerson3);
		assertNotNull(foundPerson3.getId());
		assertNotNull(foundPerson3.getFirstName());
		assertNotNull(foundPerson3.getLastName());
		assertNotNull(foundPerson3.getAddress());
		assertNotNull(foundPerson3.getGender());
		assertNotNull(foundPerson3.getEnabled());

		assertEquals(3, foundPerson3.getId());
		assertEquals("Cristiano", foundPerson3.getFirstName());
		assertEquals("Ronaldo", foundPerson3.getLastName());
		assertEquals("Portugal", foundPerson3.getAddress());
		assertEquals("M", foundPerson3.getGender());
		assertTrue(foundPerson3.getEnabled());
	}

	@Test
	@Order(9)
	void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
			.setBasePath("/api/person/v1")
			.setPort(TestConfigs.SERVER_PORT)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();

		given()
			.spec(specificationWithoutToken)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
			.when()
				.get()
			.then()
				.statusCode(403)
			.extract()
				.body()
					.asString();
	}
	
	private void mockPersonVO() {
		personVO.setFirstName("Lorem");
		personVO.setLastName("Ipsum");
		personVO.setAddress("Brasil");
		personVO.setGender("M");
		personVO.setEnabled(Boolean.TRUE);
	}

}
