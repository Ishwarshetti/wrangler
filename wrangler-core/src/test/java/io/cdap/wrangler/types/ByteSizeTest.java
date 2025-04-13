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

package io.cdap.wrangler.types;

import org.junit.Assert;
import org.junit.Test;

import io.cdap.wrangler.api.parser.ByteSize;

public class ByteSizeTest {

  @Test
  public void testByteConversions() {
    Assert.assertEquals(1024L, new ByteSize("1KB").getBytes());
    Assert.assertEquals(1048576L, new ByteSize("1MB").getBytes());
    Assert.assertEquals(1073741824L, new ByteSize("1GB").getBytes());
    Assert.assertEquals(1536L, new ByteSize("1.5KB").getBytes());
  }

  @Test
  public void testCaseInsensitivity() {
    Assert.assertEquals(2048L, new ByteSize("2kb").getBytes());
    Assert.assertEquals(3145728L, new ByteSize("3MB").getBytes());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidByteSize() {
    new ByteSize("abcMB");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingUnit() {
    new ByteSize("1000");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnknownUnit() {
    new ByteSize("5XB");
  }
}
