package de.ffm.rka.rkareddit.controller.user;

import de.ffm.rka.rkareddit.controller.MvcRequestSender;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.test.context.support.WithUserDetails;

import static de.ffm.rka.rkareddit.resultmatcher.GlobalResultMatcher.globalErrors;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class RegistrationTest extends MvcRequestSender {

    private User loggedInUser;


    @Before
    public void setup() {

        if (loggedInUser == null) {
            loggedInUser = userService.findUserById("kaproma@yahoo.de").get();
        }

    }

    /**
     * @author RKA
     */
    @Test
    public void registerNewInvalidUser() throws Exception {

        String body = "firstName=Paul&secondName=Grom&aliasName=grünes&password=tata&confirmPassword=tata";
        super.performPostRequest("/registration", body)
                .andExpect(status().is(400))
                .andExpect(view().name("auth/register"));
    }

    /**
     * @author RKA
     */
    @Test
    public void registerFailPwToShortNewUser() throws Exception {

        String body = "firstName=Paul&secondName=Grom&aliasName=grünes&email=Grbein@com.de" +
                "&password=tata&confirmPassword=tata";
        super.performPostRequest("/registration", body)
                .andExpect(status().is(400))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "password", "Size"))
                .andExpect(view().name("auth/register"));
    }

    /**
     * @author RKA
     */
    @Test
    public void registerFailFirstPwSecondPwAreNotMatched() throws Exception {
        String body = "firstName=Paul&secondName=Grom&aliasName=grünes&email=Grbein@com.de" +
                "&password=tatata&confirmPassword=tutata";
        super.performPostRequest("/registration", body)
                .andExpect(globalErrors().hasOneGlobalError("userDTO",
                        "Password and password confirmation do not match"))
                .andExpect(view().name("auth/register"));
    }

    @Test
    public void registerNewUserSuccess() throws Exception {
        String body = "firstName=Paul&secondName=Grbn&aliasName=grünes&email=Grbein@com.de" +
                "&password=tatatata&confirmPassword=tatatata";
        super.performPostRequest("/registration", body)
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("success", true));
    }

    @Test
    public void showRegisterViewAsUnauthenticatedTest() throws Exception {
        UserDTO user = UserDTO.builder().build();
        super.performGetRequest("/registration")
                .andExpect(status().isOk())
                .andExpect(model().attribute("userDto", user))
                .andExpect(view().name("auth/register"));
    }

    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void showRegisterViewAsAutheticatedTest() throws Exception {
        super.performGetRequest("/registration")
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/links"));
    }

    @Test
    public void activateAccountTest() throws Exception {
       super.performGetRequest("/activation/kaproma@yahoo.de/activation")
                .andExpect(view().name("auth/activated"));
    }

    @Test
    public void changeEmailFromLinkTest() throws Exception {
        super.performGetRequest("/mailchange/kaproma@yahoo.de/activation")
                .andExpect(status().is(302))
                .andExpect(flash().attribute("redirectMessage", "your new email has been activated"))
                .andExpect(redirectedUrl("/profile/private"));
    }

    @Test
    public void changeResetPasswordTest() throws Exception {
        super.performGetRequest("/profile/user/recover/kaproma@yahoo.de/activation")
                .andExpect(status().is(200))
                .andExpect(model().attribute("userDto", UserDTO.builder().email("notLoggedIn").build()))
                .andExpect(model().attribute("userContent", UserDTO.builder().email("kaproma@yahoo.de").build()));
    }

    @Test
    public void activateInvalidAccountTest() throws Exception {
        super.performGetRequest("/activation/kaproma@yahoo.de/action")
                .andExpect(redirectedUrl("/error/registrationError"));
    }

    /**
     * @author RKA send register request as autheticated user
     */
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void registerAsAuthenticated() throws Exception {
        String body = "firstName=Paul&secondName=Grom&aliasName=grünes" +
                "&password=tata&confirmPassword=tata";
        super.performPostRequest("/registration", body)
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/links"));
    }

    /**
     * 302 -> redirects from access-denied handler to error-page
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails("dascha@gmx.de")
    public void linksPageForAuthenticatedUserOnLogin() throws Exception {
        super.performGetRequest("/login**")
                .andExpect(status().is(302))
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
        super.performGetRequest("/registration")
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/links"));
    }
}
