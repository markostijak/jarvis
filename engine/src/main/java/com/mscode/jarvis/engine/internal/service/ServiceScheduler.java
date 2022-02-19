package com.mscode.jarvis.engine.internal.service;

import com.mscode.jarvis.engine.api.Service;
import org.springframework.test.context.TestContext;

import java.util.List;

public interface ServiceScheduler {

    void prepare(TestContext context);

    void start(List<Service> services, ExecutionParameters environment);

    void stop(List<Service> services);

    void clean(TestContext context);

}
