package com.example.imageeditor;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * Simple CLI wrapper to demonstrate image processing functions.
 */
public class ImageEditorApp {

    private static void printMenu() {
        System.out.println("Menu:");
        System.out.println("1 - Print pixel values");
        System.out.println("2 - Convert to grayscale (save as grayscaleimage.png)");
        System.out.println("3 - Alter brightness (enter percentage, negative to darken)");
        System.out.println("4 - Invert RGB (save as dark_mode.png)");
        System.out.println("5 - Rotate 90 degrees (save as rotated_image_90.png)");
        System.out.println("6 - Rotate 180 degrees (save as rotated_image_180.png)");
        System.out.println("7 - Mirror right (save as mirror_image_right.png)");
        System.out.println("8 - Mirror bottom (save as mirror_image_bottom.png)");
        System.out.println("0 - Exit");
    }

    private static void saveImage(BufferedImage image, String filename) throws IOException {
        File out = new File(filename);
        ImageIO.write(image, "png", out);
        System.out.println("File successfully saved as '" + filename + "'");
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Please enter the Filepath: ");
            String path = sc.nextLine().trim();
            File inputFile = new File(path);
            if (!inputFile.exists()) {
                System.err.println("File not found: " + path);
                return;
            }
            BufferedImage inputImage;
            try {
                inputImage = ImageIO.read(inputFile);
                if (inputImage == null) {
                    System.err.println("Could not read image from: " + path);
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            while (true) {
                printMenu();
                System.out.print("Enter choice: ");
                int choice = -1;
                if (sc.hasNextInt()) {
                    choice = sc.nextInt();
                    sc.nextLine(); // consume newline
                } else {
                    String s = sc.nextLine();
                    System.out.println("Invalid input: " + s);
                    continue;
                }

                try {
                    switch (choice) {
                        case 1:
                            ImageProcessor.printPixelValues(inputImage);
                            break;
                        case 2:
                            saveImage(ImageProcessor.toGrayscale(inputImage), "grayscaleimage.png");
                            break;
                        case 3:
                            System.out.print("Enter percentage (negative to decrease): ");
                            int percent = sc.nextInt();
                            sc.nextLine();
                            saveImage(ImageProcessor.alterBrightness(inputImage, percent), "Altered_Brightness.png");
                            break;
                        case 4:
                            saveImage(ImageProcessor.invertRGB(inputImage), "dark_mode.png");
                            break;
                        case 5:
                            saveImage(ImageProcessor.rotate90(inputImage), "rotated_image_90.png");
                            break;
                        case 6:
                            saveImage(ImageProcessor.rotate180(inputImage), "rotated_image_180.png");
                            break;
                        case 7:
                            saveImage(ImageProcessor.mirrorRight(inputImage), "mirror_image_right.png");
                            break;
                        case 8:
                            saveImage(ImageProcessor.mirrorBottom(inputImage), "mirror_image_bottom.png");
                            break;
                        case 0:
                            System.out.println("Exiting.");
                            return;
                        default:
                            System.out.println("Unknown choice: " + choice);
                    }
                } catch (IOException e) {
                    System.err.println("Failed to write output file: " + e.getMessage());
                }
            }
        }
    }
}
