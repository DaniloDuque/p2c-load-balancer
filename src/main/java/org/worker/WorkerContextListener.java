package org.worker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;

@Getter
public final class WorkerContextListener {
    private final Injector injector;

    public WorkerContextListener(final int port, final String errorPath) {
        this.injector = Guice.createInjector(new WorkerModule(port, errorPath));
    }
}
