package de.ffm.rka.rkareddit.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.exception.GlobalControllerAdvisor;
import de.ffm.rka.rkareddit.interceptor.ApplicationHandlerInterceptor;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.util.BeanUtil;
import jdk.net.SocketFlow.Status;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringSecurityTestConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class AuthControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private AuthController authController;

	@Autowired
	private UserService userService;
	
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
		this.mockMvc = MockMvcBuilders.standaloneSetup(authController)
										.addInterceptors(new ApplicationHandlerInterceptor())
										.setControllerAdvice(globalControllerAdvice)		
										.setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver(), new PageableHandlerMethodArgumentResolver())
										.build();
		entityManager = BeanUtil.getBeanFromContext(EntityManager.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void showProfileOfUserAsAutheticated() throws Exception {
		User user = userService.getUserWithLinks("romakapt@gmx.de");
		List<Link> posts = user.getUserLinks();
		List<Comment> comments = userService.getUserWithComments("romakapt@gmx.de").getUserComments();		
		ResultActions resultActions = this.mockMvc.perform(get("/profile/private"))
					.andDo(print());
		MvcResult result = resultActions.andReturn();
	    assertTrue(posts.containsAll((List<Link>) result.getModelAndView().getModel().get("posts")));
	    assertTrue(comments.containsAll((List<Comment>) result.getModelAndView().getModel().get("comments")));
	}
	
	/**
	 * @author RKA
	 */
	@Test
	public void registerNewInvalidUser() throws Exception {

	    	this.mockMvc.perform(MockMvcRequestBuilders.post("/register")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.param("firstName", "Paul")
								.param("secondName", "Grom")
								.param("aliasName", "grünes")
								.param("password", "tata")
								.param("confirmPassword", "tata"))
	    					.andDo(print())
							.andExpect(status().is(400))
							.andExpect(view().name("auth/register"));
	}
	
	/**
	 * @author RKA
	 */
	@Test
	public void registerNewUser() throws Exception {

	    	this.mockMvc.perform(post("/register")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.param("firstName", "Plau")
								.param("secondName", "Grbn")
								.param("aliasName", "grünes")
								.param("email", "Grbein@com.de")
								.param("password", "tata")
								.param("confirmPassword", "tata"))
	    					.andDo(print())
							.andExpect(status().is3xxRedirection())
							.andExpect(flash().attribute("success", true));
	}
	

	@Test
	public void activateAccountTest() throws Exception {		
            this.mockMvc.perform(get("/activate/romakapt@gmx.de/activation"))
					.andDo(print())
					.andExpect(view().name("auth/activated"));  
	}
	
	@Test
	public void activateInvalidAccountTest() throws Exception {		
            this.mockMvc.perform(get("/activate/romakapt@gmx.de/actiion"))
					.andDo(print())
					.andExpect(redirectedUrl("/"));  
	}
	
	/**
	 * show public profile from grom as autheticated user
	 */
	@Test
	public void showPublicProfileAsUnautheticated() throws Exception {
		Optional<User> user = userService.findUserById("grom@gmx.de");
		if(user.isPresent()) {
        this.mockMvc.perform(get("/profile/public/grom@gmx.de"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attribute("userContent", user.get()));  
		} else {
			fail("user for test-request not found");
		}
	}
	
	/**
	 * show public non existing profile from grm as autheticated user
	 */
	@Test
	public void showPublicNoNexistedProfileAsUnautheticated() throws Exception {		
        this.mockMvc.perform(get("/profile/public/grm@gmx.de"))
				.andDo(print())
				.andExpect(forwardedUrl("error/application"));  
	}

	@Test
	public void showPrivateProfileAsUnautheticated() throws Exception {		
        this.mockMvc.perform(get("/profile/private/romakapt@gmx.de"))
				.andDo(print())
				.andExpect(status().isNotFound());  
	}
	
	/**
	 * show profile site of login user
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void showPrivateProfileAsAutheticated() throws Exception {
		Optional<User> user = userService.findUserById("romakapt@gmx.de");
        if(user.isPresent()) {
        	this.mockMvc.perform(get("/profile/private"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(model().attribute("userContent", user.get()));
        } else {
        	fail("user for test-request not found");
        }  
	}
	
	/**
	 * show profile pulic site of some user as autheticated user
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void showPublicProfileAsAutheticated() throws Exception {
		Optional<User> user = userService.findUserById("grom@gmx.de");
        if(user.isPresent()) {
        	this.mockMvc.perform(get("/profile/public/grom@gmx.de"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(model().attribute("userContent", user.get()));
        } else {
        	fail("user for test-request not found");
        }  
	}
}
