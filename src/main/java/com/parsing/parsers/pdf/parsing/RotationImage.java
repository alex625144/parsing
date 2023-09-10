package com.parsing.parsers.pdf.parsing;

import com.parsing.exception.RotationImageException;
import com.recognition.software.jdeskew.ImageDeskew;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.util.ImageHelper;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class RotationImage {

    public String rotate(String filename) {
        log.debug("Class RotationImage started.");
        try {
            String result = "rotatedImage.jpg";
            BufferedImage image = ImageIO.read(new File(filename));
            ImageDeskew id = new ImageDeskew(image);
            image = ImageHelper.rotateImage(image, -id.getSkewAngle());
            ImageIO.write(image, "jpg", new File(result));
            log.debug("Class RotationImage finished.");
            return result;
        } catch (IOException e) {
            throw new RotationImageException(e);
        }
    }
}
