package de.ffm.rka.rkareddit.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;

/**
 * Testclass for Servicelayer
 * @author kaproma
 *
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class LinkServiceTest {

	@Autowired
    private LinkService linkService ;
	private static final Logger LOGGER = LoggerFactory.getLogger(LinkServiceTest.class);
	
	/**
	 * test for test_env for user on service-layer
	 */
	@Test
	public void linkSizeForUserOne() {
		User user = new User();
		user.setUserId(1l);
		assertEquals(5l, linkService.findAllByUser(user));
	}
	/**
	 * test for test_env
	 * expected 11 links.
	 */
	@Test
	public void findAllLinksAllCommentsAllVotesForEachUser() {
		Pageable firstPageWithElevenElements = PageRequest.of(0, 11);
		Page<Link> links = linkService.fetchAllLinksWithUsersCommentsVotes(firstPageWithElevenElements);		
		assertEquals(11l, links.getNumberOfElements());
		assertEquals(2l, ((Link)links.getContent().get(0)).getComments().size());
		assertEquals(4l, ((Link)links.getContent().get(0)).getVote().size());
		assertEquals("romakapt@gmx.de", ((Link)links.getContent().get(0)).getUser().getUsername());
			
		assertEquals(6l, ((Link)links.getContent().get(1)).getComments().size());
		assertEquals(0l, ((Link)links.getContent().get(1)).getVote().size());
		
		assertEquals(3l, ((Link)links.getContent().get(2)).getComments().size());
		assertEquals(2l, ((Link)links.getContent().get(2)).getVote().size());
		
		assertEquals(2l, ((Link)links.getContent().get(3)).getComments().size());
		assertEquals(0l, ((Link)links.getContent().get(3)).getVote().size());
		
		assertEquals(2l, ((Link)links.getContent().get(4)).getComments().size());
		assertEquals(3l, ((Link)links.getContent().get(4)).getVote().size());
		
		assertEquals(1l, ((Link)links.getContent().get(5)).getComments().size());
		assertEquals(0l, ((Link)links.getContent().get(5)).getVote().size());
		
		assertEquals(0l, ((Link)links.getContent().get(6)).getComments().size());
		assertEquals(0l, ((Link)links.getContent().get(6)).getVote().size());
	
		assertEquals(0l, ((Link)links.getContent().get(7)).getComments().size());
		assertEquals(0l, ((Link)links.getContent().get(7)).getVote().size());
		
		assertEquals(0l, ((Link)links.getContent().get(8)).getComments().size());
		assertEquals(0l, ((Link)links.getContent().get(8)).getVote().size());
		
		assertEquals(0l, ((Link)links.getContent().get(9)).getComments().size());
		assertEquals(0l, ((Link)links.getContent().get(9)).getVote().size());
		
		assertEquals(0l, ((Link)links.getContent().get(10)).getComments().size());
		assertEquals(0l, ((Link)links.getContent().get(10)).getVote().size());
		
		
		links.forEach(link -> {
			LOGGER.info("Link found {}", link.toString());
			LOGGER.info("link vote size {} ",  link.getVote().size());
			LOGGER.info("link comment size {} ", link.getComments().size());
			LOGGER.info("=========================== ");
			
		});
	}

}
