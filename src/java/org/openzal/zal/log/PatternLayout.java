/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2016 ZeXtras S.r.l.
 *
 * This file is part of ZAL.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZAL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.openzal.zal.log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PatternLayout extends org.apache.log4j.PatternLayout
{
  public static String DEFAULT_PATTERN = "%m%n";

  public PatternLayout()
  {
    this(DEFAULT_PATTERN);
  }

  public PatternLayout(String pattern) {
    super(pattern);
  }

  @NotNull
  public org.apache.log4j.helpers.PatternParser createPatternParser(@Nullable String pattern) {
    if (pattern == null) {
      pattern = DEFAULT_PATTERN;
    }
    return new PatternParser(pattern, this);
  }
}
