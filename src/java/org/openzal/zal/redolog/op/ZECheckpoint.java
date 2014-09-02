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

package org.openzal.zal.redolog.op;

import com.zimbra.cs.redolog.op.Checkpoint;

import java.util.Set;


public class ZECheckpoint
{
  private final ZERedoableOp mOp;

  public ZECheckpoint(ZERedoableOp op)
  {
    mOp = op;
  }

  public Set getActiveTxns()
  {
    return ((Checkpoint) mOp.getProxiedObject()).getActiveTxns();
  }
}
