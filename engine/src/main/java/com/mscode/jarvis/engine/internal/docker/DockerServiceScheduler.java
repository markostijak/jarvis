package com.mscode.jarvis.engine.internal.docker;

import com.mscode.jarvis.engine.ExecutionDescriptor;
import com.mscode.jarvis.engine.api.Service;
import com.mscode.jarvis.engine.api.ServiceScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
//@Component
public class DockerServiceScheduler implements ServiceScheduler {

    @Override
    public void startServices(Stream<List<Service>> services, ExecutionDescriptor environment) {

    }

    @Override
    public void stopServices(Stream<List<Service>> services, ExecutionDescriptor environment) {

    }

}
