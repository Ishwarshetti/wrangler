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

package io.cdap.wrangler.statistics;

import org.slf4j.Logger;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.annotations.Categories;
import io.cdap.wrangler.api.parser.UsageDefinition;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.DirectiveParseException;
import io.cdap.wrangler.api.ErrorRowException;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.ReportErrorAndProceed;
import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.wrangler.api.Arguments;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Plugin(type = Directive.TYPE)
@Name("aggregate-stats")
@Categories(categories = { "statistics", "aggregate" })
@Description("Computes aggregate statistics like min, max, sum, count, and average for the specified columns")
public class AggregateStats implements Directive {

  // Array to hold the column names specified by the user
  private String[] columns;

  /**
   * Initializes the directive with user-supplied arguments.
   * It expects a "columns" argument containing comma-separated column names.
   */
  @Override
  public void initialize(Arguments args) throws DirectiveParseException {
    if (!args.contains("columns")) {
      throw new DirectiveParseException("The 'columns' argument is required.");
    }

    this.columns = args.value("columns").split(",");
    for (int i = 0; i < columns.length; i++) {
      columns[i] = columns[i].trim(); // remove whitespace
    }
  }

  /**
   * Executes the directive logic to compute aggregate statistics:
   * min, max, sum, count, and average for specified numeric columns.
   */
  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext ctx) 
      throws DirectiveExecutionException, ErrorRowException, ReportErrorAndProceed {

    // Handle empty input
    if (rows.isEmpty()) {
      Row defaultResult = new Row();
      for (String col : columns) {
        defaultResult.add(col + "_min", null);
        defaultResult.add(col + "_max", null);
        defaultResult.add(col + "_sum", 0.0);
        defaultResult.add(col + "_count", 0);
        defaultResult.add(col + "_avg", null);
      }
      return List.of(defaultResult);
    }

    // Maps to track stats per column
    Map<String, Double> minMap = new HashMap<>();
    Map<String, Double> maxMap = new HashMap<>();
    Map<String, Double> sumMap = new HashMap<>();
    Map<String, Integer> countMap = new HashMap<>();

    // Loop through each row and compute stats
    for (Row row : rows) {
      for (String col : columns) {
        Object val = row.getValue(col.trim());
        if (val == null) {
          continue;
        }

        if (val instanceof Number) {
          double value = ((Number) val).doubleValue();

          // Update min, max, sum, count
          minMap.put(col, Math.min(minMap.getOrDefault(col, value), value));
          maxMap.put(col, Math.max(maxMap.getOrDefault(col, value), value));
          sumMap.put(col, sumMap.getOrDefault(col, 0.0) + value);
          countMap.put(col, countMap.getOrDefault(col, 0) + 1);
        } else {
          // Log non-numeric values (optional but useful for debugging)
          Logger logger = (Logger) ctx.getLogger();
          logger.warn(String.format("Non-numeric value '%s' found in column '%s'. Skipping.", val, col));
        }
      }
    }

    // Create a single row to hold the aggregated stats
    Row result = new Row();
    for (String col : columns) {
      result.add(col + "_min", minMap.get(col));
      result.add(col + "_max", maxMap.get(col));
      result.add(col + "_sum", sumMap.get(col));
      result.add(col + "_count", countMap.get(col));
      result.add(col + "_avg", countMap.get(col) > 0 ? sumMap.get(col) / countMap.get(col) : null);
    }

    return List.of(result); // return as a singleton list
  }

  /**
   * Optional cleanup method if needed. No-op for now.
   */
  @Override
  public void destroy() {
    // no-op
  }

  /**
   * Defines the usage of this directive and its expected arguments.
   */
  @Override
  public UsageDefinition define() {
    UsageDefinition.Builder builder = UsageDefinition.builder();
    builder.define("columns", "string"); // Expect a string argument called 'columns'
    return builder.build();
  }
}

