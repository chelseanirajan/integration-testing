package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.security.SecurityConstants;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerIntegrationTest {

    @Value("${server.port}")
    private int portNo;

    @LocalServerPort
    private int randomPort;

    @Autowired
    TestRestTemplate restTemplate;
    private String authorizationToken;

    @Test
    @Order(0)
    void testUserCreate_whenUserDetailsCreate_returnCreatedDetails() throws JSONException {
        //arrange
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstName", "Nirajan");
        jsonObject.put("lastName", "Karki");
        jsonObject.put("email", "nirajan12@gmail.com");
        jsonObject.put("password", "nirajan@123");
        jsonObject.put("repeatPassword", "nirajan@123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);


        //act
        ResponseEntity<UserRest> userRestResponseEntity = restTemplate.postForEntity("/users", httpEntity, UserRest.class);
        UserRest userRest = userRestResponseEntity.getBody();

        //assert
        Assertions.assertEquals(HttpStatus.OK, userRestResponseEntity.getStatusCode());
        Assertions.assertEquals(jsonObject.get("firstName"), userRest.getFirstName(), "Return Firstname seems  to be empty.");
        Assertions.assertEquals(jsonObject.get("lastName"), userRest.getLastName(), "Return Lastname seems to be empty");
        Assertions.assertEquals(jsonObject.get("email"), userRest.getEmail(), "Return email seems to be empty");
        Assertions.assertFalse(userRest.getUserId().trim().isEmpty(), "Return user id seems to be empty");

    }
    @Test
    @Order(1)
    @DisplayName("Get /users test")
    void testGetUsers_WhenMissedJWTToken_return403Forbidden(){
        //arrange

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", "Application/Json");

        HttpEntity<String> httpEntity = new HttpEntity(null, httpHeaders);

        //act
        ResponseEntity<List<UserRest>> userList = restTemplate.exchange("/users", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<UserRest>>() {
        });

        //assert
        Assertions.assertEquals(HttpStatus.FORBIDDEN, userList.getStatusCode());
    }
    @Test
    @Order(2)
    @DisplayName("Get /User login")
    void testUserlogin_whenUserLogin_returnUserId() throws JSONException {
        // arrange
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", "nirajan12@gmail.com");
        jsonObject.put("password", "nirajan@123");

        HttpEntity<String> httpEntity = new HttpEntity(jsonObject.toString());

        //act
        ResponseEntity responseEntity = restTemplate.postForEntity("/users/login",httpEntity,null);

        authorizationToken = responseEntity.getHeaders().getValuesAsList(SecurityConstants.HEADER_STRING).get(0);
        // assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(authorizationToken, "Response should contain the JWT token");
        Assertions.assertNotNull(responseEntity.getHeaders().getValuesAsList("UserID").get(0), "Response should contain the user ID");

    }

    @Test
    @DisplayName("Get /all users")
    @Order(3)
    void testCreatedUser_whenJWTTokenProvided_returnCreatedUsers(){
        //arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity<String> httpEntity = new HttpEntity(headers);

        //act
        ResponseEntity<List<UserRest>> response = restTemplate.exchange("/users", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<UserRest>>() {
        });

        //assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.getBody().size() == 1);
    }
}
