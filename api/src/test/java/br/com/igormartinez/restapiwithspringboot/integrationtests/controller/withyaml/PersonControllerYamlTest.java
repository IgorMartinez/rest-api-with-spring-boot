package br.com.igormartinez.restapiwithspringboot.integrationtests.controller.withyaml;

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
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.igormartinez.restapiwithspringboot.configs.TestConfigs;
import br.com.igormartinez.restapiwithspringboot.integrationtests.controller.withyaml.mapper.YAMLMapper;
import br.com.igormartinez.restapiwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.AccountCredentialsVO;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.PersonVO;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.TokenVO;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.pagedmodels.PagedModelPersonVO;
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
	void testDisablePerson() throws JsonMappingException, JsonProcessingException {

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
					.patch("{id}")
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
	@Order(8)
	void testFindAll() throws JsonMappingException, JsonProcessingException {

		PagedModelPersonVO pagedModelPersonVO = 
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
				.queryParams("page", 3, "size", 10, "direction", "asc")
				.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(PagedModelPersonVO.class, objectMapper);

		List<PersonVO> listPersonVO = pagedModelPersonVO.getContent();
		
		PersonVO foundPersonPosition0 = listPersonVO.get(0);
		
		assertNotNull(foundPersonPosition0);
		assertNotNull(foundPersonPosition0.getId());
		assertNotNull(foundPersonPosition0.getFirstName());
		assertNotNull(foundPersonPosition0.getLastName());
		assertNotNull(foundPersonPosition0.getAddress());
		assertNotNull(foundPersonPosition0.getGender());
		assertNotNull(foundPersonPosition0.getEnabled());

		assertEquals(670, foundPersonPosition0.getId());
		assertEquals("Alic", foundPersonPosition0.getFirstName());
		assertEquals("Terbrug", foundPersonPosition0.getLastName());
		assertEquals("3 Eagle Crest Court", foundPersonPosition0.getAddress());
		assertEquals("M", foundPersonPosition0.getGender());
		assertTrue(foundPersonPosition0.getEnabled());

		PersonVO foundPersonPosition5 = listPersonVO.get(5);
		
		assertNotNull(foundPersonPosition5);
		assertNotNull(foundPersonPosition5.getId());
		assertNotNull(foundPersonPosition5.getFirstName());
		assertNotNull(foundPersonPosition5.getLastName());
		assertNotNull(foundPersonPosition5.getAddress());
		assertNotNull(foundPersonPosition5.getGender());
		assertNotNull(foundPersonPosition5.getEnabled());

		assertEquals(904, foundPersonPosition5.getId());
		assertEquals("Allegra", foundPersonPosition5.getFirstName());
		assertEquals("Dome", foundPersonPosition5.getLastName());
		assertEquals("57 Roxbury Pass", foundPersonPosition5.getAddress());
		assertEquals("F", foundPersonPosition5.getGender());
		assertTrue(foundPersonPosition5.getEnabled());

		PersonVO foundPersonPosition9 = listPersonVO.get(9);
		
		assertNotNull(foundPersonPosition9);
		assertNotNull(foundPersonPosition9.getId());
		assertNotNull(foundPersonPosition9.getFirstName());
		assertNotNull(foundPersonPosition9.getLastName());
		assertNotNull(foundPersonPosition9.getAddress());
		assertNotNull(foundPersonPosition9.getGender());
		assertNotNull(foundPersonPosition9.getEnabled());

		assertEquals(680, foundPersonPosition9.getId());
		assertEquals("Almeria", foundPersonPosition9.getFirstName());
		assertEquals("Curm", foundPersonPosition9.getLastName());
		assertEquals("34 Burrows Point", foundPersonPosition9.getAddress());
		assertEquals("F", foundPersonPosition9.getGender());
		assertFalse(foundPersonPosition9.getEnabled());
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

	@Test
	@Order(10)
	void testFindByFirstName() throws JsonMappingException, JsonProcessingException {

		PagedModelPersonVO pagedModelPersonVO = 
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
					.pathParam("firstName", "Lion")
					.queryParams("page", 0, "size", 5, "direction", "asc")
				.when()
					.get("findByFirstName/{firstName}")
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(PagedModelPersonVO.class, objectMapper);

		List<PersonVO> listPersonVO = pagedModelPersonVO.getContent();
		
		PersonVO foundPersonPosition0 = listPersonVO.get(0);
		
		assertEquals(1, listPersonVO.size());

		assertNotNull(foundPersonPosition0);
		assertNotNull(foundPersonPosition0.getId());
		assertNotNull(foundPersonPosition0.getFirstName());
		assertNotNull(foundPersonPosition0.getLastName());
		assertNotNull(foundPersonPosition0.getAddress());
		assertNotNull(foundPersonPosition0.getGender());
		assertNotNull(foundPersonPosition0.getEnabled());

		assertEquals(1, foundPersonPosition0.getId());
		assertEquals("Lionel", foundPersonPosition0.getFirstName());
		assertEquals("Messi", foundPersonPosition0.getLastName());
		assertEquals("Argentina", foundPersonPosition0.getAddress());
		assertEquals("M", foundPersonPosition0.getGender());
		assertTrue(foundPersonPosition0.getEnabled());
	}

	private void mockPersonVO() {
		personVO.setFirstName("Lorem");
		personVO.setLastName("Ipsum");
		personVO.setAddress("Brasil");
		personVO.setGender("M");
		personVO.setEnabled(Boolean.TRUE);
	}

}
