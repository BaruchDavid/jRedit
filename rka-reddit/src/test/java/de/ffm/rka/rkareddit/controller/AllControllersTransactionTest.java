package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.config.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.util.BeanUtil;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
/** spring-test-support is enabled */
@RunWith(SpringRunner.class) 
/** enable of application-context */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringSecurityTestConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class AllControllersTransactionTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AllControllersTransactionTest.class);
	private static final int MAX_JDBC_TRANSACTION = 3;
	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext context;
	private Statistics hibernateStatistic;
	private Session hibernateSession;
	private EntityManager entityManager;
	
	@Before
	public void setup() {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
										.apply(springSecurity())
										.build();
		entityManager = BeanUtil.getBeanFromContext(EntityManager.class);
		hibernateSession = entityManager.unwrap(Session.class);
		hibernateStatistic = hibernateSession.getSessionFactory().getStatistics();
		hibernateStatistic.clear();

	}

	//@Ignore
	@Test
	@WithUserDetails("romakapt@gmx.de")
	public void shouldReturnAllLinksWith5JDBCStatmentsAsAutheticated() throws Exception {
		UserDTO userDto = UserDTO.builder()
								.firstName("baruc-david")
								.secondName("rka")
								.build();
		List<Integer> pages = Arrays.asList(new Integer[] {1,2});
		MvcResult result =  this.mockMvc.perform(get("/links/"))
					.andDo(print())
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
		MvcResult result =  this.mockMvc.perform(get("/links/"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attribute("pageNumbers", pages))
				.andReturn();
		LOGGER.info("QUERIES: ", hibernateStatistic.getQueries());
		assertEquals("MAX JDBC STATMENTS: ".concat(String.valueOf(MAX_JDBC_TRANSACTION)),
						MAX_JDBC_TRANSACTION, hibernateStatistic.getQueryExecutionCount());
	}


}
