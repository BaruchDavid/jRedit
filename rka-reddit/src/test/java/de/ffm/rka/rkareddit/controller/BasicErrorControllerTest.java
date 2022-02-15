package de.ffm.rka.rkareddit.controller;


import de.ffm.rka.rkareddit.util.BeanUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class BasicErrorControllerTest extends MvcRequestSender{


	private EntityManager entityManager;
	
	@Before
	public void setup() {

		entityManager = BeanUtil.getBeanFromContext(EntityManager.class);
	}

	/**
	 * 302 -> redirects from access-denied handler to error-page
	 * @throws Exception
	 */
	@Test
	@WithUserDetails("grom@gmx.de")
	public void accessDeniePage() throws Exception {

		final MvcResult mvcResult = super.performGetRequest("/data/h2-console/**")
				.andExpect(status().is(302))
				.andReturn();
		mvcResult.getResponse().getRedirectedUrl().contains("/error/accessDenied");
	}
	
	@Test
	@WithUserDetails("dascha@gmx.de")
	public void requestDirektAccessDenied() throws Exception {
		super.performGetRequest("/error/accessDenied")
					.andExpect(status().is(403))
					.andReturn();
	}
	
	@Test
	@WithUserDetails("dascha@gmx.de")
	public void requestDirektErrorPage() throws Exception {
		super.performGetRequest("/error")
					.andExpect(status().is(404))
					.andReturn();
	}
	
	@Test
	public void requestDirektErrorPageAsAnonoymus() throws Exception {
		super.performGetRequest("/error")
					.andExpect(status().is(404))
					.andReturn();
	}
}
