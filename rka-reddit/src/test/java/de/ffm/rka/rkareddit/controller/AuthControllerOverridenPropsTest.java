package de.ffm.rka.rkareddit.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.junit.Before;
import org.junit.Test; 
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, 
				classes = SpringSecurityTestConfig.class,
				properties = {"spring.mail.host=smtp.not-exist.com"})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class AuthControllerOverridenPropsTest {

	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext context;
	
	
	@Before
	public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
									.apply(springSecurity())
									.build();
	}


	@Test
	public void registerNewUserSuccess() throws Exception {

	    	this.mockMvc.perform(post("/registration")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.param("firstName", "Plau")
								.param("secondName", "Grbn")
								.param("aliasName", "grünes")
								.param("email", "Grbein@com.de")
								.param("password", "tatatata")
								.param("confirmPassword", "tatatata"))
	    					.andDo(print())
							.andExpect(status().is(504))
							.andExpect(view().name("error/basicError"));
	}
}
