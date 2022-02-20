package com.mscode.jarvis.engine.api;

import com.mscode.jarvis.engine.ExecutionDescriptor;
import org.springframework.test.context.TestContext;

import java.util.List;
import java.util.stream.Stream;

public interface ServiceScheduler {

    default void prepare(TestContext context) {
        // no-op
    }

    void startServices(Stream<List<Service>> services, ExecutionDescriptor environment);

    void stopServices(Stream<List<Service>> services, ExecutionDescriptor environment);

    default void clean(TestContext context) {
        // no-op
    }

}
