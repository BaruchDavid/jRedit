package de.ffm.rka.rkareddit.controller.user;

import de.ffm.rka.rkareddit.controller.MvcRequestSender;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.CommentDTO;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.ffm.rka.rkareddit.resultmatcher.GlobalResultMatcher.globalErrors;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTest extends MvcRequestSender {

    private User pageContentUser;
    private User loggedInUser;
    private UserDTO loggedInUserDto;

    @Before
    public void setup() {

        if (loggedInUser == null) {
            loggedInUser = userService.findUserById("romakapt@gmx.de").get();
            pageContentUser = userService.getUserWithLinks(loggedInUser.getEmail());
            loggedInUserDto = UserDTO.mapUserToUserDto(loggedInUser);
        }

    }


    @SuppressWarnings("unchecked")
    @Test
    @WithUserDetails("romakapt@gmx.de")
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
                .andExpect(model().attribute("userSince", userSince))
                .andExpect(model().attribute("commentCount", 2))
                .andExpect(model().attribute("cacheControl", StringUtils.EMPTY));
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void showProfileWithLinksOfUserAsAuthenticated() throws Exception {
        final Set<LinkDTO> loggedUserLinks = pageContentUser.getUserLinks()
                .stream()
                .map(LinkDTO::mapFullyLinkToDto)
                .collect(Collectors.toSet());
        String userSince = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .format(loggedInUserDto.getCreationDate());

        super.performGetRequest("/profile/private/links")
                .andExpect(status().isOk())
                .andExpect(view().name("auth/profileLinks"))
                .andExpect(model().attribute("posts", loggedUserLinks))
                .andExpect(model().attribute("userSince", userSince))
                .andExpect(model().attribute("commentCount", 2));

    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void showProfileWithCommentsOfUserAsAuthenticated() throws Exception {
        final Set<CommentDTO> loggedUserComments = pageContentUser.getUserComment()
                .stream()
                .map(CommentDTO::getCommentToCommentDto)
                .collect(Collectors.toSet());

        String userSince = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                .format(loggedInUserDto.getCreationDate());
        super.performGetRequest("/profile/private/comments")
                .andExpect(status().isOk())
                .andExpect(view().name("auth/profileComments"))
                .andExpect(model().attribute("userSince", userSince))
                .andExpect(model().attribute("comments", loggedUserComments));

    }

    /**
     * show public profile from grom as unautheticated user
     * cach-flag: no-cache is set, cause for content-user you will see
     * everytime actual saved picture and not cached picture
     */
    @Test
    public void showPublicProfileAsUnauthenticated() throws Exception {
        User grom = userService.findUserById("grom@gmx.de").orElseGet(() -> new User());
        super.performGetRequest("/profile/public/grom@gmx.de")
                .andExpect(view().name("auth/profileLinks"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userContent", UserDTO.mapUserToUserDto(grom)))
                .andExpect(model().attribute("cacheControl", "no-cache"));
    }

    /**
     * show public non existing profile from grm as unauthenticated user
     */
    // TODO: 27.12.2020 BEI EINEM FEHLER 
    @Test
    public void showPublicNoExistedProfileAsUnauthenticated() throws Exception {
        final MvcResult mvcResult = super.performGetRequest("/profile/public/grm@gmx.de")
               /* .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(view().name("error/basicError"))*/
                .andReturn();
        sendRedirect(mvcResult.getResponse().getHeader("location"));
    }

    @Test
    public void showPrivateProfileAsUnauthenticated() throws Exception {
        super.performGetRequest("/profile/private/romakapt@gmx.de")
                .andDo(print()).andExpect(status().isNotFound());
    }

    /**
     * show profile public site of some user as authenticated user
     * cach-flag: no-cache is set, cause for content-user you will see
     * everytime actual saved picture and not cached picture
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
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
    @WithUserDetails("romakapt@gmx.de")
    public void showEditProfilePage() throws Exception {
        super.performGetRequest("/profile/private/me")
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("userDto", loggedInUserDto));
    }

    @Test
    public void showEditProfilePageForUnknownUserAsUnauthenticated() throws Exception {
        final MvcResult mvcResult = super.performGetRequest("/profile/private/me")
                .andReturn();
        final String location = mvcResult.getResponse().getHeader("location");
        final ResultActions result = sendRedirect(location.replace("+", ""));
        result.andExpect(view().name("error/application"))
            .andExpect(status().is(HttpStatus.SC_UNAUTHORIZED));

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
            super.performGetRequest("/profile/private/me/update/email")
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("userDto", userDto))
                    .andExpect(view().name("auth/emailChange"));
        } else {
            fail("user for test-request not found");
        }
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void showChangePasswordPage() throws Exception {
        super.performGetRequest("/profile/private/me/password")
                .andExpect(status().isOk())
                .andExpect(model().attribute("userDto", loggedInUserDto))
                .andExpect(view().name("auth/passwordChange"));
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void saveChangesOnAuthUserOK() throws Exception {
        String body = "firstName=baruc-david&" +
                "email=romakapt@gmx.de&" +
                "secondName=rka.odem&" +
                "aliasName=worker";
        super.performPutRequest("/profile/private/me/update", body)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/private"))
                .andExpect(flash().attributeExists("success")).andReturn();
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void changeUserEmailOK() throws Exception {
        String body = "email=romakapt@gmx.de&" +
                    "newEmail=kaproma@yahoo.de&" +
                    "password=roman&" +
                    "confirmPassword=roman";
        super.performPatchRequest("/profile/private/me/update/email", body)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/profile/private"))
            .andExpect(flash().attributeExists("success"))
            .andReturn();

    }

    /**
     * old and new email are equal ==> wrong password is wrong
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void changeUserEmailNotOK() throws Exception {
        String body = "email=romakapt@gmx.de&newEmail=romakapt@gmx.de&password=doman&confirmPassword=doman";
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
    @WithUserDetails("romakapt@gmx.de")
    public void changeUserEmailToSmall() throws Exception {
        String body = "email=romakapt@gmx.de&newEmail=r@e&password=doman&confirmPassword=doman";
        super.performPatchRequest("/profile/private/me/update/email", body)
                .andExpect(status().is(400)).andExpect(view().name("auth/emailChange"))
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
        String body = "email=romakapt@gmx.de&" +
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
    @WithUserDetails("romakapt@gmx.de")
    public void changeUserEmailToBig() throws Exception {
        String body = "email=romakapt@gmx.de&" +
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
     * @throws Exception
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void changeUserEmailNotOKAllErrors() throws Exception {
        String body = "email=romakapt@gmx.de&" +
                        "newEmail=romakapt@gmx.de&" +
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
    @WithUserDetails("romakapt@gmx.de")
    public void saveChangesOnAuthUserWithValidationChangeUserGroup() throws Exception {
        String body = "firstName=baruc-david&" +
                "email=romakapt@gmx.de&" +
                "secondName=rka.odem&" +
                "aliasName=wor";
        super.performPutRequest("/profile/private/me/update", body)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/private/me/romakapt@gmx.de"))
                .andExpect(flash().attributeExists("bindingError"));
    }

    /**
     * old pw is ok new pw is ok
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void saveAuthUserWithValidationChangeUserPasswordGroupOK() throws Exception {
        String body = "password=roman&confirmNewPassword=rororo&newPassword=rororo";
        super.performPutRequest("/profile/private/me/password", body)
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/profile/private"))
                .andExpect(flash().attribute("success", true));
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void saveAuthUserWithValidationChangeUserPasswordGroupFalseMethod() throws Exception {
        String body = "email=romakapt@gmx.de&password=roman&confirmNewPassword=rororo&newPassword=rororo";
        final MvcResult mvcResult = super.performPostRequest("/profile/private/me/password", body)
                                         .andReturn();
        sendRedirect(mvcResult.getResponse().getHeader("location"));
               /* .andExpect(status().is(405))
                .andExpect(view().name("error/pageNotFound"));*/

    }

    /**
     * @author RKA passwordchange with wrong old password
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void changePasswordWrongOldPassword() throws Exception {
        String body = "email=romakapt@gmx.de&" +
                        "password=ronan&" +
                        "confirmNewPassword=rororo&" +
                        "newPassword=rororo";
        super.performPutRequest("/profile/private/me/password", body)
                .andDo(print()).andExpect(status().is(400))
                .andExpect(view().name("auth/passwordChange"))
                .andExpect(model().attributeHasFieldErrorCode("userDTO", "password", "CorrectPassword"));
    }

    /**
     * @author RKA passwordchange with new-password and new-password-confirmation
     * not equal
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void changePasswordNewPwNotEqualNewPWConfirm() throws Exception {
        String body = "email=romakapt@gmx.de&password=ronan&confirmNewPassword=rororo&newPassword=bobobo";
        super.performPutRequest("/profile/private/me/password", body)
            .andExpect(status().is(400))
            .andExpect(view().name("auth/passwordChange"))
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
        String body = "password=roman&confirmNewPassword=roman&newPassword=roman";
        super.performPutRequest("/profile/private/me/password", body)
            .andExpect(status().is(400))
            .andExpect(view().name("auth/passwordChange"));
    }

}
