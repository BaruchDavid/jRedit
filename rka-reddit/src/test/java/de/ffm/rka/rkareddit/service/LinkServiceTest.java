package de.ffm.rka.rkareddit.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
		
		List<Link> links = linkService.findAllCommentsForEachLink();		
		assertEquals(11l, links.size());
		assertEquals(2l, links.get(0).getComments().size());
		assertEquals(4l, links.get(0).getVote().size());
		assertEquals("romakapt@gmx.de", links.get(0).getUser().getUsername());
		
				
		assertEquals(6l, links.get(1).getComments().size());
		assertEquals(0l, links.get(1).getVote().size());
		
		assertEquals(3l, links.get(2).getComments().size());
		assertEquals(2l, links.get(2).getVote().size());
		
		assertEquals(2l, links.get(3).getComments().size());
		assertEquals(0l, links.get(3).getVote().size());
		
		assertEquals(2l, links.get(4).getComments().size());
		assertEquals(3l, links.get(4).getVote().size());
		
		assertEquals(1l, links.get(5).getComments().size());
		assertEquals(0l, links.get(5).getVote().size());
		
		assertEquals(0l, links.get(6).getComments().size());
		assertEquals(0l, links.get(6).getVote().size());
	
		assertEquals(0l, links.get(7).getComments().size());
		assertEquals(0l, links.get(6).getVote().size());
		
		assertEquals(0l, links.get(8).getComments().size());
		assertEquals(0l, links.get(6).getVote().size());
		
		assertEquals(0l, links.get(9).getComments().size());
		assertEquals(0l, links.get(6).getVote().size());
		
		assertEquals(0l, links.get(10).getComments().size());
		assertEquals(0l, links.get(6).getVote().size());
		
		
		links.forEach(link -> {
			LOGGER.info("Link found {}", link.toString());
			LOGGER.info("link vote size {} ",  link.getVote().size());
			LOGGER.info("link comment size {} ", link.getComments().size());
			LOGGER.info("=========================== ");
			
		});
	}

}
