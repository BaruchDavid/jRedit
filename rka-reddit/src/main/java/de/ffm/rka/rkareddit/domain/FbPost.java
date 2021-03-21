package de.ffm.rka.rkareddit.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
@Data
@NoArgsConstructor
public class FbPost {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	String fbId;
	
	String  link;
	String  linkName;
	String  linkCaption; 
	String  linkDesc;

}
