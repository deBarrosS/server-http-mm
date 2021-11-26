///A Simple Web Server (WebServer.java)

package http.server;

import http.server.requests.HttpRequest;
import http.server.response.HttpResponse;
import http.server.response.HttpStatusCode;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

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
  /**Path to resource directory*/
  protected static final String FILES_DIRECTORY = "pages";
  /**Path to Error HTML page*/
  protected static final String FILE_NOT_FOUND = "pages/404.html";
  /**Index page of a view inside the server. Constant to be appended to a path*/
  protected static final String INDEX = "index.html";

  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;
    Service service = new Service();

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
    while(true) {
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        System.out.println("Connection, sending data.");

        BufferedInputStream in = new BufferedInputStream( remote.getInputStream());
        BufferedOutputStream out = new BufferedOutputStream(remote.getOutputStream());

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.

        HttpRequest request = HttpRequest.readHttpRequest(in);
        HttpResponse response;
        String url = null;
        String str = ".";
      int status = 0;
        switch (request.method) {
          case GET -> {
            // Handle get
            System.out.println("Parameters:" + request.params);
            status = handleGet(out, request.params);
            System.out.println("Status : " + status);
          }
          case POST -> {
            response = service.handleAddTodoItem(request.body);
            response.sendResponse(out);
          }
          case DELETE -> {
            // TODO add file upload support
            // Handle delete
            if ("/".equals(request.params)) {
              // no file is provided, bad request
              response = HttpResponse.badRequestResponse();
            } else {
              response = new HttpResponse(HttpStatusCode.OK, service.handleDeleteFile(request.params));
              response.sendResponse(out);

            }
          }
          case PUT -> {
            // TODO add file upload support
            if ("/".equals(request.params)) {
              // no file is provided, bad request
              response = HttpResponse.badRequestResponse();
            } else {
              response = new HttpResponse(HttpStatusCode.CREATED, service.handlePutFile(request.params));
              response.sendResponse(out);
            }
          }
          case HEAD -> {
            // TODO needs to be implemented
            response = HttpResponse.badRequestResponse();// handleHead(out, request.params);
            response.sendResponse(out);
          }
          default -> {
            // Bad request
            response = HttpResponse.badRequestResponse();
            response.sendResponse(out);
          }
        }

        // After writing on the OutputStream on the required handler, we flush the OutputStream
        out.flush();
        System.out.println();
        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }


  public int handleGet(BufferedOutputStream out, String filename){
    filename = treatFilename(filename);

    String extension = filename.substring(filename.lastIndexOf('.')).toLowerCase(Locale.ROOT);
    String status = "200 OK";
    boolean accessPermited = filename.startsWith(FILES_DIRECTORY);
    System.out.println("accessPermited " + accessPermited);
    // Header still to receive status, length of the body and other headers
    try{
      File file = fetchFile(filename, out);

      // Read the file as binary allows same manipulation for every type of file
      // At this point all the headers have been sent
      // Send the body of the response; The bodies here treated are "Single-resource bodies"
      BufferedInputStream fileStream = new BufferedInputStream(new FileInputStream(file));
      byte[] fileBuffer = new byte[256];
      int contentLength;
      while((contentLength = fileStream.read(fileBuffer)) != -1) {
        out.write(fileBuffer, 0,contentLength );
      }

      fileStream.close();

    }catch(Exception e){
      System.err.println("Error in Handle Get " + e);
    }

    return Integer.parseInt(status.split(" ")[0]);
  }

  private File fetchFile(String filename, BufferedOutputStream out){
    String extension = filename.substring(filename.lastIndexOf('.')).toLowerCase(Locale.ROOT);
    String status = "200 OK";
    String header = "";

    File file = new File(filename);
    try{
      if (file.exists() && file.isFile()) {
        header = responseHeader(status, extension, file.length());

      } else {
        file = new File(FILE_NOT_FOUND);
        status = "404 Not Found";
        header = responseHeader(status, "html", file.length());
      }
      out.write(header.getBytes(StandardCharsets.UTF_8));
    }catch(Exception e){
      System.err.println("Error in Fetch File " + e);
    }

    return file;
  }

  private String treatFilename(String filename){
    String treated = filename.substring(1);
    if (treated.endsWith("/")) {
      treated += INDEX;
    } else if (!treated.contains(".")) treated += "/" + INDEX;
    return treated;
  }

  public int handleHead(BufferedOutputStream out, String filename) {
    filename = treatFilename(filename);

    boolean accessPermited = filename.startsWith(FILES_DIRECTORY);
    if(!accessPermited){
      return 403;
    }
    System.out.println("accessPermited " + accessPermited);
    // Header still to receive status, length of the body and other headers
    try{
      // The returned file is never used is not used
      File file = fetchFile(filename, out);
      if(file.exists() && file.isFile()) return 200;
    }catch(Exception e){
      System.err.println("Error in handleHead " + e);
    }

    return 404;
  }

  public String responseHeader(String status, String extension, long fileLength){
    String rn = "\r\n";
    StringBuilder headerBuilder = new StringBuilder();
    headerBuilder.append("HTTP/1.1 ");
    headerBuilder.append(status);
    headerBuilder.append(rn);

    String contentType = "text/html";
    switch(extension){
      case (".js")->{
        contentType = "text/script";
      }
      case (".png"), (".ico") ->{
        contentType = "image/png";
      }
      case (".jpeg") ->{
        contentType = "image/jpg";
      }
      case (".css") ->{
        contentType = "text/css";
      }
      default -> {
        System.out.println("default case");
      }
    }
    headerBuilder.append("Content-Type:");
    headerBuilder.append(contentType);
    headerBuilder.append(rn);

    headerBuilder.append("Content-Length:");
    headerBuilder.append(fileLength);
    headerBuilder.append(rn);

    headerBuilder.append("Server: Bot");
    headerBuilder.append(rn);
    headerBuilder.append(rn);

    return headerBuilder.toString();

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
