package com.github.markostijak.jarvis.deployment.kubernetes.helm;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HelmResult {

    private String stdout;

    private String error;

    private int statusCode;

    public boolean isSuccessful() {
        return statusCode == 0;
    }

}
