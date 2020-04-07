package de.ffm.rka.rkareddit.controller;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.ffm.rka.rkareddit.exception.GlobalControllerAdvisor;
import de.ffm.rka.rkareddit.interceptor.ApplicationHandlerInterceptor;
import de.ffm.rka.rkareddit.rest.controller.TagController;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringSecurityTestConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class TagControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private TagController tagController;

	@Autowired
	private GlobalControllerAdvisor globalControllerAdvice;

	
	/**
	 * Using Standalone-Configuration, no SpringApplicationContext.
	 * All additional elements (filter, advices, interceptors) must be set manualy
	 */
	@Before
	public void setup() {

        MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(tagController)
										.addInterceptors(new ApplicationHandlerInterceptor())
										.setControllerAdvice(globalControllerAdvice)		
										.setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver(), new PageableHandlerMethodArgumentResolver())
										.build();
	}
	
	/**
	 * @author RKA
	 * testing new post of valid comment
	 */

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void postNewComment() throws Exception {

		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/tags/tag/create")
																	.contentType(MediaType.APPLICATION_FORM_URLENCODED)
																	.param("java8", ""))
	    					.andDo(print())
							.andReturn();
		assertNotEquals("1", result.getResponse().getContentAsString().split(":")[2]);
	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void postAvailibleTag() throws Exception {

		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/tags/tag/create")
														.contentType(MediaType.APPLICATION_FORM_URLENCODED)
														.param("java", ""))
	    					.andDo(print())
							.andReturn();
		assertNotEquals("1", result.getResponse().getContentAsString().split(":")[2]);
	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void testSchouldNotDeleteRelatedTag() throws Exception {

		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/tags/tag/deleteTag/{tagId}","1")
																	.contentType(MediaType.APPLICATION_JSON))
							.andDo(print())
							.andExpect(status().isOk())
							.andReturn();
		assertEquals("", result.getResponse().getContentAsString());
	}


	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void testSchouldNotDeleteNotAvailibleTag() throws Exception {

		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/tags/tag/deleteTag/{tagId}","101")
																	.contentType(MediaType.APPLICATION_JSON))
							.andDo(print())
							.andExpect(status().isOk())
							.andReturn();
		assertEquals("", result.getResponse().getContentAsString());
	}
	
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void testSchouldDeleteNotRelatedTag() throws Exception {

		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/tags/tag/deleteTag/{tagId}","6")
																	.contentType(MediaType.APPLICATION_JSON))
							.andDo(print())
							.andExpect(status().isOk())
							.andReturn();
		assertEquals("6", result.getResponse().getContentAsString());
	}
}
