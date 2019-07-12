package de.ffm.rka.rkareddit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.util.BeanUtil;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringSecurityTestConfig.class)
public class LinkControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private LinkController linkController;

	@Before
	public void setup() {
    
        MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(linkController)
										.setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
										.build();
	}

	@Test
	public void shouldReturnAllLinks() throws Exception {
		this.mockMvc.perform(get("/links/")).andDo(print()).andExpect(status().isOk());
	}

	/**
	 * @author RKA
	 * testing new post of valid comment
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void postNewComment() throws Exception {

	    	this.mockMvc.perform(MockMvcRequestBuilders.post("/links/link/comments")
														.contentType(MediaType.APPLICATION_FORM_URLENCODED)
														.param("link.linkId", "1")
														.param("commentText", "hallo kommentar"))
	    					.andDo(print())
							.andExpect(status().is3xxRedirection())
							.andExpect(redirectedUrl("/links/link/1"))
							.andExpect(flash().attributeExists("success"));
	}
	    	
	/**
	 * @author RKA
	 * testing new post of valid comment
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void rejectNewInvalidComment() throws Exception {

	    	this.mockMvc.perform(MockMvcRequestBuilders.post("/links/link/comments")
														.contentType(MediaType.APPLICATION_FORM_URLENCODED)
														.param("commentText", "hallo kommentar"))
	    					.andDo(print())
							.andExpect(status().is4xxClientError());

	}

}
