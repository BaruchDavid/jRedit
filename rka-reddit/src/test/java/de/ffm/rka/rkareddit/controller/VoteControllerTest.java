package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.rest.controller.VoteController;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.util.BeanUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringSecurityTestConfig.class
)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class VoteControllerTest {

	@Autowired
	private WebApplicationContext context;

    private MockMvc mockMvc;

	private EntityManager entityManager;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
		entityManager = BeanUtil.getBeanFromContext(EntityManager.class);
	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void increaseVoteOnIllegleLink() throws Exception {
		this.mockMvc.perform(get("/link/01010/vote/direction/1/votecount/1"))
					.andDo(print())
					.andExpect(status().is(400))
					.andExpect(view().name("error/basicError"));
	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void increaseVoteWithIllegleCount() throws Exception {
		Link currentLink = entityManager.find(Link.class, 1l);
		LinkDTO linkDTO = LinkDTO.getMapLinkToDto(currentLink);
		MvcResult mvcResult = this.mockMvc.perform(get("/link/"+linkDTO.getLinkSignature()+"/vote/direction/1/votecount/10"))
				.andDo(print())
				.andExpect(status().is(400))
				.andReturn();
		mvcResult.getResponse().getContentAsString().equals(10);
	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void increaseVoteWithIllegleDirection() throws Exception {
		Link currentLink = entityManager.find(Link.class, 1l);
		LinkDTO linkDTO = LinkDTO.getMapLinkToDto(currentLink);
		MvcResult mvcResult = this.mockMvc.perform(get("/link/"+linkDTO.getLinkSignature()+"/vote/direction/10/votecount/1"))
							.andDo(print())
							.andExpect(status().is(400))
							.andReturn();
		mvcResult.getResponse().getContentAsString().equals(1);

	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void increaseVoteWithIllegleNegativeDirection() throws Exception {
		Link currentLink = entityManager.find(Link.class, 1l);
		LinkDTO linkDTO = LinkDTO.getMapLinkToDto(currentLink);
		MvcResult mvcResult = this.mockMvc.perform(get("/link/"+linkDTO.getLinkSignature()+"/vote/direction/-2/votecount/1"))
				.andDo(print())
				.andExpect(status().is(400))
				.andReturn();
		mvcResult.getResponse().getContentAsString().equals(1);

	}

	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void increaseVoteWithIllegleDirectionAndIllegleDirection() throws Exception {
		Link currentLink = entityManager.find(Link.class, 1l);
		LinkDTO linkDTO = LinkDTO.getMapLinkToDto(currentLink);
		MvcResult mvcResult = this.mockMvc.perform(get("/link/"+linkDTO.getLinkSignature()+"/vote/direction/10/votecount/3"))
				.andDo(print())
				.andExpect(status().is(400))
				.andReturn();
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
		LinkDTO linkDTO = LinkDTO.getMapLinkToDto(currentLink);
		MvcResult mvcResult = this.mockMvc.perform(get("/link/"+
													linkDTO.getLinkSignature()+"/vote/direction/1/votecount/"
													+linkDTO.getVoteCount()))
				.andDo(print())
				.andExpect(status().is(200))
				.andReturn();
		mvcResult.getResponse().getContentAsString().equals(6);
	}

}
