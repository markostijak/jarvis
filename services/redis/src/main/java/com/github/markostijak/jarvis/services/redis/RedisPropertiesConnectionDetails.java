package com.github.markostijak.jarvis.services.redis;

import java.util.List;

import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

public record RedisPropertiesConnectionDetails(RedisProperties properties) implements RedisConnectionDetails {

    @Override
    public String getUsername() {
        return this.properties.getUsername();
    }

    @Override
    public String getPassword() {
        return this.properties.getPassword();
    }

    @Override
    public Standalone getStandalone() {
        return Standalone.of(this.properties.getHost(), this.properties.getPort(), this.properties.getDatabase());
    }

    @Override
    public Sentinel getSentinel() {
        RedisProperties.Sentinel sentinel = this.properties.getSentinel();

        if (sentinel == null) {
            return null;
        }

        return new Sentinel() {
            @Override
            public int getDatabase() {
                return RedisPropertiesConnectionDetails.this.properties.getDatabase();
            }

            @Override
            public String getMaster() {
                return sentinel.getMaster();
            }

            @Override
            public List<Node> getNodes() {
                return sentinel.getNodes().stream().map(RedisPropertiesConnectionDetails.this::asNode).toList();
            }

            @Override
            public String getUsername() {
                return sentinel.getUsername();
            }

            @Override
            public String getPassword() {
                return sentinel.getPassword();
            }
        };
    }

    @Override
    public Cluster getCluster() {
        RedisProperties.Cluster cluster = this.properties.getCluster();
        List<Node> nodes = (cluster != null) ? cluster.getNodes().stream().map(this::asNode).toList() : null;
        return (nodes != null) ? () -> nodes : null;
    }

    private Node asNode(String node) {
        String[] components = node.split(":");
        return new Node(components[0], Integer.parseInt(components[1]));
    }

}
