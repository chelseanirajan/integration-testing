package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.service.UsersService;
import com.appsdeveloperblog.tutorials.junit.shared.UserDto;
import com.appsdeveloperblog.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;

@WebMvcTest(controllers = UsersController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UsersService usersService;
    UserDetailsRequestModel userDetailsRequestModel;

    @BeforeEach
    void setup(){
        userDetailsRequestModel = new UserDetailsRequestModel();


        userDetailsRequestModel.setLastName("Karki");
        userDetailsRequestModel.setEmail("nirajan@gmail.com");
        userDetailsRequestModel.setPassword("nirajan@123");
        userDetailsRequestModel.setRepeatPassword("nirajan@123");
    }
    @Test
    void testCreateUser_whenUserCreated_returnCreatedUser() throws Exception {
        //arrange

        userDetailsRequestModel.setFirstName("Nirajan");
        UserDto userDto = new ModelMapper().map(userDetailsRequestModel, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());

        when(usersService.createUser(any(UserDto.class))).thenReturn(userDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));


        //act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UserRest userRest = new ObjectMapper().readValue(contentAsString, UserRest.class);

        //assert


        Assertions.assertEquals(userDetailsRequestModel.getFirstName(), userRest.getFirstName(), "First name should be matched");
        Assertions.assertEquals(userDetailsRequestModel.getLastName(), userRest.getLastName(), "Last name should be matched");
        Assertions.assertEquals(userDetailsRequestModel.getEmail(), userRest.getEmail(), "Email should be matched");
        Assertions.assertFalse(userRest.getUserId().isEmpty());
    }
    @Test
    @DisplayName("Bad request test")
    void testUserTest_firstNameIsEmpty_shouldReturnBadException() throws Exception {
        //arrange
        userDetailsRequestModel.setFirstName("");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));

        //act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(),mvcResult.getResponse().getStatus());
    }
}
