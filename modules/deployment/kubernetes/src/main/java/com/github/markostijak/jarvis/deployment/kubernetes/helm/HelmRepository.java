package com.github.markostijak.jarvis.deployment.kubernetes.helm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelmRepository {

    private String name;

    private String url;

}
