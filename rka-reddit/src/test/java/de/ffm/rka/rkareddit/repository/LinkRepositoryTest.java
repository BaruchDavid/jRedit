package de.ffm.rka.rkareddit.repository;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.util.BeanUtil;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.junit.Assert.assertEquals;

/**
 * Testclass for Servicelayer
 * @author kaproma
 *
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"password.time.expiration=10"})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class LinkRepositoryTest {

	@Autowired
    private LinkRepository linkRepository;
	@Autowired
    private CommentRepository commentRepository;

	Statistics hibernateStatistic;
	@Before
	public void SetUp() {
		EntityManager entityManager = BeanUtil.getBeanFromContext(EntityManager.class);
		try(Session hibernateSession = entityManager.unwrap(Session.class)){
			hibernateSession.getSessionFactory().getStatistics().clear();
			hibernateStatistic = hibernateSession.getSessionFactory().getStatistics();
		}

	}

	@Test
	@Transactional
	public void saveLinkWithAddedComment() {
		Comment comment = Comment.builder()
				.commentText("erstes Kommentar")
				.build();
		Link link = Link.builder().title("welt.de")
				.description("news")
				.url("www.link.de")
				.build();
		link.addComment(comment);
		final Link savedLink = linkRepository.save(link);
		final Comment one = commentRepository.getOne(link.getComments().get(0).getCommentId());
		assertEquals(comment.getCommentText(), one.getCommentText());
		assertEquals(2, hibernateStatistic.getEntityInsertCount());
		assertEquals(savedLink, link);
		assertEquals(savedLink.getComments().get(0).getCommentText(), comment.getCommentText());

	}
}
