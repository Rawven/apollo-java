package com.ctrip.framework.apollo.monitor.metrics.model;

import com.ctrip.framework.apollo.monitor.metrics.util.MeterType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rawven
 */
public class MetricsSample {

  protected String name;
  protected MeterType type;
  protected final Map<String, String> tags = new HashMap<>();


  public String getName() {
    return "Apollo_Client_" + name;
  }

  public MeterType getType() {
    return type;
  }

  public Map<String, String> getTags() {
    return tags;
  }

  public MetricsSample putTag(String key, String value) {
    tags.put(key, value);
    return this;
  }

}

