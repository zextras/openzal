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

import com.zimbra.cs.account.accesscontrol.RightCommand;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ACE
{
  private final RightCommand.ACE mACE;

  protected ACE(@Nonnull Object ace)
  {
    if ( ace == null )
    {
      throw new NullPointerException();
    }
    mACE = (RightCommand.ACE)ace;
  }

  public String right()
  {
    return mACE.right();
  }

  public String rightWithModifier()
  {

    return mACE.rightModifier()!=null?mACE.rightModifier().getModifier()+mACE.right():mACE.right();
  }

  public RightModifier modifier()
  {
    return mACE.rightModifier()!=null?new RightModifier(mACE.rightModifier()):null;
  }

  public String granteeId()
  {
    return mACE.granteeId();
  }

  public String targetId()
  {
    return mACE.targetId();
  }

  @Nullable
  public String granteeName()
  {
    return mACE.granteeName();
  }

  public String granteeType()
  {
    return mACE.granteeType();
  }

  public String targetName()
  {
    return mACE.targetName();
  }

  public String targetType()
  {
    return mACE.targetType();
  }

  @Override
  public boolean equals(@Nullable Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    ACE ACE = (ACE) o;

    return mACE.equals(ACE.mACE);
  }

  @Override
  public int hashCode()
  {
    return mACE != null ? mACE.hashCode() : 0;
  }
}

