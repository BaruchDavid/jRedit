package de.ffm.rka.rkareddit.domain;

import static java.util.Date.from;

import java.time.ZoneId;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;

import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.util.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter @Setter
@ToString(exclude = {"link", "user"})
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Comment extends Auditable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long commentId;
	private String commentText;
	
	@ManyToOne(fetch = FetchType.LAZY)
	//@NotNull
	private User user;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Link link;

	@Autowired
	private transient PrettyTime prettyTime;

	
	private static final ZoneId ZONE_ID = ZoneId.systemDefault();
	
	public String getElapsedTime() {
		prettyTime = BeanUtil.getBeanFromContext(PrettyTime.class);
		return prettyTime.format(from(super.getCreationDate().atZone(ZONE_ID).toInstant()));
	}
}
