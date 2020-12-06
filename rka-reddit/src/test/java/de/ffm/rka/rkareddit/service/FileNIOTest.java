package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.util.FileNIO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
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
	public void testReadImageToPic() throws IOException {
		String path = "static/images/profile_small.png";
		byte[] pic = fileNIO.readPictureToByte(path).get();
		String url = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replaceFirst("/","");
		Path picPath = Paths.get(url).getParent().resolve("classes/");	
		path = FileNIO.writeByteToJpgPic(pic, "test@test.com");
		assertEquals(true, Files.isReadable(picPath.resolve(path)));
		assertEquals(true, Files.deleteIfExists(picPath.resolve(path)));
		
	}
	
}
