package de.ffm.rka.rkareddit.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.util.BeanUtil;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringSecurityTestConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class LinkControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;
	
	private EntityManager entityManager;
	
	/**
	 * Using Standalone-Configuration, no SpringApplicationContext.
	 * All additional elements (filter, advices, interceptors) must be set manualy
	 */
	@Before
	public void setup() {

        MockitoAnnotations.initMocks(this);
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
	 * testing new comment with invalid link as Autheticated user
	 * without suitable link
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void rejectCommentWithoutSuitableLinkId() throws Exception {

	    	this.mockMvc.perform(MockMvcRequestBuilders.post("/links/link/comments")
														.contentType(MediaType.APPLICATION_FORM_URLENCODED)
														.param("commentText", "hallo kommentar"))
	    					.andDo(print())
							.andExpect(status().is4xxClientError())
							.andExpect(view().name("error/basicError"));
	}
	
	@Test
	public void rejectCommentWithoutSuitableLinkIdAsUnautheticated() throws Exception {

	    	this.mockMvc.perform(MockMvcRequestBuilders.post("/links/link/comments")
														.contentType(MediaType.APPLICATION_FORM_URLENCODED)
														.param("commentText", "hallo kommentar"))
	    					.andDo(print())
							.andExpect(status().is(302))
							.andExpect(redirectedUrl("http://localhost/login"));
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
					.andExpect(view().name("error/basicError"));

	}
	
	/**
	 * while reading one link
	 * empty comment will be created,
	 * which will be used for new comment
	 * @throws Exception
	 */
	@Test
	public void readLinkTestAsUnautheticated() throws Exception {
		Link currentLink = entityManager.find(Link.class, 1l);	
		this.mockMvc.perform(get("/links/link/1"))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(model().attribute("link", currentLink))
					.andExpect(view().name("link/link_view"));
	}
	
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void readLinkTestAsAutheticated() throws Exception {
		Link currentLink = entityManager.find(Link.class, 1l);	
		UserDTO userDto = UserDTO.builder()
				.firstName("baruc-david")
				.secondName("rka")
				.build();
		MvcResult result =  this.mockMvc.perform(get("/links/link/1"))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(model().attribute("link", currentLink))
					.andExpect(view().name("link/link_view"))
					.andReturn();
		UserDTO usr = (UserDTO) result.getModelAndView().getModel().get("userDto");
		assertEquals(userDto.getFullName(), usr.getFullName());
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
	public void saveNewLinkTestUnautheticated() throws Exception {
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/links/link/create")
							.contentType(MediaType.APPLICATION_FORM_URLENCODED)
							.param("tags[0].tagName", "java12")
							.param("tags[1].tagName", "java13")
							.param("title", "welt.de")
							.param("url", "http://welt.de")
							)
  					.andDo(print())
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("http://localhost/login"));	
    }
	
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void createNewLinkTest() throws Exception {
		Link link = new Link();
		UserDTO userDto = UserDTO.builder()
				.firstName("baruc-david")
				.secondName("rka")
				.build();
    	MvcResult result = this.mockMvc.perform(get("/links/link/create"))
  					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(view().name("link/submit"))
					.andReturn();
    	UserDTO usr = (UserDTO) result.getModelAndView().getModel().get("userDto");
    	assertEquals(userDto.getFullName(),usr.getFullName());
    	assertTrue(result.getModelAndView().getModel().get("newLink").toString().equals(link.toString()));
    }

	@Test
	public void createNewLinkTestAsUnautheticated() throws Exception {
    	this.mockMvc.perform(get("/links/link/create"))
  					.andDo(print())
					.andExpect(status().is(302))
					.andExpect(redirectedUrl("http://localhost/login"));
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
		String[] resultArray = result.getResponse().getContentAsString().replace("[","").replace("]","").replace('"', ' ').split(",");
		assertEquals(true, Stream.of(resultArray)
								.peek(tag -> System.out.println("CURRENT TAG: "+ tag))
								.allMatch(tag -> expList.contains(tag.trim())));
		
    }
	
	/**
	 * Authetication as anonymous
	 */
	@Test
	@WithAnonymousUser
	public void linkCreateAsAnonymous() throws Exception {
		
            this.mockMvc.perform(get("/links/link/create"))
					.andDo(print())
					.andExpect(status().is(302))
					.andExpect(redirectedUrl("http://localhost/login"));  
	}
}
