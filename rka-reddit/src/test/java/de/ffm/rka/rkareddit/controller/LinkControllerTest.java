package de.ffm.rka.rkareddit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import de.ffm.rka.rkareddit.exception.GlobalControllerAdvice;
import de.ffm.rka.rkareddit.interceptor.AutheticationInterceptor;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringSecurityTestConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class LinkControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private LinkController linkController;

	@Autowired
	private GlobalControllerAdvice globalControllerAdvice;
	
	/**
	 * Using Standalone-Configuration, no SpringApplicationContext.
	 * All additional elements (filter, advices, interceptors) must be set manualy
	 */
	@Before
	public void setup() {

        MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(linkController)
										.addInterceptors(new AutheticationInterceptor())
										.setControllerAdvice(globalControllerAdvice)		
										.setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver(), new PageableHandlerMethodArgumentResolver())
										.build();
	}

	@Test
	@Ignore
	public void shouldReturnAllLinks() throws Exception {

		List pages = Arrays.asList(new Integer[] {1,2});
		this.mockMvc.perform(get("/links/"))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(model().attribute("pageNumbers", pages));
	}

	/**
	 * @author RKA
	 * testing new post of valid comment
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	@Ignore
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
	 * without suitable link
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	@Ignore
	public void rejectCommentWithoutSuitableLinkId() throws Exception {

	    	this.mockMvc.perform(MockMvcRequestBuilders.post("/links/link/comments")
														.contentType(MediaType.APPLICATION_FORM_URLENCODED)
														.param("commentText", "hallo kommentar"))
	    					.andDo(print())
							.andExpect(status().is4xxClientError());
	}
	
	/**
	 * @author RKA
	 * testing of not existing page by anonymous-user
	 */
	@Test
	//@Ignore
	//@WithUserDetails("romakapt@gmx.de")
	public void pageNotFound() throws Exception {
		String invalidPage = UUID.randomUUID().toString();
		this.mockMvc.perform(get("/links/link/".concat(invalidPage)))
					.andDo(print())
					.andExpect(status().is4xxClientError())
					.andExpect(view().name("pageNotFound"));
	}
}
