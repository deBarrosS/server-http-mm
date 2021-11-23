package http.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.InetAddress;
import java.net.Socket;


public class WebClient {

    public String requestStartLine(String method, String target, String httpVersion){
        /*
        method :
            GET
                The GET method requests a representation of the specified resource. Requests using GET should only retrieve data.
            HEAD
                The HEAD method asks for a response identical to a GET request, but without the response body.
            POST
                The POST method submits an entity to the specified resource, often causing a change in state or side effects on the server.
            PUT
                The PUT method replaces all current representations of the target resource with the request payload.
            DELETE
                The DELETE method deletes the specified resource.
            CONNECT
                The CONNECT method establishes a tunnel to the server identified by the target resource.
            OPTIONS
                The OPTIONS method describes the communication options for the target resource.
            TRACE
                The TRACE method performs a message loop-back test along the path to the target resource.
            PATCH
                The PATCH method applies partial modifications to a resource

        target :
            An absolute path, ultimately followed by a '?' and query string.
            This is the most common form, known as the origin form, and is used with GET, POST, HEAD, and OPTIONS methods.
                POST / HTTP/1.1
                GET /background.png HTTP/1.0
                HEAD /test.html?query=alibaba HTTP/1.1
                OPTIONS /anypage.html HTTP/1.0
            A complete URL, known as the absolute form, is mostly used with GET when connected to a proxy
                GET https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages HTTP/1.1
            The authority component of a URL, consisting of the domain name and optionally the port (prefixed by a ':'), is called the authority form.
            It is only used with CONNECT when setting up an HTTP tunnel
                CONNECT developer.mozilla.org:80 HTTP/1.1
            The asterisk form, a simple asterisk ('*') is used with OPTIONS, representing the server as a whole.
                OPTIONS * HTTP/1.1
         */
        return method.trim()  + " " + target.trim() + " " + httpVersion.trim();
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage java WebClient <server host name> <server port number>");
            return;
        }

        String httpServerHost = args[0];
        int httpServerPort = Integer.parseInt(args[1]);
        httpServerHost = args[0];
        httpServerPort = Integer.parseInt(args[1]);

        try {
            InetAddress addr;
            Socket sock = new Socket(httpServerHost, httpServerPort);
            addr = sock.getInetAddress();
            System.out.println("Connected to " + addr);
            sock.close();
        } catch (java.io.IOException e) {
            System.out.println("Can't connect to " + httpServerHost + ":" + httpServerPort);
            System.out.println(e);
        }
    }

}
