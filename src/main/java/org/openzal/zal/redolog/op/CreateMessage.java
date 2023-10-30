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

package org.openzal.zal.redolog.op;


public class CreateMessage
{
  private final RedoableOp mOp;

  public CreateMessage(RedoableOp op)
  {
    mOp = op;
  }

  public int getMessageId()
  {
    return ((com.zimbra.cs.redolog.op.CreateMessage) mOp.getProxiedObject()).getMessageId();
  }
  public int getCalendarItemId()
  {
    return ((com.zimbra.cs.redolog.op.CreateMessage) mOp.getProxiedObject()).getCalendarItemId();
  }

  public int getConvId()
  {
    return ((com.zimbra.cs.redolog.op.CreateMessage) mOp.getProxiedObject()).getConvId();
  }

  public int getConvFirstMsgId()
  {
    return ((com.zimbra.cs.redolog.op.CreateMessage) mOp.getProxiedObject()).getConvFirstMsgId();
  }
}
