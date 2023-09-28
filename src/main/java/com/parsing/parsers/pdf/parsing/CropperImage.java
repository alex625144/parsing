package com.parsing.parsers.pdf.parsing;

import com.recognition.software.jdeskew.ImageDeskew;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.util.ImageHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Slf4j
public class CropperImage {
    private static final String FILENAME = "cropImage.png";

    public String cropImage(String filename) throws IOException {
        log.info("Method cropImage started.");
        BufferedImage image = ImageIO.read(new File(filename));
        ImageDeskew id = new ImageDeskew(image);
        image = ImageHelper.rotateImage(image, -id.getSkewAngle());
        ImageIO.write(image, "png", new File(FILENAME));
        log.info("Method cropImage finished.");
        return FILENAME;
    }
}
