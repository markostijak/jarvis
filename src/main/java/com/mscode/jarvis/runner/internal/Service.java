package com.mscode.jarvis.runner.internal;

import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Service {

    private String name;

    private List<HasMetadata> resources;

}
