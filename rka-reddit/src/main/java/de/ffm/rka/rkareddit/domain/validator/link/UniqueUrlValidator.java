package de.ffm.rka.rkareddit.domain.validator.link;

import de.ffm.rka.rkareddit.repository.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUrlValidator implements ConstraintValidator<UniqueUrl, String> {


    @Autowired
    LinkRepository linkRepository;

    /**
     * if Link is present, it is true. On Validation view it is false,
     * because, when link is present, it is a not valid link-url,
     * that's why negotiation will be used
     *
     * @param linkUrl                    which should be saved
     * @param constraintValidatorContext containts context
     * @return true, when url is not present
     */
    @Override
    public boolean isValid(String linkUrl, ConstraintValidatorContext constraintValidatorContext) {
        return linkRepository.findByUrl(linkUrl).isEmpty();
    }
}
