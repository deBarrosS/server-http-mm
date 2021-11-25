package http.server.response;

/**
 * HttpStatusCode represents the standard http status codes used by the server
 */
public enum HttpStatusCode {

    /**
     * 200 OK
     * The request succeeded.
     */
    OK(200, "OK"),
    /**
     * 201 Created
     * The request succeeded, and a new resource was created as a result. This is typically the
     * response sent after POST requests, or some PUT requests.
     */
    CREATED(201, "Created"),
    /**
     * 400 Bad Request
     * The server could not understand the request due to invalid syntax.
     */
    BAD_REQUEST(400, "Bad Request"),
    /**
     * 404 Not Found
     * The server can not find the requested resource. In the browser, this means the URL is not
     * recognized. In an API, this can also mean that the endpoint is valid but the resource itself
     * does not exist. Servers may also send this response instead of 403 Forbidden to hide the
     * existence of a resource from an unauthorized client. This response code is probably the most
     * well known due to its frequent occurrence on the web.
     */
    NOT_FOUND(404, "Not Found"),
    /**
     * 500 Internal Server Error
     * The server has encountered a situation it does not know how to handle.
     */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");


    private int statusCode;
    private String statusName;


    HttpStatusCode(int statusCode, String statusName) {
        this.statusCode = statusCode;
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return this.statusCode + " " + this.statusName;
    }
}
