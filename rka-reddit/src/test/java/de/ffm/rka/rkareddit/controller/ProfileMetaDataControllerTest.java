package de.ffm.rka.rkareddit.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.After;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.ffm.rka.rkareddit.rest.controller.ProfileMetaDataController;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.util.BeanUtil;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringSecurityTestConfig.class
)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ProfileMetaDataControllerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileMetaDataControllerTest.class);
    private MockMvc mockMvc;
    private SpringSecurityTestConfig testConfig;
	private static EntityManager em;

    @Autowired
	private ProfileMetaDataController profileMetaDataController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(profileMetaDataController).setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver()).build();
		testConfig = BeanUtil.getBeanFromContext(SpringSecurityTestConfig.class);
		em = BeanUtil.getBeanFromContext(EntityManager.class);
	}

	/**
	 * expectedValues
	 * 1. how many links: 5
	 * 2. how many comments: 11
	 * 3. username
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void shouldReturnDefaultMessage() throws Exception {
		String today = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(LocalDate.now());
		MvcResult result = this.mockMvc.perform(get("/profile/information/content")
													.sessionAttr("user", testConfig.getUsers().iterator().next())
										.contentType(MediaType.TEXT_PLAIN)
										.content("romakapt@gmx.de"))
										.andDo(print())
										.andExpect(status().isOk())
										.andReturn();

		LOGGER.info(result.getResponse().getContentAsString());
		String[] resultValues = result.getResponse().getContentAsString().replace("[","").replace("]","").replace("\"","").split(",");
		assertEquals("5", resultValues[0]);
		assertEquals("10", resultValues[1]);
		assertEquals(today, resultValues[2]);
		assertEquals("romakapt@gmx.de", resultValues[3]);
	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void shouldReturnUserPicture() throws Exception {
		MvcResult result = this.mockMvc.perform(get("/profile/information/content/user-pic")
												.sessionAttr("user", testConfig.getUsers().iterator().next())
								.contentType(MediaType.IMAGE_PNG_VALUE)
								.content("romakapt@gmx.de"))
								.andExpect(status().isOk())
								.andReturn();
		byte [] data = result.getResponse().getContentAsByteArray();
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		BufferedImage bImage2 = ImageIO.read(bis);
		File receivedUserPic = new File("receivedUserPic.png");
		ImageIO.write(bImage2, "png", receivedUserPic);
		LOGGER.info("RECEIVED IMAGE READABLE: {}", receivedUserPic.canRead());
		LOGGER.info("RECEIVED IMAGE HAS BEEN DELETED: {}", receivedUserPic.delete());

	}

}
