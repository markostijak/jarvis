package com.github.markostijak.jarvis.deployment.kubernetes.helm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelmChart {

    private String chart;

    private String version;

    private HelmRepository repository;

    public HelmChart(String chart, String version, String repositoryName, String repositoryUrl) {
        this(chart, version, new HelmRepository(repositoryName, repositoryUrl));
    }

}
