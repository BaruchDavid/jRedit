package de.ffm.rka.rkareddit.captcha;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.backgrounds.GradiatedBackgroundProducer;
import cn.apiclub.captcha.noise.CurvedLineNoiseProducer;
import cn.apiclub.captcha.text.producer.DefaultTextProducer;
import cn.apiclub.captcha.text.renderer.DefaultWordRenderer;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.Base64;


@Slf4j
public class CaptchaUtil {

    private CaptchaUtil(){
        //should not be instantiated
    }

    public static Captcha createCaptcha(Integer width, Integer height) {

        return new Captcha.Builder(width, height)
                .addBackground(new GradiatedBackgroundProducer())
                .addText(new DefaultTextProducer(), new DefaultWordRenderer())
                .addNoise(new CurvedLineNoiseProducer())
                .build();
    }

    public static String encodeCaptcha(Captcha captcha) {
        String image = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(captcha.getImage(), "jpg", bos);
            byte[] byteArray = Base64.getEncoder().encode(bos.toByteArray());
            image = new String(byteArray);
        } catch (Exception e) {
            log.error("ERROR on CAPTCHA {} on Date  {}", captcha.getAnswer(), captcha.getTimeStamp());
        }
        return image;
    }
}
