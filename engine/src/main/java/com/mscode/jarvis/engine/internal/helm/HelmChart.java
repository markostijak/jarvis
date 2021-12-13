package com.mscode.jarvis.engine.internal.helm;

import lombok.Data;

@Data
public class HelmChart {

    private String chart;

    private String version;

}
