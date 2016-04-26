package com.github.friesw.mjpegstreamer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class MjpegStreamer
{

   private static JpegImageSource imageSource;
   
   public static void main(String[] args) throws IOException
   {
      imageSource = new Source();
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
      private static final String NL = "\r\n";
      private static final String BOUNDARY = "--boundary";
      private static final String HEAD = NL + NL + BOUNDARY + NL +
            "Content-Type: image/jpeg" + NL +
            "Content-Length: ";
      
      @Override
      public void handle(HttpExchange t) throws IOException
      {
         Headers h = t.getResponseHeaders();
         h.set("Cache-Control", "no-cache, private");
         h.set("Content-Type", "multipart/x-mixed-replace;boundary=" + BOUNDARY);
         t.sendResponseHeaders(200, 0);
         OutputStream os = t.getResponseBody();
         
         while(true)
         {
            //System.out.print("Outputting...");
            byte[] img = imageSource.getImage();
            os.write((HEAD + img.length + NL + NL).getBytes());
            os.write(img);
            //System.out.println("Done");
            try
            {
               Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
               e.printStackTrace();
            }
         }
         //os.close();
      }
      
   }
   
}
