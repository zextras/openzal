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


import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Pair;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.redolog.CommitId;
import com.zimbra.cs.redolog.RolloverManager;
import com.zimbra.cs.redolog.TransactionId;
import com.zimbra.cs.redolog.logger.LogWriter;
import com.zimbra.cs.redolog.op.RedoableOp;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class RedoLogProvider extends com.zimbra.cs.redolog.RedoLogProvider {

  volatile RedoLogListenerManager redoLogListenerManager = null;

  @Override
  public boolean isMaster() {
    return true;
  }

  @Override
  public boolean isSlave() {
    return false;
  }

  @Override
  public void startup() throws ServiceException {}

  @Override
  public void shutdown() throws ServiceException {}

  @Override
  public void initRedoLogManager() {
    mRedoLogManager = new RedoLogManager();
  }

  public void setListener(RedoLogListenerManager redoLogListenerManager) {
    this.redoLogListenerManager = redoLogListenerManager;
  }

  public class RedoLogManager extends com.zimbra.cs.redolog.RedoLogManager {
    @Override
    protected LogWriter getLogWriter() {
      throw new UnsupportedOperationException();
    }

    @Override
    public File getLogFile() {
      throw new UnsupportedOperationException();
    }

    @Override
    public File getArchiveDir() {
      throw new UnsupportedOperationException();
    }

    @Override
    public File getRolloverDestDir() {
      throw new UnsupportedOperationException();
    }

    @Override
    public LogWriter getCurrentLogWriter() {
      throw new UnsupportedOperationException();
    }

    @Override
    public LogWriter createLogWriter(
        com.zimbra.cs.redolog.RedoLogManager redoMgr,
        File logfile,
        long fsyncIntervalMS
    ) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean getInCrashRecovery() {
      return false;
    }

    @Override
    public synchronized void start() {
      throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void stop() {
      throw new UnsupportedOperationException();
    }

    @Override
    public TransactionId getNewTxnId() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void log(RedoableOp op, boolean synchronous) {}

    @Override
    public void commit(RedoableOp op) {
      if (redoLogListenerManager != null) {
        redoLogListenerManager.commit(org.openzal.zal.redolog.op.RedoableOp.fromZimbra(op));
      }
    }

    @Override
    public void abort(RedoableOp op) {}

    @Override
    public void flush() throws IOException {}

    @Override
    protected void logOnly(RedoableOp op, boolean synchronous) {}

    @Override
    protected boolean isRolloverNeeded(boolean immediate) {
      throw new UnsupportedOperationException();
    }

    @Override
    protected void setRolloverLimits(long minAgeMillis, long softMaxBytes, long hardMaxBytes) {
      throw new UnsupportedOperationException();
    }

    @Override
    protected File rollover(boolean force, boolean skipCheckpoint) {
      throw new UnsupportedOperationException();
    }

    @Override
    public File forceRollover() {
      throw new UnsupportedOperationException();
    }

    @Override
    public File forceRollover(boolean skipCheckpoint) {
      throw new UnsupportedOperationException();
    }

    @Override
    public RolloverManager getRolloverManager() {
      throw new UnsupportedOperationException();
    }

    @Override
    public long getCurrentLogSequence() {
      throw new UnsupportedOperationException();
    }

    @Override
    protected void resetActiveOps() {
      throw new UnsupportedOperationException();
    }

    @Override
    protected WriteLock acquireExclusiveLock() throws InterruptedException {
      throw new UnsupportedOperationException();
    }

    @Override
    protected void releaseExclusiveLock(WriteLock exclusiveLock) {
      throw new UnsupportedOperationException();
    }

    @Override
    protected void signalFatalError(Throwable e) {
      throw new UnsupportedOperationException();
    }

    @Override
    public File[] getArchivedLogsFromSequence(long seq) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public File[] getArchivedLogs() throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public Pair<Set<Integer>, CommitId> getChangedMailboxesSince(CommitId cid)
        throws IOException, MailServiceException {
      throw new UnsupportedOperationException();
    }

    public RedoLogManager() {
      super(null, null, false);
    }
  }
}
