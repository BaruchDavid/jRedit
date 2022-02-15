package de.ffm.rka.rkareddit.repository;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.service.LinkService;
import de.ffm.rka.rkareddit.util.BeanUtil;
import org.hibernate.Session;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.Statistics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
	private static final Logger LOGGER = LoggerFactory.getLogger(LinkRepositoryTest.class);
	private EntityManager entityManager;
	Statistics hibernateStatistic;
	@Before
	public void SetUp() {
		entityManager = BeanUtil.getBeanFromContext(EntityManager.class);
		Session hibernateSession = entityManager.unwrap(Session.class);
		hibernateSession.getSessionFactory().getStatistics().clear();
		hibernateStatistic = hibernateSession.getSessionFactory().getStatistics();
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
		assertTrue(comment.getCommentText().equals(one.getCommentText()));
		assertTrue(hibernateStatistic.getEntityInsertCount()==2);
		assertTrue(savedLink.equals(link));
		assertTrue(savedLink.getComments().get(0).getCommentText().equals(comment.getCommentText()));

	}
}
