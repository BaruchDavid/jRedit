package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.util.FileNIO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class FileNIOTest {
	
	@Autowired
	private FileNIO fileNIO;
	
	@Test
	public void testReadImage() throws IOException {
		String path = "static/images/profile_small.png";
		assertTrue(fileNIO.readPictureToByte(path).isPresent());
	}
	
	/**
	 * convert image to byte[] and back to the image
	 * test for readable and deletion
	 */
	@Test
	public void writePNG() throws IOException {
		final Path picPath = Paths.get(URI.create(FileNIO.getFullQualifiedPathWithAsURL(FileNIO.class)
				+ "/static/images/profile_small.png"));
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Files.copy(picPath, byteArrayOutputStream);
		String path = FileNIO.writeImage(byteArrayOutputStream.toByteArray(), "test@test.png");
		assertFalse(path.isEmpty());
	}

	@Test
	public void writeJPG() throws IOException {
		final Path picPath = Paths.get(URI.create(FileNIO.getFullQualifiedPathWithAsURL(FileNIO.class)
				+ "/static/images/profile_small.jpg"));
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Files.copy(picPath, byteArrayOutputStream);
		String path = FileNIO.writeImage(byteArrayOutputStream.toByteArray(), "test@test.jpg");
		assertFalse(path.isEmpty());
	}
	
}
