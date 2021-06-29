package com.mscode.jarvis;

import org.springframework.boot.CommandLineRunner;

import java.util.Arrays;

public class Application implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println(Arrays.toString(args));
    }
}
