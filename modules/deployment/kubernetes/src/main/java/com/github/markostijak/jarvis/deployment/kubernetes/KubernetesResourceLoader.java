package com.github.markostijak.jarvis.deployment.kubernetes;

import static org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX;

import com.github.markostijak.jarvis.deployment.core.internal.exceptions.DeploymentDescriptorException;
import com.github.markostijak.jarvis.deployment.core.support.DeploymentRepository;
import com.github.markostijak.jarvis.deployment.kubernetes.helm.Helm;
import com.github.markostijak.jarvis.deployment.kubernetes.helm.HelmResult;
import com.github.markostijak.jarvis.deployment.kubernetes.helm.HelmUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

@Slf4j
@RequiredArgsConstructor
public class KubernetesResourceLoader {

    private final Helm helm;
    private final KubernetesDeploymentProperties properties;
    private final DeploymentRepository<KubernetesDeploymentDescriptor> repository;

    public InputStream load(String name) {
        KubernetesDeploymentDescriptor descriptor = repository.getByName(name).descriptor();

        if (descriptor.getYaml() != null) { // load from yaml with respect to the base path
            try {
                String path = descriptor.getYaml();
                if (!path.startsWith(CLASSPATH_URL_PREFIX)) {
                    path = properties.getBasePath() + path;
                }

                if (path.startsWith(CLASSPATH_URL_PREFIX)) {
                    path = path.substring(CLASSPATH_URL_PREFIX.length());
                    ClassPathResource resource = new ClassPathResource(path);
                    return resource.getInputStream();
                }

                path = properties.getBasePath() + descriptor.getYaml();
                return Files.newInputStream(Path.of(path));
            } catch (Exception e) {
                throw new DeploymentDescriptorException("Can't read kubernetes descriptor", e);
            }
        }

        if (descriptor.getHelm() != null) { // load from helm chart
            HelmResult result = HelmUtils.loadHelmChart(helm, name, descriptor.getHelm());
            Assert.isTrue(result.isSuccessful(), "Helm error: " + result.getError());
            return new ByteArrayInputStream(result.getStdout().getBytes());
        }

        throw new DeploymentDescriptorException("Unable to load " + name + " service descriptor");
    }

}
