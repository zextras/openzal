/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2014 ZeXtras S.r.l.
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

import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;

public class PatternParser extends org.apache.log4j.helpers.PatternParser
{
  PatternLayout mLayout;

  PatternParser(String pattern, PatternLayout layout) {
    super(pattern);
    mLayout = layout;
  }

  public void finalizeConverter(char c) {
    if (c == 'z') {
      addConverter(new ZimbraPatternConverter(formattingInfo));
      currentLiteral.setLength(0);
    } else {
      super.finalizeConverter(c);
    }
  }

  private class ZimbraPatternConverter extends PatternConverter
  {
    ZimbraPatternConverter(FormattingInfo formattingInfo) {
      super(formattingInfo);
    }

    public String convert(LoggingEvent event) {
      // ZimbraLog.getContext() is private...
      return event.getThreadName();
    }
  }
}
