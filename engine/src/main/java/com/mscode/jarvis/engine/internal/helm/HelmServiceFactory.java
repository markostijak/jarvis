package com.mscode.jarvis.engine.internal.helm;

import com.mscode.jarvis.engine.DeploymentDescriptor;
import com.mscode.jarvis.engine.annotation.Deployment;
import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.api.ServiceFactory;
import com.mscode.jarvis.engine.internal.kubernetes.KubernetesServiceFactory;
import io.fabric8.kubernetes.api.model.HasMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;
import org.springframework.util.Assert;

import java.util.List;

import static com.mscode.jarvis.engine.internal.helm.HelmUtils.loadHelmChart;
import static com.mscode.jarvis.engine.internal.kubernetes.KubernetesUtils.load;
import static org.springframework.util.StringUtils.hasLength;

@Order(2)
@Component
public class HelmServiceFactory implements ServiceFactory {

    private final Helm helm;
    private final KubernetesServiceFactory factory;

    @Autowired
    public HelmServiceFactory(KubernetesServiceFactory factory, Helm helm) {
        this.factory = factory;
        this.helm = helm;
    }

    @Override
    public Service create(TestContext context, DeploymentDescriptor descriptor, MergedAnnotation<Deployment> deployment) {
        String name = deployment.getString("name");

        HelmResult result = loadHelmChart(helm, name, descriptor.getHelm());
        Assert.isTrue(result.isSuccessful(), "Helm error: " + result.getError());

        List<HasMetadata> resources = load(factory.getClient(), result.getStdout());

        return factory.create(context, resources, descriptor, deployment);
    }

    @Override
    public boolean supports(DeploymentDescriptor descriptor) {
        return descriptor.getHelm() != null && hasLength(descriptor.getHelm().getChart());
    }

}
