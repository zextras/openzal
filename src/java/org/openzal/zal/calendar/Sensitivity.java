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

package org.openzal.zal.calendar;

import java.util.HashMap;
import java.util.Map;

public enum Sensitivity
{
  PUBLIC("PUB"),
  PRIVATE("PRI"),
  CONFIDENTIAL("CON"),
  PERSONAL("PUB");

  public String getRawSensitivity()
  {
    return mSensitivity;
  }

  private final String mSensitivity;

  Sensitivity(String sensitivity)
  {
    mSensitivity = sensitivity;
  }

  private static final Map<String, Sensitivity> sZimbra2Zal;

  static {
    sZimbra2Zal = new HashMap<String, Sensitivity>(3);
    sZimbra2Zal.put("PUB",PUBLIC);
    sZimbra2Zal.put("PRI",PRIVATE);
    sZimbra2Zal.put("CON",CONFIDENTIAL);
    sZimbra2Zal.put("PUBLIC", PUBLIC);
    sZimbra2Zal.put("PRIVATE", PRIVATE);
    sZimbra2Zal.put("CONFIDENTIAL", CONFIDENTIAL);
  }

  public static Sensitivity fromZimbra(String classProp)
  {
    Sensitivity sensitivity = sZimbra2Zal.get(classProp);
    if( sensitivity == null ){
      throw new RuntimeException("Invalid invite sensitivity: "+classProp);
    }

    return sensitivity;
  }
}
