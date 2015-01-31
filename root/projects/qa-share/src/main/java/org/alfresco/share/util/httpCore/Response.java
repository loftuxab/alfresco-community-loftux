package org.alfresco.share.util.httpCore;

/**
 * Created with IntelliJ IDEA.
 * User: aliaksei.bul
 * Date: 17.05.13
 * Time: 13:21
 */
public class Response {

    private int statusCode;
    private String response;

    public Response() {
        response = "Not Response";
        statusCode = -666;
    }

    public Response(int statusCode, String response) {
        this.statusCode = statusCode;
        this.response = response;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponse() {
        return response;
    }

    public boolean contains(String part) {
        return response.contains(part);
    }

    @Override
    public String toString() {
        return "response[" +
                "statusCode=" + statusCode +
                ", response='" + response + '\'' +
                "' ]";
    }
}
