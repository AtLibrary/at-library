package ru.bcs.at.library.core.core.helpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.appium.mitmproxy.InterceptedMessage;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MitmInterceptedMessage {

    private MitmInterceptedMessage.Request request;

    private MitmInterceptedMessage.Response response;

    public MitmInterceptedMessage(InterceptedMessage message) {
        request = new Request(message.getRequest());
        response = new Response(message.getResponse());
    }

    @Data
    public static class Request {

        public Request(InterceptedMessage.Request request) {
            method = request.getMethod();
            url = request.getUrl();
            body = request.getBody();
            headers = new ArrayList<>();
            for (String[] header : request.getHeaders()) {
                if (header.length > 1) {
                    headers.add(new Header(header[0], header[1]));
                } else if (header.length > 0) {
                    headers.add(new Header(header[0]));
                }
            }
        }

        public String getUrl() {
            return url;
        }

        private String method;

        private String url;

        private List<Header> headers;

        private byte[] body;
    }

    @Data
    public static class Response {

        public Response(InterceptedMessage.Response response) {
            statusCode = response.getStatusCode();
            body = response.getBody();
            headers = new ArrayList<>();
            for (String[] header : response.getHeaders()) {
                if (header.length > 1) {
                    headers.add(new Header(header[0], header[1]));
                } else if (header.length > 0) {
                    headers.add(new Header(header[0]));
                }
            }
        }

        @JsonProperty("status_code")
        private int statusCode;

        private List<Header> headers;

        private byte[] body;
    }

    @Data
    public static class Header {

        public Header(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public Header(String name) {
            this.name = name;
            this.value = "";
        }

        private String name;

        private String value;
    }

}
