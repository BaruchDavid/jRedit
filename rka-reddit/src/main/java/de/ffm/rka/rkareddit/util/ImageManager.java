package de.ffm.rka.rkareddit.util;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class ImageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageManager.class);
    private ImageManager(){}

    public static BufferedImage simpleResizeImage(InputStream inputStream , int targetWidth) throws Exception {
        LOGGER.info("TRY TO RESIZE IMAGE");
        BufferedImage imBuff = ImageIO.read(inputStream);
        return Scalr.resize(imBuff, Scalr.Method.SPEED, targetWidth);
    }
}
