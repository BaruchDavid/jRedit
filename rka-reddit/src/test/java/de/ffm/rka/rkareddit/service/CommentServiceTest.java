package de.ffm.rka.rkareddit.service;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.rest.controller.TagController;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommentServiceTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceTest.class);
	
	@Autowired
    private CommentService commentService ;
	
	@Test
	public void commentSizeForUserOne() {
		User user = new User();
		user.setUserId(1l);
		assertEquals(10l, commentService.countAllByUser(user));
	}

	@Test
	public void testPrettyTime() {
		Comment comment = Comment.builder()
								.commentId(1l)
								.build();
	assertEquals("gerade eben", commentService.getElapsedTimeFromComment(comment)); 
		
	}
}
