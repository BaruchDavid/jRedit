package de.ffm.rka.rkareddit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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

	public String readByteToPic(byte[] profileFoto, String name) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(profileFoto);
		BufferedImage bImage2 = ImageIO.read(bis);	
		String webResourcePath = "static/images/".concat(name).concat(".png");
		URL fileUrl = this.getClass().getProtectionDomain().getCodeSource().getLocation();
		String filePath = fileUrl.getPath();
		File file = new File(filePath.concat(webResourcePath));
		ImageIO.write(bImage2, "png", file);
		return webResourcePath;
	}

}
