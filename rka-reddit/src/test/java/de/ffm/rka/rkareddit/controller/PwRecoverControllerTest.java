package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class PwRecoverControllerTest extends MvcRequestSender {


    private User loggedInUser;


    @Before
    public void setup() {

        if (loggedInUser == null) {
            loggedInUser = userService.findUserById("kaproma@yahoo.de").get();
        }

    }


    @Test
    public void showRecoverUserPwRequest() throws Exception {

        super.performGetRequest("/profile/user/recover/view")
                .andExpect(status().is(200))
                .andExpect(view().name("recover/recoverUserPwRequest"))
                .andExpect(model().attribute("userDto", UserDTO.builder().email("notLoggedIn").build()))
                .andExpect(model().attribute("userContent", UserDTO.builder().firstName("Guest").build()));
    }

    @Test
    public void getPasswordRecoveryForm() throws Exception {
        super.performGetRequest("/profile/user/recover/kaproma@yahoo.de/activation")
                .andExpect(status().is(200))
                .andExpect(model().attribute("userDto", UserDTO.builder().email("notLoggedIn").build()))
                .andExpect(model().attribute("userContent", UserDTO.builder().email("kaproma@yahoo.de").build()));
    }


    /*@Test
    void showRecoveringError() {
    }*/

    @Test
    public void createActivationCodeAndSendMail() throws Exception {
        String body = "userEmail=kaproma@yahoo.de";
        super.performPostRequest("/profile/user/recover/", body)
                .andExpect(status().is(200))
                .andExpect(model().attribute("success", true))
                .andExpect(model().attribute("userDto", UserDTO.builder().email("notLoggedIn").build()))
                .andExpect(model().attribute("userContent", UserDTO.builder().firstName("Guest").build()))
                .andExpect(view().name("recover/recoverUserPwRequestApplied"));
    }

    @Test
    public void userPasswordRecovery() throws Exception {

        String body = "email=kaproma@yahoo.de&newPassword=brabusor&confirmNewPassword=brabusor";
        super.performPutRequest("/profile/user/recover", body)
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/profile/private"))
                .andExpect(flash().attribute("success", true))
                .andExpect(flash().attribute("redirectMessage", "your password has been changed!"));
    }
}
