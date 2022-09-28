/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2017 ZeXtras S.r.l.
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

public enum WeekOfMonth
{
  First(1), Second(2), Third(3), Fourth(4), Last(-1), Custom(-256);

  private final int mValue;
  private Integer customValue;

  WeekOfMonth(int zimbra)
  {
    mValue = zimbra;
    customValue = null;
  }

  public static WeekOfMonth fromZimbra(int value)
  {
    switch (value)
    {
      case -1:
        return Last;
      case 1:
      default:
        return First;
      case 2:
        return Second;
      case 3:
        return Third;
      case 4:
        return Fourth;
    }
  }

  public static WeekOfMonth fromEAS(int value)
  {
    switch (value)
    {
      case 1:
        return First;
      case 2:
        return Second;
      case 3:
        return Third;
      case 4:
        return Fourth;
      case 5:
        return Last;
    }

    throw new RuntimeException("Invalid EAS WeekOfMonth "+value);
  }

  public int toZimbra()
  {
    return mValue;
  }

  public int toEAS()
  {
    switch( this )
    {
      case First:
        return 1;
      case Second:
        return 2;
      case Third:
        return 3;
      case Fourth:
        return 4;
      case Last:
        return 5;
    }

    throw new RuntimeException();
  }

  public void set(int value) {
    customValue = value;
  }

  public int get() {
    return customValue;
  }

}
