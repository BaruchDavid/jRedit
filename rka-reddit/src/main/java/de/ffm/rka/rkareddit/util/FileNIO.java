package de.ffm.rka.rkareddit.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

@Service
public class FileNIO {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileNIO.class);

	public byte[] readPictureToByte(String path) throws IOException {

		File fnew = new File(this.getClass().getClassLoader().getResource(path).getFile());
		BufferedImage originalImage = ImageIO.read(fnew);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(originalImage, "png", baos);
		byte[] pic = baos.toByteArray();
		LOGGER.debug("message byte-array as string {}: ", pic.toString());
		return pic;
	}

	public String readByteToPic(byte[] profileFoto, String name) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(profileFoto);
		BufferedImage bImage2 = ImageIO.read(bis);
		
		String resourcePath = "static/images/".concat(name).concat(".png");
		URL fileUrl = this.getClass().getProtectionDomain().getCodeSource().getLocation();
		String filePath = fileUrl.getPath();
		filePath = filePath.startsWith("/")? filePath.substring(1, filePath.length()) : filePath;
		File file = new File(filePath.concat(resourcePath));
		ImageIO.write(bImage2, "png", file);
		return file.getAbsolutePath();
	}

}
