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

import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.redolog.op.DataExtractor;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.openzal.zal.Operation;
import org.openzal.zal.Utils;
import org.openzal.zal.extension.BootstrapClassLoader;
import org.openzal.zal.lib.Version;
import org.openzal.zal.log.ZimbraLog;
import org.openzal.zal.redolog.*;

import java.io.IOException;
import java.lang.reflect.Method;
import org.openzal.zal.redolog.RedoLogOutput.Reader;


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

  public RedoableOp(@Nonnull Object redoableOp)
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

  @Nonnull
  public TransactionId getTransactionId()
  {
    return new TransactionId(mRedoableOp.getTransactionId());
  }

  @Nonnull
  public Version getVersion()
    throws Exception
  {
    return Version.parse(sGetVersionMethod.invoke(mRedoableOp).toString());
  }

  public String toString()
  {
    return mRedoableOp.toString();
  }

  public int getMailboxId()
  {
    return mRedoableOp.getMailboxId();
  }

  com.zimbra.cs.redolog.op.RedoableOp getProxiedObject()
  {
    return mRedoableOp;
  }

  @Nonnull
  public CreateFolderPath toCreateFolderPath()
  {
    return new CreateFolderPath(this);
  }

  @Nonnull
  public CreateMessage toCreateMessage()
  {
    return new CreateMessage(this);
  }

  @Nonnull
  public CreateTag toCreateTag()
  {
    return new CreateTag(this);
  }

  @Nonnull
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
    return mRedoableOp.getOperation().getCode();
  }

  protected DataInputStream getDataInputStream() throws IOException {
    return new DataInputStream(getProxiedObject().getInputStream());
  }

  public void extractData(RedoLogOutput redoLogOutput) throws Exception {
    DataExtractor.extract(mRedoableOp, redoLogOutput);
  }

  public org.openzal.zal.OperationContext getOperationContext() {
    return org.openzal.zal.OperationContext.buildFromZimbra(mRedoableOp.getOperationContext());
  }

  static
  {
    try
    {
      Method defineClassMethod = ClassLoader.class.getDeclaredMethod(
          "defineClass", byte[].class, int.class, int.class
      );
      defineClassMethod.setAccessible(true);

      InputStream is = null;
      try
      {
        Class<?> parentClass = Class.forName("com.zimbra.cs.redolog.op.RedoableOp");
        ClassLoader parentClassLoader = parentClass.getClassLoader();

        is = BootstrapClassLoader.class.getResourceAsStream("/com/zimbra/cs/redolog/op/DataExtractor");
        byte[] buffer = new byte[6 * 1024];
        int idx = 0;
        int read = 0;
        while (read > -1)
        {
          idx += read;
          if (buffer.length == idx)
          {
            buffer = Arrays.copyOf(buffer, buffer.length * 2);
          }
          read = is.read(buffer, idx, buffer.length - idx);
        }

        defineClassMethod.invoke(
            parentClassLoader,
            buffer, 0, idx
        );
      }
      catch (Exception ignore) {}
      finally
      {
        IOUtils.closeQuietly(is);
      }
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }
}
