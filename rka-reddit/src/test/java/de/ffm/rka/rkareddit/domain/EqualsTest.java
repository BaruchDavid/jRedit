package de.ffm.rka.rkareddit.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EqualsTest {

    @Test
    public void roleEquals() {
        Role admin = new Role("admin");
        assertThat(admin).isEqualTo(new Role("admin"));
    }

    @Test
    public void roleNotEquals() {
        Role admin = new Role("admin");
        assertThat(admin).isNotEqualTo(new Role("user"));
    }

    @Test
    public void userNotEquals() {
        final User user1 = User.builder().userId(1L).email("test@.com").build();
        final User user2 = User.builder().userId(2L).email("test@.de").build();
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    public void userNotEqualsInstanceOf() {
        final User user1 = User.builder().userId(1L).email("test@.com").build();
        Role admin = new Role("admin");
        assertThat(user1).isNotEqualTo(admin);
    }

    @Test
    public void userEquals() {
        final User user1 = User.builder().userId(1L).email("test@.com").build();
        final User user2 = User.builder().userId(2L).email("test@.com").build();
        assertThat(user1).isEqualTo(user2);
    }

    @Test
    public void userEqualsObjects() {
        class AdminUser extends User { }
        final User user1 = User.builder().userId(1L).email("test@.com").build();
        final AdminUser userAdmin = new AdminUser();
        userAdmin.setUserId(1L);
        userAdmin.setEmail("test@.com");
        assertThat(user1).isEqualTo(userAdmin);
    }

    @Test
    public void linkNotEquals() {
        final Link weltLink = Link.builder().url("www.welt.de").subtitle("News").build();
        final Link weltLink2 = Link.builder().url("www.cnn.com").subtitle("Comments").build();
        assertThat(weltLink).isNotEqualTo(weltLink2);
    }

    @Test
    public void linkNotEqualsObjects() {
        final Link weltLink = Link.builder().url("www.welt.de").subtitle("News").build();
        final User user2 = User.builder().userId(2L).email("test@.com").build();
        assertThat(weltLink).isNotEqualTo(user2);
    }

    @Test
    public void linkEquals() {
        final Link weltLink = Link.builder().url("www.welt.de").subtitle("News").build();
        final Link weltLink2 = Link.builder().url("www.welt.de").subtitle("Comments").build();
        assertThat(weltLink).isEqualTo(weltLink2);
    }

    @Test
    public void linkEqualsObjectReferences() {
        final Link weltLink = Link.builder().url("www.welt.de").subtitle("News").build();
        final Link weltLink2 = weltLink;
        assertThat(weltLink).isEqualTo(weltLink2);
    }

    @Test
    public void tagNotEquals() {
        final Tag java = Tag.builder().tagName("java").tagId(2L).build();
        final Tag java1 = Tag.builder().tagName("java8").tagId(3L).build();
        assertThat(java).isNotEqualTo(java1);
    }

    @Test
    public void tagNotEqualsObjects() {
        final Tag java = Tag.builder().tagName("java").tagId(2L).build();
        final Comment comment = Comment.builder().commentText("hallo").commentId(1L).build();
        assertThat(java).isNotEqualTo(comment);
    }

    @Test
    public void tagEquals() {
        final Tag java = Tag.builder().tagName("java").tagId(2L).build();
        final Tag java1 = Tag.builder().tagName("java").tagId(3L).build();
        assertThat(java).isEqualTo(java1);
    }

    @Test
    public void tagEqualsObjects() {
        final Tag java = Tag.builder().tagName("java").tagId(2L).build();
        final Tag java1 = java;
        assertThat(java).isEqualTo(java);
    }

    @Test
    public void commentNotEquals() {
        final Comment comment = Comment.builder().commentText("hallo").commentId(1L).build();
        final Comment comment1 = Comment.builder().commentText("hallo hallo").commentId(2L).build();
        assertThat(comment).isNotEqualTo(comment1);
    }

    @Test
    public void commentNotEqualsObject() {
        final Comment comment = Comment.builder().commentText("hallo").commentId(1L).build();
        final Tag java1 = Tag.builder().tagName("java").tagId(3L).build();
        assertThat(comment).isNotEqualTo(java1);
    }

    @Test
    public void commentEquals() {
        final Comment comment = Comment.builder().commentText("hallo").commentId(1L).build();
        final Comment comment1 = Comment.builder().commentText("hallo").commentId(2L).build();
        assertThat(comment).isEqualTo(comment1);
    }

    @Test
    public void commentEqualsObjects() {
        final Comment comment = Comment.builder().commentText("hallo").commentId(1L).build();
        final Comment comment1 = comment;
        assertThat(comment).isEqualTo(comment1);
    }

}
