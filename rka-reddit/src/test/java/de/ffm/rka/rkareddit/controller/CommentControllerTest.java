package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.repository.LinkRepository;
import de.ffm.rka.rkareddit.util.StringUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CommentControllerTest extends MvcRequestSender{


	@Autowired
	private LinkRepository linkRepository;

	LinkDTO linkDTO;

	@Before
	public void setup() {
		linkDTO = LinkDTO.mapFullyLinkToDto(linkRepository.findByLinkId(1).get());
	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void shouldReturnAllLinks() throws Exception {
		UserDTO userDto = UserDTO.builder()
								.firstName("baruc-david")
								.secondName("rka")
								.build();
		List<Integer> pages = Arrays.asList(new Integer[] {1,2});
		MvcResult result = super.performGetRequest("/links/")
				.andExpect(status().isOk())
				.andExpect(model().attribute("pageNumbers", pages))
				.andReturn();
		UserDTO usr = (UserDTO) result.getModelAndView().getModel().get("userDto");
    	assertEquals(userDto.getFullName(), usr.getFullName());
	}
	
	@Test
	@WithAnonymousUser
	public void shouldReturnAllLinksForAnonymous() throws Exception {

		List<Integer> pages = Arrays.asList(new Integer[] {1,2});
		MvcResult result = super.performGetRequest("/links/")
								.andExpect(status().isOk())
								.andExpect(model().attribute("pageNumbers", pages))
								.andReturn();
		assertTrue(!result.getResponse().getContentAsString().contains("Submit Link"));
	}
	
	@Test
	public void shouldReturnAllLinksForCURL() throws Exception {
		List<Integer> pages = Arrays.asList(new Integer[] {1,2});
		MvcResult result = super.performGetRequest("/links/")
										.andExpect(status().isOk())
										.andExpect(model().attribute("pageNumbers", pages))
										.andReturn();
		assertTrue(!result.getResponse().getContentAsString().contains("Submit Link"));
	}

	/**
	 * @author RKA
	 * testing new post of valid comment
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void postNewComment() throws Exception {

		String body = "lSig="+linkDTO.getLinkSignature()+"&commentText="+StringUtil.generateRandomString(600);
		super.performPostRequest("/comments/comment", body)
									.andExpect(status().is3xxRedirection())
									.andExpect(redirectedUrlPattern("/links/link/*1"))
									.andExpect(flash().attributeExists("success"));
	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void rejectToBigComment() throws Exception {
		LinkDTO linkDTO = LinkDTO.mapFullyLinkToDto(linkRepository.findByLinkId(1).get());
		String body = "lSig="+linkDTO.getLinkSignature()+"&commentText="+StringUtil.generateRandomString(601);
		super.performPostRequest("/comments/comment", body)
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrlPattern("/links/link/*1"))
					.andExpect(flash().attributeExists("error_message"));
	}
	/**
	 * @author RKA
	 * testing new comment with invalid link as Autheticated user
	 * without suitable link
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void rejectCommentWithoutSuitableLinkId() throws Exception {
		String body = "lSig="+null+"&commentText=hallo Kommentar";
		final MvcResult mvcResult = super.performPostRequest("/comments/comment", body)
												.andExpect(status().is(302))
												.andReturn();
		sendRedirect( mvcResult.getResponse().getHeader("location"));
	}
	
	@Test
	public void rejectCommentWithoutSuitableLinkIdAsUnauthenticated() throws Exception {
		String body = "lSig="+null+"&commentText=hallo Kommentar";
		final MvcResult mvcResult = super.performPostRequest("/comments/comment", body)
										.andExpect(status().is(302))
										.andReturn();
		sendRedirect(mvcResult.getResponse().getHeader("location"));

	}


}
