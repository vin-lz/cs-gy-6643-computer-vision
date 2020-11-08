package project_1;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.HashSet;
import javax.imageio.ImageIO;

public class Project1 {
    private final static String[] FILENAMES = {"data13", "fruits2b", "tiger1-24bits"};
    private final static int[][] DIMENSIONS = {{320, 240}, {484, 363}, {690, 461}};
//    private final static String[] FILENAMES = {"tiger1"};
    public static void main(String[] args) {
        BufferedImage image = null;
        for (int i = 0; i < FILENAMES.length; i++) {
            // Read image file
            image = readImage(FILENAMES[i], DIMENSIONS[i][0], DIMENSIONS[i][1]);
            System.out.println(FILENAMES[i]);
            // Convert to grayscale
            image = convertToGrayScale(image);
            // Generate magnitude distribution of all pixels
            int[] distribution = generateDistribution(image);
            // Compute thresholds
            int[] thresholds = computeThresholds(distribution);
            // modify image according to the threshold
            int width = image.getWidth();
            int height = image.getHeight();
            int t1 = (thresholds[0]<<16)|(thresholds[0]<<8)|(thresholds[0]<<0);
            int t2 = (thresholds[1]<<16)|(thresholds[1]<<8)|(thresholds[1]<<0);
            int t3 = (thresholds[2]<<16)|(thresholds[2]<<8)|(thresholds[2]<<0);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int p = image.getRGB(x, y);
                    int v = p&0xff;
                    if (v <= thresholds[0]) {
                        image.setRGB(x, y, t1);
                    } else if (v <= thresholds[0]) {
                        image.setRGB(x, y, t2);
                    } else if (v <= thresholds[1]) {
                        image.setRGB(x, y, t3);
                    } else {
                        image.setRGB(x, y, (255<<16)|(255<<8)|255);
                    }
                }
            }
            // Write image
            writeImage(image, FILENAMES[i]);
            System.out.println("-------------");
        }
    }

    private static BufferedImage readImage(String fileName, int width, int height) {
        BufferedImage image = null;
        try {
            File inputFile = new File("src/project_1/" + fileName + ".bmp");
            image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            image = ImageIO.read(inputFile);
            return image;
        } catch (IOException e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    private static BufferedImage convertToGrayScale(BufferedImage image) {
        // Get width & height of the image
        int width = image.getWidth();
        int height = image.getHeight();

        // Convert to grayscale image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = (p>>0)&0xff;
                int gray = (int)Math.round(0.299 * r + 0.587 * g + 0.114 * b);
                p = (gray<<16)|(gray<<8)|(gray<<0);
                image.setRGB(x, y, p);
                Color clr = new Color(image.getRGB(x,y));
                p = clr.getRGB();
//                System.out.println(Integer.toBinaryString(r) + " " + Integer.toBinaryString(g) + " " + Integer.toBinaryString(b));
                String binaryString = String.format("%32s", Integer.toBinaryString(p)).replace(' ', '0');
                Set<String> set = new HashSet<>();
                set.add(binaryString.substring(24,32));
                set.add(binaryString.substring(8,16));
                set.add(binaryString.substring(16,24));
                if (set.size() != 1) {
                    System.out.println("PRE!!!" + binaryString.substring(0,8) + "!!" + binaryString.substring(8,16) + "!!" + binaryString.substring(16,24) + "!!" + binaryString.substring(24,32));
                }

//                int pp = image.getRGB(x, y);
//                String binaryStringAfter = String.format("%24s", Integer.toBinaryString(pp)).replace(' ', '0');
//                Set<String> set = new HashSet<>();
//                set.add(binaryStringAfter.substring(0,8));
//                set.add(binaryStringAfter.substring(8,16));
//                set.add(binaryStringAfter.substring(16,24));
//                if (set.size() != 1) {
//                    System.out.println("PRE!!!" + binaryString.substring(0,8) + "!!" + binaryString.substring(8,16) + "!!" + binaryString.substring(16,24) + "\nPOST!!" + binaryStringAfter.substring(0,8) + "!!" + binaryStringAfter.substring(8,16) + "!!" + binaryStringAfter.substring(16,24));
//                } else {
//                    System.out.println("##" + binaryString.substring(0,8) + "#" + binaryString.substring(8,16) + "#" + binaryString.substring(16,24));
//                }
            }
        }
        return image;
    }

    private static void writeImage(BufferedImage image, String fileName) {
        try {
            File file = new File("src/project_1/" + fileName + "_out.bmp");
            ImageIO.write(image, "bmp", file);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static int[] generateDistribution(BufferedImage image) {
        int[] distribution = new int[256];
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                int v = p&0xff;
                distribution[v]++;
            }
        }

//        int sum = 0;
//        for (int i : distribution) {
//            sum += i;
//            System.out.print(i + " ");
//        }
//        System.out.print("***SUM***" + sum);

        return distribution;
    }

    private static long computeWeight(int[] distribution, int boundaryLow, int boundaryHigh) {
        long weight = 0;
        for (int i = boundaryLow; i < boundaryHigh; i++) {
            weight += i * distribution[i];
        }

        return weight;
    }

    private static double computeVariance(int[] distribution, int boundaryLow, int boundaryHigh) {
        long sum = 0;
        long numberOfPixels = 0;
        for (int i = boundaryLow; i < boundaryHigh; i++) {
            sum += distribution[i] * i;
            numberOfPixels += distribution[i];
        }
        double mean = (double)sum / (double)numberOfPixels;
//        System.out.println("MEAN: " + mean);
        double sqDiff = 0;
        for (int i = boundaryLow; i < boundaryHigh; i++) {
            sqDiff += (i - mean) * (i - mean);
        }
        double variance = (double)sqDiff / (double)numberOfPixels;

        return variance;
    }

    private static int[] computeThresholds(int[] distribution) {
        int[] thresholds = new int[3];
        double varianceOverall = Double.MAX_VALUE;
        for (int i = 0; i < 254; i++) {
            for (int j = i + 1; j < 255; j++) {
                for (int k = j + 1; k < 256; k++) {
                    double varianceTemp = computeWeight(distribution, 0, i) * computeVariance(distribution, 0, i) + computeWeight(distribution, i, j) * computeVariance(distribution, i, j) + computeWeight(distribution, j, k) * computeVariance(distribution, j, k) + computeWeight(distribution, k, 256) * computeVariance(distribution, k, 256);
                    if (varianceTemp < varianceOverall) {
                        thresholds = new int[]{i, j, k};
                        varianceOverall = varianceTemp;
                    }
                }
            }
        }
        System.out.println("t1: " + thresholds[0] + "\nt2: " + thresholds[1] + "\nt3: " + thresholds[2]);
        return thresholds;
    }
}
