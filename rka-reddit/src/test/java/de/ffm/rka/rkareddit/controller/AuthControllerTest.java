package de.ffm.rka.rkareddit.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.beans.HasProperty;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test; 
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.resultmatcher.GlobalResultMatcher;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.service.UserService;
import static de.ffm.rka.rkareddit.resultmatcher.GlobalResultMatcher.globalErrors;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, 
				classes = SpringSecurityTestConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthControllerTest {

	private MockMvc mockMvc;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private WebApplicationContext context;
	
	
	@Before
	public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
									.apply(springSecurity())
									.build();
	}

	@SuppressWarnings("unchecked")
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void showProfileOfUserAsAutheticated() throws Exception {
		User user = userService.getUserWithLinks("romakapt@gmx.de");
		List<Link> posts = user.getUserLinks();
		List<Comment> comments = userService.getUserWithComments("romakapt@gmx.de").getUserComments();		
		ResultActions resultActions = this.mockMvc.perform(get("/profile/private"))
													.andDo(print());
		MvcResult result = resultActions.andReturn();
	    assertTrue(posts.containsAll((List<Link>) result.getModelAndView().getModel().get("posts")));
	    assertTrue(comments.containsAll((List<Comment>) result.getModelAndView().getModel().get("comments")));
	}
	
	/**
	 * @author RKA
	 */
	@Test
	public void registerNewInvalidUser() throws Exception {

	    	this.mockMvc.perform(MockMvcRequestBuilders.post("/registration")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.param("firstName", "Paul")
								.param("secondName", "Grom")
								.param("aliasName", "grünes")
								.param("password", "tata")
								.param("confirmPassword", "tata"))
	    					.andDo(print())
							.andExpect(status().is(400))
							.andExpect(view().name("auth/register"));
	}
	
	

	
	/**
	 * @author RKA
	 */
	@Test
	public void registerFailPwToShortNewUser() throws Exception {

	    	this.mockMvc.perform(post("/registration")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.param("firstName", "Plau")
								.param("secondName", "Grbn")
								.param("aliasName", "grünes")
								.param("email", "Grbein@com.de")
								.param("password", "tata")
								.param("confirmPassword", "tata"))
	    					.andDo(print())
							.andExpect(status().is(400))
							.andExpect(model().attributeHasFieldErrorCode("userDTO", "password","Size"))
							.andExpect(view().name("auth/register"));
	}
	
	/**
	 * @author RKA
	 */
	@Test
	public void registerFailFirstPwSecondPwAreNotMatched() throws Exception {

	    	this.mockMvc.perform(post("/registration")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.param("firstName", "Plau")
								.param("secondName", "Grbn")
								.param("aliasName", "grünes")
								.param("email", "Grbein@com.de")
								.param("password", "tatata")
								.param("confirmPassword", "tutata"))
	    					.andDo(print())
							.andExpect(globalErrors().hasOneGlobalError("userDTO", "Password and password confirmation do not match")) 
							.andExpect(view().name("auth/register"));
	}
	
	@Test
	public void registerNewUserSuccess() throws Exception {

    	this.mockMvc.perform(post("/registration")
							.contentType(MediaType.APPLICATION_FORM_URLENCODED)
							.param("firstName", "Plau")
							.param("secondName", "Grbn")
							.param("aliasName", "grünes")
							.param("email", "Grbein@com.de")
							.param("password", "tatatata")
							.param("confirmPassword", "tatatata"))
    					.andDo(print())
						.andExpect(status().is3xxRedirection())
						.andExpect(flash().attribute("success", true));
	}
	
	@Test
	public void showRegisterViewAsUnautheticatedTest() throws Exception { 
			UserDTO user = UserDTO.builder().build();
            this.mockMvc.perform(get("/registration"))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(model().attribute("userDto", user))
					.andExpect(view().name("auth/register"));  
	}
	
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void showRegisterViewAsAutheticatedTest() throws Exception {
			this.mockMvc.perform(get("/registration"))
					.andDo(print())
					.andExpect(status().is(403))
					.andExpect(forwardedUrl("/links/"));  
	}

	@Test
	public void activateAccountTest() throws Exception {		
            this.mockMvc.perform(get("/activation/romakapt@gmx.de/activation"))
					.andDo(print())
					.andExpect(view().name("auth/activated"));  
	}
	
	@Test
	public void changeEmailFromLinkTest() throws Exception {		
            this.mockMvc.perform(get("/mailchange/romakapt@gmx.de/activation"))
					.andDo(print())
					.andExpect(view().name("auth/activated"));  
	}
	
	@Test
	public void activateInvalidAccountTest() throws Exception {		
            this.mockMvc.perform(get("/activation/romakapt@gmx.de/actiion"))
					.andDo(print())
					.andExpect(redirectedUrl("/error/registrationError"));  
	}
	
	/**
	 * show public profile from grom as autheticated user
	 */
	@Test
	public void showPublicProfileAsUnautheticated() throws Exception {
		Optional<User> user = userService.findUserById("grom@gmx.de");
		if(user.isPresent()) {
        this.mockMvc.perform(get("/profile/public/grom@gmx.de"))
				.andDo(print())
				.andExpect(view().name("auth/profile"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("userContent", user.get()));  
		} else {
			fail("user for test-request not found");
		}
	}
	
	/**
	 * show public non existing profile from grm as autheticated user
	 */
	@Test
	public void showPublicNoNexistedProfileAsUnautheticated() throws Exception {		
        this.mockMvc.perform(get("/profile/public/grm@gmx.de"))
				.andDo(print())
				.andExpect(status().is(401))
				.andExpect(view().name("error/application"));  
	}

	@Test
	public void showPrivateProfileAsUnautheticated() throws Exception {		
        this.mockMvc.perform(get("/profile/private/romakapt@gmx.de"))
				.andDo(print())
				.andExpect(status().isNotFound());  
	}
	
	/**
	 * show profile site of login user
	 */
	@Test
	@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
	@WithUserDetails("romakapt@gmx.de")
	public void showPrivateProfileAsAutheticated() throws Exception {
		Optional<User> user = userService.findUserById("romakapt@gmx.de");
        if(user.isPresent()) {
        	UserDTO userDto = modelMapper.map(user.get(), UserDTO.class); 
        	this.mockMvc.perform(get("/profile/private"))
						.andDo(print())
						.andExpect(status().isOk())
						.andExpect(model().attribute("userContent", user.get()))
						.andExpect(model().attribute("userDto", userDto));
        } else {
        	fail("user for test-request not found");
        }  
	}
	
	/**
	 * show profile pulic site of some user as autheticated user
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void showPublicProfileAsAutheticated() throws Exception {
		Optional<User> user = userService.findUserById("grom@gmx.de");
        if(user.isPresent()) {
        	this.mockMvc.perform(get("/profile/public/grom@gmx.de"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(model().attribute("userContent", user.get()));
        } else {
        	fail("user for test-request not found");
        }  
	}
	
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void showEditProfilePage() throws Exception {
		Optional<User> user = userService.findUserById("romakapt@gmx.de");
        if(user.isPresent()) {
        	UserDTO userDto = modelMapper.map(user.get(), UserDTO.class); 
        	this.mockMvc.perform(get("/profile/private/me/romakapt@gmx.de"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(model().attribute("userDto", userDto));
        } else {
        	fail("user for test-request not found");
        }  
	}
	
	/**
	 * only for testcase new email will be keeped in db,
	 * cause get-request on activation looks wo this email
	 * and when no one is present, error will be thrown
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void showChangeEmailPage() throws Exception {
		Optional<User> user = userService.findUserById("romakapt@gmx.de");
		if(user.isPresent()) {
			user.get().setNewEmail(StringUtils.EMPTY); 
        	UserDTO userDto = modelMapper.map(user.get(), UserDTO.class); 
        	this.mockMvc.perform(get("/profile/private/me/update/romakapt@gmx.de"))
			.andDo(print())
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
		Optional<User> user = userService.findUserById("romakapt@gmx.de");
        if(user.isPresent()) {
        	UserDTO userDto = modelMapper.map(user.get(), UserDTO.class); 
        	this.mockMvc.perform(get("/profile/private/me/romakapt@gmx.de/password"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(model().attribute("userDto", userDto))
			.andExpect(view().name("auth/passwordChange"));
        } else {
        	fail("user for test-request not found");
        }  
	}
	
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void showEditProfilePageForKnownUser() throws Exception {
		Optional<User> user = userService.findUserById("romakapt@gmx.de");
        if(user.isPresent()) { 
        	this.mockMvc.perform(get("/profile/private/me/romapt@gmx.de"))
			.andDo(print())
			.andExpect(status().is(401))
			.andExpect(view().name("error/application"));
        } else {
        	fail("user for test-request not found");
        }  
	}
	
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void saveChangesOnAuthUserOK() throws Exception {
		Optional<User> user = userService.findUserById("romakapt@gmx.de");      
		if(user.isPresent()) { 
			this.mockMvc.perform(MockMvcRequestBuilders.put("/profile/private/me/update")
        			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        			.param("firstName", "baruc-david")
        			.param("email", "romakapt@gmx.de")
        			.param("secondName", "rka.odem")
        			.param("aliasName", "worker"))
        			.andDo(print())
			        .andExpect(status().is3xxRedirection())
			        .andExpect(redirectedUrl("/profile/private"))
			        .andExpect(flash().attributeExists("success"))
			        .andReturn();
        } else {
        	fail("user for test-request not found");
        }  
	}
	
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void changeUserEmailOK() throws Exception {
		Optional<User> user = userService.findUserById("romakapt@gmx.de");      
		if(user.isPresent()) { 
			this.mockMvc.perform(MockMvcRequestBuilders.patch("/profile/private/me/update/romakapt@gmx.de")
        			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        			.param("email", "romakapt@gmx.de")
        			.param("newEmail", "kaproma@yahoo.de")
        			.param("password", "roman")
					.param("confirmPassword", "roman"))
        			.andDo(print())
			        .andExpect(status().is3xxRedirection())
			        .andExpect(redirectedUrl("/logout"))
			        .andExpect(flash().attributeExists("success"))
			        .andReturn();
        } else {
        	fail("user for test-request not found");
        }  
	}
	
	/**
	 * old and new email are equal ==> wrong
	 * password is wrong
	 * @throws Exception
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void changeUserEmailNotOK() throws Exception {
		Optional<User> user = userService.findUserById("romakapt@gmx.de");      
		if(user.isPresent()) { 
			this.mockMvc.perform(MockMvcRequestBuilders.patch("/profile/private/me/update/romakapt@gmx.de")
        			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        			.param("email", "romakapt@gmx.de")
        			.param("newEmail", "romakapt@gmx.de")
        			.param("password", "doman")
					.param("confirmPassword", "doman"))
        			.andDo(print())
			        .andExpect(status().is(400))
			        .andExpect(view().name("auth/emailChange"))
			        .andExpect(model().errorCount(2))
			        .andExpect(globalErrors().hasOneGlobalError("userDTO", "Old and new email must be different"))
			        .andExpect(model().attributeHasFieldErrorCode("userDTO", "password", "CorrectPassword"));

        } else {
        	fail("user for test-request not found");
        }  
	}
	
	/**
	 * old and new email are equal ==> wrong
	 * password is wrong
	 * confirm-password and password is not equal
	 * @throws Exception
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void changeUserEmailNotOKAllErrors() throws Exception {
	
 		Optional<User> user = userService.findUserById("romakapt@gmx.de");      
 		if(user.isPresent()) { 
 			this.mockMvc.perform(MockMvcRequestBuilders.patch("/profile/private/me/update/romakapt@gmx.de")
         			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
         			.param("newEmail", "romakapt@gmx.de")
         			.param("password", "doman")
 					.param("confirmPassword", "soman"))
         			.andDo(print())
 			        .andExpect(status().is(400))
 			        .andExpect(view().name("auth/emailChange"))
 			        .andExpect(model().errorCount(3))
 			        .andExpect(globalErrors().hasTwoGlobalErrors("userDTO", "Old and new email must be different"))
 			        .andExpect(globalErrors().hasTwoGlobalErrors("userDTO", "Password and password confirmation do not match"))
 			        .andExpect(model().attributeHasFieldErrorCode("userDTO", "password", "CorrectPassword"));
 
         } else {
         	fail("user for test-request not found");
         }  
	}
	
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void saveChangesOnAuthUserWithValidationChangeUserGroup() throws Exception {
		Optional<User> user = userService.findUserById("romakapt@gmx.de");      
		if(user.isPresent()) { 
        	this.mockMvc.perform(MockMvcRequestBuilders.put("/profile/private/me/update")
		        			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		        			.param("firstName", "baruc-david")
		        			.param("email", "romakapt@gmx.de")
		        			.param("secondName", "rka.odem")
		        			.param("aliasName", "wor"))
        			.andDo(print())
			        .andExpect(status().is3xxRedirection())
			        .andExpect(redirectedUrl("/profile/private/me/romakapt@gmx.de"))
			        .andExpect(flash().attributeExists("bindingError"));
        } else {
        	fail("user for test-request not found");
        }  
	}
	
	/**
	 * old pw is ok
	 * new pw is ok
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void saveAuthUserWithValidationChangeUserPasswordGroupOK() throws Exception {
		Optional<User> user = userService.findUserById("romakapt@gmx.de");      
		if(user.isPresent()) { 
        	this.mockMvc.perform(MockMvcRequestBuilders.put("/profile/private/me/romakapt@gmx.de/password")
		        			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		        			.param("email", "romakapt@gmx.de")
		        			.param("password", "roman")
		        			.param("confirmNewPassword", "rororo")
		        			.param("newPassword", "rororo"))
        			.andDo(print())
			        .andExpect(status().is3xxRedirection())
			        .andExpect(view().name("redirect:/profile/private"))
        			.andExpect(flash().attribute("success", true));
        } else {
        	fail("user for test-request not found");
        }  
	}
	
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void saveAuthUserWithValidationChangeUserPasswordGroupFalseMethod() throws Exception {
		Optional<User> user = userService.findUserById("romakapt@gmx.de");      
		if(user.isPresent()) { 
        	this.mockMvc.perform(MockMvcRequestBuilders.post("/profile/private/me/romakapt@gmx.de/password")
		        			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		        			.param("email", "romakapt@gmx.de")
		        			.param("password", "roman")
		        			.param("confirmNewPassword", "rororo")
		        			.param("newPassword", "rororo"))
        			.andDo(print())
			        .andExpect(status().is(404))
			        .andExpect(view().name("error/pageNotFound"));
        } else {
        	fail("user for test-request not found");
        }  
	}
	
	/**
	 * @author RKA
	 * send register request as autheticated user
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void registerAsAutheticated() throws Exception {

	    	this.mockMvc.perform(MockMvcRequestBuilders.post("/registration")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.param("firstName", "Paul")
								.param("secondName", "Grom")
								.param("aliasName", "grünes")
								.param("password", "tata")
								.param("confirmPassword", "tata"))
	    					.andDo(print())
							.andExpect(status().is(403))
							.andExpect(forwardedUrl("/links/"));
	}

	/**
	 * @author RKA
	 * passwordchange with wrong old password
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void changePasswordWrongOldPassword() throws Exception {
		Optional<User> user = userService.findUserById("romakapt@gmx.de");
		if(user.isPresent()) { 
        	this.mockMvc.perform(MockMvcRequestBuilders.put("/profile/private/me/romakapt@gmx.de/password")
		        			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		        			.param("email", "romakapt@gmx.de")
		        			.param("password", "ronan")
		        			.param("confirmNewPassword", "rororo")
		        			.param("newPassword", "rororo"))
        			.andDo(print())
			        .andExpect(status().is(400))
			        .andExpect(view().name("auth/passwordChange"))
        			.andExpect(model().attributeHasFieldErrorCode("userDTO", "password", "CorrectPassword"));
        } else {
        	fail("user for test-request not found");
        }  
	}
	
	/**
	 * @author RKA
	 * passwordchange with new-password and new-password-confirmation not equal
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void changePasswordNewPwNotEqualNewPWConfirm() throws Exception {
		Optional<User> user = userService.findUserById("romakapt@gmx.de");
		if(user.isPresent()) { 
        	this.mockMvc.perform(MockMvcRequestBuilders.put("/profile/private/me/romakapt@gmx.de/password")
		        			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		        			.param("email", "romakapt@gmx.de")
		        			.param("password", "roman")
		        			.param("confirmNewPassword", "rororo")
		        			.param("newPassword", "bobobo"))
        			.andDo(print())
			        .andExpect(status().is(400))
			        .andExpect(view().name("auth/passwordChange"))
        			.andExpect(model().attributeHasErrors("userDTO"));
        } else {
        	fail("user for test-request not found");
        }  
	}
	
	/**
	 * old and new password should not be equal
	 * @throws Exception
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void changePasswordNewAndOldShouldNotBeQual() throws Exception {
		Optional<User> user = userService.findUserById("romakapt@gmx.de");
		if(user.isPresent()) { 
        	this.mockMvc.perform(MockMvcRequestBuilders.put("/profile/private/me/romakapt@gmx.de/password")
		        			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		        			.param("email", "romakapt@gmx.de")
		        			.param("password", "roman")
		        			.param("confirmNewPassword", "roman")
		        			.param("newPassword", "roman"))
        			.andDo(print())
			        .andExpect(status().is(400))
			        .andExpect(view().name("auth/passwordChange"));
         } else {
        	fail("user for test-request not found");
        }  
	}
}
