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
 * Represents a token for byte sizes (e.g., 10KB, 1.5MB).
 */
public class ByteSize implements Token {
  private final double value;
  private final String unit;

  public ByteSize(String raw) {
    this.unit = raw.replaceAll("[0-9.]", "").toUpperCase(); // Extract unit (e.g., KB)
    this.value = Double.parseDouble(raw.replaceAll("[^0-9.]", "")); // Extract numeric value
  }

  /**
   * Converts the stored value and unit to bytes.
   */
  public long getBytes() {
    switch (unit) {
      case "B": return (long) value;
      case "KB": return (long) (value * 1024);
      case "MB": return (long) (value * 1024 * 1024);
      case "GB": return (long) (value * 1024 * 1024 * 1024);
      case "TB": return (long) (value * 1024L * 1024 * 1024 * 1024);
      default: throw new IllegalArgumentException("Unknown unit: " + unit);
    }
  }

  @Override
  public Object value() {
    return getBytes();
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(getBytes());
  }

  @Override
  public String[] split(String string) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'split'");
  }
}
