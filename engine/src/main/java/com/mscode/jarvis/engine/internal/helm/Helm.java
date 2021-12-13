package com.mscode.jarvis.engine.internal.helm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.Charset;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Component
public class Helm {

    public HelmResult template(String... args) {
        return helm("template", args);
    }

    public HelmResult install(String... args) {
        return helm("install", args);
    }

    public HelmResult delete(String... args) {
        return helm("delete", args);
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
            throw new IllegalStateException("Unable to execute command: " + String.join(" ", command), e);
        }
    }

    //----- static helper methods -----//

    public static String version(String version) {
        if (hasText(version)) {
            return "--version " + version;
        }

        return "";
    }

}
