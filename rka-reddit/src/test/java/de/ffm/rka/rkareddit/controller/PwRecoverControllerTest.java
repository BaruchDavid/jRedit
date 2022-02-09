package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.config.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.service.UserService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringSecurityTestConfig.class)
@Transactional
class PwRecoverControllerTest {

    private MockMvc mockMvc;

    private User loggedInUser;
    private UserDTO loggedInUserDto;

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        if (loggedInUser == null) {
            loggedInUser = userService.findUserById("kaproma@yahoo.de").get();
            loggedInUserDto = UserDTO.mapUserToUserDto(loggedInUser);
        }

    }

    @Test
    void showRecoverUserPwRequest() {
    }

    @Test
    void getPasswordRecoveryForm() throws Exception {
        this.mockMvc.perform(get("/profile/user/recover/kaproma@yahoo.de/activation"))
                .andDo(print()).andExpect(status().is(200))
                .andExpect(model().attribute("userDto", loggedInUserDto));
    }

    @Test
    void showRecoveringError() {
    }

    @Test
    void createActivationCodeAndSendMail() {
    }

    @Test
    void userPasswordRecovery() {
    }
}