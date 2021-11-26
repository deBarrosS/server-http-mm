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
 *
 * WebServer is a simple web-server that handles HTTP GET, POST, PUT, HEAD and DELETE methods.
 * @author Matheus de Barros Silva, Matthieu Roux
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
            out.flush();
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
              response = service.handleDeleteFile(request);
              response.sendResponse(out);

            }
          }
          case PUT -> {

            if ("/".equals(request.params)) {
              // no file is provided, bad request
              response = HttpResponse.badRequestResponse();
            } else {
              response = service.handlePutFile(request);
              response.sendResponse(out);
            }
          }
          case HEAD -> {
            handleHead(out, request.params);
            out.flush();
          }
          default -> {
            // Bad request
            response = HttpResponse.badRequestResponse();
            response.sendResponse(out);
          }
        }

        // After writing on the OutputStream on the required handler, we flush the OutputStream
        System.out.println();
        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  /**
   * Handles the GET request
   *
   * @param out BufferedOutputStream attached to the connected socket
   * @param filename String showing the name of the researched file
   * @return status code of the http request
   */
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

  /**
   *  Fetches the file located in the authorized file directory
   *
   * @param filename String showing the name of the researched file
   * @param out BufferedOutputStream attached to the connected socket
   * @return File searched file
   */
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

  /**
   *  Simplifies and unifies the resource identifying.
   * @param filename String identifying the researched resource
   * @return String
   */
  private String treatFilename(String filename){
    String treated = filename.substring(1);
    if (treated.endsWith("/")) {
      treated += INDEX;
    } else if (!treated.contains(".")) treated += "/" + INDEX;
    return treated;
  }

  /**
   *  Handles HEAD request
   * @param out BufferedOutputStream attached to the connected socket
   * @param filename String showing the name of the researched file
   * @return int status code
   */
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

  /**
   *  Returns the equivalent Headers taking into account the paramenters
   * @param status statusCode and statusText
   * @param extension extension of the corresponding file
   * @param fileLength length of the corresponding file
   * @return Headers
   */
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
