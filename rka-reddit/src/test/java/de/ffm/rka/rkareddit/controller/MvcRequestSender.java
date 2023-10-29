package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.config.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.util.BeanUtil;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import jakarta.persistence.EntityManager;
import java.net.URI;
import java.util.Map;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ActiveProfiles("test")
/** spring-test-support is enabled */
@RunWith(SpringRunner.class)
/** enable of application-context */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringSecurityTestConfig.class,
        properties = {"password.time.expiration=10"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Transactional
public abstract class MvcRequestSender {

    public MockMvc mockMvc;

    @Autowired
    protected  UserService userService;

    @Autowired
    private WebApplicationContext context;

    protected EntityManager entityManager;

    @Before
    public void setUp(){
        entityManager = BeanUtil.getBeanFromContext(EntityManager.class);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    public MvcRequestSender(){

    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public ResultActions performGetRequest(String ressource) throws Exception {
        return this.mockMvc.perform(get(new URI(ressource)).secure(true))
                .andDo(print());

    }

    public ResultActions performGetRequestWithFalshAttributes(String ressource, Map<String, Object> flashAttributes) throws Exception {
        return this.mockMvc.perform(get(new URI(ressource))
                                        .flashAttrs(flashAttributes)
                                        .secure(true))
                .andDo(print());

    }

    public ResultActions performPostRequest(String ressource, String body) throws Exception {
        return this.mockMvc.perform(MockMvcRequestBuilders.post(ressource)
                                                        .secure(true)
                                                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                        .content(body))
                                                        .andDo(print());
    }

    public ResultActions performPostByteArray(String url, String fileName, byte[] byteContent, String[] content) throws Exception {
                 return this.mockMvc.perform(MockMvcRequestBuilders.multipart(url)
                            .file(fileName,byteContent)
                            .secure(true)
                            .param(content[0], content[1]))
                            .andDo(print());

    }

    public ResultActions performPutRequest(String ressource, String body) throws Exception {
        return this.mockMvc.perform(MockMvcRequestBuilders.put(ressource)
                                                        .secure(true)
                                                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                        .content(body))
                                                        .andDo(print());

    }

    public ResultActions performPatchRequest(String ressource, String body) throws Exception {
        return this.mockMvc.perform(MockMvcRequestBuilders.patch(ressource)
                                                        .secure(true)
                                                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                        .content(body))
                                                        .andDo(print());
    }

    public ResultActions sendRedirect(String redirectionSource) throws Exception {
        return performGetRequest(redirectionSource);

    }
}
