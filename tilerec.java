// display a text file
//

import java.io.*;
import java.util.Arrays;
import java.security.*;
import java.awt.image.*;
import javax.imageio.*;
import java.math.BigInteger;


class TileRec {
  public static void main(String args[]) {
    BufferedImage img = null;

    try {
      img = ImageIO.read(new File("smb3map.png"));
    } catch (IOException e) {
    }

    System.out.println("\nHeight: " + img.getHeight() +
                        "\nWidth: " + img.getWidth() +
                        "\nColorType: " + img.getColorModel() +
                        "\nColorSpace: " + img.getColorModel().getColorSpace());
    //IndexColorModel pal = ;

    System.out.println(img.toString());

    int source_image_bpp = 1; // bytes per pixel
    int tile_h, tile_w;
    tile_h = tile_w = 8; 
    int tile_len = tile_w * source_image_bpp; // how many pixels to scan on each line
    int p; // the pixel we are copying

    for (int tile_x = 0; tile_x < 16; tile_x++) {
      for (int tile_y = 0; tile_y < 14; tile_y++) {
        BufferedImage tile = new BufferedImage(tile_w, tile_h, BufferedImage.TYPE_BYTE_INDEXED);
        byte[] clipped_image_data = new byte[tile_h * tile_w];

        for (int v = 0; v < tile_w; v++) {
          for (int h = 0; h < tile_h; h++) {
            p = img.getRGB((tile_x * tile_w) + h, (tile_y * tile_h) + v);
            tile.setRGB(h, v, p);
            clipped_image_data[(v * tile_w) + h] = (byte)p;
            //System.out.print(((v * tile_w) + h) + "," + (byte)p + ",");
            // add to pixel 'string'
          }
        }

        //System.out.println("");

        try {
          MessageDigest m = MessageDigest.getInstance("MD5");
        
          m.reset();
          m.update(clipped_image_data);

          byte[] digest = m.digest();
          BigInteger bigInt = new BigInteger(1, digest);
          String hash = bigInt.toString(16);
          while(hash.length() < 32 ){
            hash = "0" + hash;
          }
          //System.out.println(hash);
        }
        catch (NoSuchAlgorithmException e) {
          System.err.println("I'm sorry, but MD5 is not a valid message digest algorithm");
        }

        // hash pixel 'string'
        // does tile exist already? if not, add it and write to file
        try {
          File outputfile = new File("output-java/" + tile_x + "-" + tile_y + ".png");
          ImageIO.write(tile, "png", outputfile);
        } catch (IOException e) {
          System.out.println("Something went wrong:" + e);
          // tile_dict = {} # store our hashes of tiles
        }
      }
    }

  }
}

/*
  import java.security.*;

  ..

  byte[] bytesOfMessage = yourString.getBytes("UTF-8");

  MessageDigest md = MessageDigest.getInstance("MD5");
  byte[] thedigest = md.digest(bytesOfMessage);
*/

