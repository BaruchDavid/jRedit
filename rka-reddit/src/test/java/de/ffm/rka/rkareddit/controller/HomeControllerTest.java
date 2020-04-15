package de.ffm.rka.rkareddit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import de.ffm.rka.rkareddit.exception.GlobalControllerAdvisor;
import de.ffm.rka.rkareddit.interceptor.ApplicationHandlerInterceptor;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringSecurityTestConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class HomeControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private HomeController homeController;

	@Autowired
	private GlobalControllerAdvisor globalControllerAdvice;
	
	/**
	 * Using Standalone-Configuration, no SpringApplicationContext.
	 * All additional elements (filter, advices, interceptors) must be set manualy
	 */
	@Before
	public void setup() {

        MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(homeController)
										.addInterceptors(new ApplicationHandlerInterceptor())
										.setControllerAdvice(globalControllerAdvice)		
										.setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver(), new PageableHandlerMethodArgumentResolver())
										.build();
	}

	@Ignore
	@Test
	public void shouldReturnAllLinks() throws Exception {
		List<Integer> pageNumbers = Arrays.asList(1,2);
		this.mockMvc.perform(get("/"))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(model().attribute("pageNumbers", pageNumbers))
					.andReturn();

	}
}
