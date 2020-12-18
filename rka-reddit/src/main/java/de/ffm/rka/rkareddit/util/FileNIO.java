package de.ffm.rka.rkareddit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class FileNIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileNIO.class);
    public static Optional<ByteArrayOutputStream> readPictureToBytes(String resourceName, String resourcePath)
            throws IOException {
        if (Optional.ofNullable(resourceName)
                .orElse("").isEmpty()) {
            return Optional.empty();
        }
        if (!Optional.ofNullable(resourcePath)
                .orElseGet(() -> System.getProperty("java.io.tmpdir"))
                .isEmpty()) {
            Path filePath = Paths.get(resourcePath + resourceName);
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                Files.copy(filePath, baos);
                return Optional.of(baos);
            }
        }
        return Optional.empty();
    }

    /**
     * converts pictures into byte-array as png
     */
    public Optional<byte[]> readPictureToByte(String path) throws IOException {
        Optional<byte[]> picByteArray = Optional.empty();
        URL resourceUrl = this.getClass().getClassLoader().getResource(path);
        if (resourceUrl != null) {
            File tmpFile = new File(resourceUrl.getFile());
            BufferedImage originalImage = ImageIO.read(tmpFile);
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(originalImage, "png", baos);
                picByteArray = Optional.of(baos.toByteArray());
                LOGGER.debug("message byte-array {}: ", picByteArray);
            }
        } else {
            LOGGER.warn("no picture found for converting into byte-array {}: ", picByteArray);
        }
        return picByteArray;
    }

    public static String writeImage(byte[] profilePhoto, String name) throws IOException {
        return writeImage(new ByteArrayInputStream(profilePhoto), name);
    }

    public static String writeImage(InputStream inputStream, String name) throws IOException {
        String webResourcePath="";
        final BufferedImage bufferedImage = ImageIO.read(inputStream);
        if (Optional.ofNullable(bufferedImage).isPresent()) {
            webResourcePath = System.getProperty("java.io.tmpdir")+name;
            File newPic = new File(webResourcePath);
            if(ImageIO.write(bufferedImage, getExtensionFromFile(webResourcePath), newPic)){
                return webResourcePath;
            } else {
                LOGGER.warn("COULD NO WRITE PICTURE {}",  newPic.getAbsolutePath());
                webResourcePath="";
            }
        }
        return webResourcePath;
    }

    public static String getExtensionFromFile(String ressource){
        final String[] parts = ressource.split("\\.");
        return parts[parts.length-1];
    }
    /**
     * @param clazz which represents path for required resource
     * @return URL with schema like 'file/C:/folder1'
     */
    public static URL getFullQualifiedPathWithAsURL(Class clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation();
    }

    /**
     * @param inputStream fills pictureBuffer with content
     * @return amount of read bytes
     * @throws IOException
     */
    public static byte[] readPictureStreamToByte(InputStream inputStream) throws IOException {
        byte[] pictureBuffer = new byte[inputStream.available()];
        final int readBytes = inputStream.read(pictureBuffer);
        if (readBytes == pictureBuffer.length) {
            LOGGER.info("read {} bytes of input stream length {}", readBytes, pictureBuffer.length);
            return pictureBuffer;
        } else {
            LOGGER.info("could not read input stream length {} into byte-array {}",pictureBuffer.length,  inputStream.available());
            return new byte[0];
        }
    }

    public static float sizeInMB(Float size) {
        return  size / (1024 * 1024);
    }
}
