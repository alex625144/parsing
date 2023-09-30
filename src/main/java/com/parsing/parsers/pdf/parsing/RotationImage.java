package com.parsing.parsers.pdf.parsing;

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

    public String rotateImage(String filename, int pageNumber) throws IOException {
        log.info("Method rotateImage started.");
        String result = pageNumber + "_#2_rotatedImage.png";
        BufferedImage image = ImageIO.read(new File(filename));
        ImageDeskew id = new ImageDeskew(image);
        image = ImageHelper.rotateImage(image, -id.getSkewAngle());
        ImageIO.write(image, "png", new File(result));
        log.info("Method rotateImage finished.");
        return result;
    }
}
