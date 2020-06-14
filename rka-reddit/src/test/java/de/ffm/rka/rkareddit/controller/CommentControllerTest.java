package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.util.BeanUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
/** spring-test-support is enabled */
@RunWith(SpringRunner.class) 
/** enable of application-context */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringSecurityTestConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Transactional
public class CommentControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;
	
	private EntityManager entityManager;
	
	@Before
	public void setup() {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
										.apply(springSecurity())
										.build();
		entityManager = BeanUtil.getBeanFromContext(EntityManager.class);
	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void shouldReturnAllLinks() throws Exception {
		UserDTO userDto = UserDTO.builder()
								.firstName("baruc-david")
								.secondName("rka")
								.build();
		List<Integer> pages = Arrays.asList(new Integer[] {1,2});
		MvcResult result =  this.mockMvc.perform(get("/links/"))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(model().attribute("pageNumbers", pages))
					.andReturn();
		UserDTO usr = (UserDTO) result.getModelAndView().getModel().get("userDto");
    	assertEquals(userDto.getFullName(), usr.getFullName());
	}
	
	@Test
	@WithAnonymousUser
	public void shouldReturnAllLinksForAnonymous() throws Exception {

		List<Integer> pages = Arrays.asList(new Integer[] {1,2});
		MvcResult result = this.mockMvc.perform(get("/links/"))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(model().attribute("pageNumbers", pages))
					.andReturn();
		assertTrue(!result.getResponse().getContentAsString().contains("Submit Link"));
	}
	
	@Test
	public void shouldReturnAllLinksForCURL() throws Exception {
		List<Integer> pages = Arrays.asList(new Integer[] {1,2});
		MvcResult result = this.mockMvc.perform(get("/links/"))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(model().attribute("pageNumbers", pages))
					.andReturn();
		assertTrue(!result.getResponse().getContentAsString().contains("Submit Link"));
	}

	/**
	 * @author RKA
	 * testing new post of valid comment
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void postNewComment() throws Exception {

	    	this.mockMvc.perform(MockMvcRequestBuilders.post("/comments/comment")
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
	 * testing new comment with invalid link as Autheticated user
	 * without suitable link
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void rejectCommentWithoutSuitableLinkId() throws Exception {

	    	this.mockMvc.perform(MockMvcRequestBuilders.post("/comments/comment")
														.contentType(MediaType.APPLICATION_FORM_URLENCODED)
														.param("commentText", "hallo kommentar"))
	    					.andDo(print())
							.andExpect(status().is4xxClientError())
							.andExpect(view().name("error/basicError"));
	}
	
	@Test
	public void rejectCommentWithoutSuitableLinkIdAsUnautheticated() throws Exception {

	    	this.mockMvc.perform(MockMvcRequestBuilders.post("/comments/comment")
														.contentType(MediaType.APPLICATION_FORM_URLENCODED)
														.param("commentText", "hallo kommentar"))
	    					.andDo(print())
							.andExpect(status().is(401));
	}
}
