package de.ffm.rka.rkareddit.domain.validator;

import de.ffm.rka.rkareddit.domain.dto.PictureDTO;
import de.ffm.rka.rkareddit.util.FileNIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;

public class PictureValidator implements Validator {
    private static String PICTURE_SIZE_ERROR = "Picture size is bigger then 1MB";
    private static String PICTURE_CONTENT_ERROR = "Only jpg or png files are allowed";
    private static int MAX_MB_SIZE = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(PictureValidator.class);
    private static final Random random = new Random();
    private static final int A = 97;
    private static final int Z = 122;

    @Override
    public boolean supports(Class<?> clazz) {

        return PictureDTO.class.equals(clazz);
    }


    @Override
    public void validate(Object target, Errors errors) {
        try {
            final PictureDTO pictureDTO = (PictureDTO) target;
            final String picName = "newPic" + random.ints(A, Z).limit(10) + "."+pictureDTO.getPictureExtension();
            String newPicturePath = FileNIO.writeJpgPic(pictureDTO.getFormDataWithFile().getInputStream(), picName);
            Optional<File> newPicture = Optional.empty();
            if (!newPicturePath.isEmpty()) {
                newPicture = Optional.ofNullable(new File(newPicturePath));
            } else {
                errors.rejectValue("formDataWithFile", "content", PICTURE_CONTENT_ERROR);
            }
            newPicture.ifPresent(pic -> {
                Float fileSize = (float) pic.length();
                if (FileNIO.sizeInMB(fileSize) > MAX_MB_SIZE) {
                    errors.rejectValue("formDataWithFile", "size", PICTURE_SIZE_ERROR);
                }
            });
            newPicture.ifPresent(pic -> pic.deleteOnExit());
        } catch (IOException ex) {
            LOGGER.error("PICTURE VALIDATOR ERROR {}", ex);
        }
    }
}
