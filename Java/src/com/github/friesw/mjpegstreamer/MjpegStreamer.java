package com.github.friesw.mjpegstreamer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class MjpegStreamer
{

   public static void main(String[] args) throws IOException
   {
      HttpServer server = HttpServer.create(new InetSocketAddress(80),0);
      server.createContext("/", new rootHandler());
      server.setExecutor(null);
      server.start();
   }

   static class rootHandler implements HttpHandler
   {

      @Override
      public void handle(HttpExchange t) throws IOException
      {
         // TODO Auto-generated method stub
         byte[] response = "Test response".getBytes();
         t.sendResponseHeaders(200, response.length);
         OutputStream os = t.getResponseBody();
         os.write(response);
         os.close();
      }
      
   }
   
}
