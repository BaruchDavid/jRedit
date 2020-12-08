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
                return Optional.ofNullable(baos);
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
            File fnew = new File(resourceUrl.getFile());
            BufferedImage originalImage = ImageIO.read(fnew);
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

    public static String writeJpgPic(byte[] profilePhoto, String name) throws IOException {
        return writeJpgPic(new ByteArrayInputStream(profilePhoto), name);
    }

    public static String writeJpgPic(InputStream inputStream,  String name) throws IOException {
        String webResourcePath="";
        final BufferedImage bufferedImage = ImageIO.read(inputStream);
        if (Optional.ofNullable(bufferedImage).isPresent()) {
            webResourcePath = System.getProperty("java.io.tmpdir")+name;
            File newPic = new File(webResourcePath);
            String extension = webResourcePath.substring(webResourcePath.length()-3);
            if(ImageIO.write(bufferedImage, extension, newPic)){
                return webResourcePath;
            } else {
                LOGGER.warn("COULD NO WRITE PICTURE {}",  newPic.getAbsolutePath());
                webResourcePath="";
            }
        }
        return webResourcePath;
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
        if (readBytes > -1 && readBytes == pictureBuffer.length) {
            LOGGER.info("read {} bytes of inputstream length {}", readBytes, pictureBuffer.length);
            return pictureBuffer;
        } else {
            LOGGER.info("could not read input stream length {} into byte-array {}", inputStream.available());
            return new byte[0];
        }
    }

    public static float sizeInMB(Float size) {
        return  Float.valueOf(size / Long.valueOf(1024 * 1024));
    }
}
