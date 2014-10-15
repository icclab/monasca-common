/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package monasca.common.model.metric;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.Map;

/**
 * Metric with definition information flattened alongside value information.
 */
public class Metric implements Serializable {
  private static final long serialVersionUID = 3455749495426525634L;

  public String name;
  public Map<String, String> dimensions;
  public long timestamp;
  public String value;
  public double[][] timeValues = null;
  private MetricDefinition definition;

  public Metric() {}

  public Metric(@NotNull MetricDefinition definition, long timestamp, double value) {
    this.definition = Preconditions.checkNotNull(definition, "definition");
    this.name = definition.name;
    setDimensions(definition.dimensions);
    this.timestamp = timestamp;
    this.value = String.valueOf(Preconditions.checkNotNull(value, "value"));
  }
  
  public Metric(@NotNull MetricDefinition definition, long timestamp, String value) {
	this.definition = Preconditions.checkNotNull(definition, "definition");
	this.name = definition.name;
	setDimensions(definition.dimensions);
	this.timestamp = timestamp;
	this.value = Preconditions.checkNotNull(value, "value");
  }

  public Metric(String name, @Nullable Map<String, String> dimensions, long timestamp, double value) {
    this.name = Preconditions.checkNotNull(name, "name");
    setDimensions(dimensions);
    this.timestamp = timestamp;
    this.value = String.valueOf(value);
  }
  
  public Metric(String name, @Nullable Map<String, String> dimensions, long timestamp, String value) {
	this.name = Preconditions.checkNotNull(name, "name");
	setDimensions(dimensions);
	this.timestamp = timestamp;
	this.value = String.valueOf(value);
  }

  public Metric(String name, @Nullable Map<String, String> dimensions, long timestamp,
      double[][] timeValues) {
    this.name = Preconditions.checkNotNull(name, "name");
    setDimensions(dimensions);
    this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
    this.timeValues = Preconditions.checkNotNull(timeValues, "timeValues");
  }

  /**
   * Returns the MetricDefinition.
   */
  public MetricDefinition definition() {
    if (definition == null)
      definition = new MetricDefinition(name, dimensions);
    return definition;
  }

  @Override
  public String toString() {
    return "Metric{" + "name='" + name + '\'' + ", dimensions=" + dimensions + ", timeStamp='"
        + timestamp + '\'' + ", value=" + value + ", timeValues=" + Arrays.toString(timeValues)
        + '}';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof Metric))
      return false;
    Metric other = (Metric) obj;
    if (definition == null) {
      if (other.definition != null)
        return false;
    } else if (!definition.equals(other.definition))
      return false;
    if (dimensions == null) {
      if (other.dimensions != null)
        return false;
    } else if (!dimensions.equals(other.dimensions))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (!Arrays.deepEquals(timeValues, other.timeValues))
      return false;
    if (timestamp != other.timestamp)
      return false;
    if (value == null) {
    	if (other.value != null)
    		return false;
    	return true;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((definition == null) ? 0 : definition.hashCode());
    result = prime * result + ((dimensions == null) ? 0 : dimensions.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + Arrays.hashCode(timeValues);
    result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
    long temp;
    temp = value.hashCode();
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, String> getDimensions() {
    return dimensions;
  }

  public void setDimensions(Map<String, String> dimensions) {
    this.dimensions = dimensions;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @JsonProperty("time_values")
  public double[][] getTimeValues() {
    return timeValues;
  }

  @JsonProperty("time_values")
  public void setTimeValues(double[][] timeValues) {
    this.timeValues = timeValues;
  }
}
