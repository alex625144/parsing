package com.parsing.parsers.pdf.parsing;

import com.recognition.software.jdeskew.ImageDeskew;
import net.sourceforge.tess4j.util.ImageHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CropperImage {

    public String cropImage(String filename) throws IOException {
        String result = "cropImage.png";
        BufferedImage image = ImageIO.read(new File(filename));
        ImageDeskew id = new ImageDeskew(image);
        image = ImageHelper.rotateImage(image, -id.getSkewAngle());
        ImageIO.write(image, "png", new File(result));
        return result;
    }
}
