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

import io.cdap.wrangler.api.parser.TimeDuration;

public class TimeDurationTest {

  @Test
  public void testTimeConversions() {
    Assert.assertEquals(500L, new TimeDuration("500ms").getMilliseconds());
    Assert.assertEquals(2000L, new TimeDuration("2s").getMilliseconds());
    Assert.assertEquals(90000L, new TimeDuration("1.5min").getMilliseconds());
  }

  @Test
  public void testCaseInsensitivity() {
    Assert.assertEquals(1000L, new TimeDuration("1S").getMilliseconds());
    Assert.assertEquals(60000L, new TimeDuration("1Min").getMilliseconds());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidDuration() {
    new TimeDuration("xyzms");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingUnit() {
    new TimeDuration("1500");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnknownUnit() {
    new TimeDuration("3centuries");
  }
}
