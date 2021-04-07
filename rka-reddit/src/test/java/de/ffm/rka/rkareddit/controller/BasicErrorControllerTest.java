package de.ffm.rka.rkareddit.controller;


import de.ffm.rka.rkareddit.config.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.util.BeanUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
/** spring-test-support is enabled */
@RunWith(SpringRunner.class) 
/** enable of application-context */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringSecurityTestConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class BasicErrorControllerTest {

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

	/**
	 * 302 -> redirects from access-denied handler to error-page
	 * @throws Exception
	 */
	@Test
	@WithUserDetails("dascha@gmx.de")
	public void accessDeniePage() throws Exception {
		final MvcResult mvcResult = this.mockMvc.perform(get("/data/h2-console/**"))
				.andDo(print())
				.andExpect(status().is(302))
				.andReturn();
		mvcResult.getResponse().getRedirectedUrl().contains("/error/accessDenied");
	}
	
	@Test
	@WithUserDetails("dascha@gmx.de")
	public void requestDirektAccessDenied() throws Exception {
		this.mockMvc.perform(get("/error/accessDenied"))
					.andDo(print())
					.andExpect(status().is(403))
					.andReturn();
	}
	
	@Test
	@WithUserDetails("dascha@gmx.de")
	public void requestDirektErrorPage() throws Exception {
		this.mockMvc.perform(get("/error"))
					.andDo(print())
					.andExpect(status().is(404))
					.andReturn();
	}
	
	@Test
	public void requestDirektErrorPageAsAnonoymus() throws Exception {
		this.mockMvc.perform(get("/error"))
					.andDo(print())
					.andExpect(status().is(404))
					.andReturn();
	}
}
