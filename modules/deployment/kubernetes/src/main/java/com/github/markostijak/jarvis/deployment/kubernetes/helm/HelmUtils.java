package com.github.markostijak.jarvis.deployment.kubernetes.helm;

import com.github.markostijak.jarvis.deployment.core.internal.utils.Utils;

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

public class HelmUtils {

    public static boolean exists(Helm helm, @NonNull HelmRepository repository) {
        HelmResult helmResult = helm.repoList();
        return helmResult.isSuccessful() && helmResult.getStdout().contains(repository.getName());
    }

    public static HelmResult loadHelmChart(Helm helm, String name, HelmChart chart) {
        HelmRepository repository = chart.getRepository();

        Assert.notNull(repository, "Missing helm repository for " + name);

        if (!exists(helm, repository)) {
            helm.repoAdd(repository.getName(), repository.getUrl());
        }

        String version = Utils.mapOrElse(chart.getVersion(), v -> "--version " + v, "");
        return helm.template(name, chart.getChart(), version);
    }

}
