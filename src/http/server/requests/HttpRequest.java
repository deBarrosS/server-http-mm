package http.server.requests;

import http.server.requests.HttpMethods;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static http.server.requests.HttpMethods.POST;


public class HttpRequest {
    public HttpMethods method;
    public String params;
    public String contentType;
    public String[] accepts;
    public Map<String, String> body;
    private static int INPUT_BUFFER_LENGTH = 1000;
    private Long contentLength;

    HttpRequest(String s){
        String[] strSplit = s.split(" ");
        String methodString = strSplit[0];
        method = HttpMethods.getRequestMethod(methodString);
        params = strSplit[1];
        if (params ==null){
            params = "";
        }
    }

    public static HttpRequest readHttpRequest(BufferedInputStream in){
        HttpRequest request = null;
        try {
            byte[] inputBuffer = new byte[INPUT_BUFFER_LENGTH];

            in.read(inputBuffer, 0,
                    INPUT_BUFFER_LENGTH);

            String requestStr = new String(inputBuffer);
            String lines[] = requestStr.split("\\u0000?\\r?\\n");

            int index = 0;
            String str = lines[index++];

            // We are only capable of treating one file for each request
            while (str != null && !str.equals("")) {
                try {
                    if (request == null) {
                        request = new HttpRequest(str);
                    }else{
                        if(str.startsWith("Accept:")){
                            String list = str.substring(str.indexOf(':')+1);
                            request.accepts =  (list.split(","));
                        }
                        if(str.startsWith("Content-Type:")){
                            request.contentType = str.substring(str.indexOf(':')+1);
                        }
                        if(str.startsWith("Content-Length:")){
                            request.contentLength =  Long.parseLong(str.substring(str.indexOf(':')+1).trim());
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Error in HttpRequest: here "+ e);
                }
                str = lines[index++];
            }

            // Get request body
            if (request.method == POST) {
                str = lines[index++];
                //  load body
                request.body = new HashMap<>();
                while (str != null && !str.equals("")) {
                    String[] strSplit = str.split("=");
                    if(strSplit.length > 1){
                        request.body.put(strSplit[0], strSplit[1]);
                    } else {
                        request.body.put(strSplit[0], "");
                    }
                    if (!indexInBounds(index, lines))break;
                    str = lines[index++];
                }
            }
        } catch (IOException e){
            System.err.println("Error parsing request in HttpRequest: "+ e);
        }
        return request;
    }

    private static boolean indexInBounds(int index, String[] lines) {
        return index < lines.length;
    }
}