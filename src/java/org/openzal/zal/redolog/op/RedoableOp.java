/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2015 ZeXtras S.r.l.
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openzal.zal.Utils;
import org.openzal.zal.lib.Version;
import org.openzal.zal.log.ZimbraLog;
import org.openzal.zal.redolog.*;

import java.io.IOException;
import java.lang.reflect.Method;


public class RedoableOp
{

  public static final String REDO_MAGIC     = com.zimbra.cs.redolog.op.RedoableOp.REDO_MAGIC;
  public static final int    UNKNOWN_ID     = com.zimbra.cs.redolog.op.RedoableOp.UNKNOWN_ID;
  public static final int    MAILBOX_ID_ALL = com.zimbra.cs.redolog.op.RedoableOp.MAILBOX_ID_ALL;

  private final com.zimbra.cs.redolog.op.RedoableOp mRedoableOp;


  @Nullable private static Method sGetVersionMethod = null;

  static
  {
    try
    {
      sGetVersionMethod = com.zimbra.cs.redolog.op.RedoableOp.class.getDeclaredMethod("getVersion");
      sGetVersionMethod.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  public RedoableOp(@NotNull Object redoableOp)
  {
    mRedoableOp = (com.zimbra.cs.redolog.op.RedoableOp) redoableOp;
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

  @NotNull
  public TransactionId getTransactionId()
  {
    return new TransactionId(mRedoableOp.getTransactionId());
  }

  @NotNull
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

  @NotNull
  public static RedoableOp deserializeOp(RedoLogInput redoLogInput)
    throws IOException
  {
    return new RedoableOp(
      com.zimbra.cs.redolog.op.RedoableOp.deserializeOp(
        redoLogInput.toZimbra(com.zimbra.cs.redolog.RedoLogInput.class)
      )
    );
  }

  com.zimbra.cs.redolog.op.RedoableOp getProxiedObject()
  {
    return mRedoableOp;
  }

  @NotNull
  public CreateFolderPath toCreateFolderPath()
  {
    return new CreateFolderPath(this);
  }

  @NotNull
  public CreateMessage toCreateMessage()
  {
    return new CreateMessage(this);
  }

  @NotNull
  public CreateTag toCreateTag()
  {
    return new CreateTag(this);
  }

  @NotNull
  public Checkpoint toCheckpoint()
  {
    return new Checkpoint(this);
  }

  public boolean isCheckPointOp()
  {
    return mRedoableOp instanceof com.zimbra.cs.redolog.op.Checkpoint;
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
