package de.ffm.rka.rkareddit.repository;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.service.PostService;
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

/**
 * Testclass for Servicelayer
 *
 * @author kaproma
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"password.time.expiration=10"})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class LinkTransactionTest {

    @Autowired
    private PostService postService;
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkTransactionTest.class);
    private EntityManager entityManager;
    private static final int MAX_TRANSACTION_NUMBER = 3;

    @Before
    public void SetUp() {
        entityManager = BeanUtil.getBeanFromContext(EntityManager.class);
    }

    /**
     * test for checking number of transactions during
     * init-process of each link
     * Expected two statements.
     * All get-calls return nothing / 0 result cause of lazy-loading
     * and ignoring within mapping.
     * all calls of child-entity (comments, tags etc.) should executed on extra join-fetch query
     * expected 11 links.
     */
    @Test
    @Transactional
    public void findAllLinksAllCommentsAllVotesForEachUser() {
        Pageable firstPageWithElevenElements = PageRequest.of(0, 11);

        try (Session hibernateSession = entityManager.unwrap(Session.class)) {
            hibernateSession.getSessionFactory().getStatistics().clear();
            Statistics hibernateStatistic = hibernateSession.getSessionFactory().getStatistics();
            Page<LinkDTO> links = postService.findLinksWithUsers(firstPageWithElevenElements, "");
            assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATEMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());

            assertEquals("THEY ARE ELEVEN LINKS", 11L, links.getNumberOfElements());
            assertEquals("LINK ON INDEX 0 SHOULD HAVE 1 COMMENT", 1L, (links.getContent().get(0)).getCommentDTOS().size());
            LOGGER.debug("QUERY EXECUTION COUNT {}", hibernateStatistic.getQueryExecutionCount());
            assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATEMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());

            assertEquals("FIRST LINK SHOULD HAVE TWO COMMENT", 2L, (links.getContent().get(1)).getCommentDTOS().size());
            LOGGER.debug("QUERY EXECUTION COUNT {}", hibernateStatistic.getQueryExecutionCount());
            assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATEMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());

            assertEquals("SECOND LINK SHOULD HAVE FOUR COMMENT", 4L, (links.getContent().get(2)).getCommentDTOS().size());
            LOGGER.debug("QUERY EXECUTION COUNT {}", hibernateStatistic.getQueryExecutionCount());
            assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATEMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());

            assertEquals("THIRD LINK SHOULD HAVE THREE COMMENT", 3L, (links.getContent().get(3)).getCommentDTOS().size());
            LOGGER.debug("QUERY EXECUTION COUNT {}", hibernateStatistic.getQueryExecutionCount());
            assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATEMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());

            assertEquals("FOURTH LINK SHOULD HAVE ONE COMMENT", 1L, (links.getContent().get(4)).getCommentDTOS().size());
            LOGGER.debug("QUERY EXECUTION COUNT {}", hibernateStatistic.getQueryExecutionCount());
            assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATEMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());

            assertEquals("FIFTH LINK SHOULD HAVE ONE COMMENT", 1L, links.getContent().get(5).getCommentDTOS().size());
            LOGGER.debug("QUERY EXECUTION COUNT {}", hibernateStatistic.getQueryExecutionCount());
            assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATEMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());

            assertEquals("SIXTH LINK SHOULD HAVE TWO COMMENTS", 2L, (links.getContent().get(6)).getCommentDTOS().size());
            LOGGER.debug("QUERY EXECUTION COUNT {}", hibernateStatistic.getQueryExecutionCount());
            assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATEMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());

            assertEquals("SEVENTH LINK SHOULD HAVE NO COMMENTS", 0L, (links.getContent().get(7)).getCommentDTOS().size());
            LOGGER.debug("QUERY EXECUTION COUNT {}", hibernateStatistic.getQueryExecutionCount());
            assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATEMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());

            assertEquals("EIGTH LINK SHOULD HAVE NO COMMENTS", 0L, (links.getContent().get(8)).getCommentDTOS().size());
            LOGGER.debug("QUERY EXECUTION COUNT {}", hibernateStatistic.getQueryExecutionCount());
            assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATEMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());

            assertEquals("NINTH LINK SHOULD HAVE NO COMMENTS", 0L, (links.getContent().get(9)).getCommentDTOS().size());
            LOGGER.debug("QUERY EXECUTION COUNT {}", hibernateStatistic.getQueryExecutionCount());
            assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATEMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());

            assertEquals("TENTH LINK SHOULD HAVE TWO COMMENTS", 2L, (links.getContent().get(10)).getCommentDTOS().size());
            LOGGER.debug("QUERY EXECUTION COUNT {}", hibernateStatistic.getQueryExecutionCount());
            assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATEMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());


            links.forEach(link -> {
                LOGGER.info("Link found {}", link.toString());
                LOGGER.info("link comment size {} ", link.getCommentDTOS().size());
                LOGGER.info("=========================== ");

            });
            EntityStatistics entityStats = hibernateStatistic.getEntityStatistics(Link.class.getName());
        }
    }

}
