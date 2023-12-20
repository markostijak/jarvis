package com.github.markostijak.jarvis.services.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.stubbing.StubImport;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.intellij.lang.annotations.Language;
import org.yaml.snakeyaml.Yaml;

public class WireMockClient extends WireMock implements AutoCloseable {

    private final WireMockConnectionDetails connection;

    private String baseUrl;

    public WireMockClient(WireMockConnectionDetails connection) {
        super(connection.getScheme(), connection.getHost(), connection.getPort(), connection.getUrlPathPrefix());
        this.connection = connection;
    }

    public StubMapping stubFor(@Language("yaml") String yaml) {
        String json = Json.write(new Yaml().load(yaml));

        StubMapping mapping = StubMapping.buildFrom(json);
        StubImport stubImport = StubImport.stubImport().stub(mapping).build();
        importStubMappings(stubImport);

        return mapping;
    }

    public String baseUrl() {
        if (baseUrl != null) {
            return baseUrl;
        }

        return baseUrl = connection.getScheme() + "://" + connection.getHost() + ":" + connection.getPort() + connection.getUrlPathPrefix();
    }

    @Override
    public void close() throws Exception {
        // no-op: workaround
    }
}
