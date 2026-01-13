import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
public class image_editor {
    public static void print_pixel_values(BufferedImage input_image){
        int height = input_image.getHeight();
        int width = input_image.getWidth();
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                // System.out.print(input_image.getRGB(j,i)+" ");
                Color pixel = new Color(input_image.getRGB(j,i));
                System.out.print("("+pixel.getRed() + "," + pixel.getGreen() + "," + pixel.getBlue()+")" + " ");
            }
            System.out.println();
        }
    }
    public static BufferedImage convert_to_gray_scale(BufferedImage input_image){
        int height = input_image.getHeight();
        int width = input_image.getWidth(); 
        BufferedImage output_image = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                output_image.setRGB(j,i,input_image.getRGB(j, i));
            }
        }
        return output_image;
    }
    public static BufferedImage alter_brightness(BufferedImage input_image,int increase){
        int height = input_image.getHeight();
        int width = input_image.getWidth();
        BufferedImage output_image = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                Color pixel = new Color(input_image.getRGB(j,i));
                int R = pixel.getRed();
                int G = pixel.getGreen();
                int B = pixel.getBlue();
                R = R + (increase*R/100);
                G = G + (increase*G/100);
                B = B + (increase*B/100);
                if(R>255) R = 255;
                if(G>255) G = 255;
                if(B>255) B = 255;
                if(R<0) R = 0;
                if(G<0) G = 0;
                if(B<0) B = 0;
                Color new_pixel = new Color(R, G, B);
                output_image.setRGB(j, i, new_pixel.getRGB());
                
            }
        }
        return output_image;
    }
    public static BufferedImage invert_RGB(BufferedImage input_image){
        int height = input_image.getHeight();
        int width = input_image.getWidth();
        BufferedImage output_image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                Color pixel = new Color(input_image.getRGB(j, i));
                int R = pixel.getRed();
                int G = pixel.getGreen();
                int B = pixel.getBlue();
                R = 255 - R;
                G = 255 - G;
                B = 255 - B;
                Color new_pixel = new Color(R,G,B);
                output_image.setRGB(j, i , new_pixel.getRGB());
            }
        }
        return output_image;
    }
    public static BufferedImage rotate90(BufferedImage input_image){
        int height = input_image.getHeight();
        int width = input_image.getWidth();
        BufferedImage rotated_image = new BufferedImage(height,width,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                int rgb = input_image.getRGB(j,i);
                rotated_image.setRGB(height-i-1,j,rgb);
            }
        }
        return rotated_image;
    }
    public static BufferedImage rotate180(BufferedImage input_image){
        int height = input_image.getHeight();
        int width = input_image.getWidth();
        BufferedImage rotated_image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                int rgb = input_image.getRGB(j,i);
                rotated_image.setRGB(width-j-1,height-i-1,rgb);
            }
        }
        return rotated_image;
    }
    public static BufferedImage mirror_image(BufferedImage input_image){
        int height = input_image.getHeight();
        int width = input_image.getWidth();
        BufferedImage inverted_image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                int inverted_rgb = input_image.getRGB(width-j-1,i);
                inverted_image.setRGB(j,i,inverted_rgb);
            }
        }
        return inverted_image;
    }
    public static BufferedImage mirror_image_bottom(BufferedImage input_image){
        int height = input_image.getHeight();
        int width = input_image.getWidth();
        BufferedImage mirror_image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                int inverted_rgb = input_image.getRGB(j,height-i-1);
                mirror_image.setRGB(j,i,inverted_rgb);
            }
        }
        return mirror_image;
    }
    /*public static BufferedImage apply_blur_filter(BufferedImage input_image,int blur_percent){
        int height = input_image.getHeight();
        int width = input_image.getWidth();
        BufferedImage blurred_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int blur_radius = (int)(Math.min(height,width)*blur_percent);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                int rgb = getAverageRGB
            }
        } 
    }*/
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.print("Please enter the Filepath:");
        String S = sc.nextLine();
        System.out.println("To get the pixel values,ENTER 1(ONE)");
        System.out.println("To get the grey scale image,ENTER 2(TWO)");
        System.out.println("To alter the brightness,ENTER 3(THREE) (increase/decrease)");
        System.out.println("To invert the RGB Values of the image,ENTER 4(FOUR)");
        System.out.println("To rotate the image right (90 degrees) ,ENTER 5(FIVE)");
        System.out.println("To rotate the image right (180 degrees) ,ENTER 6(SIX)");
        System.out.println("To invert the image (with a mirror on the RIGHT) ,ENTER 7(SEVEN)");
        System.out.println("To invert the image (with a mirror on the BOTTOM) ,ENTER 8(EIGHT)");
        System.out.print("enter EXIT to leave");
        int inp = sc.nextInt();
        File input_file = new File(S);
        BufferedImage input_image;
        try {
            input_image = ImageIO.read(input_file);
            if(inp == 1){
                print_pixel_values(input_image);
            }
            else if(inp == 2){
                try{
                    BufferedImage grayscale = convert_to_gray_scale(input_image);
                    System.out.println(input_image);
                    File Gray_image = new File("grayscaleimage.png");
                    ImageIO.write(grayscale,"png",Gray_image);
                    System.out.println("File successfully saved as 'grayscaleimage.png'");
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            else if(inp == 3){
                try{
                    System.out.print("Enter the percentage of brightness to be increased & enter negative value if you want the brightness to be decreased :");
                    int percent = sc.nextInt();
                    BufferedImage brightness_altered = alter_brightness(input_image, percent);
                    File brightness_changed = new File("Altered_Brightness.png");
                    ImageIO.write(brightness_altered, "png", brightness_changed);
                    System.out.println("File successfully saved as 'Altered_Brightness.png'");
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            else if(inp == 4){
                try{
                    BufferedImage inverted_RGB = invert_RGB(input_image);
                    File image_invert = new File("dark_mode.png");
                    ImageIO.write(inverted_RGB,"png",image_invert);
                    System.out.println("File successfully saved as 'darkmode.png'");
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            else if(inp == 5){
                try{
                    BufferedImage rotated_image = rotate90(input_image);
                    File image_rotate = new File("rotated_image_90.png");
                    ImageIO.write(rotated_image,"png",image_rotate);
                    System.out.println("File successfully saved as 'rotated_image_90.png'");
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            else if(inp == 6){
                try{
                    BufferedImage rotated_image = rotate180(input_image);
                    File image_rotate = new File("rotated_image_180.png");
                    ImageIO.write(rotated_image,"png",image_rotate);
                    System.out.println("File successfully saved as 'rotated_image_180.png'");
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            else if(inp == 7){
                try{
                    BufferedImage inverted_image = mirror_image(input_image);
                    File image_invert = new File("mirror_image_right.png");
                    ImageIO.write(inverted_image,"png",image_invert);
                    System.out.println("File successfully saved as 'mirror_image_right.png'");
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
            else if(inp == 8){
                try{
                    BufferedImage inverted_image = mirror_image_bottom(input_image);
                    File mirror_image = new File("mirror_image_bottom.png");
                    ImageIO.write(inverted_image,"png",mirror_image);
                    System.out.println("File successfully saved as 'mirror_image_bottom.png'");
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                
            }
            
        } 
        catch (IOException e){
            e.printStackTrace();
        }
        sc.close();
            
    }
}
