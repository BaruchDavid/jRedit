package de.ffm.rka.rkareddit.controller.user;

import de.ffm.rka.rkareddit.controller.AuthController;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.CommentDTO;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.ffm.rka.rkareddit.resultmatcher.GlobalResultMatcher.globalErrors;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringSecurityTestConfig.class)
@Transactional
public class RegistrationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext context;

    private User loggedInUser;
    private UserDTO loggedInUserDto;

    @Before
    public void setup() {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        if (loggedInUser == null) {
            loggedInUser = userService.findUserById("romakapt@gmx.de").get();
            loggedInUserDto = UserDTO.mapUserToUserDto(loggedInUser);
        }

    }

    /**
     * @author RKA
     */
    @Test
    public void registerNewInvalidUser() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.post("/registration").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", "Paul").param("secondName", "Grom").param("aliasName", "grünes")
                .param("password", "tata").param("confirmPassword", "tata"))
                .andDo(print()).andExpect(status().is(400))
                .andExpect(view().name("auth/register"));
    }

    /**
     * @author RKA
     */
    @Test
    public void registerFailPwToShortNewUser() throws Exception {

        this.mockMvc
                .perform(post("/registration").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "Plau").param("secondName", "Grbn").param("aliasName", "grünes")
                        .param("email", "Grbein@com.de").param("password", "tata").param("confirmPassword", "tata"))
                .andDo(print()).andExpect(status().is(400))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "password", "Size"))
                .andExpect(view().name("auth/register"));
    }

    /**
     * @author RKA
     */
    @Test
    public void registerFailFirstPwSecondPwAreNotMatched() throws Exception {

        this.mockMvc
                .perform(post("/registration").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "Plau").param("secondName", "Grbn").param("aliasName", "grünes")
                        .param("email", "Grbein@com.de").param("password", "tatata").param("confirmPassword", "tutata"))
                .andDo(print())
                .andExpect(
                        globalErrors().hasOneGlobalError("userDTO", "Password and password confirmation do not match"))
                .andExpect(view().name("auth/register"));
    }

    @Test
    public void registerNewUserSuccess() throws Exception {

        this.mockMvc.perform(post("/registration").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", "Plau").param("secondName", "Grbn").param("aliasName", "grünes")
                .param("email", "Grbein@com.de").param("password", "tatatata").param("confirmPassword", "tatatata"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("success", true));
    }

    @Test
    public void showRegisterViewAsUnauthenticatedTest() throws Exception {
        UserDTO user = UserDTO.builder().build();
        this.mockMvc.perform(get("/registration"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(model().attribute("userDto", user))
                .andExpect(view().name("auth/register"));
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void showRegisterViewAsAutheticatedTest() throws Exception {
        this.mockMvc.perform(get("/registration")).andDo(print()).andExpect(status().is(302))
                .andExpect(redirectedUrl("/links"));
    }

    @Test
    public void activateAccountTest() throws Exception {
        this.mockMvc.perform(get("/activation/romakapt@gmx.de/activation")).andDo(print())
                .andExpect(view().name("auth/activated"));
    }

    @Test
    public void changeEmailFromLinkTest() throws Exception {
        this.mockMvc.perform(get("/mailchange/kaproma@yahoo.de/activation")).andDo(print()).andExpect(status().is(302))
                .andExpect(flash().attribute("redirectMessage", "your new email has been activated"))
                .andExpect(redirectedUrl("/profile/private"));
    }

    @Test
    public void activateInvalidAccountTest() throws Exception {
        this.mockMvc.perform(get("/activation/romakapt@gmx.de/actiion")).andDo(print())
                .andExpect(redirectedUrl("/error/registrationError"));
    }

    /**
     * @author RKA send register request as autheticated user
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void registerAsAuthenticated() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/registration").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "Paul").param("secondName", "Grom").param("aliasName", "grünes")
                        .param("password", "tata").param("confirmPassword", "tata"))
                .andDo(print()).andExpect(status().is(302)).andExpect(redirectedUrl("/links"));
    }

    /**
     * 302 -> redirects from access-denied handler to error-page
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails("dascha@gmx.de")
    public void linksPageForAuthenticatedUserOnLogin() throws Exception {
        this.mockMvc.perform(get("/login**")).andDo(print()).andExpect(status().is(302))
                .andExpect(redirectedUrl("/links"));
    }

    /**
     * 302 -> redirects from access-denied handler to error-page
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails("dascha@gmx.de")
    public void linksPageForAuthenticatedUserOnRegistration() throws Exception {
        this.mockMvc.perform(get("/registration")).andDo(print()).andExpect(status().is(302))
                .andExpect(redirectedUrl("/links"));
    }
}
