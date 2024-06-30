package com.ctrip.framework.apollo.metrics.util;

import java.lang.management.ManagementFactory;
import javax.management.InstanceAlreadyExistsException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * @author Rawven
 */
public final class JMXUtil {

  public static final String JMX = "jmx";
  public static final String MBEAN_NAME = "apollo.client.monitor:type=";

  public static ObjectName register(String name, Object mbean) {
    try {
      ObjectName objectName = new ObjectName(name);

      MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

      try {
        mbeanServer.registerMBean(mbean, objectName);
      } catch (InstanceAlreadyExistsException ex) {
        mbeanServer.registerMBean(mbean, objectName);
        mbeanServer.unregisterMBean(objectName);
      }

      return objectName;
    } catch (JMException e) {
      throw new IllegalArgumentException(name, e);
    }
  }

  public static void unregister(String name) {
    try {
      MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
      mbeanServer.unregisterMBean(new ObjectName(name));
    } catch (JMException e) {
      throw new IllegalArgumentException(name, e);
    }
  }
  //TODO MBeanServer自定义
}
