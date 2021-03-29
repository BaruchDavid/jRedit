package de.ffm.rka.rkareddit.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.util.BeanUtil;
import lombok.*;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.ZoneId;
import java.util.*;

import static java.util.Date.from;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Link extends Auditable implements Serializable {

    private static final long serialVersionUID = -5337989744648444109L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long linkId;

    @NotEmpty(message = "title is required")
    @Column(length = 50)
    private String title;

    @Column(length = 80)
    private String subtitle;

    @Column(length = 100, nullable = true)
    private String description;

    @Column(nullable = false, unique = true)
    private String url;


    @Column(nullable = false)
    private int commentCount;

    @Builder.Default
    private int voteCount = 0;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinTable(
            name = "link_tags",
            joinColumns = @JoinColumn(name = "linkId", referencedColumnName = "linkId"),
            inverseJoinColumns = @JoinColumn(name = "tagId", referencedColumnName = "tagId")
    )
    @JsonIgnore
    private Set<Tag> tags = new HashSet<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_clickedLinks",
            joinColumns = @JoinColumn(name = "linkId", referencedColumnName = "linkId"),
            inverseJoinColumns = @JoinColumn(name = "userId", referencedColumnName = "userId")
    )
    @JsonIgnore
    private Set<User> usersLinksHistory = new HashSet<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Comment is a owner cause of mappedBy argument
     */
    @Builder.Default
    @OneToMany(mappedBy = "link",
            fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Comment> comments = new ArrayList<>();

    @Autowired
    private transient PrettyTime prettyTime;

    public String getElapsedTime() {
        prettyTime = BeanUtil.getBeanFromContext(PrettyTime.class);
        return prettyTime.format(from(super.getCreationDate().atZone(ZoneId.systemDefault()).toInstant()));

    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setLink(this);
    }

    public void removeComment(Comment comment) {
        this.comments.add(comment);
        comment.setLink(null);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getLinks().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getLinks().remove(this);
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Link)) {
            return false;
        }
        Link link = (Link) o;
        return Objects.equals(url, link.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    public void addUserToLinkHistory(User userModel) {
        this.usersLinksHistory.add(userModel);
        userModel.getUserClickedLinks().add(this);
    }

    public void removeUserFromHistory(User user) {
        this.usersLinksHistory.remove(user);
        user.getUserClickedLinks().remove(this);
    }

    @Override
    public String toString() {
        return "Link [linkId=" + linkId + ", title=" + title + ", description=" + description + ", url=" + url
                + ", commentCount=" + commentCount + ", voteCount=" + voteCount + "]";
    }


}
