package http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static http.server.HttpMethods.POST;

public class HttpRequest {
    public HttpMethods method;
    public String params;
    public Map<String, String> body;
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
            if (request.method == POST) {
                str = in.readLine();
                //  load body
                request.body = new HashMap<>();
                while (str != null && !str.equals("")) {
                    String[] strSplit = str.split("=");
                    if(strSplit.length > 1){
                        request.body.put(strSplit[0], strSplit[1]);
                    } else {
                        request.body.put(strSplit[0], "");
                    }
                    str = in.readLine();
                }
            }
        } catch (IOException e){
            System.err.println("Error parsing request in HttpRequest: "+ e);
        }
        return request;
    }
}
