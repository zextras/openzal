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

package org.openzal.zal.soap;

import java.util.Objects;

public class QName
{
  private final String mName;
  private final String mNamespace;

  public QName(String name, String namespace)
  {
    mName = name;
    mNamespace = namespace;
  }

  public String getNamespace()
  {
    return mNamespace;
  }

  public String getName()
  {
    return mName;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    QName qName = (QName) o;
    return Objects.equals(mName, qName.mName) &&
      Objects.equals(mNamespace, qName.mNamespace);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(mName, mNamespace);
  }
}
