package de.ffm.rka.rkareddit.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class FileNIO {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileNIO.class);
	
	public byte[] readPictureToByte(String path) throws IOException {

		File fnew=new File(this.getClass().getClassLoader().getResource(path).getFile());
		BufferedImage originalImage=ImageIO.read(fnew);
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ImageIO.write(originalImage, "png", baos );
		byte[] pic = baos.toByteArray();
		LOGGER.debug("message byte-array as string {}: " , pic.toString());
		return pic;
	}

	
}
