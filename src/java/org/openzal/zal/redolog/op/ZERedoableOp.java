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

import com.zimbra.cs.redolog.RedoLogInput;
import org.openzal.zal.Utils;
import org.openzal.zal.lib.Version;
import org.openzal.zal.log.ZimbraLog;
import org.openzal.zal.redolog.*;
import com.zimbra.cs.redolog.op.Checkpoint;
import com.zimbra.cs.redolog.op.RedoableOp;
import java.io.IOException;
import java.lang.reflect.Method;


public class ZERedoableOp
{

  public static final String REDO_MAGIC     = RedoableOp.REDO_MAGIC;
  public static final int    UNKNOWN_ID     = RedoableOp.UNKNOWN_ID;
  public static final int    MAILBOX_ID_ALL = RedoableOp.MAILBOX_ID_ALL;

  private final RedoableOp mRedoableOp;


  private static Method sGetVersionMethod = null;

  static
  {
    try
    {
      sGetVersionMethod = RedoableOp.class.getDeclaredMethod("getVersion");
      sGetVersionMethod.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
    }
  }

  public ZERedoableOp(Object redoableOp)
  {
    mRedoableOp = (RedoableOp)redoableOp;
  }

  public boolean isStartMarker()
  {
    return mRedoableOp.isStartMarker();
  }

  public boolean isEndMarker()
  {
    return mRedoableOp.isEndMarker();
  }


  public long getTimestamp()
  {
    return mRedoableOp.getTimestamp();
  }

  public ZETransactionId getTransactionId()
  {
    return new ZETransactionId(mRedoableOp.getTransactionId());
  }

  public Version getVersion()
    throws Exception
  {
    return new Version(sGetVersionMethod.invoke(mRedoableOp).toString());
  }

  public String toString()
  {
    return mRedoableOp.toString();
  }

  public int getMailboxId()
  {
    /* $if ZimbraVersion >= 7.0.0 $ */
    return mRedoableOp.getMailboxId();
    /* $else $
    return (int) mRedoableOp.getMailboxId();
    /* $endif $ */
  }

  public static ZERedoableOp deserializeOp(ZERedoLogInput redoLogInput)
    throws IOException
  {
    return new ZERedoableOp(
      RedoableOp.deserializeOp(
        redoLogInput.toZimbra(RedoLogInput.class)
      )
    );
  }

  public RedoableOp getProxiedObject()
  {
    return mRedoableOp;
  }

  public ZECreateFolderPath toCreateFolderPath()
  {
    return new ZECreateFolderPath(this);
  }

  public ZECreateMessage toCreateMessage()
  {
    return new ZECreateMessage(this);
  }

  public ZECreateTag toCreateTag()
  {
    return new ZECreateTag(this);
  }

  public ZECheckpoint toCheckpoint()
  {
    return new ZECheckpoint(this);
  }

  public boolean isCheckPointOp()
  {
    return mRedoableOp instanceof Checkpoint;
  }

  public int getOpCode()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return mRedoableOp.getOperation().getCode();
    /* $else $
    return mRedoableOp.getOpCode();
    /* $endif $ */
  }
}
