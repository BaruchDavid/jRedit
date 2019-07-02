package de.ffm.rka.rkareddit.service;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Ignore;
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
	 */
	@Test
	public void findAllLinksFromAllUsers() {
		Set<Link> links = linkService.findAll();
		links.forEach(link -> {
			LOGGER.info("Link found {}", link.toString());
			LOGGER.info("link vote size {} ",  link.getVote().size());
			LOGGER.info("link comment size {} ", link.getComments().size());
			LOGGER.info("=========================== ");
			
		});
	}

}
