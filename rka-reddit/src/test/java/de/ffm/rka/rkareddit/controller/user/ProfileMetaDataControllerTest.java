package de.ffm.rka.rkareddit.controller.user;

import de.ffm.rka.rkareddit.controller.MvcRequestSender;
import de.ffm.rka.rkareddit.domain.dto.PictureDTO;
import de.ffm.rka.rkareddit.rest.controller.ProfileMetaDataController;
import de.ffm.rka.rkareddit.util.FileNIO;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ProfileMetaDataControllerTest extends MvcRequestSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileMetaDataControllerTest.class);


    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void shouldReturnUserPicture() throws Exception {
        String getRequest = "/profile/information/content/user-pic?user=romakapt@gmx.de";
        MvcResult result = super.performGetRequest(getRequest)
                            .andExpect(status().isOk())
                            .andReturn();
        byte[] data = result.getResponse().getContentAsByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        BufferedImage bImage2 = ImageIO.read(bis);
        File receivedUserPic = new File("receivedUserPic.png");
        ImageIO.write(bImage2, "png", receivedUserPic);
        LOGGER.info("RECEIVED IMAGE READABLE: {}", receivedUserPic.canRead());
        LOGGER.info("RECEIVED IMAGE HAS BEEN DELETED: {}", receivedUserPic.delete());
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void shouldReturnUserPicForNotExistUser() throws Exception {
        String getRequest = "/profile/information/content/user-pic?user=xxx@gmx.de";
        final MvcResult mvcResult = super.performGetRequest(getRequest)
                .andExpect(status().is3xxRedirection())
                .andReturn();
        sendRedirect(mvcResult.getResponse().getHeader("location"));
    }

    @Test
    public void schouldNotReturnPicWithEmptyParamNameAsUnauthenticated() throws Exception {
        super.performGetRequest("/profile/information/content/user-pic?romakapt@gmx.de")
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnUserPictureAsUnauthenticated() throws Exception {
        MvcResult result = super.performGetRequest("/profile/information/content/user-pic?user=romakapt@gmx.de")
                                .andExpect(status().isOk())
                                .andReturn();
        byte[] data = result.getResponse().getContentAsByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        BufferedImage bImage2 = ImageIO.read(bis);
        File receivedUserPic = new File("receivedUserPic.png");
        ImageIO.write(bImage2, "png", receivedUserPic);
        LOGGER.info("RECEIVED IMAGE READABLE: {}", receivedUserPic.canRead());
        LOGGER.info("RECEIVED IMAGE HAS BEEN DELETED: {}", receivedUserPic.delete());
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void clickedLinksHistoryForAuthenticatedUser() throws Exception {
        MvcResult result = super.performGetRequest("/profile/information/userClickedLinks?user=romakapt@gmx.de")
                            .andExpect(status().isOk())
                            .andReturn();
        final String stringRes = result.getResponse().getContentAsString();
        assertTrue(stringRes.contains("\"linkId\" : 4"));
        assertTrue(stringRes.contains("\"linkId\" : 2"));
        assertTrue(stringRes.contains("\"linkId\" : 3"));
    }


    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void clickedLinksHistoryForStrangeAuthenticatedUser() throws Exception {
        MvcResult result =  super.performGetRequest("/profile/information/userClickedLinks?user=dascha@gmx.de")
                                .andExpect(status().is(200))
                                .andReturn();
        final String stringRes = result.getResponse().getContentAsString();
        assertEquals("[ ]",stringRes);
    }

    @Test
    public void clickedLinksHistoryForEmptyUser() throws Exception {
        final MvcResult mvcResult = super.performGetRequest("/profile/information/userClickedLinks")
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andReturn();
        sendRedirect(mvcResult.getResponse().getHeader("location"));
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void postValidNewPicture() throws Exception {
        Path path = Paths.get(URI.create(FileNIO.getFullQualifiedPathWithAsURL(ProfileMetaDataController.class)
                + "/static/images/profile_small.png"));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Files.copy(path, byteArrayOutputStream);
        //MockMultipartFile firstFile = new MockMultipartFile("pic", byteArrayOutputStream.toByteArray());
        String[] oneRequestParam = new String[]{"pictureExtension","png"};
        super.performPostByteArray("/profile/information/content/user-pic",
                "formDataWithFile",
                byteArrayOutputStream.toByteArray(), oneRequestParam)
                .andExpect(status().is(201))
                .andExpect(content().string("ok"));
    }

    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void postInValidNewPictureWithWrongExtension() throws Exception {
        final String defaultBaseDir = System.getProperty("java.io.tmpdir");
        final String fileName = "postgresql-12.2-2-windows-x64.exe";
        final Optional<ByteArrayOutputStream> byteArrayOutputStream = FileNIO.readPictureToBytes(fileName, defaultBaseDir);
        if(byteArrayOutputStream.isPresent()){
            MockMultipartFile firstFile = new MockMultipartFile("pic", byteArrayOutputStream.get().toByteArray());
            PictureDTO pictureDTO = new PictureDTO();
            pictureDTO.setFormDataWithFile(firstFile);
            String[] oneRequestParam = new String[]{"pictureExtension","exe"};
            super.performPostByteArray("/profile/information/content/user-pic",
                    "formDataWithFile", byteArrayOutputStream.get().toByteArray(), oneRequestParam)
                    .andExpect(status().is(HttpStatus.SC_BAD_REQUEST))
                    .andExpect(content().string("Only jpg/jpeg, png or gif picture is allowed"));
        } else {
            fail("FILE "+ fileName + " ON PATH " + defaultBaseDir + " COULD NOT BE READ");
        }
    }

    /**
     * test jpg-picture 12.760KB
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails("romakapt@gmx.de")
    public void postToBigValidNewPicture() throws Exception {
        String defaultBaseDir = System.getProperty("java.io.tmpdir");
        Path path = Paths.get(defaultBaseDir + "1mb-TestBild.jpg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Files.copy(path, byteArrayOutputStream);
        MockMultipartFile firstFile = new MockMultipartFile("pic", byteArrayOutputStream.toByteArray());
        PictureDTO pictureDTO = new PictureDTO();
        pictureDTO.setFormDataWithFile(firstFile);
        String[] oneRequestParam = new String[]{"pictureExtension","jpg"};
        super.performPostByteArray("/profile/information/content/user-pic",
                "formDataWithFile", byteArrayOutputStream.toByteArray(), oneRequestParam)
                .andExpect(status().is(HttpStatus.SC_BAD_REQUEST))
                .andExpect(content().string("Picture size is bigger then 1MB"));
    }

}
