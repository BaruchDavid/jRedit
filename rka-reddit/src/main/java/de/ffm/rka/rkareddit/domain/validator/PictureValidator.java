package de.ffm.rka.rkareddit.domain.validator;

import de.ffm.rka.rkareddit.domain.dto.PictureDTO;
import de.ffm.rka.rkareddit.util.FileNIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.ffm.rka.rkareddit.domain.dto.PictureExtension.*;


public class PictureValidator implements Validator {
    private static final String PICTURE_SIZE_ERROR = "Picture size is bigger then 1MB";
    private static final String WRONG_EXTENSION_ERROR = "Only jpg/jpeg, png or gif picture is allowed";
    private static final String PICTURE_CONTENT_ERROR = "Picture is corrupt";
    private static final int MAX_MB_SIZE = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(PictureValidator.class);
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int A = 97;
    private static final int Z = 122;

    @Override
    public boolean supports(Class<?> clazz) {

        return PictureDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        try {
            Optional<File> newPicture = Optional.empty();
            final PictureDTO pictureDTO = (PictureDTO) target;
            if(isExtensionValid(errors, pictureDTO.getPictureExtension())){
                newPicture = checkPictureContent(errors, pictureDTO);
            }
            newPicture.ifPresent(picture -> checkPictureSize(errors, picture));
            newPicture.ifPresent(File::deleteOnExit);
        } catch (IOException ex) {
            LOGGER.error("PICTURE VALIDATOR ERROR {}", ex.getMessage(), ex);
        }
    }

    private void checkPictureSize(Errors errors, File newPicture) {
        Float fileSize = (float) newPicture.length();
        if (FileNIO.sizeInMB(fileSize) > MAX_MB_SIZE) {
            errors.rejectValue("formDataWithFile", "size", PICTURE_SIZE_ERROR);
        }
    }

    private Optional<File> checkPictureContent(Errors errors, PictureDTO pictureDTO) throws IOException {

        final String newFileName = secureRandom.ints(A, Z)
                .limit(10)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining());

        final String picName = String.join("", "newPic",
                newFileName,
                ".",
                pictureDTO.getPictureExtension());

        String newPicturePath = FileNIO.writeImage(pictureDTO.getFormDataWithFile().getInputStream(), picName);
        Optional<File> newPicture = Optional.empty();
        if (!newPicturePath.isEmpty()) {
            newPicture = Optional.of(new File(newPicturePath));
        } else {
            errors.rejectValue("formDataWithFile", "content", PICTURE_CONTENT_ERROR);
        }
        return newPicture;
    }

    private boolean isExtensionValid(Errors errors, String extension) {
        if (!PNG.equalsName(extension) && !JPG.equalsName(extension)
                && !JPEG.equalsName(extension) && !GIF.equalsName(extension)) {
            errors.rejectValue("pictureExtension", "content", WRONG_EXTENSION_ERROR);
            return false;
        } else {
            return true;
        }
    }
}
