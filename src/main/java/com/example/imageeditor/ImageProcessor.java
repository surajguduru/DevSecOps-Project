package com.example.imageeditor;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Collection of image processing utilities (pure static methods).
 */
public class ImageProcessor {

    public static void printPixelValues(BufferedImage inputImage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color pixel = new Color(inputImage.getRGB(j, i));
                System.out.print("(" + pixel.getRed() + "," + pixel.getGreen() + "," + pixel.getBlue() + ") ");
            }
            System.out.println();
        }
    }

    public static BufferedImage toGrayscale(BufferedImage inputImage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(inputImage.getRGB(x, y));
                int gray = (int) (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue());
                gray = clamp(gray);
                Color g = new Color(gray, gray, gray);
                output.setRGB(x, y, g.getRGB());
            }
        }
        return output;
    }

    public static BufferedImage alterBrightness(BufferedImage inputImage, int percent) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(inputImage.getRGB(x, y));
                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();
                r = clamp(r + (percent * r / 100));
                g = clamp(g + (percent * g / 100));
                b = clamp(b + (percent * b / 100));
                output.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return output;
    }

    public static BufferedImage invertRGB(BufferedImage inputImage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(inputImage.getRGB(x, y));
                int r = 255 - c.getRed();
                int g = 255 - c.getGreen();
                int b = 255 - c.getBlue();
                output.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return output;
    }

    public static BufferedImage rotate90(BufferedImage inputImage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        BufferedImage rotated = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = inputImage.getRGB(x, y);
                rotated.setRGB(height - 1 - y, x, rgb);
            }
        }
        return rotated;
    }

    public static BufferedImage rotate180(BufferedImage inputImage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        BufferedImage rotated = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = inputImage.getRGB(x, y);
                rotated.setRGB(width - 1 - x, height - 1 - y, rgb);
            }
        }
        return rotated;
    }

    public static BufferedImage mirrorRight(BufferedImage inputImage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        BufferedImage mirrored = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = inputImage.getRGB(width - 1 - x, y);
                mirrored.setRGB(x, y, rgb);
            }
        }
        return mirrored;
    }

    public static BufferedImage mirrorBottom(BufferedImage inputImage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        BufferedImage mirrored = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = inputImage.getRGB(x, height - 1 - y);
                mirrored.setRGB(x, y, rgb);
            }
        }
        return mirrored;
    }

    private static int clamp(int v) {
        if (v < 0) return 0;
        if (v > 255) return 255;
        return v;
    }
}
