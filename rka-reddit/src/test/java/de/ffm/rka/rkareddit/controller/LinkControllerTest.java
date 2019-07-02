package de.ffm.rka.rkareddit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.ffm.rka.rkareddit.controller.rest.ProfileMetaDataController;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.util.BeanUtil;



@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringSecurityTestConfig.class
)
public class LinkControllerTest {

	
    private MockMvc mockMvc;
    private SpringSecurityTestConfig testConfig;
	
    @Autowired
	private LinkController linkController;
    
    
    @Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(linkController).setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver()).build();
		testConfig = BeanUtil.getBeanFromContext(SpringSecurityTestConfig.class);  
	}
	
	@Test
	public void shouldReturnAllLinks() throws Exception {
		this.mockMvc.perform(get("/links/"))
					.andDo(print())
					.andExpect(status().isOk());		
	}
	@Ignore
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void postNewComment() throws Exception {
		this.mockMvc.perform(post("/links/link/comments"))
					.andDo(print())
					.andExpect(status().isOk());		
	}
	 
	 
	
}
