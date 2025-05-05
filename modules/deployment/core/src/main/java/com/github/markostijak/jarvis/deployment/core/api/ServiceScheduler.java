package com.github.markostijak.jarvis.deployment.core.api;

import java.util.Collection;

public interface ServiceScheduler {

    boolean deploy(Collection<Service> services);

    boolean destroy(Collection<Service> services);

}
