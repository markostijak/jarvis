package com.mscode.jarvis.runner.internal;

import com.mscode.jarvis.runner.DeploymentDescriptor;
import com.mscode.jarvis.runner.annotations.Deployment;
import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.annotation.MergedAnnotation;

import java.util.List;

@Data
@AllArgsConstructor
public class Service {

    private String name;

    private DeploymentDescriptor descriptor;

    private List<HasMetadata> resources;

    private MergedAnnotation<Deployment> annotation;

}
