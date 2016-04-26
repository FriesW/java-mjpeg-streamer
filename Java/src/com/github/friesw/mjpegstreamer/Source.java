package com.github.friesw.mjpegstreamer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

public class Source implements JpegImageSource
{
   
   private static final int DEFAULT_HEIGHT = 250;
   private static final int DEFAULT_WIDTH = 400;
   
   private static final float SATURATION = 0.5f;
   private static final float LIGHTNESS = 0.25f;
   private static final float HUE_DIVIDER = 10000f;
   
   private int width;
   private int height;
   private long start;

   public Source()
   {
      this(DEFAULT_WIDTH, DEFAULT_HEIGHT); 
   }
   
   public Source(int width, int height)
   {
      this.width = width;
      this.height = height;
      start = System.currentTimeMillis();
   }
   
   @Override
   public byte[] getImage()
   {
      BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      long t = System.currentTimeMillis() - start;
      
      //BG color
      Graphics g = img.getGraphics();
      g.setColor(Color.getHSBColor(
    		  (t % HUE_DIVIDER) / HUE_DIVIDER,
    		  SATURATION,
    		  LIGHTNESS));
      g.fillRect(0, 0, width, height);
      
      //Time
      g.setColor(Color.WHITE);
      g.drawString("Running for " + t / 1000 + " seconds.", 10, 15);
      g.dispose();
      
      return toJpeg(img, 80);
   }
   
   private static byte[] toJpeg(BufferedImage image, int qualityPercent)
   {
       if ((qualityPercent < 0) || (qualityPercent > 100)) {
         throw new IllegalArgumentException("Quality out of bounds!");
       }
       float quality = qualityPercent / 100f;
       ImageWriter writer = null;
       Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpg");
       if (iter.hasNext())
         writer = (ImageWriter) iter.next();
       ByteArrayOutputStream stream = new ByteArrayOutputStream();
       try
       {
          ImageOutputStream ios = ImageIO.createImageOutputStream(stream);
          writer.setOutput(ios);
          ImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());
          iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
          iwparam.setCompressionQuality(quality);
          writer.write(null, new IIOImage(image, null, null), iwparam);
          ios.flush();
          writer.dispose();
          ios.close();
          byte[] out = stream.toByteArray(); 
          stream.close();
          return out;
       }
       catch (IOException e)
       {
          e.printStackTrace();
          return null;
       }
     }

}
