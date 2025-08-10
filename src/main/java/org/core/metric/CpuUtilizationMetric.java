package org.core.metric;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public final class CpuUtilizationMetric implements Metric<Double> {
    private static final OperatingSystemMXBean OS_BEAN
            = (OperatingSystemMXBean)
            ManagementFactory.getOperatingSystemMXBean();

    @Override
    public MetricValue getValue() {
        return new MetricValue(
                MetricName.CPU_UTILIZATION,
                OS_BEAN.getCpuLoad()
        );
    }
}
