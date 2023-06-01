package br.com.igormartinez.restapiwithspringboot.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.igormartinez.restapiwithspringboot.configs.TestConfigs;
import br.com.igormartinez.restapiwithspringboot.integrationtests.controller.withyaml.mapper.YAMLMapper;
import br.com.igormartinez.restapiwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.AccountCredentialsVO;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.PersonVO;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerYamlTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static YAMLMapper objectMapper;
	private static PersonVO personVO;

	@BeforeAll
	public static void setup() {
		objectMapper = new YAMLMapper();

		personVO = new PersonVO();
	}

	@Test
	@Order(0)
	void authorization() {
		AccountCredentialsVO user = new AccountCredentialsVO("igormartinez", "admin1234");

		String accessToken = 
			given()
				.config(RestAssuredConfig
					.config()
					.encoderConfig(EncoderConfig.encoderConfig()
					.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT))
				)
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_YAML)
					.accept(TestConfigs.CONTENT_TYPE_YAML)
				.body(user, objectMapper)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class, objectMapper)
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

		PersonVO createdPersonVO = 
			given()
				.spec(specification)
				.config(RestAssuredConfig
					.config()
					.encoderConfig(EncoderConfig.encoderConfig()
					.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT))
				)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
					.body(personVO, objectMapper)
				.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(PersonVO.class, objectMapper);
		
		personVO = createdPersonVO;
		
		assertNotNull(createdPersonVO);

		assertNotNull(createdPersonVO.getId());
		assertNotNull(createdPersonVO.getFirstName());
		assertNotNull(createdPersonVO.getLastName());
		assertNotNull(createdPersonVO.getAddress());
		assertNotNull(createdPersonVO.getGender());

		assertTrue(createdPersonVO.getId() > 0);
		assertEquals("Lorem", createdPersonVO.getFirstName());
		assertEquals("Ipsum", createdPersonVO.getLastName());
		assertEquals("Brasil", createdPersonVO.getAddress());
		assertEquals("M", createdPersonVO.getGender());
	}

	@Test
	@Order(2)
	void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {

		String content = 
			given()
				.spec(specification)
				.config(RestAssuredConfig
					.config()
					.encoderConfig(EncoderConfig.encoderConfig()
					.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT))
				)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_NOTALLOWED)
					.body(personVO, objectMapper)
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

		PersonVO persistedPersonVO = 
			given()
				.spec(specification)
				.config(RestAssuredConfig
					.config()
					.encoderConfig(EncoderConfig.encoderConfig()
					.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT))
				)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
					.pathParam("id", personVO.getId())
				.when()
					.get("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(PersonVO.class, objectMapper);
		
		assertNotNull(persistedPersonVO);

		assertNotNull(persistedPersonVO.getId());
		assertNotNull(persistedPersonVO.getFirstName());
		assertNotNull(persistedPersonVO.getLastName());
		assertNotNull(persistedPersonVO.getAddress());
		assertNotNull(persistedPersonVO.getGender());

		assertEquals(personVO.getId(), persistedPersonVO.getId());
		assertEquals("Lorem", persistedPersonVO.getFirstName());
		assertEquals("Ipsum", persistedPersonVO.getLastName());
		assertEquals("Brasil", persistedPersonVO.getAddress());
		assertEquals("M", persistedPersonVO.getGender());
	}

	@Test
	@Order(4)
	void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {

		String content = 
			given()
				.spec(specification)
				.config(RestAssuredConfig
					.config()
					.encoderConfig(EncoderConfig.encoderConfig()
					.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT))
				)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
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
	void testUpdate() throws JsonMappingException, JsonProcessingException {
		personVO.setFirstName("Lorem Ipsum");
		personVO.setLastName("Dolor Sit");
		personVO.setAddress("Argentina");
		personVO.setGender("F");

		PersonVO updatedPersonVO = 
			given()
				.spec(specification)
				.config(RestAssuredConfig
					.config()
					.encoderConfig(EncoderConfig.encoderConfig()
					.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT))
				)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
					.body(personVO, objectMapper)
				.when()
					.put()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(PersonVO.class, objectMapper);
		
		assertNotNull(updatedPersonVO);

		assertNotNull(updatedPersonVO.getId());
		assertNotNull(updatedPersonVO.getFirstName());
		assertNotNull(updatedPersonVO.getLastName());
		assertNotNull(updatedPersonVO.getAddress());
		assertNotNull(updatedPersonVO.getGender());

		assertEquals(personVO.getId(), updatedPersonVO.getId());
		assertEquals("Lorem Ipsum", updatedPersonVO.getFirstName());
		assertEquals("Dolor Sit", updatedPersonVO.getLastName());
		assertEquals("Argentina", updatedPersonVO.getAddress());
		assertEquals("F", updatedPersonVO.getGender());

		personVO = updatedPersonVO;
	}

	@Test
	@Order(6)
	void testDelete() throws JsonMappingException, JsonProcessingException {

		given()
			.spec(specification)
			.config(RestAssuredConfig
				.config()
				.encoderConfig(EncoderConfig.encoderConfig()
				.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT))
			)
			.contentType(TestConfigs.CONTENT_TYPE_YAML)
			.accept(TestConfigs.CONTENT_TYPE_YAML)
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
	@Order(7)
	void testFindAll() throws JsonMappingException, JsonProcessingException {

		PersonVO arrayPersonVO[] = 
			given()
				.spec(specification)
				.config(RestAssuredConfig
					.config()
					.encoderConfig(EncoderConfig.encoderConfig()
					.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT))
				)
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
				.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(PersonVO[].class, objectMapper);

		List<PersonVO> listPersonVO = Arrays.asList(arrayPersonVO);
		
		PersonVO foundPerson1 = listPersonVO.get(0);
		
		assertNotNull(foundPerson1);
		assertNotNull(foundPerson1.getId());
		assertNotNull(foundPerson1.getFirstName());
		assertNotNull(foundPerson1.getLastName());
		assertNotNull(foundPerson1.getAddress());
		assertNotNull(foundPerson1.getGender());

		assertEquals(1, foundPerson1.getId());
		assertEquals("Lionel", foundPerson1.getFirstName());
		assertEquals("Messi", foundPerson1.getLastName());
		assertEquals("Argentina", foundPerson1.getAddress());
		assertEquals("M", foundPerson1.getGender());

		PersonVO foundPerson3 = listPersonVO.get(2);
		
		assertNotNull(foundPerson3);
		assertNotNull(foundPerson3.getId());
		assertNotNull(foundPerson3.getFirstName());
		assertNotNull(foundPerson3.getLastName());
		assertNotNull(foundPerson3.getAddress());
		assertNotNull(foundPerson3.getGender());

		assertEquals(3, foundPerson3.getId());
		assertEquals("Cristiano", foundPerson3.getFirstName());
		assertEquals("Ronaldo", foundPerson3.getLastName());
		assertEquals("Portugal", foundPerson3.getAddress());
		assertEquals("M", foundPerson3.getGender());
	}

	@Test
	@Order(8)
	void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
			.setBasePath("/api/person/v1")
			.setPort(TestConfigs.SERVER_PORT)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();

		given()
			.spec(specificationWithoutToken)
			.config(RestAssuredConfig
				.config()
				.encoderConfig(EncoderConfig.encoderConfig()
				.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT))
			)
			.contentType(TestConfigs.CONTENT_TYPE_YAML)
			.accept(TestConfigs.CONTENT_TYPE_YAML)
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
	}

}