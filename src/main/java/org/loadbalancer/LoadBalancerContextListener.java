package org.loadbalancer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.NonNull;

@Getter
public final class LoadBalancerContextListener {
    private final Injector injector;

    public LoadBalancerContextListener(final int port,
                                       @NonNull final String errorPath) {
        this.injector = Guice.createInjector(
                new LoadBalancerModule(port, errorPath)
        );
    }
}
