package de.ffm.rka.rkareddit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Optional;

@Service
public class FileNIO {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileNIO.class);

	/**
	 * converts pictures into byte-array as png
	 */
	public Optional<byte[]> readPictureToByte(String path) throws IOException {
		Optional<byte[]> pic = Optional.empty() ;
		URL resourceUrl = this.getClass().getClassLoader().getResource(path);
		if(resourceUrl != null) {
			File fnew = new File(resourceUrl.getFile());
			BufferedImage originalImage = ImageIO.read(fnew);
			try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
				ImageIO.write(originalImage, "png", baos);
				pic = Optional.of(baos.toByteArray());
				LOGGER.debug("message byte-array {}: ", pic);
			}
		}else {
			LOGGER.warn("no picture found for converting into byte-array {}: ", pic);
		}
		return pic;
	}

	public static String writeByteToJpgPic(byte[] profilePhoto, String name) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(profilePhoto);
		BufferedImage bImage2 = ImageIO.read(bis);
		String webResourcePath = getFullQualifiedPathWithAsURL(FileNIO.class).getPath() + "static/images/".concat(name).concat(".jpg");
		File file1 = new File(webResourcePath);
		ImageIO.write(bImage2, "jpg", file1);
		return webResourcePath;
	}

	/**
	 *
	 * @param clazz which represents path for required resource
	 * @return URL with schema like 'file/C:/folder1'
	 */
	public static URL getFullQualifiedPathWithAsURL(Class clazz){
		return clazz.getProtectionDomain().getCodeSource().getLocation();
	}

	/**
	 *
	 * @param inputStream fills pictureBuffer with content
	 * @return amount of read bytes
	 * @throws IOException
	 */
	public static byte[] readPictureStreamToByte(InputStream inputStream) throws IOException {
		byte[] pictureBuffer = new byte[inputStream.available()];
		final int readBytes = inputStream.read(pictureBuffer);
		if (readBytes > -1 && readBytes == pictureBuffer.length) {
			LOGGER.info("read {} bytes of inputstream length {}", readBytes,pictureBuffer.length);
			return pictureBuffer;
		} else {
			LOGGER.info("could not read input stream length {} into byte-array {}", inputStream.available());
			return new byte[0];
		}
	}

	public static float sizeInMB(Float size){
		return Float.valueOf(size / Long.valueOf(1024*1024));
	}
}
