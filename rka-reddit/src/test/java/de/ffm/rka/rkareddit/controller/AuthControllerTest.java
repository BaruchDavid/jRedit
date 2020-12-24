package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.CommentDTO;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.ffm.rka.rkareddit.resultmatcher.GlobalResultMatcher.globalErrors;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringSecurityTestConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext context;

    private User romakaptUser;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        romakaptUser = userService.findUserById("romakapt@gmx.de").get();

    }

    @SuppressWarnings("unchecked")
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void showProfileOfUserAsAutheticated() throws Exception {

        ResultActions resultActions = this.mockMvc.perform(get("/profile/private")).andDo(print());
        MvcResult result = resultActions.andReturn();
//	    assertTrue(2, result.getModelAndView().getModel().get("comments").size()); //es müssen zwei kommentare sein, auf jdbc transaktionen achten
//	    assertTrue(5, result.getModelAndView().getModel().get("posts").size()); //es muss x posts sein, auf jdbc transaktionen achten
        assertNotNull(result);
    }

    /**
     * @author RKA
     */
    @Test
    public void registerNewInvalidUser() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/registration").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "Paul").param("secondName", "Grom").param("aliasName", "grünes")
                        .param("password", "tata").param("confirmPassword", "tata"))
                .andDo(print()).andExpect(status().is(400)).andExpect(view().name("auth/register"));
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
    public void showRegisterViewAsUnautheticatedTest() throws Exception {
        UserDTO user = UserDTO.builder().build();
        this.mockMvc.perform(get("/registration")).andDo(print()).andExpect(status().isOk())
                .andExpect(model().attribute("userDto", user)).andExpect(view().name("auth/register"));
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
     * show public profile from grom as unautheticated user
     * cach-flag: no-cache is set, cause for content-user you will see
     * everytime actual saved picture and not cached picture
     */
    @Test
    public void showPublicProfileAsUnautheticated() throws Exception {
        User grom = userService.findUserById("grom@gmx.de").orElseGet(() -> new User());
        this.mockMvc.perform(get("/profile/public/grom@gmx.de")).andDo(print()).andExpect(view().name("auth/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userContent", UserDTO.mapUserToUserDto(grom)))
                .andExpect(model().attribute("cacheControl", "no-cache"));
    }

    /**
     * show public non existing profile from grm as unauthenticated user
     */
    @Test
    public void showPublicNoNexistedProfileAsUnautheticated() throws Exception {
        this.mockMvc.perform(get("/profile/public/grm@gmx.de"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(view().name("error/basicError"));
    }

    @Test
    public void showPrivateProfileAsUnautheticated() throws Exception {
        this.mockMvc.perform(get("/profile/private/romakapt@gmx.de")).andDo(print()).andExpect(status().isNotFound());
    }

    /**
     * show profile site of login user
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    @WithUserDetails("romakapt@gmx.de")
    @Transactional
    public void showPrivateProfileAsAutheticated() throws Exception {
        UserDTO userDto = UserDTO.mapUserToUserDto(romakaptUser);
        Set<CommentDTO> commentDto = romakaptUser.getUserComment()
                .stream()
                .map(comment -> CommentDTO.getCommentToCommentDto(comment))
                .collect(Collectors.toSet());
        this.mockMvc.perform(get("/profile/private")).andDo(print()).andExpect(status().isOk())
                .andExpect(model().attribute("userContent", UserDTO.mapUserToUserDto(romakaptUser)))
                .andExpect(model().attribute("userDto", userDto))
                .andExpect(model().attribute("comments", commentDto))
                .andExpect(model().attribute("cacheControl", StringUtils.EMPTY));

    }

    /**
     * show profile public site of some user as authenticated user
     * cach-flag: no-cache is set, cause for content-user you will see
     * everytime actual saved picture and not cached picture
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void showPublicProfileAsAutheticated() throws Exception {
        Optional<User> userContent = userService.findUserById("grom@gmx.de");
        if (userContent.isPresent()) {
            this.mockMvc.perform(get("/profile/public/grom@gmx.de")).andDo(print()).andExpect(status().isOk())
                    .andExpect(model().attribute("userContent", UserDTO.mapUserToUserDto(userContent.get())))
                    .andExpect(model().attribute("userDto", UserDTO.mapUserToUserDto(romakaptUser)))
                    .andExpect(model().attribute("cacheControl", "no-cache"));
        } else {
            fail("user for test-request not found");
        }
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void showEditProfilePage() throws Exception {
        UserDTO userDto = UserDTO.mapUserToUserDto(romakaptUser);
        this.mockMvc.perform(get("/profile/private/me")).andDo(print()).andExpect(status().isOk())
                .andExpect(model().attribute("userDto", userDto));
    }

    @Test
    public void showEditProfilePageForUnknownUser() throws Exception {
        this.mockMvc.perform(get("/profile/private/me")).andDo(print())
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(view().name("error/application"));

    }

    /**
     * only for testcase new email will be keeped in db, cause get-request on
     * activation looks wo this email and when no one is present, error will be
     * thrown
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void showChangeEmailPage() throws Exception {
        Optional<User> user = userService.findUserById("romakapt@gmx.de");
        if (user.isPresent()) {
            user.get().setNewEmail(StringUtils.EMPTY);
            UserDTO userDto = UserDTO.mapUserToUserDto(user.get());
            this.mockMvc.perform(get("/profile/private/me/update/email")).andDo(print()).andExpect(status().isOk())
                    .andExpect(model().attribute("userDto", userDto)).andExpect(view().name("auth/emailChange"));
        } else {
            fail("user for test-request not found");
        }
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void showChangePasswordPage() throws Exception {

        UserDTO userDto = UserDTO.mapUserToUserDto(romakaptUser);
        this.mockMvc.perform(get("/profile/private/me/password")).andDo(print()).andExpect(status().isOk())
                .andExpect(model().attribute("userDto", userDto)).andExpect(view().name("auth/passwordChange"));
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void saveChangesOnAuthUserOK() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/profile/private/me/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("firstName", "baruc-david")
                        .param("email", "romakapt@gmx.de").param("secondName", "rka.odem").param("aliasName", "worker"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/private"))
                .andExpect(flash().attributeExists("success")).andReturn();
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void changeUserEmailOK() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.patch("/profile/private/me/update/email")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "romakapt@gmx.de")
                        .param("newEmail", "kaproma@yahoo.de").param("password", "roman")
                        .param("confirmPassword", "roman"))
                .andDo(print()).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/profile/private"))
                .andExpect(flash().attributeExists("success")).andReturn();

    }

    /**
     * old and new email are equal ==> wrong password is wrong
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void changeUserEmailNotOK() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.patch("/profile/private/me/update/email")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("email", "romakapt@gmx.de")
                        .param("newEmail", "romakapt@gmx.de").param("password", "doman")
                        .param("confirmPassword", "doman"))
                .andDo(print()).andExpect(status().is(400))
                .andExpect(view().name("auth/emailChange"))
                .andExpect(model().errorCount(2))
                .andExpect(globalErrors().hasOneGlobalError("userDTO", "Old and new email must be different"))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "password", "CorrectPassword"));

    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void changeUserEmailToSmall() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.patch("/profile/private/me/update/email")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("email", "romakapt@gmx.de")
                        .param("newEmail", "r@e").param("password", "doman").param("confirmPassword", "doman"))
                .andDo(print()).andExpect(status().is(400)).andExpect(view().name("auth/emailChange"))
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "password", "CorrectPassword"))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "newEmail", "Size"));

    }

    /**
     * validator vor NotEmpty confirmPw invokes pw-and-confirmPw matcher
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void changeUserEmailNoEmail() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.patch("/profile/private/me/update/email")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("email", "romakapt@gmx.de")
                        .param("password", "roman"))
                .andDo(print()).andExpect(status().is(400)).andExpect(view().name("auth/emailChange"))
                .andExpect(model().errorCount(3))
                .andExpect(globalErrors().hasTwoGlobalErrors("userDTO", "Old and new email must be different"))
                .andExpect(
                        globalErrors().hasTwoGlobalErrors("userDTO", "Password and password confirmation do not match"))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "newEmail", "NotEmpty"));

    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void changeUserEmailToBig() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.patch("/profile/private/me/update/email")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("email", "romakapt@gmx.de")
                        .param("newEmail", "radfadfadfsddsdfadfadfsdsdfs@de").param("password", "doman")
                        .param("confirmPassword", "doman"))
                .andDo(print()).andExpect(status().is(400)).andExpect(view().name("auth/emailChange"))
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "password", "CorrectPassword"))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "newEmail", "Size"));

    }

    /**
     * old and new email are equal ==> wrong password is wrong confirm-password and
     * password is not equal
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void changeUserEmailNotOKAllErrors() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.patch("/profile/private/me/update/email")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("email", "romakapt@gmx.de")
                        .param("newEmail", "romakapt@gmx.de").param("password", "doman")
                        .param("confirmPassword", "soman"))
                .andDo(print()).andExpect(status().is(400)).andExpect(view().name("auth/emailChange"))
                .andExpect(globalErrors().hasTwoGlobalErrors("userDTO", "Old and new email must be different"))
                .andExpect(
                        globalErrors().hasTwoGlobalErrors("userDTO", "Password and password confirmation do not match"))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "password", "CorrectPassword"))
                .andExpect(model().errorCount(3));

    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void saveChangesOnAuthUserWithValidationChangeUserGroup() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/profile/private/me/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("firstName", "baruc-david")
                        .param("email", "romakapt@gmx.de").param("secondName", "rka.odem").param("aliasName", "wor"))
                .andDo(print()).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/private/me/romakapt@gmx.de"))
                .andExpect(flash().attributeExists("bindingError"));

    }

    /**
     * old pw is ok new pw is ok
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void saveAuthUserWithValidationChangeUserPasswordGroupOK() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/profile/private/me/password")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("email", "romakapt@gmx.de")
                        .param("password", "roman").param("confirmNewPassword", "rororo")
                        .param("newPassword", "rororo"))
                .andDo(print()).andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/profile/private")).andExpect(flash().attribute("success", true));

    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void saveAuthUserWithValidationChangeUserPasswordGroupFalseMethod() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.post("/profile/private/me/password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("email", "romakapt@gmx.de")
                .param("password", "roman").param("confirmNewPassword", "rororo").param("newPassword", "rororo"))
                .andDo(print()).andExpect(status().is(405)).andExpect(view().name("error/pageNotFound"));

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
     * @author RKA passwordchange with wrong old password
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void changePasswordWrongOldPassword() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/profile/private/me/password")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("email", "romakapt@gmx.de")
                        .param("password", "ronan").param("confirmNewPassword", "rororo")
                        .param("newPassword", "rororo"))
                .andDo(print()).andExpect(status().is(400)).andExpect(view().name("auth/passwordChange"))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "password", "CorrectPassword"));

    }

    /**
     * @author RKA passwordchange with new-password and new-password-confirmation
     * not equal
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void changePasswordNewPwNotEqualNewPWConfirm() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/profile/private/me/password")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("email", "romakapt@gmx.de")
                        .param("password", "roman").param("confirmNewPassword", "rororo")
                        .param("newPassword", "bobobo"))
                .andDo(print()).andExpect(status().is(400)).andExpect(view().name("auth/passwordChange"))
                .andExpect(model().attributeHasErrors("userDTO"));

    }

    /**
     * old and new password should not be equal
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void changePasswordNewAndOldShouldNotBeEqual() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/profile/private/me/password")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("email", "romakapt@gmx.de")
                        .param("password", "roman").param("confirmNewPassword", "roman").param("newPassword", "roman"))
                .andDo(print()).andExpect(status().is(400)).andExpect(view().name("auth/passwordChange"));
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
