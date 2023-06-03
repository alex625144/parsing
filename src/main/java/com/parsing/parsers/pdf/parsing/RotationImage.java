package com.parsing.parsers.pdf.parsing;

import com.recognition.software.jdeskew.ImageDeskew;
import net.sourceforge.tess4j.util.ImageHelper;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class RotationImage {

    public String rotate(String filename) throws IOException {
        String result = "rotatedImage.jpg";
        BufferedImage image = ImageIO.read(new File(filename));
        ImageDeskew id = new ImageDeskew(image);
        image = ImageHelper.rotateImage(image, -id.getSkewAngle());
        ImageIO.write(image, "jpg", new File(result));
        return result;
    }
}
