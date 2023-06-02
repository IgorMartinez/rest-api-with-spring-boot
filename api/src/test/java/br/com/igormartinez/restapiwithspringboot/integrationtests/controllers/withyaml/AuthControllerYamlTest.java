package br.com.igormartinez.restapiwithspringboot.integrationtests.controllers.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.igormartinez.restapiwithspringboot.configs.TestConfigs;
import br.com.igormartinez.restapiwithspringboot.integrationtests.controllers.withyaml.mapper.YAMLMapper;
import br.com.igormartinez.restapiwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.AccountCredentialsVO;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.TokenVO;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class AuthControllerYamlTest extends AbstractIntegrationTest {

	private static YAMLMapper objectMapper;
    private static TokenVO tokenVO;

	@BeforeAll
	public static void setup() {
		objectMapper = new YAMLMapper();
	}

    @Test
	@Order(1)
	void testSignin() {
		AccountCredentialsVO user = new AccountCredentialsVO("igormartinez", "admin1234");

		tokenVO = 
			given()
				.config(RestAssuredConfig
					.config()
					.encoderConfig(EncoderConfig.encoderConfig()
					.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT))
				)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.body(user, objectMapper)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class, objectMapper);

		assertNotNull(tokenVO.getAccessToken());
        assertNotNull(tokenVO.getRefreshToken());
	}

    @Test
	@Order(2)
    void testRefreshToken(){
        
		TokenVO refreshedTokenVO = 
			given()
				.config(RestAssuredConfig
					.config()
					.encoderConfig(EncoderConfig.encoderConfig()
					.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT))
				)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
				.basePath("/auth/refresh")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_YAML)
				    .pathParam("username", tokenVO.getUsername())
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken())
				.when()
				    .put("{username}")
				.then()
					.statusCode(200)
				.extract()
					.body()
					    .as(TokenVO.class, objectMapper);

		assertNotNull(refreshedTokenVO.getAccessToken());
        assertNotNull(refreshedTokenVO.getRefreshToken());
    }
}
