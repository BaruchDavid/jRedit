package de.ffm.rka.rkareddit.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.ffm.rka.rkareddit.util.FileNIO;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class FileNIOTest {
	
	@Autowired
	private FileNIO fileNIO;
	
	@Test
	public void testReadImage() throws IOException {
		String path = "static/images/profile_small.png";
		assertEquals(1088660, fileNIO.readPictureToByte(path).length);
	}
	
	@Test
	public void testReadImageToPic() throws IOException {
		String path = "static/images/profile_small.png";
		byte[] pic = fileNIO.readPictureToByte(path);
		String url = fileNIO.readByteToPic(pic, "test@test.com");
		assertEquals(true, Files.isReadable(Paths.get(url)));
	}
	
}
