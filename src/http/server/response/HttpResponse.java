package http.server.response;

import http.server.Service;

import java.io.BufferedOutputStream;
import java.io.PrintWriter;

/**
 * HttpResponse is used to create and send Http responses easily.
 */
public class HttpResponse {
    public HttpStatusCode statusCode;
    public String body;
    public String contentType;

    public HttpResponse(HttpStatusCode statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
        this.contentType = "text/html";
    }

    /**
     * sendHeader will send the header of the response on the PrintWriter out
      * but not flush it.
      * @param out the PrintWriter used to send the body
     */
    public void sendHeader(PrintWriter out){
        // Send the headers
        out.println("HTTP/1.0 " + this.statusCode);
        out.println("Content-Type: " + contentType);
        out.println("Server: Bot");
        // this blank line signals the end of the headers
        out.println("");
    }

    /**
     * sendBody will send the body of the response on the PrintWriter out
     * but not flush it.
     * @param out the PrintWriter used to send the body
     */
    public void sendBody(PrintWriter out){
        // Send the HTML page or JSON File
        out.println(this.body);
    }

    /**
     * sendResponse sends the response through the PrintWriter
     * Please be awater that it will flush the PrintWriter
     * @param out he PrintWriter used to send the response
     */
    public void sendResponse(PrintWriter out){
        // TODO Buffered output support
        sendHeader(out);
        if (body != null) {
            sendBody(out);
        }
        out.flush();
    }

    public static HttpResponse badRequestResponse(){
        return new HttpResponse(HttpStatusCode.BAD_REQUEST, Service.getHTMLFile("400.html"));
    }

    public static HttpResponse internalServerErrorResponse(){
        return new HttpResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, Service.getHTMLFile("500.html"));
    }
}
