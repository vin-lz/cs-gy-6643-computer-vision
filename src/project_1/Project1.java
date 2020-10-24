package project_1;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Project1 {
    private final static String[] FILENAMES = {"data13.bmp", "fruits2b.bmp", "tiger1.bmp"};

    public static void main(String[] args) {
        BufferedImage image = null;

        // Read image file
        BufferedImage image = readImage(FILENAMES[1]);

        // Get width & height of the image
        int width = image.getWidth();
        int height = image.getHeight();

        // Convert to grayscale image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;
                int gray = (int)Math.round(0.299 * r + 0.587 * g + 0.114 * b);
                p = (gray<<16)|(gray<<8)|gray;
                image.setRGB(x, y, p);
            }
        }

        // Write image
        try {
            File f = new File("src/project_1/1.bmp");
            ImageIO.write(image, "bmp", f);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static BufferedImage readImage(String fileName) {
        try {
            File inputFile = new File("src/project_1/" + fileName);
            BufferedImage image = ImageIO.read(inputFile);
            return image;
        } catch (IOException e) {
            System.out.println("Error: " + e);
            return null;
        }
    }
}
