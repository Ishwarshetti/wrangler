/*
 *  Copyright © 2017-2019 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package io.cdap.wrangler.statistics;

import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.Row;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class AggregateStatsTest {

  @Test
  public void testAggregateStatsDirective() throws Exception {
    // Sample input data for byte sizes (in bytes) and time durations (in milliseconds)
    String[] rows = new String[] {
      "10KB,1000ms", // Row 1
      "5MB,2000ms",  // Row 2
      "1GB,1500ms",  // Row 3
    };

    // Define the recipe for aggregation
    String[] recipe = new String[] {
      "aggregate-stats :byte_size_col :time_duration_col total_size_bytes total_time_seconds"
    };

    // Run the recipe with sample data
    List<Row> results = TestingRig.execute(recipe, rows);

    // Assertions to check if the aggregation works as expected
    Assert.assertEquals(1, results.size());  // Only one aggregated row

    // Get aggregated values from the result row
    double totalSize = (double) results.get(0).getValue("total_size_bytes");
    double totalTime = (double) results.get(0).getValue("total_time_seconds");

    // Assert the values based on expected aggregation
    Assert.assertEquals(1073741824 + 5242880 + 10240, totalSize, 0.001); // Total size in bytes
    Assert.assertEquals(1000 + 2000 + 1500, totalTime, 0.001); // Total time in milliseconds
  }

  @Test
  public void testAggregateStatsWithAverage() throws Exception {
    // Sample input data for byte sizes (in bytes) and time durations (in milliseconds)
    String[] rows = new String[] {
      "10KB,1000ms", // Row 1
      "5MB,2000ms",  // Row 2
      "1GB,1500ms",  // Row 3
    };

    // Define the recipe for average aggregation
    String[] recipe = new String[] {
      "aggregate-stats :byte_size_col :time_duration_col total_size_avg total_time_avg average"
    };

    // Run the recipe with sample data
    List<Row> results = TestingRig.execute(recipe, rows);

    // Assertions to check if the aggregation works as expected
    Assert.assertEquals(1, results.size());  // Only one aggregated row

    // Get average values from the result row
    double avgSize = (double) results.get(0).getValue("total_size_avg");
    double avgTime = (double) results.get(0).getValue("total_time_avg");

    // Assert the average values based on expected aggregation
    Assert.assertEquals((1073741824 + 5242880 + 10240) / 3.0, avgSize, 0.001); // Average size in bytes
    Assert.assertEquals((1000 + 2000 + 1500) / 3.0, avgTime, 0.001); // Average time in milliseconds
  }
}
