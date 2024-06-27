package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.core.utils.DeferredLoggerFactory;
import com.ctrip.framework.apollo.metrics.collector.ClientEventCollector;
import com.ctrip.framework.apollo.metrics.collector.MemoryStatusCollector;
import com.ctrip.framework.apollo.metrics.collector.TracerEventCollector;
import com.ctrip.framework.apollo.metrics.reporter.MetricsReporter;
import com.ctrip.framework.apollo.metrics.util.JMXUtil;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.ctrip.framework.foundation.internals.ServiceBootstrap;
import com.google.common.collect.Lists;
import java.util.Objects;
import org.slf4j.Logger;

/**
 * exposes all collected data through ConfigService
 * @author Rawven
 */
public class ConfigMonitor implements ConfigMonitorMBean {
    private static final Logger logger = DeferredLoggerFactory.getLogger(ConfigMonitor.class);
    private MetricsReporter reporter;
    private MemoryStatusCollector memoryStatusCollector;
    private TracerEventCollector tracerEventCollector;
    private ClientEventCollector clientEventCollector;

    @Override
    public String getAppId() {
        return memoryStatusCollector.getAppId();
    }

    @Override
    public String getCluster() {
        return memoryStatusCollector.getCluster();
    }

    @Override
    public String getEnv() {
        return memoryStatusCollector.getApolloEnv();
    }

    @Override
    public String getNamespace404() {
        return tracerEventCollector.getNamespace404();
    }

    @Override
    public String getNamespaceTimeout() {
        return tracerEventCollector.getNamespaceTimeout();
    }

    @Override
    public int getExceptionNum() {
        return tracerEventCollector.getExceptionNum();
    }

    @Override
    public String getNamespaceUsedName() {
        return memoryStatusCollector.getAllUsedNamespaceName().toString();
    }

    @Override
    public String getNamespaceUsedTime() {
        return clientEventCollector.getAllNamespaceUsedTimes();
    }

    @Override
    public String getNamespaceLatestUpdateTime() {
        return clientEventCollector.getNamespaceLatestUpdate();
    }

    @Override
    public String getNamespaceFirstLoadTimeSpend() {
        return "";
    }

    @Override
    public String getDataWithCurrentMonitoringSystemFormat() {
        if (reporter == null) {
            return "No MonitoringSystem Use";
        }
        return reporter.response();
    }

    public void init(ClientEventCollector clientEventCollector,
        MemoryStatusCollector memoryStatusCollector, TracerEventCollector tracerEventCollector,ConfigUtil configUtil) {
        this.clientEventCollector = clientEventCollector;
        this.memoryStatusCollector = memoryStatusCollector;
        this.tracerEventCollector = tracerEventCollector;

        //init reporter
        String protocol = configUtil.getMonitorProtocol();
        if (Objects.equals(protocol, JMXUtil.JMX)) {
            JMXUtil.register(JMXUtil.MBEAN_NAME, this);
        } else if (protocol != null) {
            //init reporter
            try {
                reporter = ServiceBootstrap.loadPrimary(MetricsReporter.class);
                if (reporter != null) {
                    reporter.init(Lists.newArrayList(clientEventCollector, memoryStatusCollector,
                        tracerEventCollector));
                } else {
                    logger.warn("No MetricsReporter found for protocol: {}", protocol);
                }
            } catch (Exception e) {
                logger.error("Error initializing MetricsReporter for protocol: {}", protocol, e);
            }
        }
    }

}
