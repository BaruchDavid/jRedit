package de.ffm.rka.rkareddit.domain.audit;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@SuperBuilder
@Getter @Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public abstract class Auditable {

	@CreatedBy
	private String createdBy;

	@NotNull
	@CreatedDate
	private LocalDateTime creationDate;

}
