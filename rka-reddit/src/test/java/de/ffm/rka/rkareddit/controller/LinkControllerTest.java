package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.Tag;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.security.mock.SpringSecurityTestConfig;
import de.ffm.rka.rkareddit.service.LinkService;
import de.ffm.rka.rkareddit.util.BeanUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
/** spring-test-support is enabled */
@RunWith(SpringRunner.class)
/** enable of application-context */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringSecurityTestConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Transactional
public class LinkControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private EntityManager entityManager;

    @Autowired
    private LinkService linkService;

    @Before
    public void setup() {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        entityManager = BeanUtil.getBeanFromContext(EntityManager.class);
    }

    @Test
    @DisplayName("Beim Aufruf von der Hauptseite werden alle links für den eingelogten User zurückgegeben")
    @WithUserDetails("romakapt@gmx.de")
    public void shouldReturnAllLinks() throws Exception {
        UserDTO userDto = UserDTO.builder()
                .firstName("baruc-david")
                .secondName("rka")
                .build();
        List<Integer> pages = Arrays.asList(new Integer[]{1, 2});
        MvcResult result = this.mockMvc.perform(get("/links/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("pageNumbers", pages))
                .andReturn();
        UserDTO usr = (UserDTO) result.getModelAndView().getModel().get("userDto");
        assertEquals(userDto.getFullName(), usr.getFullName());
        Page<LinkDTO> links = (Page<LinkDTO>) result.getModelAndView().getModel().get("links");
        assertEquals("no comment-object loaded with links", 0, links.getContent().get(0).getCommentDTOS().size());
    }

    @Test
    @DisplayName("Beim Aufruf von der Hauptseite werden alle links für zurückgegeben")
    @WithAnonymousUser
    public void shouldReturnAllLinksForAnonymous() throws Exception {

        List<Integer> pages = Arrays.asList(new Integer[]{1, 2});
        MvcResult result = this.mockMvc.perform(get("/links/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("pageNumbers", pages))
                .andReturn();
        assertTrue(!result.getResponse().getContentAsString().contains("Submit Link"));
    }

    @Test
    @DisplayName("Beim Aufruf von der Hauptseite mit CURL werden alle links für zurückgegeben")
    public void shouldReturnAllLinksForCURL() throws Exception {
        List<Integer> pages = Arrays.asList(new Integer[]{1, 2});
        MvcResult result = this.mockMvc.perform(get("/links/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("pageNumbers", pages))
                .andReturn();
        assertTrue(!result.getResponse().getContentAsString().contains("Submit Link"));
    }

    /**
     * test for illegal link
     */
    @Test
    @DisplayName("Beim Aufruf von nicht existierendem Link wird basicError angezeigt")
    public void illegalArguments() throws Exception {
        String invalidPage = UUID.randomUUID().toString();
        this.mockMvc.perform(get("/links/link/".concat(invalidPage)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error/basicError"));

    }

    /**
     * while reading one link
     * empty comment will be created,
     * which will be used for new comment
     *
     * @throws Exception
     */
    @Test
    @DisplayName("Anzeigen von einem Link für einen nicht eingelogten User")
    public void readLinkTestAsUnautheticated() throws Exception {
        Link currentLink = linkService.findLinkModelBySignature("15921918064983");
        LinkDTO linkDTO = LinkDTO.getMapLinkToDto(currentLink);
        MvcResult mvcResult = this.mockMvc.perform(get("/links/link/15921918064983"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("linkDto", linkDTO))
                .andExpect(view().name("link/link_view"))
                .andReturn();
        linkDTO = (LinkDTO) mvcResult.getModelAndView().getModel().get("linkDto");
        User linkCreator = new User();
        linkCreator.setEmail("romakapt@gmx.de");
        assertEquals("user anzeigen, der den Link erzeugt hat",
                linkCreator.getEmail(), linkDTO.getUser().getEmail());

        assertEquals("diser link zeigt zwei kommentare an",
                2, linkDTO.getCommentDTOS().size());
    }

    @Test
    @DisplayName("Anzeigen von einem Link für einen eingelogten User")
    @WithUserDetails("romakapt@gmx.de")
    public void readLinkTestAsAutheticated() throws Exception {
        Link currentLink = entityManager.find(Link.class, 1L);
        LinkDTO linkDTO = LinkDTO.getMapLinkToDto(currentLink);
        UserDTO userDto = UserDTO.builder()
                .firstName("baruc-david")
                .secondName("rka")
                .build();
        MvcResult result = this.mockMvc.perform(get("/links/link/15921918064981"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("linkDto", linkDTO))
                .andExpect(view().name("link/link_view"))
                .andReturn();
        UserDTO usr = (UserDTO) result.getModelAndView().getModel().get("userDto");
        assertEquals(userDto.getFullName(), usr.getFullName());
    }

    @Test
    @DisplayName("Speichere einen Link mit Tags")
    @WithUserDetails("romakapt@gmx.de")
    public void saveNewLinkTest() throws Exception {
        MvcResult res = this.mockMvc.perform(MockMvcRequestBuilders.post("/links/link")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("tags[0].tagName", "java12")
                .param("tags[1].tagName", "java13")
                .param("title", "welt.de")
                .param("subtitle", "neues subtitle")
                .param("description", "news")
                .param("url", "http://welt.de")
        )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("success", true))
                .andReturn();
        String signature = res.getResponse().getRedirectedUrl().split("/")[3];
        assertEquals("12", signature.substring(13));
        Link link = linkService.findLinkWithTags(signature);
        assertEquals("welt.de", link.getTitle());
        assertEquals("neues subtitle", link.getSubtitle());
        assertEquals("news", link.getDescription());
        assertEquals("http://welt.de", link.getUrl());

        assertTrue(link.getTags().stream()
                .map(Tag::getTagName)
                //.anyMatch(tagName -> "java12".equals(tagName)));
                .anyMatch("java12"::equals));

        assertTrue(link.getTags().stream()
                .map(Tag::getTagName)
                .anyMatch("java13"::equals));

        assertFalse(link.getTags().stream()
                .map(Tag::getTagName)
                .anyMatch("java"::equals));
    }

    @Test
    @DisplayName("Speichere einen Link mit Tags als nicht angemeldeter user")
    public void saveNewLinkTestUnautheticated() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/links/link")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("tags[0].tagName", "java12")
                .param("tags[1].tagName", "java13")
                .param("title", "welt.de")
                .param("url", "http://welt.de")
        )
                .andDo(print())
                .andExpect(status().is(302))
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    @DisplayName("Zeige Seite für einen neuen Test")
    public void createNewLinkTest() throws Exception {
        Link link = new Link();
        UserDTO userDto = UserDTO.builder()
                .firstName("baruc-david")
                .secondName("rka")
                .build();
        MvcResult result = this.mockMvc.perform(get("/links/link"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("link/submit"))
                .andReturn();
        UserDTO usr = (UserDTO) result.getModelAndView().getModel().get("userDto");
        assertEquals(userDto.getFullName(), usr.getFullName());
        assertTrue(result.getModelAndView().getModel().get("newLink").toString().equals(link.toString()));
    }

    @Test
    @DisplayName("Zeige eine neue Seite zum Linkanlegen für einen nicht angemeldeten User")
    public void createNewLinkTestAsUnautheticated() throws Exception {
        this.mockMvc.perform(get("/links/link"))
                .andDo(print())
                .andExpect(status().is(302))
                .andExpect(redirectedUrlPattern("**/login"));
    }

    /**
     * get initial tags
     */
    @Test
    @DisplayName("Suche nach einem Tag mit dem Ausdruck 'sc' und erwarte TypeScript, JavaScript, Delphi/Object, Pascal")
    public void getTags() throws Exception {
        List<String> expList = Arrays.asList("TypeScript", "JavaScript", "Delphi/Object Pascal");
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/links/link/tags")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("search", "sc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String[] resultArray = result.getResponse().getContentAsString().replace("[", "").replace("]", "").replace('"', ' ').split(",");
        assertEquals(true, Stream.of(resultArray)
                .peek(tag -> System.out.println("CURRENT TAG: " + tag))
                .allMatch(tag -> expList.contains(tag.trim())));

    }

    /**
     * Authetication as anonymous
     */
    @Test
    @WithAnonymousUser
    @DisplayName("Zeige Seite zum Linkanlegen als Anonymous user")
    public void linkCreateAsAnonymous() throws Exception {

        this.mockMvc.perform(get("/links/link"))
                .andDo(print())
                .andExpect(status().is(302))
                .andExpect(redirectedUrlPattern("**/login*"));
    }
}
