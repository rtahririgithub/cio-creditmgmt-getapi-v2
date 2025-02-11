package com.telus.credit.pds.model;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Reference data key object. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"keyName", "keyValue"})
public class Key {

  /** Input Key name for reference data item. (Required) */
  @JsonProperty("keyName")
  @JsonPropertyDescription("Input Key name for reference data item.")
  private String keyName;
  /** Input key value for reference data item. (Required) */
  @JsonProperty("keyValue")
  @JsonPropertyDescription("Input key value for reference data item.")
  private String keyValue;

  @JsonIgnore private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

  /** Input Key name for reference data item. (Required) */
  @JsonProperty("keyName")
  public String getKeyName() {
    return keyName;
  }

  /** Input Key name for reference data item. (Required) */
  @JsonProperty("keyName")
  public void setKeyName(String keyName) {
    this.keyName = keyName;
  }

  /** Input key value for reference data item. (Required) */
  @JsonProperty("keyValue")
  public String getKeyValue() {
    return keyValue;
  }

  /** Input key value for reference data item. (Required) */
  @JsonProperty("keyValue")
  public void setKeyValue(String keyValue) {
    this.keyValue = keyValue;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Key key = (Key) o;
    return Objects.equals(keyName, key.keyName) && Objects.equals(keyValue, key.keyValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(keyName, keyValue);
  }

  @Override
  public String toString() {
    return "Key{" +
            "keyName='" + keyName + '\'' +
            ", keyValue='" + keyValue + '\'' +
            ", additionalProperties=" + additionalProperties +
            '}';
  }
}
