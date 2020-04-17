package de.ffm.rka.rkareddit.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import org.junit.Before;
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
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.exception.GlobalControllerAdvisor;
import de.ffm.rka.rkareddit.interceptor.ApplicationHandlerInterceptor;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.util.BeanUtil;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringSecurityTestConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class LinkControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private LinkController linkController;

	@Autowired
	private GlobalControllerAdvisor globalControllerAdvice;

	private EntityManager entityManager;
	
	/**
	 * Using Standalone-Configuration, no SpringApplicationContext.
	 * All additional elements (filter, advices, interceptors) must be set manualy
	 */
	@Before
	public void setup() {

        MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(linkController)
										.addInterceptors(new ApplicationHandlerInterceptor())
										.setControllerAdvice(globalControllerAdvice)		
										.setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver(), new PageableHandlerMethodArgumentResolver())
										.build();
		entityManager = BeanUtil.getBeanFromContext(EntityManager.class);
	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void shouldReturnAllLinks() throws Exception {
		UserDTO userDto = UserDTO.builder()
								.firstName("baruc-david")
								.secondName("rka")
								.fullName("baruc-david rka")
								.build();
		List<Integer> pages = Arrays.asList(new Integer[] {1,2});
		this.mockMvc.perform(get("/links/"))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(model().attribute("pageNumbers", pages))
					.andExpect(model().attribute("user", userDto));
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
	 * without suitable link
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void rejectCommentWithoutSuitableLinkId() throws Exception {

	    	this.mockMvc.perform(MockMvcRequestBuilders.post("/links/link/comments")
														.contentType(MediaType.APPLICATION_FORM_URLENCODED)
														.param("commentText", "hallo kommentar"))
	    					.andDo(print())
							.andExpect(status().is4xxClientError());
	}
	

	    
	/**
	 * test for illegal link
	 */
	@Test
	public void illegalArguments() throws Exception {
		String invalidPage = UUID.randomUUID().toString();
		this.mockMvc.perform(get("/links/link/".concat(invalidPage)))
					.andDo(print())
					.andExpect(status().is4xxClientError())
					.andExpect(forwardedUrl("error/basicError"));

	}
	
	/**
	 * while reading one link
	 * empty comment will be created,
	 * which will be used for new comment
	 * @throws Exception
	 */
	@Test
	public void readLinkTest() throws Exception {
		Link currentLink = entityManager.find(Link.class, 1l);	
		this.mockMvc.perform(get("/links/link/1"))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(model().attribute("link", currentLink))
					.andExpect(model().attribute("success", false))
					.andExpect(view().name("link/link_view"));


	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void saveNewLinkTest() throws Exception {
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/links/link/create")
							.contentType(MediaType.APPLICATION_FORM_URLENCODED)
							.param("tags[0].tagName", "java12")
							.param("tags[1].tagName", "java13")
							.param("title", "welt.de")
							.param("url", "http://welt.de")
							)
  					.andDo(print())
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/links/link/12"))
					.andExpect(flash().attribute("success", true));	
    }
	
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void createNewLinkTest() throws Exception {
		Link link = new Link();
    	MvcResult result = this.mockMvc.perform(get("/links/link/create"))
  					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(forwardedUrl("link/submit"))
					.andReturn();
    	assertTrue(result.getModelAndView().getModel().get("newLink").toString().equals(link.toString()));
    }
	
	/**
	 * try to save link without authetication
	 */

	@Test
	public void saveNewLinkTestForUnknownUser() throws Exception {
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/links/link/create")
							.contentType(MediaType.APPLICATION_FORM_URLENCODED)
							.param("title", "welt.de")
							.param("url", "http://welt.de")
							.param("name", "java12")
							.param("name", "java13"))
  					.andDo(print())
					.andExpect(status().is(500))
					.andExpect(forwardedUrl("error/application"));	
    }
	
	/**
	 * get initial tags
	 */

	@Test
	public void getTags() throws Exception {
		List<String> expList = Arrays.asList("TypeScript","JavaScript","Delphi/Object Pascal");
		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/links/link/search")
							.contentType(MediaType.APPLICATION_FORM_URLENCODED)
							.param("search", "sc"))
					    	.andDo(print())
							.andExpect(status().isOk())
							.andReturn();
		String[] resultArray = result.getResponse().getContentAsString().replace("[","").replace("]","").replace("\"","").split(",");
		assertEquals(true, Stream.of(resultArray).allMatch(tag -> expList.contains(tag)));
    }
	
	/**
	 * create link as autheticated user
	 */
	@Test
	public void linkCreateAsUnautheticatedTest() throws Exception {
		
            this.mockMvc.perform(get("/links/link/create"))
					.andDo(print())
					.andExpect(forwardedUrl("error/application"));  
	}
}
