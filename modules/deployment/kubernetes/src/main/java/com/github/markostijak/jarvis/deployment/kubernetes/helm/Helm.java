package com.github.markostijak.jarvis.deployment.kubernetes.helm;

import java.nio.charset.Charset;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

@Slf4j
public class Helm {

    @Getter
    private static final Helm instance = new Helm();

    public HelmResult template(String... args) {
        return helm("template", args);
    }

    public HelmResult install(String... args) {
        return helm("install", args);
    }

    public HelmResult delete(String... args) {
        return helm("delete", args);
    }

    public HelmResult repo(String command, String... args) {
        return helm("repo " + command, args);
    }

    public HelmResult repoAdd(String name, String url) {
        return repo("add", name, url);
    }

    public HelmResult repoList() {
        return repo("list");
    }

    public HelmResult repoUpdate(String name) {
        return repo("update", name);
    }

    private HelmResult helm(String command, String... args) {
        String line = "helm " + command + " " + String.join(" ", args);
        return exec(line.split(" +"));
    }

    private HelmResult exec(String... command) {
        try {
            log.debug(String.join(" ", command));
            Process process = new ProcessBuilder(command).start();

            String stdout = StreamUtils.copyToString(process.getInputStream(), Charset.defaultCharset());
            String error = StreamUtils.copyToString(process.getErrorStream(), Charset.defaultCharset());

            int statusCode = process.waitFor();
            process.destroy();

            return new HelmResult(stdout, error, statusCode);
        } catch (Exception e) {
            throw new HelmException("Unable to execute command: " + String.join(" ", command), e);
        }
    }

}
