package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.util.BeanUtil;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class AllControllersTransactionTest extends MvcRequestSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(AllControllersTransactionTest.class);
	private static final int MAX_JDBC_TRANSACTION = 3;

	private Statistics hibernateStatistic;
	private Session hibernateSession;
	private EntityManager entityManager;
	
	@Before
	public void setup() {
		entityManager = BeanUtil.getBeanFromContext(EntityManager.class);
		hibernateSession = entityManager.unwrap(Session.class);
		hibernateStatistic = hibernateSession.getSessionFactory().getStatistics();
		hibernateStatistic.clear();

	}

	//@Ignore
	@Test
	@WithUserDetails("kaproma@yahoo.de")
	public void shouldReturnAllLinksWith5JDBCStatmentsAsAutheticated() throws Exception {
		UserDTO userDto = UserDTO.builder()
								.firstName("baruc-david")
								.secondName("rka")
								.build();
		List<Integer> pages = Arrays.asList(new Integer[] {1,2});
		MvcResult result =  super.performGetRequest("/links/")
					.andExpect(status().isOk())
					.andExpect(model().attribute("pageNumbers", pages))
					.andReturn();
		LOGGER.info("QUERIES: ", hibernateStatistic.getQueries());
		assertEquals("MAX JDBC STATMENTS:".concat(String.valueOf(MAX_JDBC_TRANSACTION)),
				MAX_JDBC_TRANSACTION, hibernateStatistic.getQueryExecutionCount());
	}

	@Test
	public void shouldReturnAllLinksWith5JDBCStatmentsAsUnAutheticated() throws Exception {
		UserDTO userDto = UserDTO.builder()
				.firstName("baruc-david")
				.secondName("rka")
				.build();
		List<Integer> pages = Arrays.asList(new Integer[] {1,2});
		MvcResult result =  super.performGetRequest("/links/")
				.andExpect(status().isOk())
				.andExpect(model().attribute("pageNumbers", pages))
				.andReturn();
		LOGGER.info("QUERIES: ", hibernateStatistic.getQueries());
		assertEquals("MAX JDBC STATMENTS: ".concat(String.valueOf(MAX_JDBC_TRANSACTION)),
						MAX_JDBC_TRANSACTION, hibernateStatistic.getQueryExecutionCount());
	}


}
