package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Testclass for Servicelayer
 *
 * @author kaproma
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(properties = {"password.time.expiration=10"})
public class LinkServiceTest {

    @Autowired
    private PostService postService;

    /**
     * test for test_env for user on service-layer
     */
    @Test
    public void linkSizeForUserOne() {
        User user = new User();
        user.setUserId(1L);
        assertEquals(5L, postService.countLinkByUser(user));
    }

    @Test
    public void linkPrettyTimeTest() throws InterruptedException {
        LinkDTO link = LinkDTO.builder()
                .title("test")
                .url("http://test.de")
                .tags(Collections.emptyList())
                .build();
        link = postService.saveLink("kaproma@yahoo.de", link);
        Thread.sleep(5000L);
        assertEquals("gerade eben", link.getElapsedTime());
    }

}
