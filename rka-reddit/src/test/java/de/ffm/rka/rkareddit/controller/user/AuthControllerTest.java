package de.ffm.rka.rkareddit.controller.user;

import de.ffm.rka.rkareddit.controller.MvcRequestSender;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.CommentDTO;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

import static de.ffm.rka.rkareddit.resultmatcher.GlobalResultMatcher.globalErrors;
import static org.junit.Assert.fail;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.AFTER_METHOD;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class AuthControllerTest extends MvcRequestSender {

    private User pageContentUser;
    private User loggedInUser;
    private UserDTO loggedInUserDto;

    private UserDTO emptyUser = UserDTO.builder()
            .userComment(Collections.emptyList())
            .userLinks(Collections.emptyList())
            .build();

    @Before
    public void setup() {

        initilizeLoggedInAndContentUsers();

    }

    private void initilizeLoggedInAndContentUsers() {
        if (loggedInUser == null) {
            loggedInUser = userService.findUserById("kaproma@yahoo.de").get();
            pageContentUser = userService.getUserWithLinks(loggedInUser.getEmail());
            loggedInUserDto = UserDTO.mapUserToUserDto(loggedInUser);
        }
    }

    @Test
    @WithUserDetails("kaproma@yahoo.de")
    @DirtiesContext(methodMode = BEFORE_METHOD)
    public void showProfileOfUserAsAuthenticated() throws Exception {
        final Set<LinkDTO> loggedUserLinks = pageContentUser.getUserLinks()
                .stream()
                .map(LinkDTO::mapFullyLinkToDto)
                .collect(Collectors.toSet());
        String userSince = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .format(loggedInUserDto.getCreationDate());
        super.performGetRequest("/profile/private")
                .andExpect(status().isOk())
                .andExpect(view().name("auth/profileLinks"))
                .andExpect(model().attribute("posts", loggedUserLinks))
                .andExpect(model().attribute("userDto", loggedInUserDto))
                .andExpect(model().attribute("userContent", loggedInUserDto))
                .andExpect(model().attribute("userSince", userSince))
                .andExpect(model().attribute("commentCount", 2))
                .andExpect(model().attribute("cacheControl", ""));
    }

    @DirtiesContext(methodMode = AFTER_METHOD)
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void showProfileWithLinksOfLoggedInUserAsAuthenticated() throws Exception {
        final Set<LinkDTO> loggedUserLinks = pageContentUser.getUserLinks()
                .stream()
                .map(LinkDTO::mapFullyLinkToDto)
                .collect(Collectors.toSet());
        String userSince = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .format(loggedInUserDto.getCreationDate());

        super.performGetRequest("/profile/" + loggedInUser.getEmail() + "/links")
                .andExpect(status().isOk())
                .andExpect(view().name("auth/profileLinks"))
                .andExpect(model().attribute("posts", loggedUserLinks))
                .andExpect(model().attribute("userDto", loggedInUserDto))
                .andExpect(model().attribute("userContent", loggedInUserDto))
                .andExpect(model().attribute("userSince", userSince))
                .andExpect(model().attribute("commentCount", 2));
    }

    /**
     * anonymous guets can see profile links from user
     *
     * @throws Exception may be thrown
     */
    @DirtiesContext(methodMode = AFTER_METHOD)
    @Test
    public void showProfileWithLinksOfUserAsUnAuthenticated() throws Exception {
        final Set<LinkDTO> loggedUserLinks = pageContentUser.getUserLinks()
                .stream()
                .map(LinkDTO::mapFullyLinkToDto)
                .collect(Collectors.toSet());
        String userSince = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .format(loggedInUserDto.getCreationDate());

        super.performGetRequest("/profile/" + loggedInUser.getEmail() + "/links")
                .andExpect(status().isOk())
                .andExpect(view().name("auth/profileLinks"))
                .andExpect(model().attribute("posts", loggedUserLinks))
                .andExpect(model().attribute("userDto", emptyUser))

                .andExpect(model().attribute("userContent", loggedInUserDto))
                .andExpect(model().attribute("userSince", userSince))
                .andExpect(model().attribute("commentCount", 2));
    }
    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void showProfileWithCommentsOfLoggedInAsAuthenticated() throws Exception {
        final Set<CommentDTO> loggedUserComments = pageContentUser.getUserComment()
                .stream()
                .map(CommentDTO::mapCommentToCommentDto)
                .collect(Collectors.toSet());

        String userSince = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .format(loggedInUserDto.getCreationDate());
        super.performGetRequest("/profile/" + pageContentUser.getEmail() + "/comments")
                .andExpect(status().isOk())
                .andExpect(view().name("auth/profileComments"))
                .andExpect(model().attribute("userSince", userSince))
                .andExpect(model().attribute("userDto", loggedInUserDto))
                .andExpect(model().attribute("userContent", loggedInUserDto))
                .andExpect(model().attribute("comments", loggedUserComments))
                .andExpect(model().attribute("postsCount", 5));
    }

    @DirtiesContext(methodMode = AFTER_METHOD)
    @Test
    public void showProfileWithCommentsOfAsUnAuthenticated() throws Exception {
        final Set<CommentDTO> loggedUserComments = pageContentUser.getUserComment()
                .stream()
                .map(CommentDTO::mapCommentToCommentDto)
                .collect(Collectors.toSet());

        String userSince = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .format(loggedInUserDto.getCreationDate());
        super.performGetRequest("/profile/" + pageContentUser.getEmail() + "/comments")
                .andExpect(status().isOk())
                .andExpect(view().name("auth/profileComments"))
                .andExpect(model().attribute("userSince", userSince))
                .andExpect(model().attribute("userDto", emptyUser))
                .andExpect(model().attribute("userContent", loggedInUserDto))
                .andExpect(model().attribute("comments", loggedUserComments))
                .andExpect(model().attribute("postsCount", 5));
    }

    @DirtiesContext(methodMode = AFTER_METHOD)
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void showProfileWithCommentsOfUserAsAuthenticated() throws Exception {
        final Set<CommentDTO> loggedUserComments = pageContentUser.getUserComment()
                .stream()
                .map(CommentDTO::mapCommentToCommentDto)
                .collect(Collectors.toSet());

        String userSince = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .format(loggedInUserDto.getCreationDate());
        super.performGetRequest("/profile/" + pageContentUser.getEmail() + "/comments")
                .andExpect(status().isOk())
                .andExpect(view().name("auth/profileComments"))
                .andExpect(model().attribute("userSince", userSince))
                .andExpect(model().attribute("userDto", loggedInUserDto))
                .andExpect(model().attribute("userContent", loggedInUserDto))
                .andExpect(model().attribute("comments", loggedUserComments))
                .andExpect(model().attribute("postsCount", 5));
    }

    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void showProfileWithLinksOfOtherUserAsAuthenticated() throws Exception {
        pageContentUser = userService.getUserWithLinks("dascha@gmx.de");
        final Set<LinkDTO> userContentLinks = pageContentUser.getUserLinks()
                .stream()
                .map(LinkDTO::mapFullyLinkToDto)
                .collect(Collectors.toSet());
        String userSince = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .format(pageContentUser.getCreationDate());

        super.performGetRequest("/profile/" + pageContentUser.getEmail() + "/links")
                .andExpect(status().isOk())
                .andExpect(view().name("auth/profileLinks"))
                .andExpect(model().attribute("posts", userContentLinks))
                .andExpect(model().attribute("userDto", loggedInUserDto))
                .andExpect(model().attribute("userContent", UserDTO.mapUserToUserDto(pageContentUser)))
                .andExpect(model().attribute("userSince", userSince))
                .andExpect(model().attribute("commentCount", 6));
    }

    
    @Test
    public void showProfileWithLinksOfOtherUserAsUnAuthenticated() throws Exception {
        pageContentUser = userService.getUserWithLinks("dascha@gmx.de");
        final Set<LinkDTO> userContentLinks = pageContentUser.getUserLinks()
                .stream()
                .map(LinkDTO::mapFullyLinkToDto)
                .collect(Collectors.toSet());
        String userSince = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .format(pageContentUser.getCreationDate());
        super.performGetRequest("/profile/" + pageContentUser.getEmail() + "/links")
                .andExpect(status().isOk())
                .andExpect(view().name("auth/profileLinks"))
                .andExpect(model().attribute("posts", userContentLinks))
                .andExpect(model().attribute("userDto", emptyUser))
                .andExpect(model().attribute("userContent", UserDTO.mapUserToUserDto(pageContentUser)))
                .andExpect(model().attribute("userSince", userSince))
                .andExpect(model().attribute("commentCount", 6));
    }

    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void showProfileWithCommentsOfOtherUserAsAuthenticated() throws Exception {
        pageContentUser = userService.getUserWithComments("dascha@gmx.de");
        final Set<CommentDTO> userContentComments = pageContentUser.getUserComment()
                .stream()
                .map(CommentDTO::mapCommentToCommentDto)
                .collect(Collectors.toSet());
        String userSince = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .format(pageContentUser.getCreationDate());

        super.performGetRequest("/profile/" + pageContentUser.getEmail() + "/comments")
                .andExpect(status().isOk())
                .andExpect(view().name("auth/profileComments"))
                .andExpect(model().attribute("comments", userContentComments))
                .andExpect(model().attribute("userDto", loggedInUserDto))
                .andExpect(model().attribute("userContent", UserDTO.mapUserToUserDto(pageContentUser)))
                .andExpect(model().attribute("userSince", userSince))
                .andExpect(model().attribute("postsCount", 5));
    }

    /**
     * show public profile from grom as unautheticated user
     * cach-flag: no-cache is set, cause for content-user you will see
     * everytime actual saved picture and not cached picture
     */
    
    @Test
    public void showPublicProfileAsUnauthenticated() throws Exception {
        User grom = userService.findUserById("grom@gmx.de").orElseGet(User::new);
        super.performGetRequest("/profile/public/grom@gmx.de")
                .andExpect(view().name("auth/profileLinks"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userDto", emptyUser))
                .andExpect(model().attribute("userContent", UserDTO.mapUserToUserDto(grom)))
                .andExpect(model().attribute("cacheControl", "no-cache"));
    }

    /**
     * show public non existing profile from grm as unauthenticated user
     */
    
    @Test
    public void showPublicNoExistedProfileAsUnauthenticated() throws Exception {
        final MvcResult mvcResult = super.performGetRequest("/profile/public/grm@gmx.de")
                .andReturn();
        final String urlEncoded = mvcResult.getResponse().getHeader("location");
        final ResultActions resultActions = sendRedirect(urlEncoded.replace("+", ""));
        resultActions.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(view().name("error/basicError"));
    }

    
    @Test
    public void showPrivateProfileAsUnauthenticated() throws Exception {
        super.performGetRequest("/profile/private/kaproma@yahoo.de")
                .andExpect(status().isNotFound());
    }

    
    @Test
    @WithUserDetails("grom@gmx.de")
    public void accessToDBAsWrongUser() throws Exception {
        final MvcResult mvcResult = super.performGetRequest("/data/h2-console")
                .andExpect(status().is3xxRedirection())
                .andReturn();
        final String redirectedUrl = mvcResult.getResponse().getHeader("location");
        final ResultActions result = sendRedirect(Optional.ofNullable(redirectedUrl).orElse(""));
        result.andExpect(status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(view().name("error/accessDenied"))
                .andExpect(model().attribute("userDto", UserDTO.builder()
                        .firstName("grom")
                        .build()));
    }

    /**
     * without authentication, you will be redirected to login for authentication
     *
     * @throws Exception may be thrown 
     */
    
    @Test
    public void accessWithNoAuthorizationAsUnauthenticated() throws Exception {
        final MvcResult mvcResult = super.performGetRequest("/data/h2-console")
                .andExpect(status().is3xxRedirection())
                .andReturn();
        final String redirectedUrl = mvcResult.getResponse().getHeader("location");
        final ResultActions result = sendRedirect(Optional.ofNullable(redirectedUrl).orElse(""));
        result.andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("auth/login"));
    }

    /**
     * show profile public site of some user as authenticated user
     * cach-flag: no-cache is set, cause for content-user you will see
     * everytime actual saved picture and not cached picture
     */
    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void showPublicProfileAsAuthenticated() throws Exception {
        Optional<User> userContent = userService.findUserById("grom@gmx.de");
        if (userContent.isPresent()) {
            super.performGetRequest("/profile/public/grom@gmx.de")
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("userContent", UserDTO.mapUserToUserDto(userContent.get())))
                    .andExpect(model().attribute("userDto", UserDTO.mapUserToUserDto(loggedInUser)))
                    .andExpect(model().attribute("cacheControl", "no-cache"));
        } else {
            fail("user for test-request not found");
        }
    }

    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void showEditProfilePage() throws Exception {
        super.performGetRequest("/profile/private/me")
                .andExpect(status().isOk())
                .andExpect(model().attribute("userDto", loggedInUserDto))
                .andExpect(model().attribute("userContent", loggedInUserDto));
    }

    /**
     * expect login-page, cause request protected resources as unautheticated
     * needs to be login at first, not depend about which kind of resource
     * has been requested
     *
     * @throws Exception may be thrown
     */
    
    @Test
    public void showEditProfilePageForUnknownUserAsUnauthenticated() throws Exception {
        final MvcResult mvcResult = super.performGetRequest("/profile/private/me")
                .andReturn();
        final String location = mvcResult.getResponse().getHeader("location");
        final ResultActions result = sendRedirect(location.replace("+", ""));
        result.andExpect(view().name("auth/login"))
                .andExpect(status().is(HttpStatus.OK.value()));

    }

    /**
     * only for testcase new email will be keeped in db, cause get-request on
     * activation looks wo this email and when no one is present, error will be
     * thrown
     */
    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void showChangeEmailPage() throws Exception {
        loggedInUserDto.setNewEmail("");
        super.performGetRequest("/profile/private/me/update/email")
                .andExpect(status().isOk())
                .andExpect(model().attribute("userDto", loggedInUserDto))
                .andExpect(model().attribute("userContent", loggedInUserDto))
                .andExpect(view().name("auth/emailChange"));
    }

    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void showForgotPasswordView() throws Exception {
        loggedInUserDto.setNewEmail("");
        super.performGetRequest("/profile/user/recovering")
                .andExpect(status().isOk())
                .andExpect(model().attribute("userDto", UserDTO.builder().email("notLoggedIn").build()))
                .andExpect(model().attribute("userContent", UserDTO.builder().firstName("Guest").build()))
                .andExpect(view().name("recover/recoverUserPwRequest"));
    }

    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void showChangePasswordPage() throws Exception {
        super.performGetRequest("/profile/private/me/password")
                .andExpect(status().isOk())
                .andExpect(model().attribute("userDto", loggedInUserDto))
                .andExpect(model().attribute("userContent", loggedInUserDto))
                .andExpect(view().name("auth/passwordChange"));
    }

    @DirtiesContext(methodMode = AFTER_METHOD)
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void saveChangesOnAuthUserOK() throws Exception {
        String body = "firstName=barucdavid&" +
                "email=kaproma@yahoo.de&" +
                "secondName=rka.odem&" +
                "aliasName=worker";
        final MvcResult mvcResult = super.performPutRequest("/profile/private/me/update", body)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/private"))
                .andExpect(flash().attribute("success", true))
                .andExpect(flash().attribute("redirectMessage", "your profile has been updated"))
                .andExpect(flash().attributeExists("success"))
                .andReturn();
        loggedInUserDto.setSecondName("rka.odem");

        Map<String, Object> flashAttributes = new HashMap<>();
        flashAttributes.put("success", true);
        loggedInUser = null;
        initilizeLoggedInAndContentUsers();
        performGetRequestWithFalshAttributes(mvcResult.getResponse().getRedirectedUrl(), flashAttributes)
                .andExpect(view().name("auth/profileLinks"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userDto", loggedInUserDto))
                .andExpect(model().attribute("userContent", loggedInUserDto))
                .andExpect(model().attribute("success", true));
    }


    /**
     * @throws Exception may be thrown
     */
    //
    @DirtiesContext(methodMode = AFTER_METHOD)
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void changeUserEmailOK() throws Exception {
        String body = "email=kaproma@yahoo.de&" +
                "newEmail=romakap@yahoo.de&" +
                "password=roman&" +
                "confirmPassword=roman";
        super.performPatchRequest("/profile/private/me/update/email", body)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/private"))
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("success", true))
                .andExpect(flash().attribute("redirectMessage", "you got email, check it out!"))
                .andReturn();
    }
    @DirtiesContext(methodMode = AFTER_METHOD)
    @Test
    public void activateChangedEmail() throws Exception {
        final Optional<User> user = super.userService.findUserById("kaproma@yahoo.de");
        user.map(userMailChanged -> {
            userMailChanged.setNewEmail("romakap@yahoo.de");
            return super.userService.save(userMailChanged);
        }).orElseThrow(() -> new ServiceException("Testuser konnte nicht gespeichert werden"));
        super.performGetRequest("/mailchange/romakap@yahoo.de/activation")
                .andExpect(status().is(302))
                .andExpect(flash().attribute("redirectMessage", "Your new email is active! Please login"))
                .andExpect(redirectedUrl("/links"));
    }

    /**
     * old and new email are equal ==> wrong password is wrong
     *
     * @throws Exception may be thrown
     */
    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void changeUserEmailNotOK() throws Exception {
        String body = "email=kaproma@yahoo.de&newEmail=kaproma@yahoo.de&password=doman&confirmPassword=doman";
        super.performPatchRequest("/profile/private/me/update/email", body)
                .andExpect(status().is(400))
                .andExpect(view().name("auth/emailChange"))
                .andExpect(model().errorCount(2))
                .andExpect(globalErrors().hasOneGlobalError("userDTO",
                        "Old and new email must be different"))
                .andExpect(model().attributeHasFieldErrorCode("userDTO",
                        "password", "CorrectPassword"));

    }

    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void changeUserEmailToSmall() throws Exception {
        String body = "email=kaproma@yahoo.de&newEmail=r@e&password=doman&confirmPassword=doman";
        super.performPatchRequest("/profile/private/me/update/email", body)
                .andExpect(status().is(400)).andExpect(view().name("auth/emailChange"))
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "password", "CorrectPassword"))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "newEmail", "Size"));

    }

    /**
     * validator vor NotEmpty confirmPw invokes pw-and-confirmPw matcher
     *
     * @throws Exception may be thrown
     */

    @DirtiesContext(methodMode = BEFORE_METHOD)
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void changeUserEmailNoEmail() throws Exception {
        String body = "email=kaproma@yahoo.de&" +
                "password=roman";
        super.performPatchRequest("/profile/private/me/update/email", body)
                .andExpect(status().is(400)).andExpect(view().name("auth/emailChange"))
                .andExpect(model().errorCount(3))
                .andExpect(globalErrors().hasTwoGlobalErrors("userDTO",
                        "Old and new email must be different"))
                .andExpect(globalErrors().hasTwoGlobalErrors("userDTO",
                        "Password and password confirmation do not match"))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "newEmail", "NotEmpty"));

    }

    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void changeUserEmailToBig() throws Exception {
        String body = "email=kaproma@yahoo.de&" +
                "newEmail=radfadfadfsddsdfadfadfsdsdfs@de&" +
                "password=doman&" +
                "confirmPassword=doman";
        super.performPatchRequest("/profile/private/me/update/email", body)
                .andExpect(status().is(400))
                .andExpect(view().name("auth/emailChange"))
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "password", "CorrectPassword"))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "newEmail", "Size"));
    }

    /**
     * old and new email are equal ==> wrong password is wrong confirm-password and
     * password is not equal
     *
     * @throws Exception may be thrown
     */
    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void changeUserEmailNotOKAllErrors() throws Exception {
        String body = "email=kaproma@yahoo.de&" +
                "newEmail=kaproma@yahoo.de&" +
                "password=doman&" +
                "confirmPassword=soman";
        super.performPatchRequest("/profile/private/me/update/email", body)
                .andExpect(status().is(400))
                .andExpect(view().name("auth/emailChange"))
                .andExpect(globalErrors().hasTwoGlobalErrors("userDTO",
                        "Old and new email must be different"))
                .andExpect(globalErrors().hasTwoGlobalErrors("userDTO",
                        "Password and password confirmation do not match"))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "password", "CorrectPassword"))
                .andExpect(model().errorCount(3));
    }

    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void saveChangesOnAuthUserWithValidationChangeUserGroup() throws Exception {
        String body = "firstName=barucdavid&" +
                "email=kaproma@yahoo.de&" +
                "secondName=rka.odem&" +
                "aliasName=worlord";
        super.performPutRequest("/profile/private/me/update", body)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/private"))
                .andExpect(flash().attributeExists("success"));
    }

    /**
     * old pw is ok new pw is ok
     */
    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void saveAuthUserWithValidationChangeUserPasswordGroupOK() throws Exception {
        String body = "password=roman&confirmNewPassword=rororo&newPassword=rororo";
        super.performPutRequest("/profile/private/me/password", body)
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/profile/private"))
                .andExpect(flash().attribute("success", true));
    }

    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void saveAuthUserWithValidationChangeUserPasswordGroupFalseMethod() throws Exception {
        String body = "email=kaproma@yahoo.de&password=roman&confirmNewPassword=rororo&newPassword=rororo";
        final MvcResult mvcResult = super.performPostRequest("/profile/private/me/password", body)
                .andReturn();
        final String encodedUrl = mvcResult.getResponse().getHeader("location");
        final ResultActions result = sendRedirect(encodedUrl.replace("+", ""));
        result.andExpect(status().is(HttpStatus.METHOD_NOT_ALLOWED.value()))
                .andExpect(view().name("error/pageNotFound"));
    }

    /**
     * @author RKA passwordchange with wrong old password
     */
    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void changePasswordWrongOldPassword() throws Exception {
        String body = "email=kaproma@yahoo.de&" +
                "password=ronan&" +
                "confirmNewPassword=rororo&" +
                "newPassword=rororo";
        super.performPutRequest("/profile/private/me/password", body)
                .andExpect(status().is(400))
                .andExpect(view().name("auth/passwordChange"))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "password", "CorrectPassword"));
    }

    /**
     * @author RKA passwordchange with new-password and new-password-confirmation
     * not equal
     */
    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void changePasswordNewPwNotEqualNewPWConfirm() throws Exception {
        String body = "email=kaproma@yahoo.de&password=ronan&confirmNewPassword=rororo&newPassword=bobobo";
        super.performPutRequest("/profile/private/me/password", body)
                .andExpect(status().is(400))
                .andExpect(view().name("auth/passwordChange"))
                .andExpect(model().attributeHasErrors("userDTO"));

    }

    /**
     * old and new password should not be equal
     *
     * @throws Exception may be thrown
     */
    
    @Test
    @WithUserDetails("kaproma@yahoo.de")
    public void changePasswordNewAndOldShouldNotBeEqual() throws Exception {
        String body = "password=roman&confirmNewPassword=roman&newPassword=roman";
        super.performPutRequest("/profile/private/me/password", body)
                .andExpect(status().is(400))
                .andExpect(view().name("auth/passwordChange"));
    }

}
