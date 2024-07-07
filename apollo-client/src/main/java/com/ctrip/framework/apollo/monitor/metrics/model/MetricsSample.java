/*
 * Copyright 2022 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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

