package de.ffm.rka.rkareddit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.context.junit4.SpringRunner;
import de.ffm.rka.rkareddit.controller.rest.ProfileMetaDataController;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.util.BeanUtil;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringSecurityTestConfig.class
)
public class ProfileMetaDataControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileMetaDataControllerTest.class);
    private MockMvc mockMvc;
    private SpringSecurityTestConfig testConfig;    
    
    
    @Autowired
	private ProfileMetaDataController profileMetaDataController;
    
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(profileMetaDataController).setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver()).build();
		testConfig = BeanUtil.getBeanFromContext(SpringSecurityTestConfig.class);  
	}
    
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void shouldReturnDefaultMessage() throws Exception {
		String today = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(LocalDate.now()); 
		String expectedValue ="[\"5\",\"9\",\""+today+"\",\"C:\\\\Drops\\\\rka-reddit\\\\target\\\\classes\\\\static\\\\images\\\\romakapt@gmx.de.png\"]";
		MvcResult result = this.mockMvc.perform(get("/profile/information/content")
												.sessionAttr("user", testConfig.getUsers().iterator().next())
								.contentType(MediaType.TEXT_PLAIN)
								.content("romakapt@gmx.de"))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(content().string(expectedValue))
					.andReturn();
		LOGGER.info(result.getResponse().getContentAsString());
		
	}
	
}
