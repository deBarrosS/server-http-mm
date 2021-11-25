package http.server.requests;

/**
 * HttpMethods lists all http methods supported by this server
 */
public enum HttpMethods {
    GET,
    POST,
    DELETE,
    HEAD,
    PUT,
    UNKNOWN;

    public static HttpMethods getRequestMethod(String str) {
        if (GET.toString().equals(str)) {
            return GET;
        } else if(POST.toString().equals(str)) {
            return POST;
        } else if(DELETE.toString().equals(str)){
            return DELETE;
        }else if(HEAD.toString().equals(str)){
            return HEAD;
        }
        return UNKNOWN;
    }
}
