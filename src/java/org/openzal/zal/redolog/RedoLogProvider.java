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


import com.zimbra.cs.redolog.RedoConfig;
import com.zimbra.cs.redolog.op.RedoableOp;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.openzal.zal.Utils;
import org.openzal.zal.log.ZimbraLog;

public class RedoLogProvider extends com.zimbra.cs.redolog.RedoLogProvider {

  private final AtomicReference<RedoLogListenerManager> redoLogListenerManagerRef;

  public RedoLogProvider() {
    redoLogListenerManagerRef = new AtomicReference<>();
    super.mRedoLogManager = new RedoLogManager();
  }

  @Override
  public boolean isMaster() {
    return true;
  }

  @Override
  public boolean isSlave() {
    return false;
  }

  @Override
  public void startup() {}

  @Override
  public void shutdown() {}

  @Override
  public void initRedoLogManager() {
  }

  private static RedoLogProvider wrap(com.zimbra.cs.redolog.RedoLogProvider rp) {
    if( rp instanceof RedoLogProvider) {
      return (RedoLogProvider)rp;
    } else {
      return new RedoLogProvider();
    }
  }

  public static RedoLogProvider getRedoLogProvider() {
    return wrap(com.zimbra.cs.redolog.RedoLogProvider.getInstance());
  }

  public void setListener(RedoLogListenerManager redoLogListenerManager) {
    redoLogListenerManagerRef.set(redoLogListenerManager);
  }

  public void removeListener() {
    redoLogListenerManagerRef.set(null);
  }

  public class RedoLogManager extends com.zimbra.cs.redolog.RedoLogManager {

    @Override
    public void commit(RedoableOp op) {
      try {
        if (Objects.nonNull(redoLogListenerManagerRef.get())) {
          redoLogListenerManagerRef.get().commit(new org.openzal.zal.redolog.op.RedoableOp(op));
        }
      } catch (Throwable e) {
        ZimbraLog.mailbox.error(Utils.exceptionToString(e));
      }
      super.commit(op);
    }

    public RedoLogManager() {
      super(new File(com.zimbra.cs.redolog.RedoConfig.redoLogPath()), new File(RedoConfig.redoLogArchiveDir()), true);
    }
  }
}
