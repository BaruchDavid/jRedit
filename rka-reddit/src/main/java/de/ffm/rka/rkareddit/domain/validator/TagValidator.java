package de.ffm.rka.rkareddit.domain.validator;

import de.ffm.rka.rkareddit.domain.Tag;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 
 * validates given constraint for given object
 * @author RKA
 *
 */
public class TagValidator implements ConstraintValidator<TagResolver, Tag>{

	@Override
	public boolean isValid(Tag tag, ConstraintValidatorContext context) {
		return true;
	}

}
