package de.ffm.rka.rkareddit.controller.user;

import de.ffm.rka.rkareddit.controller.MvcRequestSender;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class AuthControllerOverridenPropsTest extends MvcRequestSender {

    /**
     * invalid mail rises no error and threats as successful.
     * no new user will be saved
     *
     * @throws Exception
     */
    @Test
    public void registerNewUserWithNonExistsEmail() throws Exception {
        String body = "firstName=Plau&secondName=Grbn&aliasName=grünes&" +
				"email=Grbein@com.de&password=tatatata&confirmPassword=tatatata&captcha=test&hiddenCaptcha=test";
        super.performPostRequest("/registration",body)
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/registration"))
                .andReturn();
    }
}
