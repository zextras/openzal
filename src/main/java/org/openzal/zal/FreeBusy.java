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

package org.openzal.zal;

import javax.annotation.Nonnull;

public class FreeBusy {
  @Nonnull
  private final com.zimbra.cs.fb.FreeBusy mFreeBusy;

  public FreeBusy(@Nonnull Object item) {
    mFreeBusy = (com.zimbra.cs.fb.FreeBusy) item;
  }


  public String getName() {
    return mFreeBusy.getName();
  }


  public String getBusiest() {
    return mFreeBusy.getBusiest();
  }


  // FBTYPE values defined in iCalendar
  public final static String FBTYPE_BUSY = "B";
  public final static String FBTYPE_FREE = "F";
  public final static String FBTYPE_BUSY_TENTATIVE = "T";
  public final static String FBTYPE_BUSY_UNAVAILABLE = "O";
  public final static String FBTYPE_NODATA = "N";

  private static String sBusyOrder[] = new String[5];

  static {
    // The lower index, the busier.
    sBusyOrder[0] = FBTYPE_BUSY_UNAVAILABLE;
    sBusyOrder[1] = FBTYPE_BUSY;
    sBusyOrder[2] = FBTYPE_BUSY_TENTATIVE;
    sBusyOrder[3] = FBTYPE_NODATA;
    sBusyOrder[4] = FBTYPE_FREE;
  }

  public static String chooseBusier(String freeBusy1, String freeBusy2) {
    for (int i = 0; i < sBusyOrder.length; i++) {
      String busy = sBusyOrder[i];
      if (busy.equals(freeBusy1))
        return freeBusy1;
      if (busy.equals(freeBusy2))
        return freeBusy2;
    }
    if (freeBusy1 != null)
      return freeBusy1;
    else
      return freeBusy2;
  }

  public enum Method {
    PUBLISH, REQUEST, REPLY
  }

  private static final String NL = "\r\n";
  private static final String MAILTO = "mailto:";
  private static final String HTTP = "http:";

  public String toString() {
    return mFreeBusy.toString();
  }

  public long getStartTime() {
    return mFreeBusy.getStartTime();
  }

  public long getEndTime() {
    return mFreeBusy.getEndTime();
  }
}
