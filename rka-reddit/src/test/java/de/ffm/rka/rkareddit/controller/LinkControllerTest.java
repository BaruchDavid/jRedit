package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.Tag;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.service.LinkService;
import org.apache.commons.httpclient.HttpStatus;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class LinkControllerTest extends MvcRequestSender {
    private static final int MAX_JDBC_TRANSACTION = 3;

    @Autowired
    private LinkService linkService;

    UserDTO userDto = UserDTO.builder()
                            .firstName("baruc-david")
                            .secondName("rka")
                            .build();

    String linksWithTagsBody = "tags[0].tagName=java12&" +
            "tags[1].tagName=java13&" +
            "title=welt.de&" +
            "subtitle=neues subtitle&" +
            "description=news&"+
            "url=http://welt.de";

    private Statistics hibernateStatistic;
    private Session hibernateSession;
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkControllerTest.class);
    @Before
    public void setup() {
/*
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        entityManager = BeanUtil.getBeanFromContext(EntityManager.class);*/
        hibernateSession = super.getEntityManager().unwrap(Session.class);
        hibernateStatistic = hibernateSession.getSessionFactory().getStatistics();
    }

    @Test()
    //@DisplayName"Beim Aufruf von der Hauptseite werden alle links für den eingelogten User zurückgegeben")
    @WithUserDetails("romakapt@gmx.de")
    public void shouldReturnAllLinks() throws Exception {

        List<Integer> pages = Arrays.asList(new Integer[]{1, 2});
        hibernateStatistic.clear();
        MvcResult result = super.performGetRequest("/links/")
                .andExpect(status().isOk())
                .andExpect(model().attribute("pageNumbers", pages))
                .andReturn();
        UserDTO usr = (UserDTO) result.getModelAndView().getModel().get("userDto");
        assertEquals(userDto.getFullName(), usr.getFullName());
        Page<LinkDTO> links = (Page<LinkDTO>) result.getModelAndView().getModel().get("links");
        assertEquals("comments will be loaded with links", 1, links.getContent().get(0).getCommentDTOS().size());
        Arrays.stream(hibernateStatistic.getQueries()).forEach(query -> LOGGER.info("QUERY: {}", query));
        assertEquals("MAX JDBC STATEMENTS:".concat(String.valueOf(MAX_JDBC_TRANSACTION)),
                MAX_JDBC_TRANSACTION, hibernateStatistic.getQueryExecutionCount());
    }

    @Test
    //@DisplayName"Beim Aufruf von der Hauptseite werden alle links für zurückgegeben")
    @WithAnonymousUser
    public void shouldReturnAllLinksForAnonymous() throws Exception {

        List<Integer> pages = Arrays.asList(new Integer[]{1, 2});
        MvcResult result = super.performGetRequest("/links/")
                .andExpect(status().isOk())
                .andExpect(model().attribute("pageNumbers", pages))
                .andReturn();
        assertTrue(!result.getResponse().getContentAsString().contains("Submit Link"));
    }

    @Test
    //@DisplayName"Beim Aufruf von der Hauptseite mit CURL werden alle links für zurückgegeben")
    public void shouldReturnAllLinksForCURL() throws Exception {
        List<Integer> pages = Arrays.asList(new Integer[]{1, 2});
        MvcResult result = super.performGetRequest("/links/")
                .andExpect(status().isOk())
                .andExpect(model().attribute("pageNumbers", pages))
                .andReturn();
        assertTrue(!result.getResponse().getContentAsString().contains("Submit Link"));
    }

    /**
     * test for illegal link
     */
    @Test
    //@DisplayName"Beim Aufruf von nicht existierendem Link wird basicError angezeigt")
    public void illegalArguments() throws Exception {
        String invalidPage = UUID.randomUUID().toString();
        final MvcResult mvcResult = super.performGetRequest("/links/link/".concat(invalidPage))
                .andExpect(status().is(302))
                .andReturn();
        final String location = mvcResult.getResponse().getHeader("location");
        final ResultActions result = sendRedirect(location.replace("+", ""));
        result.andExpect(view().name("error/basicError"))
                .andExpect(status().is(HttpStatus.SC_NOT_FOUND));


    }

    /**
     * while reading one link
     * empty comment will be created,
     * which will be used for new comment
     *
     * @throws Exception
     */
    @Test
    //@DisplayName"Anzeigen von einem Link für einen nicht eingelogten User")
    public void readLinkTestAsUnautheticated() throws Exception {
        Link currentLink = linkService.findLinkModelBySignature("15921918064983");
        LinkDTO linkDTO = LinkDTO.mapFullyLinkToDto(currentLink);
        MvcResult mvcResult = super.performGetRequest("/links/link/15921918064983")
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
    //@DisplayName"Anzeigen von einem Link für einen eingelogten User")
    @WithUserDetails("romakapt@gmx.de")
    public void readLinkTestAsAutheticated() throws Exception {
        Link currentLink = super.getEntityManager().find(Link.class, 1L);
        LinkDTO linkDTO = LinkDTO.mapFullyLinkToDto(currentLink);

        MvcResult result = super.performGetRequest("/links/link/15921918064981")
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("linkDto", linkDTO))
                .andExpect(view().name("link/link_view"))
                .andReturn();
        UserDTO usr = (UserDTO) result.getModelAndView().getModel().get("userDto");
        assertEquals(userDto.getFullName(), usr.getFullName());
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void saveNewLinkTest() throws Exception {

        MvcResult res = super.performPostRequest("/links/link", linksWithTagsBody)
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
                .anyMatch("java12"::equals));

        assertTrue(link.getTags().stream()
                .map(Tag::getTagName)
                .anyMatch("java13"::equals));

        assertFalse(link.getTags().stream()
                .map(Tag::getTagName)
                .anyMatch("java"::equals));
    }

    @Test
    public void saveNewLinkTestUnautheticated() throws Exception {
        super.performPostRequest("/links/link", linksWithTagsBody)
                .andExpect(status().is(302))
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    //@DisplayName"Zeige Seite für einen neuen Test")
    public void createNewLinkTest() throws Exception {
        Link link = new Link();
        MvcResult result = super.performGetRequest("/links/link")
                .andExpect(status().isOk())
                .andExpect(view().name("link/submit"))
                .andReturn();
        UserDTO usr = (UserDTO) result.getModelAndView().getModel().get("userDto");
        assertEquals(userDto.getFullName(), usr.getFullName());
        assertTrue(result.getModelAndView().getModel().get("newLink").toString().equals(link.toString()));
    }

    @Test
    //@DisplayName"Zeige eine neue Seite zum Linkanlegen für einen nicht angemeldeten User")
    public void createNewLinkTestAsUnautheticated() throws Exception {
        super.performGetRequest("/links/link")
                .andExpect(status().is(302))
                .andExpect(redirectedUrlPattern("**/login"));
    }

    /**
     * get initial tags
     */
    @Test
    //@DisplayName"Suche nach einem Tag mit dem Ausdruck 'sc' und erwarte TypeScript, JavaScript, Delphi/Object, Pascal")
    public void getTags() throws Exception {
        String body = "search=sc";
        List<String> expList = Arrays.asList("TypeScript", "JavaScript", "Delphi/Object Pascal");
        MvcResult result = super.performPostRequest("/links/link/tags", body)
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
    //@DisplayName"Zeige Seite zum Linkanlegen als Anonymous user")
    public void linkCreateAsAnonymous() throws Exception {

        super.performGetRequest("/links/link")
                .andExpect(status().is(302))
                .andExpect(redirectedUrlPattern("**/login*"));
    }
}
