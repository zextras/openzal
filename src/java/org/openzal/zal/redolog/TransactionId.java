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

package org.openzal.zal.redolog;


import javax.annotation.Nonnull;

import java.io.IOException;


public class TransactionId
{
  @Nonnull private final com.zimbra.cs.redolog.TransactionId mTransactionId;

  public TransactionId()
  {
    this(new com.zimbra.cs.redolog.TransactionId());
  }

  public TransactionId(Object transactionId)
  {
    mTransactionId = (com.zimbra.cs.redolog.TransactionId) transactionId;
  }

  public void deserialize(@Nonnull RedoLogInput redologInput)
    throws IOException
  {
    mTransactionId.deserialize(redologInput.toZimbra(com.zimbra.cs.redolog.RedoLogInput.class));
  }

  @Override
  public boolean equals(Object o)
  {
    if (o instanceof com.zimbra.cs.redolog.TransactionId)
    {
      return mTransactionId.equals(o);
    }

    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    TransactionId that = (TransactionId) o;

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
