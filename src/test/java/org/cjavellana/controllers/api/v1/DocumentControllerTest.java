package org.cjavellana.controllers.api.v1;

import org.cjavellana.services.UserInfoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(DocumentController.class)
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserInfoService userInfoService;

    @Before
    public void setup() {
        given(userInfoService.findAll())
                .willReturn(new ArrayList<>());
    }

    @Test
    public void getDemoTest() throws Exception {
        mockMvc.perform(get("/api/v1/documents"))
                .andExpect(jsonPath("$.status", is("ok")))
                .andExpect(status().isOk());
    }

    @Test
    public void putDemo() throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("content", asList("test"));

        MockHttpServletRequestBuilder requestBuilder = put("/api/v1/document/{id}", 1)
                .params(map);
        mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.status", is("test")))
                .andExpect(status().isOk());

    }

}