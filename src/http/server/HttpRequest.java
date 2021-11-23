package http.server;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    public HttpMethods method;
    public String params;
    HttpRequest(String s){
        String[] strSplit = s.split(" ");
        String methodString = strSplit[0];
        method = HttpMethods.getRequestMethod(methodString);
        params = strSplit[1];
        if (params ==null){
            params = "";
        }

    }

    public static HttpRequest readHttpRequest(BufferedReader in){
        String str = ".";
        HttpRequest request = null;
        try {
            while (str != null && !str.equals("")) {
                str = in.readLine();
                if (str == null) continue;
                // Only look for GET now
                try {
                    if (request == null) {
                        request = new HttpRequest(str);
                    }
                } catch (Exception e) {
                    System.err.println("Error in HttpRequest: "+ e);
                }
            }
        } catch (IOException e){
            System.err.println("Error parsing request in HttpRequest: "+ e);
        }
        return request;
    }
}
