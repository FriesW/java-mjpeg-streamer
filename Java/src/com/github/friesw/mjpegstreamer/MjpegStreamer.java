package com.github.friesw.mjpegstreamer;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class MjpegStreamer
{

   public static void main(String[] args) throws IOException
   {
      HttpServer server = HttpServer.create(new InetSocketAddress(80),0);
      server.createContext("/", new rootHandler());
      server.createContext("/stream", new streamHandler());
      server.setExecutor(null);
      server.start();
   }

   static class rootHandler implements HttpHandler
   {

      @Override
      public void handle(HttpExchange t) throws IOException
      {
         byte[] response = "<!DOCTYPE html><html><body><img src=\"./stream\"></body></html>".getBytes();
         t.sendResponseHeaders(200, response.length);
         OutputStream os = t.getResponseBody();
         os.write(response);
         os.close();
      }
      
   }
   
   static class streamHandler implements HttpHandler
   {

      @Override
      public void handle(HttpExchange t) throws IOException
      {
         Headers h = t.getResponseHeaders();
         h.set("Cache-Control", "no-cache");
         h.set("Content-Type", "multipart/x-mixed-replace;boundary=--boundary");
         t.sendResponseHeaders(200, 0);
         OutputStream os = t.getResponseBody();
         
         String head = "\r\n\r\n--boundary\r\nContent-Type: image/jpeg\r\nContent-Length: ";
         ImageSource is = new Source();
         while(true)
         {
            System.out.print("Outputting...");
            byte[] img = saveImageAsJPEG(is.getImage(), 80);
            os.write((head + img.length + "\r\n\r\n").getBytes());
            os.write(img);
            System.out.println("Done");
            try
            {
               Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
         //os.close();
      }
      
   }
   
   public static byte[] saveImageAsJPEG(BufferedImage image,
         int qualityPercent) throws IOException
   {
       if ((qualityPercent < 0) || (qualityPercent > 100)) {
         throw new IllegalArgumentException("Quality out of bounds!");
       }
       float quality = qualityPercent / 100f;
       ImageWriter writer = null;
       Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
       if (iter.hasNext()) {
         writer = (ImageWriter) iter.next();
       }
       ByteArrayOutputStream stream = new ByteArrayOutputStream();
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
}
