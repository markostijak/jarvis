package com.github.markostijak.jarvis.services.wiremock;

import lombok.Data;
import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

@Data
public class WireMockConnectionDetails implements ConnectionDetails {

    private String scheme = "http";

    private String host = "wiremock";

    private int port = 8080;

    private String urlPathPrefix = "";

}
