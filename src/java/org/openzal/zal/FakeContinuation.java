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

import javax.servlet.http.HttpServletRequest;

public class FakeContinuation implements Continuation
{
  private final HttpServletRequest mReq;
  private Object mObject;

  public FakeContinuation(HttpServletRequest req)
  {
    mReq = req;
    mObject = null;
  }

  @Override
  public boolean isSuspended()
  {
    return false;
  }

  @Override
  public void resume()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isInitial()
  {
    return true;
  }

  @Override
  public void suspend()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void suspend(long timeoutMs) throws Error
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isExpired()
  {
    return false;
  }

  @Override
  public void setObject(Object obj)
  {
    mObject = obj;
  }

  @Override
  public Object getObject()
  {
    return mObject;
  }
}
