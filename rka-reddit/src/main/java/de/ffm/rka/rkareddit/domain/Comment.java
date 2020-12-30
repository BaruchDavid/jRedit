package de.ffm.rka.rkareddit.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.util.BeanUtil;
import lombok.*;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZoneId;
import java.util.Objects;

import static java.util.Date.from;


@Entity
@Getter @Setter
@ToString(exclude = {"link", "user"})
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Comment extends Auditable implements Serializable{
	
	private static final long serialVersionUID = -5839947949942907414L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long commentId;
	
	@NotEmpty(message = "comment text must be present")
	private String commentText;

	@JsonIgnore
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Link link;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "user_id")
	private User user;
	
	@Autowired
	private transient PrettyTime prettyTime;

	
	private static final ZoneId ZONE_ID = ZoneId.systemDefault();
	
	public String getElapsedTime() {
		prettyTime = BeanUtil.getBeanFromContext(PrettyTime.class);
		return prettyTime.format(from(super.getCreationDate().atZone(ZONE_ID).toInstant()));
	}
	
	@Override
    public boolean equals(Object o) {
		boolean result;
		if (this == o) {
			result = true;
		} else if (!(o instanceof Comment)) {
			result = false;
		} else {
			Comment other = (Comment) o;
			result = commentText.equals(other.getCommentText());
		}
		return result;
	}
	 
    @Override
    public int hashCode() {
        return Objects.hash(commentText);
    }
}
