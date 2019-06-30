package de.ffm.rka.rkareddit.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.ffm.rka.rkareddit.controller.HomeController;
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
	@Ignore
	@Test
	public void linkSizeForUserOne() {
		User user = new User();
		user.setUserId(1l);
		assertEquals(5l, linkService.findAllByUser(user));
	}
	/**
	 * test for test_env
	 */
	@Test
	public void findAllLinksFromAllUsers() {
		List<Link> links = linkService.findAll();	
		links.forEach(link -> {
			LOGGER.info("link id {} and vote size {} ", link.getLinkId(), link.getVote().size());
			LOGGER.info("link id {} and comment size {} ", link.getLinkId(), link.getComments().size());
			LOGGER.info("=========================== ");
			
		});
	}

}
