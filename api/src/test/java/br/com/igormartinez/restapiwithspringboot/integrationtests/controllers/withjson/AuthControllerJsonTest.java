package br.com.igormartinez.restapiwithspringboot.integrationtests.controllers.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.igormartinez.restapiwithspringboot.configs.TestConfigs;
import br.com.igormartinez.restapiwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.AccountCredentialsVO;
import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.TokenVO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class AuthControllerJsonTest extends AbstractIntegrationTest {
    private static TokenVO tokenVO;

    @Test
	@Order(1)
	void testSignin() {
		AccountCredentialsVO user = new AccountCredentialsVO("igormartinez", "admin1234");

		tokenVO = 
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
								.as(TokenVO.class);

		assertNotNull(tokenVO.getAccessToken());
        assertNotNull(tokenVO.getRefreshToken());
	}

    @Test
	@Order(2)
    void testRefreshToken(){
        
		TokenVO refreshedTokenVO = 
			given()
				.basePath("/auth/refresh")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				    .pathParam("username", tokenVO.getUsername())
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken())
				.when()
				    .put("{username}")
				.then()
					.statusCode(200)
				.extract()
					.body()
					    .as(TokenVO.class);

		assertNotNull(refreshedTokenVO.getAccessToken());
        assertNotNull(refreshedTokenVO.getRefreshToken());
    }
}
