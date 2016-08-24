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


import org.jetbrains.annotations.Nullable;

import java.io.File;


public class RedoLogManager
{
  private final com.zimbra.cs.redolog.RedoLogManager mRedoLogManager;

  public RedoLogManager(File redolog, File archdir, boolean supportsCrashRecovery)
  {
    this(new com.zimbra.cs.redolog.RedoLogManager(redolog, archdir, supportsCrashRecovery));
  }

  public RedoLogManager(@Nullable Object redoLogManager)
  {
    if (redoLogManager == null)
    {
      throw new NullPointerException("RedoLogManager is null");
    }
    mRedoLogManager = (com.zimbra.cs.redolog.RedoLogManager) redoLogManager;
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mRedoLogManager);
  }

  public RedologLogWriter getCurrentLogWriter()
  {
    return new RedologLogWriter(mRedoLogManager.getCurrentLogWriter());
  }

  public long getCurrentLogSequence()
  {
    return mRedoLogManager.getCurrentLogSequence();
  }

  public void checkpointRedoLogManager()
  {
    mRedoLogManager.forceRollover(true);
  }
}
