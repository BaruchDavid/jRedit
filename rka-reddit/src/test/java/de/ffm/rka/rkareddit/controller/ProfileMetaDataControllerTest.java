package de.ffm.rka.rkareddit.controller;

import static org.hamcrest.CoreMatchers.containsString;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.ffm.rka.rkareddit.controller.rest.ProfileMetaDataController;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProfileMetaDataControllerTest {

	
    private MockMvc mockMvc;
        
    @Autowired
	private ProfileMetaDataController profileMetaDataController;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(profileMetaDataController).build();
	}
	
	 @Test
	public void shouldReturnDefaultMessage() throws Exception {
		this.mockMvc.perform(get("/profile/information/content"))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(content().string("[\"5\",\"9\",\"13. Juni 2019\"]"));		
	}
	
}
