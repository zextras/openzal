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


import com.zimbra.cs.redolog.RedoLogManager;

import java.io.File;


public class ZERedoLogManager
{
  private final RedoLogManager mRedoLogManager;

  public ZERedoLogManager(File redolog, File archdir, boolean supportsCrashRecovery)
  {
    this(new RedoLogManager(redolog, archdir, supportsCrashRecovery));
  }

  public ZERedoLogManager(Object redoLogManager)
  {
    if (redoLogManager == null )
    {
      throw new NullPointerException("RedoLogManager is null");
    }
    mRedoLogManager = (RedoLogManager)redoLogManager;
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mRedoLogManager);
  }

  public ZERedologLogWriter getCurrentLogWriter()
  {
    return new ZERedologLogWriter(mRedoLogManager.getCurrentLogWriter());
  }

  public long getCurrentLogSequence()
  {
    return mRedoLogManager.getCurrentLogSequence();
  }
}
