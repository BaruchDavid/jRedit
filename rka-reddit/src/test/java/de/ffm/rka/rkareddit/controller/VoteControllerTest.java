package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class VoteControllerTest extends  MvcRequestSender{

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void increaseVoteOnIllegleLink() throws Exception {
		final MvcResult mvcResult = super.performGetRequest("/link/01010/vote/direction/1/votecount/1")
				.andExpect(status().isBadRequest())
				.andReturn();
		Assert.assertEquals("illegal vote!", mvcResult.getResponse().getContentAsString());


		}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void increaseVoteOnNotExistentLink() throws Exception {
		final MvcResult mvcResult = super.performGetRequest("/link/987654321911499/vote/direction/1/votecount/1")
				.andExpect(status().isBadRequest())
				.andReturn();
		Assert.assertEquals("illegal vote!", mvcResult.getResponse().getContentAsString());

	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void increaseVoteWithIllegleCount() throws Exception {
		Link currentLink = entityManager.find(Link.class, 1l);
		LinkDTO linkDTO = LinkDTO.mapFullyLinkToDto(currentLink);
		MvcResult mvcResult = super.performGetRequest("/link/"+linkDTO.getLinkSignature()+"/vote/direction/1/votecount/10")
				.andExpect(status().is(400))
				.andReturn();
		mvcResult.getResponse().getContentAsString().equals(10);
	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void increaseVoteWithIllegleDirection() throws Exception {
		Link currentLink = entityManager.find(Link.class, 1l);
		LinkDTO linkDTO = LinkDTO.mapFullyLinkToDto(currentLink);
		MvcResult mvcResult = super.performGetRequest("/link/"+linkDTO.getLinkSignature()+
													"/vote/direction/10/votecount/1")
							.andExpect(status().is(400))
							.andReturn();
		mvcResult.getResponse().getContentAsString().equals(1);

	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void increaseVoteWithIllegleNegativeDirection() throws Exception {
		Link currentLink = entityManager.find(Link.class, 1l);
		LinkDTO linkDTO = LinkDTO.mapFullyLinkToDto(currentLink);
		MvcResult mvcResult = super.performGetRequest("/link/"+linkDTO.getLinkSignature()+
														"/vote/direction/-2/votecount/1")
				.andExpect(status().is(400))
				.andReturn();
		mvcResult.getResponse().getContentAsString().equals(1);

	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void increaseVoteWithIllegleDirectionAndIllegleDirection() throws Exception {
		Link currentLink = entityManager.find(Link.class, 1l);
		LinkDTO linkDTO = LinkDTO.mapFullyLinkToDto(currentLink);
		MvcResult mvcResult = super.performGetRequest("/link/"+linkDTO.getLinkSignature()+
														"/vote/direction/10/votecount/3")
				.andExpect(status().is(400))
				.andReturn();
			/*MvcResult mvcResult = this.mockMvc.perform(get("/link/"+linkDTO.getLinkSignature()+"/vote/direction/10/votecount/3"))
					.andDo(print())
					.andExpect(status().is(400))
					.andReturn();*/
		mvcResult.getResponse().getContentAsString().equals(3);
	}

	/**
	 * increase on one point
	 * @throws Exception
	 */
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void increaseVote() throws Exception {
		Link currentLink = entityManager.find(Link.class, 1l);
		LinkDTO linkDTO = LinkDTO.mapFullyLinkToDto(currentLink);
		MvcResult mvcResult = super.performGetRequest("/link/"+
														linkDTO.getLinkSignature()+"/vote/direction/1/votecount/"
														+linkDTO.getVoteCount())
				.andDo(print())
				.andExpect(status().is(200))
				.andReturn();
		mvcResult.getResponse().getContentAsString().equals(6);
	}

}
