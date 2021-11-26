package http.server.requests;

import http.server.requests.HttpMethods;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static http.server.requests.HttpMethods.POST;
import static http.server.requests.HttpMethods.PUT;


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

    /**
     * Parses an Http request sent to the BufferedInputStream attached to the socket
     * @param in  BufferedInputStream attached to the socket
     * @return HttpRequest created accordingly to the information received
     */
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
            if (request.method == POST || request.method == PUT) {
                str = lines[index++];
                //  load body
                request.body = new HashMap<>();
                while (str != null && !str.equals("")) {
                    String[] strSplit = str.split("=");
                    if(strSplit.length > 1){
                        request.body.put(strSplit[0], strSplit[1].replaceAll("\\u0000", ""));
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

    /**
     *  Returns whether index is lower than the length of the lines in parameter
     * @param index int
     * @param lines String[]
     * @return index < lines.length
     */
    private static boolean indexInBounds(int index, String[] lines) {
        return index < lines.length;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + method +
                ", params='" + params + '\'' +
                ", contentType='" + contentType + '\'' +
                ", accepts=" + Arrays.toString(accepts) +
                ", body=" + body +
                ", contentLength=" + contentLength +
                '}';
    }
}