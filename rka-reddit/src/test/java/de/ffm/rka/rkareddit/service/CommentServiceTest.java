package de.ffm.rka.rkareddit.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.service.CommentService;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommentServiceTest {

	@Autowired
    private CommentService commentService ;
	
	@Test
	public void commentSizeForUserOne() {
		User user = new User();
		user.setUserId(1l);
		assertEquals(10l, commentService.countAllByUser(user));
	}

}
