package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.rest.controller.ProfileMetaDataController;
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

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class FileNIOTest {
	
	@Autowired
	private FileNIO fileNIO;
	
	@Test
	public void testReadImage() throws IOException {
		String path = "static/images/profile_small.png";
		assertEquals(true, fileNIO.readPictureToByte(path).isPresent());
	}
	
	/**
	 * convert image to byte[] and back to the image
	 * test for readable and deletion
	 */
	@Test
	public void writePNG() throws IOException {
		final Path picPath = Paths.get(URI.create(FileNIO.getFullQualifiedPathWithAsURL(ProfileMetaDataController.class)
				+ "/static/images/profile_small.png"));
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Files.copy(picPath, byteArrayOutputStream);
		String path = FileNIO.writeJpgPic(byteArrayOutputStream.toByteArray(), "test@test.com");
		assertEquals(true, Files.isReadable(picPath.resolve(path)));
		assertEquals(true, Files.deleteIfExists(picPath.resolve(path)));
		
	}

	// TODO: 09.12.2020 jpg anlegen und hier testen 
	@Test
	public void writeJPG() throws IOException {
		final Path picPath = Paths.get(URI.create(FileNIO.getFullQualifiedPathWithAsURL(ProfileMetaDataController.class)
				+ "/static/images/profile_small.png"));
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Files.copy(picPath, byteArrayOutputStream);
		String path = FileNIO.writeJpgPic(byteArrayOutputStream.toByteArray(), "test@test.com");
		assertEquals(true, Files.isReadable(picPath.resolve(path)));
		assertEquals(true, Files.deleteIfExists(picPath.resolve(path)));

	}
	
}
