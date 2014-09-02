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

package org.openzal.zal.redolog;


import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.TransactionId;

import java.io.IOException;


public class ZETransactionId
{
  private final TransactionId mTransactionId;

  public ZETransactionId()
  {
    this(new TransactionId());
  }

  public ZETransactionId(Object transactionId)
  {
    mTransactionId = (TransactionId)transactionId;
  }

  public void deserialize(ZERedoLogInput redologInput)
    throws IOException
  {
    mTransactionId.deserialize(redologInput.toZimbra(RedoLogInput.class));
  }

  @Override
  public boolean equals(Object o)
  {
    if (o instanceof TransactionId)
    {
      return mTransactionId.equals(o);
    }

    if (this == o) { return true; }
    if (o == null || getClass() != o.getClass()) { return false; }

    ZETransactionId that = (ZETransactionId) o;

    if (mTransactionId != null ? !mTransactionId.equals(that.mTransactionId) : that.mTransactionId != null)
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    return mTransactionId != null ? mTransactionId.hashCode() : 0;
  }
}
