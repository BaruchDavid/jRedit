package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

/**
 * Testclass for Servicelayer
 * @author kaproma
 *
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class LinkServiceTest {

	@Autowired
    private LinkService linkService ;

	/**
	 * test for test_env for user on service-layer
	 */
	@Test
	public void linkSizeForUserOne() {
		User user = new User();
		user.setUserId(1l);
		assertEquals(5l, linkService.findAllLinksByUser(user));
	}
	
	@Test
	public void linkPrettyTimeTest() {
		Link link = Link.builder()
						.title("test")
						.url("http://test.de")
						.tags(Collections.emptyList())
						.build();
		link = linkService.saveLink(link);
		assertEquals("gerade eben", link.getElapsedTime());		
	}

}
