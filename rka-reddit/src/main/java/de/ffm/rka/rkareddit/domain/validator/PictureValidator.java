package de.ffm.rka.rkareddit.domain.validator;

import de.ffm.rka.rkareddit.domain.dto.PictureDTO;
import de.ffm.rka.rkareddit.util.FileNIO;
import lombok.SneakyThrows;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.io.File;

public class PictureValidator implements Validator {
    private static String PICTURE_SIZE_ERROR = "Picture size is bigger then 1MB";
    private static int MAX_MB_SIZE = 1;

    @Override
    public boolean supports(Class<?> clazz) {

        return PictureDTO.class.equals(clazz);
    }

    @SneakyThrows
    @Override
    public void validate(Object target, Errors errors) {

        final PictureDTO pictureDTO = (PictureDTO) target;

        byte[] bytes = FileNIO.readPictureStreamToByte(pictureDTO.getFormDataWithFile().getInputStream());

        File newPicture = new File(FileNIO.writeByteToJpgPic(bytes, "newPicture"));
        if (FileNIO.sizeInMB(Float.valueOf(newPicture.length())) > MAX_MB_SIZE) {
            errors.rejectValue("formDataWithFile", "size", PICTURE_SIZE_ERROR);
        }
    }
}
