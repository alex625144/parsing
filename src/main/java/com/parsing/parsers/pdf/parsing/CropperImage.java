package com.parsing.parsers.pdf.parsing;

import com.parsing.exception.CropImageException;
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

    public String cropImage(String filename) {
        log.debug("Method cropImage started.");
        try {
            BufferedImage image = ImageIO.read(new File(filename));
            ImageDeskew id = new ImageDeskew(image);
            image = ImageHelper.rotateImage(image, -id.getSkewAngle());
            ImageIO.write(image, "png", new File(FILENAME));
            log.debug("Method cropImage finished.");
            return FILENAME;
        } catch (IOException e) {
            throw new CropImageException(e);
        }
    }
}
