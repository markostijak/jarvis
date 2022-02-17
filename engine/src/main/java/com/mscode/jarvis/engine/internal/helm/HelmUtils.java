package com.mscode.jarvis.engine.internal.helm;

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import static org.springframework.util.StringUtils.hasText;

public class HelmUtils {

    public static String version(String version) {
        if (hasText(version)) {
            return "--version " + version;
        }

        return "";
    }

    public static boolean exists(Helm helm, @NonNull HelmRepository repository) {
        HelmResult helmResult = helm.repoList();
        return helmResult.isSuccessful() && helmResult.getStdout().contains(repository.getName());
    }

    public static HelmResult loadHelmChart(Helm helm, String name, HelmChart helmChart) {
        HelmRepository helmRepo = helmChart.getRepository();

        Assert.notNull(helmRepo, "Missing helm repo for " + name);

        if (!exists(helm, helmRepo)) {
            helm.repoAdd(helmRepo.getName(), helmRepo.getUrl());
        }

        return helm.template(name, helmChart.getChart(), version(helmChart.getVersion()));
    }

}
