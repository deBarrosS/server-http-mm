package http.server;

public class Request {
    public HttpMethods method;
    public String params;
    Request(String s){
        String[] strSplit = s.split(" ");
        String methodString = strSplit[0];
        method = HttpMethods.getRequestMethod(methodString);
        params = strSplit[1];
        if (params ==null){
            params = "";
        }

    }
}
