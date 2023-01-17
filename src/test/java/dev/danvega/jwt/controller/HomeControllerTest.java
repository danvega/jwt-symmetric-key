package dev.danvega.jwt.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void shouldReturnUnauthorizedWithNoJwt() throws Exception {
        this.mvc.perform(get("/")).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWithInvalidJwt() throws Exception {
        this.mvc.perform(get("/").header(HttpHeaders.AUTHORIZATION,"Bearer ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnWelcomeMessageWithValidJwt() throws Exception {
        MvcResult result = this.mvc.perform(post("/api/auth/token").with(httpBasic("dvega", "password"))).andReturn();
        String jwt = result.getResponse().getContentAsString();
        assertThat(jwt).isNotEmpty();

        MvcResult response = this.mvc.perform(get("/").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("Hello, dvega",response.getResponse().getContentAsString());
    }

}