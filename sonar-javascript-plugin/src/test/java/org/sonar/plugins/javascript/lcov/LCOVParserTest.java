/*
 * Sonar JavaScript Plugin
 * Copyright (C) 2011 Eriks Nukis and SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.javascript.lcov;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.measures.CoverageMeasuresBuilder;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

public class LCOVParserTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private LCOVParser parser = new LCOVParser();

  @Test
  public void test() {
    Map<String, CoverageMeasuresBuilder> result = parser.parse(Arrays.asList(
        "SF:file1.js",
        "DA:1,1",
        "end_of_record",
        "SF:file2.js",
        "FN:2,(anonymous_1)",
        "FNDA:2,(anonymous_1)",
        "DA:1,1",
        "DA:2,0",
        "BRDA:11,1,0,1",
        "BRDA:11,1,0,0",
        "end_of_record"));
    assertThat(result).hasSize(2);

    CoverageMeasuresBuilder fileCoverage = result.get("file1.js");
    assertThat(fileCoverage.getLinesToCover()).isEqualTo(1);
    assertThat(fileCoverage.getCoveredLines()).isEqualTo(1);
    assertThat(fileCoverage.getConditions()).isEqualTo(0);
    assertThat(fileCoverage.getCoveredConditions()).isEqualTo(0);

    fileCoverage = result.get("file2.js");
    assertThat(fileCoverage.getLinesToCover()).isEqualTo(2);
    assertThat(fileCoverage.getCoveredLines()).isEqualTo(1);
    assertThat(fileCoverage.getConditions()).isEqualTo(2);
    assertThat(fileCoverage.getCoveredConditions()).isEqualTo(1);
  }

  @Test
  public void unreadable_file() {
    thrown.expect(SonarException.class);
    thrown.expectMessage("Could not read content from file: not-found");
    parser.parseFile(new File("not-found"));
  }

}
