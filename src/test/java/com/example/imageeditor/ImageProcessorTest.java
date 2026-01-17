package com.example.imageeditor;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class ImageProcessorTest {

    @Test
    void invertShouldInvertColors() {
        BufferedImage img = new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, new Color(10, 20, 30).getRGB());
        img.setRGB(1, 0, new Color(200, 150, 100).getRGB());

        BufferedImage out = ImageProcessor.invertRGB(img);

        Color c0 = new Color(out.getRGB(0, 0));
        assertEquals(245, c0.getRed());
        assertEquals(235, c0.getGreen());
        assertEquals(225, c0.getBlue());

        Color c1 = new Color(out.getRGB(1, 0));
        assertEquals(55, c1.getRed());
        assertEquals(105, c1.getGreen());
        assertEquals(155, c1.getBlue());
    }

    @Test
    void rotate90ShouldSwapDimensions() {
        BufferedImage img = new BufferedImage(3, 2, BufferedImage.TYPE_INT_RGB);
        BufferedImage rotated = ImageProcessor.rotate90(img);
        assertEquals(2, rotated.getWidth());
        assertEquals(3, rotated.getHeight());
    }

    @Test
    void invertDoubleInvertRestoresImage() {
        BufferedImage img = new BufferedImage(4, 2, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                img.setRGB(x, y, new Color(x * 20, y * 30, x + y).getRGB());
            }
        }

        BufferedImage inverted = ImageProcessor.invertRGB(img);
        BufferedImage doubleInverted = ImageProcessor.invertRGB(inverted);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                assertEquals(img.getRGB(x, y), doubleInverted.getRGB(x, y));
            }
        }
    }

    @Test
    void toGrayscaleShouldConvertToCorrectGrayValues() {
        BufferedImage img = new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, new Color(255, 0, 0).getRGB()); // expected gray ~76
        img.setRGB(1, 0, new Color(0, 255, 0).getRGB()); // expected gray ~149

        BufferedImage gray = ImageProcessor.toGrayscale(img);

        Color c0 = new Color(gray.getRGB(0, 0));
        assertEquals(76, c0.getRed());
        assertEquals(76, c0.getGreen());
        assertEquals(76, c0.getBlue());

        Color c1 = new Color(gray.getRGB(1, 0));
        assertEquals(149, c1.getRed());
        assertEquals(149, c1.getGreen());
        assertEquals(149, c1.getBlue());
    }

    @Test
    void toGrayscalePreservesDimensionsAndChannelsEqual() {
        BufferedImage img = new BufferedImage(3, 2, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, new Color(10, 20, 30).getRGB());
        img.setRGB(1, 0, new Color(100, 150, 200).getRGB());
        img.setRGB(2, 0, new Color(255, 255, 255).getRGB());

        BufferedImage gray = ImageProcessor.toGrayscale(img);
        assertEquals(img.getWidth(), gray.getWidth());
        assertEquals(img.getHeight(), gray.getHeight());

        for (int y = 0; y < gray.getHeight(); y++) {
            for (int x = 0; x < gray.getWidth(); x++) {
                Color c = new Color(gray.getRGB(x, y));
                assertEquals(c.getRed(), c.getGreen());
                assertEquals(c.getGreen(), c.getBlue());
            }
        }
    }

    @Test
    void alterBrightnessIncreaseAndClamp() {
        BufferedImage img = new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, new Color(100, 100, 100).getRGB());
        img.setRGB(1, 0, new Color(200, 200, 200).getRGB());

        BufferedImage brighter = ImageProcessor.alterBrightness(img, 50); // +50%

        Color c0 = new Color(brighter.getRGB(0, 0));
        assertEquals(150, c0.getRed());
        assertEquals(150, c0.getGreen());
        assertEquals(150, c0.getBlue());

        Color c1 = new Color(brighter.getRGB(1, 0));
        assertEquals(255, c1.getRed()); // clamped from 300
        assertEquals(255, c1.getGreen());
        assertEquals(255, c1.getBlue());
    }

    @Test
    void alterBrightnessDecreaseAndClampToZero() {
        BufferedImage img = new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, new Color(100, 50, 0).getRGB());
        img.setRGB(1, 0, new Color(10, 20, 30).getRGB());

        BufferedImage darker = ImageProcessor.alterBrightness(img, -50); // -50%

        Color c0 = new Color(darker.getRGB(0, 0));
        assertEquals(50, c0.getRed());
        assertEquals(25, c0.getGreen());
        assertEquals(0, c0.getBlue());

        BufferedImage veryDark = ImageProcessor.alterBrightness(img, -100); // -100% -> zero
        Color c1 = new Color(veryDark.getRGB(1, 0));
        assertEquals(0, c1.getRed());
        assertEquals(0, c1.getGreen());
        assertEquals(0, c1.getBlue());
    }

    @Test
    void rotate90ShouldMovePixelsCorrectly() {
        int w = 2, h = 3;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                img.setRGB(x, y, new Color(x * 10 + y, x * 10 + y + 1, x * 10 + y + 2).getRGB());
            }
        }

        BufferedImage rotated = ImageProcessor.rotate90(img);
        assertEquals(h, rotated.getWidth());
        assertEquals(w, rotated.getHeight());

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int expected = img.getRGB(x, y);
                int actual = rotated.getRGB(h - 1 - y, x);
                assertEquals(expected, actual);
            }
        }
    }

    @Test
    void rotate180ShouldRotatePixelsCorrectly() {
        BufferedImage img = new BufferedImage(3, 2, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                img.setRGB(x, y, new Color(x + 1, y + 2, x + y + 3).getRGB());
            }
        }

        BufferedImage rotated = ImageProcessor.rotate180(img);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int expected = img.getRGB(x, y);
                int actual = rotated.getRGB(img.getWidth() - 1 - x, img.getHeight() - 1 - y);
                assertEquals(expected, actual);
            }
        }
    }

    @Test
    void rotate180DoubleEqualsOriginal() {
        BufferedImage img = new BufferedImage(4, 3, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                img.setRGB(x, y, (x * 31 + y * 17) & 0xFFFFFF);
            }
        }

        BufferedImage r1 = ImageProcessor.rotate180(img);
        BufferedImage r2 = ImageProcessor.rotate180(r1);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                assertEquals(img.getRGB(x, y), r2.getRGB(x, y));
            }
        }
    }

    @Test
    void mirrorRightShouldMirrorPixelsCorrectly() {
        BufferedImage img = new BufferedImage(3, 2, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                img.setRGB(x, y, new Color(x * 10, y * 20, x + y).getRGB());
            }
        }

        BufferedImage mirrored = ImageProcessor.mirrorRight(img);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int expected = img.getRGB(img.getWidth() - 1 - x, y);
                int actual = mirrored.getRGB(x, y);
                assertEquals(expected, actual);
            }
        }
    }

    @Test
    void mirrorRightDoubleEqualsOriginal() {
        BufferedImage img = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                img.setRGB(x, y, (x * 13 + y * 7) & 0xFFFFFF);
            }
        }

        BufferedImage m1 = ImageProcessor.mirrorRight(img);
        BufferedImage m2 = ImageProcessor.mirrorRight(m1);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                assertEquals(img.getRGB(x, y), m2.getRGB(x, y));
            }
        }
    }

    @Test
    void mirrorBottomShouldMirrorPixelsCorrectly() {
        BufferedImage img = new BufferedImage(2, 3, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                img.setRGB(x, y, new Color(x * 5, y * 7, x + y + 10).getRGB());
            }
        }

        BufferedImage mirrored = ImageProcessor.mirrorBottom(img);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int expected = img.getRGB(x, img.getHeight() - 1 - y);
                int actual = mirrored.getRGB(x, y);
                assertEquals(expected, actual);
            }
        }
    }

    @Test
    void mirrorBottomDoubleEqualsOriginal() {
        BufferedImage img = new BufferedImage(2, 4, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                img.setRGB(x, y, (x * 19 + y * 11) & 0xFFFFFF);
            }
        }

        BufferedImage m1 = ImageProcessor.mirrorBottom(img);
        BufferedImage m2 = ImageProcessor.mirrorBottom(m1);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                assertEquals(img.getRGB(x, y), m2.getRGB(x, y));
            }
        }
    }

    @Test
    void printPixelValuesSinglePixel() {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, new Color(1, 2, 3).getRGB());

        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos)) {
            System.setOut(ps);
            ImageProcessor.printPixelValues(img);
        } finally {
            System.setOut(originalOut);
        }

        String out = baos.toString();
        assertTrue(out.contains("(1,2,3)"));
    }

    @Test
    void printPixelValuesMultipleLines() {
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, new Color(1, 2, 3).getRGB());
        img.setRGB(1, 0, new Color(4, 5, 6).getRGB());
        img.setRGB(0, 1, new Color(7, 8, 9).getRGB());
        img.setRGB(1, 1, new Color(10, 11, 12).getRGB());

        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos)) {
            System.setOut(ps);
            ImageProcessor.printPixelValues(img);
        } finally {
            System.setOut(originalOut);
        }

        String out = baos.toString();
        String[] lines = out.split("\\r?\\n");
        assertEquals(2, lines.length);
        assertTrue(lines[0].contains("(1,2,3)") && lines[0].contains("(4,5,6)"));
        assertTrue(lines[1].contains("(7,8,9)") && lines[1].contains("(10,11,12)"));
    }
}
