package com.github.friesw.mjpegstreamer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Source implements ImageSource
{
   
   private static final int DEFAULT_HEIGHT = 250;
   private static final int DEFAULT_WIDTH = 400;
   
   private static final float SATURATION = 0.50f;
   private static final float LIGHTNESS = 0.25f;
   private static final float HUE_DIVIDER = 1000f;
   
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
   public BufferedImage getImage()
   {
      BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      long t = System.currentTimeMillis() - start;
      
      //BG color
      Graphics g = img.getGraphics();
      g.setColor(Color.getHSBColor(t / HUE_DIVIDER % 1, SATURATION, LIGHTNESS));
      g.drawRect(0, 0, width, height);
      
      //Time
      g.setColor(Color.WHITE);
      g.drawString("" + t, 10, 10);
      
      g.dispose();
      
      return img;
   }

}