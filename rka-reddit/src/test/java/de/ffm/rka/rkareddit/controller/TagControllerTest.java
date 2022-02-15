package de.ffm.rka.rkareddit.controller;


import de.ffm.rka.rkareddit.exception.GlobalControllerAdvisor;
import de.ffm.rka.rkareddit.rest.controller.TagController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TagControllerTest extends MvcRequestSender{


	/**
	 * Using Standalone-Configuration, no SpringApplicationContext.
	 * All additional elements (filter, advices, interceptors) must be set manualy
	 */
	@Before
	public void setup() {

       /* MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(tagController)
										.addInterceptors(new ApplicationHandlerInterceptor())
										.setControllerAdvice(globalControllerAdvice)		
										.setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver(), new PageableHandlerMethodArgumentResolver())
										.build();*/
	}
	
	/**
	 * @author RKA
	 * testing new post of valid comment
	 */

	@Test
	@WithUserDetails("kaproma@yahoo.de")
	public void postNewComment() throws Exception {

		String body = "java8=";

		MvcResult result = super.performPostRequest("/tags/tag/create", body)
							.andReturn();
		assertNotEquals("1", result.getResponse().getContentAsString().split(":")[2]);
	}

	@Test
	@WithUserDetails("kaproma@yahoo.de")
	public void postAvailibleTag() throws Exception {
		String body = "java=";
		MvcResult result = super.performPostRequest("/tags/tag/create", body)
							.andReturn();
		assertNotEquals("1", result.getResponse().getContentAsString().split(":")[2]);
	}

	@Test
	@WithUserDetails("kaproma@yahoo.de")
	public void testSchouldNotDeleteRelatedTag() throws Exception {

		MvcResult result = super.mockMvc.perform(MockMvcRequestBuilders.delete("/tags/tag/deleteTag/{tagId}","1")
																	.contentType(MediaType.APPLICATION_JSON))
							.andDo(print())
							.andExpect(status().isOk())
							.andReturn();
		assertEquals("", result.getResponse().getContentAsString());
	}


	@Test
	@WithUserDetails("kaproma@yahoo.de")
	public void testSchouldNotDeleteNotAvailibleTag() throws Exception {

		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/tags/tag/deleteTag/{tagId}","101")
																	.contentType(MediaType.APPLICATION_JSON))
							.andDo(print())
							.andExpect(status().isOk())
							.andReturn();
		assertEquals("", result.getResponse().getContentAsString());
	}
	
	@Test
	@WithUserDetails("kaproma@yahoo.de")
	public void testSchouldDeleteNotRelatedTag() throws Exception {

		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/tags/tag/deleteTag/{tagId}","6")
																	.contentType(MediaType.APPLICATION_JSON))
							.andDo(print())
							.andExpect(status().isOk())
							.andReturn();
		assertEquals("6", result.getResponse().getContentAsString());
	}
}
