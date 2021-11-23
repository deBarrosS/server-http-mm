///A Simple Web Server (WebServer.java)

package http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 3000");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(3000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(new InputStreamReader(
            remote.getInputStream()));
        PrintWriter out = new PrintWriter(remote.getOutputStream());

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String url = null;
        String str = ".";
        while (str != null && !str.equals("")){
          str = in.readLine();

          // ON s'en bat les couilles des headers pour le moment, la seule chose qui importe est le GET
          if(str !=  null){
            String[] strSplit = str.split(" ");
            if(strSplit[0].equals("GET")) url = strSplit[1];
          }
        }
        String bodyResponse = "";
        if(url!=null){
          System.out.println("URL:" + url);
          bodyResponse = getHTMLFile(url);
        }

        // Send the response
        // Send the headers
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        // this blank line signals the end of the headers
        out.println("");

        // Send the HTML page
        out.println(bodyResponse);

        out.flush();
        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  private String getHTMLFile(String url){
    StringBuilder stringBuilder = new StringBuilder();
    String html = "";
    try{
      FileReader fileReader = new FileReader(url);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String line = bufferedReader.readLine() ;

      while(line !=null){
        stringBuilder.append(line);
        line = bufferedReader.readLine();
      }

      bufferedReader.close();
    }catch(FileNotFoundException e){
      System.out.println("File Not Found, POST a 404");
      html = "";
    } catch (IOException e) {
      e.printStackTrace();
      html = "";
    }
    html = stringBuilder.toString();
    System.out.println("> html");;
    System.out.println(html);
    return html;
  }

  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }
}