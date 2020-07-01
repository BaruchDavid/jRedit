package de.ffm.rka.rkareddit.repository;

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

/**
 * Testclass for Servicelayer
 * @author kaproma
 *
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class LinkTransactionTest {

	@Autowired
    private LinkService linkService ;
	private static final Logger LOGGER = LoggerFactory.getLogger(LinkTransactionTest.class);
	private EntityManager entityManager;
	private static final int MAX_TRANSACTION_NUMBER = 2;
	@Before
	public void SetUp() {
		entityManager = BeanUtil.getBeanFromContext(EntityManager.class);
	}
	
	/**
	 * test for checking number of transactions during 
	 * init-process of each link
	 * expected 11 links.
	 */
	@Test
	@Transactional
	public void findAllLinksAllCommentsAllVotesForEachUser() {
		Pageable firstPageWithElevenElements = PageRequest.of(0, 11);
		
		Session hibernateSession = entityManager.unwrap(Session.class);
		hibernateSession.getSessionFactory().getStatistics().clear();
		Statistics hibernateStatistic = hibernateSession.getSessionFactory().getStatistics();	
		Page<LinkDTO> links = linkService.fetchAllLinksWithUsersCommentsVotes(firstPageWithElevenElements);

		assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());
		assertEquals(11L, links.getNumberOfElements());
		assertEquals(0L, (links.getContent().get(0)).getComments().size());
		assertEquals(0L, (links.getContent().get(0)).getVote().size());

		LOGGER.debug("QUERY EXECTUION COUNT {}", hibernateStatistic.getQueryExecutionCount());
		assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());
		assertEquals(0L, (links.getContent().get(1)).getComments().size());
		assertEquals(0L, (links.getContent().get(1)).getVote().size());

		LOGGER.debug("QUERY EXECTUION COUNT {}", hibernateStatistic.getQueryExecutionCount());
		assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());
		assertEquals(0L, (links.getContent().get(2)).getComments().size());
		assertEquals(0L, (links.getContent().get(2)).getVote().size());

		LOGGER.debug("QUERY EXECTUION COUNT {}", hibernateStatistic.getQueryExecutionCount());
		assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());
		assertEquals(0L, (links.getContent().get(3)).getComments().size());
		assertEquals(0L, (links.getContent().get(3)).getVote().size());

		assertEquals(0L, (links.getContent().get(4)).getComments().size());
		assertEquals(0L, (links.getContent().get(4)).getVote().size());

		LOGGER.debug("QUERY EXECTUION COUNT {}", hibernateStatistic.getQueryExecutionCount());
		assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());
		assertEquals(1L, links.getContent().get(5).getComments().size());
		assertEquals(0L, (links.getContent().get(5)).getVote().size());

		LOGGER.debug("QUERY EXECTUION COUNT {}", hibernateStatistic.getQueryExecutionCount());
		assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());
		assertEquals(2L, (links.getContent().get(6)).getComments().size());
		assertEquals(3L, (links.getContent().get(6)).getVote().size());

		LOGGER.debug("QUERY EXECTUION COUNT {}", hibernateStatistic.getQueryExecutionCount());
		assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());
		assertEquals(2L, (links.getContent().get(7)).getComments().size());
		assertEquals(0L, (links.getContent().get(7)).getVote().size());

		LOGGER.debug("QUERY EXECTUION COUNT {}", hibernateStatistic.getQueryExecutionCount());
		assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());
		assertEquals(3L, (links.getContent().get(8)).getComments().size());
		assertEquals(2L, (links.getContent().get(8)).getVote().size());
		LOGGER.debug("QUERY EXECTUION COUNT {}", hibernateStatistic.getQueryExecutionCount());
		assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());
		assertEquals(6L, (links.getContent().get(9)).getComments().size());
		assertEquals(0L, (links.getContent().get(9)).getVote().size());
		LOGGER.debug("QUERY EXECTUION COUNT {}", hibernateStatistic.getQueryExecutionCount());
		assertEquals("SHOULD EXECUTED ONLY TWO JDBC STATMENTS", MAX_TRANSACTION_NUMBER, hibernateStatistic.getQueryExecutionCount());
		assertEquals(2L, (links.getContent().get(10)).getComments().size());
		assertEquals(4L, (links.getContent().get(10)).getVote().size());
		
		
		links.forEach(link -> {
			LOGGER.info("Link found {}", link.toString());
			LOGGER.info("link vote size {} ",  link.getVote().size());
			LOGGER.info("link comment size {} ", link.getComments().size());
			LOGGER.info("=========================== ");
			
		});		
		EntityStatistics entityStats = hibernateStatistic.getEntityStatistics( Link.class.getName() );
	}

}
