package br.com.igormartinez.restapiwithspringboot.integrationtests.controller.withxml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import br.com.igormartinez.restapiwithspringboot.configs.TestConfigs;
import br.com.igormartinez.restapiwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.AccountCredentialsVO;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.BookVO;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.TokenVO;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.pagedmodels.PagedModelBookVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
	private static XmlMapper objectMapper;
	private static BookVO bookVO;

    private final String MOCK_BOOK_AUTHOR = "Lorem Ipsum";
    private final Date MOCK_BOOK_LAUCH_DATE = new Date(1663668598);
    private final String MOCK_BOOK_TITLE = "Dolor Sit";
    private final Double MOCK_BOOK_PRICE = 32.02;
    private final String MOCK_BOOK_AUTHOR_UPDATED = "Lorem Ipsum Dolor Sit";
    private final Date MOCK_BOOK_LAUCH_DATE_UPDATED = new Date(1785698454);
    private final String MOCK_BOOK_TITLE_UPDATED = "Amet, consectetur adipiscing elit";
    private final Double MOCK_BOOK_PRICE_UPDATED = 1582.85;

    @BeforeAll
	public static void setup() {
		objectMapper = new XmlMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		bookVO = new BookVO();
	}

    @Test
	@Order(0)
	void authorization() {
		AccountCredentialsVO user = new AccountCredentialsVO("igormartinez", "admin1234");

		String accessToken = 
			given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_XML)
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
			.setBasePath("/api/book/v1")
			.setPort(TestConfigs.SERVER_PORT)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
	}

    @Test
	@Order(1)
	void testCreate() throws JsonMappingException, JsonProcessingException {
		mockBookVO();

		String content = 
			given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
					.body(bookVO)
				.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();

		BookVO createdBookVO = objectMapper.readValue(content, BookVO.class);
		bookVO = createdBookVO;
		
		assertNotNull(createdBookVO);

		assertNotNull(createdBookVO.getId());
        assertNotNull(createdBookVO.getAuthor());
        assertNotNull(createdBookVO.getLaunchDate());
        assertNotNull(createdBookVO.getTitle());
        assertNotNull(createdBookVO.getPrice());

		assertTrue(createdBookVO.getId() > 0);
		assertEquals(MOCK_BOOK_AUTHOR, createdBookVO.getAuthor());
		assertEquals(MOCK_BOOK_LAUCH_DATE, createdBookVO.getLaunchDate());
		assertEquals(MOCK_BOOK_TITLE, createdBookVO.getTitle());
		assertEquals(MOCK_BOOK_PRICE, createdBookVO.getPrice());
	}

    @Test
	@Order(2)
	void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {

		String content = 
			given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_NOTALLOWED)
					.body(bookVO)
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
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
					.pathParam("id", bookVO.getId())
				.when()
					.get("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();

		BookVO persistedBookVO = objectMapper.readValue(content, BookVO.class);
		
		assertNotNull(persistedBookVO);

		assertNotNull(persistedBookVO.getId());
		assertNotNull(persistedBookVO.getAuthor());
		assertNotNull(persistedBookVO.getLaunchDate());
		assertNotNull(persistedBookVO.getTitle());
		assertNotNull(persistedBookVO.getPrice());

		assertEquals(bookVO.getId(), persistedBookVO.getId());
		assertEquals(MOCK_BOOK_AUTHOR, persistedBookVO.getAuthor());
		assertEquals(MOCK_BOOK_LAUCH_DATE, persistedBookVO.getLaunchDate());
		assertEquals(MOCK_BOOK_TITLE, persistedBookVO.getTitle());
		assertEquals(MOCK_BOOK_PRICE, persistedBookVO.getPrice());
	}

	@Test
	@Order(4)
	void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {

		String content = 
			given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_NOTALLOWED)
					.pathParam("id", bookVO.getId())
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
		bookVO.setAuthor(MOCK_BOOK_AUTHOR_UPDATED);
        bookVO.setLaunchDate(MOCK_BOOK_LAUCH_DATE_UPDATED);
        bookVO.setTitle(MOCK_BOOK_TITLE_UPDATED);
        bookVO.setPrice(MOCK_BOOK_PRICE_UPDATED);

		String content = 
			given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
					.body(bookVO)
				.when()
					.put()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();

		BookVO updatedBookVO = objectMapper.readValue(content, BookVO.class);
		
		assertNotNull(updatedBookVO);

		assertNotNull(updatedBookVO.getId());
        assertNotNull(updatedBookVO.getAuthor());
        assertNotNull(updatedBookVO.getLaunchDate());
        assertNotNull(updatedBookVO.getTitle());
        assertNotNull(updatedBookVO.getPrice());

		assertEquals(bookVO.getId(), updatedBookVO.getId());
		assertEquals(MOCK_BOOK_AUTHOR_UPDATED, updatedBookVO.getAuthor());
		assertEquals(MOCK_BOOK_LAUCH_DATE_UPDATED, updatedBookVO.getLaunchDate());
		assertEquals(MOCK_BOOK_TITLE_UPDATED, updatedBookVO.getTitle());
		assertEquals(MOCK_BOOK_PRICE_UPDATED, updatedBookVO.getPrice());

		bookVO = updatedBookVO;
	}

	@Test
	@Order(6)
	void testDelete() throws JsonMappingException, JsonProcessingException {

		given()
			.spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.accept(TestConfigs.CONTENT_TYPE_XML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
				.pathParam("id", bookVO.getId())
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
	void testFindAll() throws JsonMappingException, JsonProcessingException, ParseException {

		String content = 
			given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
				.queryParams("page", 1, "size", 5, "direction", "asc")
				.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();

		PagedModelBookVO pagedModelBookVO = objectMapper.readValue(content, PagedModelBookVO.class);
		List<BookVO> listBookVO = pagedModelBookVO.getContent();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		BookVO foundBookVOPosition0 = listBookVO.get(0);
		
		assertNotNull(foundBookVOPosition0);
		assertNotNull(foundBookVOPosition0.getId());
        assertNotNull(foundBookVOPosition0.getAuthor());
        assertNotNull(foundBookVOPosition0.getLaunchDate());
        assertNotNull(foundBookVOPosition0.getTitle());
        assertNotNull(foundBookVOPosition0.getPrice());

		assertEquals(11, foundBookVOPosition0.getId());
		assertEquals("Roger S. Pressman", foundBookVOPosition0.getAuthor());
		assertEquals(simpleDateFormat.parse("2017-11-07 15:09:01.674"), foundBookVOPosition0.getLaunchDate());
		assertEquals("Engenharia de Software: uma abordagem profissional", foundBookVOPosition0.getTitle());
		assertEquals(56.00, foundBookVOPosition0.getPrice());

		BookVO foundBookVOPosition2 = listBookVO.get(2);
		
		assertNotNull(foundBookVOPosition2);
		assertNotNull(foundBookVOPosition2.getId());
        assertNotNull(foundBookVOPosition2.getAuthor());
        assertNotNull(foundBookVOPosition2.getLaunchDate());
        assertNotNull(foundBookVOPosition2.getTitle());
        assertNotNull(foundBookVOPosition2.getPrice());

		assertEquals(15, foundBookVOPosition2.getId());
		assertEquals("Aguinaldo Aragon Fernandes e Vladimir Ferraz de Abreu", foundBookVOPosition2.getAuthor());
		assertEquals(simpleDateFormat.parse("2017-11-07 15:09:01.674"), foundBookVOPosition2.getLaunchDate());
		assertEquals("Implantando a governança de TI", foundBookVOPosition2.getTitle());
		assertEquals(54.00, foundBookVOPosition2.getPrice());

        BookVO foundBookVOPosition4 = listBookVO.get(4);
		
		assertNotNull(foundBookVOPosition4);
		assertNotNull(foundBookVOPosition4.getId());
        assertNotNull(foundBookVOPosition4.getAuthor());
        assertNotNull(foundBookVOPosition4.getLaunchDate());
        assertNotNull(foundBookVOPosition4.getTitle());
        assertNotNull(foundBookVOPosition4.getPrice());

		assertEquals(4, foundBookVOPosition4.getId());
		assertEquals("Crockford", foundBookVOPosition4.getAuthor());
		assertEquals(simpleDateFormat.parse("2017-11-07 15:09:01.674"), foundBookVOPosition4.getLaunchDate());
		assertEquals("JavaScript", foundBookVOPosition4.getTitle());
		assertEquals(67.00, foundBookVOPosition4.getPrice());
	}

	@Test
	@Order(8)
	void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
			.setBasePath("/api/book/v1")
			.setPort(TestConfigs.SERVER_PORT)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();

		given()
			.spec(specificationWithoutToken)
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.accept(TestConfigs.CONTENT_TYPE_XML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
			.when()
				.get()
			.then()
				.statusCode(403)
			.extract()
				.body()
					.asString();
	}


    private void mockBookVO() {
        bookVO.setAuthor(MOCK_BOOK_AUTHOR);
        bookVO.setLaunchDate(MOCK_BOOK_LAUCH_DATE);
        bookVO.setTitle(MOCK_BOOK_TITLE);
        bookVO.setPrice(MOCK_BOOK_PRICE);
	}
}
