/*
 * ZAL - An abstraction layer for Zimbra.
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

package org.openzal.zal;

import com.zimbra.common.util.Pair;

public class ZEPair<F, S>
{
  private final Pair<F, S> mPair;

  public ZEPair(F first, S second)
  {
    mPair = new Pair<F, S>(first, second);
  }

  protected ZEPair(Pair<F, S> pair)
  {
    mPair = pair;
  }

  public F getFirst()
  {
    return mPair.getFirst();
  }

  public S getSecond()
  {
    return mPair.getSecond();
  }
}
