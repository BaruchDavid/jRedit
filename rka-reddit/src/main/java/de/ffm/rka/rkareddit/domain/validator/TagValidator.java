package de.ffm.rka.rkareddit.domain.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import de.ffm.rka.rkareddit.domain.Tag;

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
