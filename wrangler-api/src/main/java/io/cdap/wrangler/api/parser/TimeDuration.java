/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Represents a token for time durations (e.g., 500ms, 2s, 1.5min).
 */
public class TimeDuration implements Token {
  private final double value;
  private final String unit;

  public TimeDuration(String raw) {
    this.unit = raw.replaceAll("[0-9.]", "").toLowerCase(); // Normalize unit
    this.value = Double.parseDouble(raw.replaceAll("[^0-9.]", "")); // Extract numeric value
  }

  /**
   * Converts the stored value and unit to milliseconds.
   */
  public long getMilliseconds() {
    switch (unit) {
      case "ns": return (long) (value / 1_000_000);
      case "us": return (long) (value / 1_000);
      case "ms": return (long) value;
      case "s": return (long) (value * 1000);
      case "min": return (long) (value * 60 * 1000);
      case "h": return (long) (value * 60 * 60 * 1000);
      default: throw new IllegalArgumentException("Unknown unit: " + unit);
    }
  }

  @Override
  public Object value() {
    return getMilliseconds();
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(getMilliseconds());
  }

  @Override
  public String[] split(String string) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'split'");
  }
}
