package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.Comment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"password.time.expiration=10"})
public class PostServiceCommentTest {

    @Autowired
    private PostService postService;

    @Test
    public void testPrettyTime() {
        Comment comment = Comment.builder()
                .commentId(1l)
                .build();
        assertEquals("gerade eben", postService.findCommentWithElapsedtime(comment));

    }
}