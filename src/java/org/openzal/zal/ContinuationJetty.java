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

package org.openzal.zal;

import org.jetbrains.annotations.Nullable;

/* $if MajorZimbraVersion < 8 $
import org.mortbay.jetty.HttpConnection;
import org.mortbay.util.ajax.ContinuationSupport;
import org.mortbay.jetty.nio.SelectChannelConnector;
  $else$ */
import org.eclipse.jetty.continuation.ContinuationSupport;
/* $endif$ */

import javax.servlet.http.HttpServletRequest;

public class ContinuationJetty implements Continuation
{
/* $if ZimbraVersion >= 8.0.0 $ */
  org.eclipse.jetty.continuation.Continuation mContinuation;
/* $else$
  org.mortbay.util.ajax.Continuation mContinuation;
 $endif$ */

  public ContinuationJetty(HttpServletRequest req)
  {
/* $if MajorZimbraVersion <= 7 $
    mContinuation = ContinuationSupport.getContinuation( req, null );
   $else$ */
    mContinuation = ContinuationSupport.getContinuation(req);
/* $endif$ */
  }

  @Override
  public boolean isSuspended()
  {
/* $if MajorZimbraVersion <= 7 $
    return mContinuation.isPending();
   $else$ */
    return mContinuation.isSuspended();
/* $endif$ */
  }

  @Override
  public void resume()
  {
    mContinuation.resume();
  }

  @Override
  public boolean isInitial()
  {
/* $if MajorZimbraVersion <= 7 $
    SelectChannelConnector.RetryContinuation retry = (SelectChannelConnector.RetryContinuation)mContinuation;
    return !mContinuation.isResumed() && !retry.isExpired();
   $else$ */
    return mContinuation.isInitial();
/* $endif$ */
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
/* $if MajorZimbraVersion <= 7 $
      mContinuation.suspend(timeoutMs);
   $else$ */
      mContinuation.setTimeout(timeoutMs);
      mContinuation.suspend();
      mContinuation.undispatch();
/* $endif$ */
    }
    catch (Throwable ex)
    {
      throw new ContinuationThrowable(ex);
    }
  }

  @Override
  public boolean isExpired()
  {
/* $if MajorZimbraVersion <= 7 $
      return !mContinuation.isPending();
   $else$ */
    return mContinuation.isExpired();
/* $endif$ */
  }

  private static final String sAttributeKey = "ZAL";

  @Override
  public void setObject(Object obj)
  {
/* $if MajorZimbraVersion <= 7 $
      mContinuation.setObject(obj);
   $else$ */
    mContinuation.setAttribute(sAttributeKey, obj);
/* $endif$ */
  }

  @Override
  public Object getObject()
  {
/* $if MajorZimbraVersion <= 7 $
    return mContinuation.getObject();
   $else$ */
    return mContinuation.getAttribute(sAttributeKey);
/* $endif$ */
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
