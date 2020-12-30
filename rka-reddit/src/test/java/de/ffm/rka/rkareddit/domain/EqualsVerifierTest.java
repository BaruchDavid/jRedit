package de.ffm.rka.rkareddit.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


public class EqualsVerifierTest {
    @Mock
    private PersistentAttributeInterceptor interceptor1;
    @Mock
    private PersistentAttributeInterceptor interceptor2;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(interceptor1.readObject(any(), anyString(), any()))
                .thenAnswer(interceptor -> interceptor.getArguments()[2]); // return the same value, that passed to this method
        when(interceptor2.readObject(any(), anyString(), any())).thenAnswer(i -> i.getArguments()[2]);
    }

    @Test
    public void equalsHashCodeRoleContracts() {
        final User user1 = User.builder().userId(1L).email("test@.com").build();
        final User user2 = User.builder().userId(2L).email("test@.de").build();
        EqualsVerifier.forClass(Role.class)
                .withOnlyTheseFields("roleName")
                .withPrefabValues(User.class, user1, user2)
                .withPrefabValues(PersistentAttributeInterceptor.class, interceptor1, interceptor2) // this is how i inject mocks in each entity:)
                .verify();
    }

    @Test
    public void equalsHashCodeUserContracts() {
        final Link link1 = Link.builder().url("www.url.com").build();
        final Link link2 = Link.builder().url("url.com").build();
        final User user1 = User.builder().userId(1L).email("test@.com").build();
        final User user2 = User.builder().userId(2L).email("test@.de").build();
        EqualsVerifier.forClass(User.class)
                .withOnlyTheseFields("email")
                .withPrefabValues(Link.class, link1, link2)
                .withPrefabValues(User.class, user1, user2)
                .withPrefabValues(PersistentAttributeInterceptor.class, interceptor1, interceptor2) // this is how i inject mocks in each entity:)
                .verify();
    }

    @Test
    public void equalsHashCodeLinkContracts() {
        final User user1 = User.builder().userId(1L).email("test@.com").build();
        final User user2 = User.builder().userId(2L).email("test@.de").build();
        final Tag javaTag = Tag.builder().tagName("java").build();
        final Tag java8Tag = Tag.builder().tagName("java8").build();
        final Link link1 = Link.builder().url("www.url.com").build();
        final Link link2 = Link.builder().url("url.com").build();
        EqualsVerifier.forClass(Link.class)
                .withOnlyTheseFields("url")
                .withPrefabValues(Tag.class, javaTag, java8Tag)
                .withPrefabValues(User.class, user1, user2)
                .withPrefabValues(Link.class, link1, link2)
                .withPrefabValues(PersistentAttributeInterceptor.class, interceptor1, interceptor2) // this is how i inject mocks in each entity:)
                .verify();
    }

    @Test
    public void equalsHashCodeTagContracts() {
        final Link link1 = Link.builder().url("www.url.com").build();
        final Link link2 = Link.builder().url("url.com").build();
        EqualsVerifier.forClass(Tag.class)
                .withOnlyTheseFields("tagName")
                .withPrefabValues(Link.class, link1, link2)
                .withPrefabValues(PersistentAttributeInterceptor.class, interceptor1, interceptor2) // this is how i inject mocks in each entity:)
                .verify();
    }

    /**
     * comentText can be null, since commentDto has @NonNull check
     */
    @Test
    public void equalsHashCodeCommentContracts() {
        final Link link1 = Link.builder().url("www.url.com").build();
        final Link link2 = Link.builder().url("url.com").build();
        final User user1 = User.builder().userId(1L).email("test@.com").build();
        final User user2 = User.builder().userId(2L).email("test@.de").build();
        EqualsVerifier.forClass(Comment.class)
                .withOnlyTheseFields("commentText")
                .withNonnullFields("commentText")
                .withPrefabValues(Link.class, link1, link2)
                .withPrefabValues(User.class, user1, user2)
                .withPrefabValues(PersistentAttributeInterceptor.class, interceptor1, interceptor2) // this is how i inject mocks in each entity:)
                .verify();
    }

}
