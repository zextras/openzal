/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2015 ZeXtras S.r.l.
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

package org.openzal.zal.lib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Version implements Comparable<Version>
{
  private final int[] mVersionParts;

  public Version(int ... versionParts)
  {
    if(versionParts.length == 0) {
      throw new InvalidParameterException();
    }
    mVersionParts = Arrays.copyOf(versionParts,versionParts.length);
  }

  public Version(@NotNull String version)
    throws NumberFormatException
  {
    final List<Integer> versionParts = new ArrayList<Integer>(3);

    for(String part : version.split("\\."))
    {
      versionParts.add(Integer.valueOf(part));
    }

    final int size = versionParts.size();

    mVersionParts = new int[versionParts.size()];

    for(int i = 0 ; i < size ; i++)
    {
      mVersionParts[i] = versionParts.get(i);
    }
  }

  @Override
  public int compareTo(@NotNull Version version)
  {
    int maxSize = Math.max(mVersionParts.length, version.mVersionParts.length);

    for (int i = 0 ; i < maxSize ; i++)
    {
      int thisPart  = getPartValue(i);
      int otherPart = version.getPartValue(i);

      if (otherPart > thisPart)
      {
        return -1;
      }

      if (otherPart < thisPart)
      {
        return 1;
      }
    }

    return 0;
  }

  public int getPartCount()
  {
    return mVersionParts.length;
  }

  private int getPartValue(int partIndex)
  {
    return partIndex < mVersionParts.length ? mVersionParts[partIndex] : 0;
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder(8);

    for(int i = 0 ; i < mVersionParts.length - 1 ; i++)
    {
      sb.append(mVersionParts[i]);
      sb.append('.');
    }

    sb.append(mVersionParts[mVersionParts.length-1]);

    return sb.toString();
  }

  public int getMajor()
  {
    return getPartValue(0);
  }

  public int getMinor()
  {
    return getPartValue(1);
  }

  public int getMicro()
  {
    return getPartValue(2);
  }

  public boolean is(int major)
  {
    return getPartValue(0) == major;
  }

  public boolean is(int major, int minor)
  {
    return is(major) && getPartValue(1) == minor;
  }

  public boolean is(int major, int minor, int micro)
  {
    return is(major, minor) && getPartValue(2) == micro;
  }

  public boolean isAtLeast(int major)
  {
    return compareTo(new Version(major)) >= 0;
  }

  public boolean isAtLeast(int major, int minor)
  {
    return compareTo(new Version(major, minor)) >= 0;
  }

  public boolean isAtLeast(int major, int minor, int micro)
  {
    return compareTo(new Version(major, minor, micro)) >= 0;
  }

  public boolean isAtLeast(Version version)
  {
    return compareTo(version) >= 0;
  }

  @Override
  public boolean equals(@Nullable Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || !(o instanceof Version))
    {
      return false;
    }

    Version version = (Version) o;

    return compareTo(version) == 0;
  }

  @Override
  public int hashCode()
  {
    return Arrays.hashCode(mVersionParts);
  }

  public boolean isAtMost(int major)
  {
    return compareTo(new Version(major)) <= 0;
  }

  public boolean isAtMost(int major, int minor)
  {
    return compareTo(new Version(major, minor)) <= 0;
  }

  public boolean isAtMost(int major, int minor, int micro)
  {
    return compareTo(new Version(major, minor, micro)) <= 0;
  }

  public boolean isAtMost(Version version)
  {
    return compareTo(version) <= 0;
  }


  public boolean lessThan(int major)
  {
    return compareTo(new Version(major)) < 0;
  }

  public boolean lessThan(int major, int minor)
  {
    return compareTo(new Version(major, minor)) < 0;
  }

  public boolean lessThan(int major, int minor, int micro)
  {
    return compareTo(new Version(major, minor, micro)) < 0;
  }

  public boolean lessThan(Version version)
  {
    return compareTo(version) < 0;
  }

  public Version truncate(int maxParts)
  {
    final int howManyParts = Math.min(maxParts, mVersionParts.length);
    int[] versionParts = Arrays.copyOf(mVersionParts, howManyParts);
    return new Version(versionParts);
  }
}
