package de.ffm.rka.rkareddit.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


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
