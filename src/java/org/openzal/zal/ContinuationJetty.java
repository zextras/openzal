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

import javax.annotation.Nullable;

import org.eclipse.jetty.continuation.ContinuationSupport;

import javax.servlet.http.HttpServletRequest;

public class ContinuationJetty implements Continuation
{
  org.eclipse.jetty.continuation.Continuation mContinuation;

  public ContinuationJetty(HttpServletRequest req)
  {
    mContinuation = ContinuationSupport.getContinuation(req);
  }

  @Override
  public boolean isSuspended()
  {
    return mContinuation.isSuspended();
  }

  @Override
  public void resume()
  {
    mContinuation.resume();
  }

  @Override
  public boolean isInitial()
  {
    return mContinuation.isInitial();
  }

  @Override
  public void suspend()
  {
    suspend(0);
  }

  @Override
  public void suspend(long timeoutMs) throws Error
  {
    try
    {
      mContinuation.setTimeout(timeoutMs);
      mContinuation.suspend();
      mContinuation.undispatch();
    }
    catch (Throwable ex)
    {
      throw new ContinuationThrowable(ex);
    }
  }

  @Override
  public boolean isExpired()
  {
    return mContinuation.isExpired();
  }

  private static final String sAttributeKey = "ZAL";

  @Override
  public void setObject(Object obj)
  {
    mContinuation.setAttribute(sAttributeKey, obj);
  }

  @Override
  public Object getObject()
  {
    return mContinuation.getAttribute(sAttributeKey);
  }

  @Override
  public String toString()
  {
    return mContinuation.toString();
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

    ContinuationJetty that = (ContinuationJetty) o;

    if (!mContinuation.equals(that.mContinuation))
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    return mContinuation.hashCode();
  }
}
