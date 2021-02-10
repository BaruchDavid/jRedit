package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.util.BeanUtil;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ActiveProfiles("test")
/** spring-test-support is enabled */
@RunWith(SpringRunner.class)
/** enable of application-context */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringSecurityTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Transactional
public abstract class MvcRequestSender {

    public MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private EntityManager entityManager;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    public MvcRequestSender(){

    }

    public ResultActions performGetRequest(String ressource) throws Exception {
        final ResultActions resultActions = this.mockMvc.perform(get(ressource))
                                                        .andDo(print());
        return resultActions;
    }
}
