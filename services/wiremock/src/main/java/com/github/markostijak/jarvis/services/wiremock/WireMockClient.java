package com.github.markostijak.jarvis.services.wiremock;

import java.util.List;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.stubbing.StubMappingCollection;
import org.intellij.lang.annotations.Language;
import org.yaml.snakeyaml.Yaml;

public class WireMockClient implements AutoCloseable {

    private final WireMock wireMock;
    private final WireMockConnectionDetails connection;

    private String baseUrl;

    public WireMockClient(WireMockConnectionDetails connection) {
        this.connection = connection;
        this.wireMock = new WireMock(connection.getScheme(), connection.getHost(), connection.getPort(), connection.getUrlPathPrefix());
    }

    public StubMapping stubFor(@Language("yaml") String yaml) {
        return yamlStubFor(yaml);
    }

    public StubMapping yamlStubFor(@Language("yaml") String yaml) {
        return jsonStubFor(Json.write(new Yaml().load(yaml)));
    }

    public StubMapping jsonStubFor(@Language("json") String json) {
        StubMappingCollection stubCollection = Json.read(json, StubMappingCollection.class);

        for (StubMapping mapping : stubCollection.getMappingOrMappings()) {
            wireMock.register(mapping);
        }

        return stubCollection;
    }

    public void removeStubMapping(StubMapping stubMapping) {
        List<? extends StubMapping> mappings = List.of(stubMapping);

        if (stubMapping instanceof StubMappingCollection collection) {
            mappings = collection.getMappingOrMappings();
        }

        for (StubMapping mapping : mappings) {
            wireMock.removeStubMapping(mapping);
        }
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
