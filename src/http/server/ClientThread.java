package http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private final Service service;
    private Socket clientSocket;

    public ClientThread(Service service, Socket clientSocket) {
        this.service = service;
        this.clientSocket = clientSocket;
    }

    public void run() {
        for(;;) {
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(
                        this.clientSocket.getInputStream()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            PrintWriter out = null;
            try {
                out = new PrintWriter(this.clientSocket.getOutputStream());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // read the data sent. We basically ignore it,
            // stop reading once a blank line is hit. This
            // blank line signals the end of the client HTTP
            // headers.
            String url = null;
            String str = ".";
            String bodyResponse = "";


            HttpRequest request = HttpRequest.readHttpRequest(in);
            switch (request.method) {
                case GET: {
                    // Handle get
                    System.out.println("Parameters:" + request.params);
                    bodyResponse = Service.getHTMLFile(request.params);
                    break;
                }
                case POST: {
                    if (request.params.equals("/")) {
                        bodyResponse = Service.getHTMLFile(request.params);

//                bodyResponse = service.handleAddTodoItem(request.body);
                    } else {
                        // 404
                    }
                    break;
                }
                case DELETE: {
                    // Handle delete
                    break;
                }
                default:
                    // Bad request: 400
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
            try {
                this.clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
