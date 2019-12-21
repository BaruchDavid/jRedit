package de.ffm.rka.rkareddit.domain;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import de.ffm.rka.rkareddit.domain.audit.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Data
@NoArgsConstructor
@ToString
public class Tag extends Auditable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long tagId;
	
	@NotNull
	private String tag;
	
	public Tag(@NotNull String tag) {
		this.tag = tag;
	}	
}
