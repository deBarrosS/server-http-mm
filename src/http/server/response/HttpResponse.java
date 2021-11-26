package http.server.response;

import http.server.Service;

import java.io.*;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;

/**
 * HttpResponse is used to create and send Http responses easily.
 */
public class HttpResponse<T> {
    public HttpStatusCode statusCode;
    public String contentType;
    public T body;
    public long contentLength;


    public HttpResponse(HttpStatusCode statusCode,String contentType, T body) {
        this.statusCode = statusCode;
        this.body = body;
        this.contentType = contentType;
        if(body instanceof String){
            this.contentLength = (((String) body).getBytes()).length;
        }else if(body instanceof byte[]){
            this.contentLength = ((byte[]) body).length;
        }
    }

    // OLD
    public HttpResponse(HttpStatusCode statusCode, T body) {
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
     * writeHeader will write the header of the response on the BufferedOutputStream out
     * but not flush it.
     * @param out the BufferedOutputStream attached to the open Socket
     */
    public void writeHeaders(BufferedOutputStream out){
        // Write the headers
        try{
            String rn = "\r\n";
            System.out.println(("HTTP/1.1 " + this.statusCode));
            System.out.println(("Content-Length: " + contentLength));
            System.out.println(("Content-Type: " + contentType));
            System.out.println(("Server: Bot"));
            System.out.println((rn));
            // this blank line signals the end of the headers
            out.write(("").getBytes(StandardCharsets.UTF_8));
            out.write(("HTTP/1.1 " + this.statusCode).getBytes(StandardCharsets.UTF_8));
            out.write(("Content-Type:" + contentType).getBytes(StandardCharsets.UTF_8));
            out.write(("Server: Bot").getBytes(StandardCharsets.UTF_8));
            out.write((rn).getBytes(StandardCharsets.UTF_8));
            // this blank line signals the end of the headers
        } catch(IOException e){
            System.err.println(e);
        }
    }

    /**
     * writeBody will write the body of the response on the BufferedOutputStream out
     * but not flush it.
     * @param out the BufferedOutputStream attached to the open Socket
     */
    public void writeBody(BufferedOutputStream out){
        try{
            if (body instanceof String) {
                out.write(((String)body).getBytes(StandardCharsets.UTF_8));
                System.out.println((String)body);
            } else if (body instanceof byte[]) {
                out.write((byte[]) body);
                System.out.println((String)body);
            }
            out.write("\r\n".getBytes(StandardCharsets.UTF_8));
            System.out.println("\r\n");
        }catch(IOException e){
            System.err.println("Error in HttpResponse " + e);
        }
    }

    /**
     * sendResponse sends the response through the PrintWriter
     * Please be awater that it will flush the PrintWriter
     * @param out he PrintWriter used to send the response
     */
    public void sendResponse(BufferedOutputStream out){
        // TODO Buffered output support
        try{
            writeHeaders(out);
            if (body != null) {
                writeBody(out);
            }
            out.flush();
        }catch(Exception e) {
            System.err.println("Error in HttpResponse " + e);
        }

    }

    public static HttpResponse badRequestResponse(){
        return new HttpResponse(HttpStatusCode.BAD_REQUEST, Service.getHTMLFile("400.html"));
    }

    public static HttpResponse internalServerErrorResponse(){
        return new HttpResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, Service.getHTMLFile("500.html"));
    }
}
