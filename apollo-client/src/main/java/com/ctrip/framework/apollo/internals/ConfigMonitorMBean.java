package com.ctrip.framework.apollo.internals;

public interface ConfigMonitorMBean {

    String getAppId();

    String getCluster();

    String getEnv();

    String getNamespace404();

    String getNamespaceTimeout();

    int getExceptionNum();

    String getNamespaceUsedName();

    String getNamespaceUsedTime();

    String getNamespaceLatestUpdateTime();

    String getNamespaceFirstLoadTimeSpend();

    String getDataWithCurrentMonitoringSystemFormat();
    //more method....
}
