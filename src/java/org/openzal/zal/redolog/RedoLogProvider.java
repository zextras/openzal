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


import com.zimbra.cs.redolog.DefaultRedoLogProvider;
import com.zimbra.cs.redolog.ProxyRedoLogManager;
import com.zimbra.cs.redolog.RedoLogManager;

public class RedoLogProvider extends com.zimbra.cs.redolog.RedoLogProvider {

  private RedoLogManager defaultRedoLogManager;
  private ProxyRedoLogManager proxyRedoLogManager;

  public RedoLogProvider() {
    DefaultRedoLogProvider defaultRedoLogProvider = new DefaultRedoLogProvider();
    defaultRedoLogProvider.initRedoLogManager();
    this.defaultRedoLogManager = defaultRedoLogProvider.getRedoLogManager();
    initRedoLogManager();
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
    proxyRedoLogManager = new ProxyRedoLogManager(defaultRedoLogManager);
    super.mRedoLogManager = proxyRedoLogManager;
    defaultRedoLogManager = null;
  }

  public void setListener(RedoLogListenerManager redoLogListenerManager) {
    proxyRedoLogManager.setListener(redoLogListenerManager);
  }

  public void removeListener() {
    proxyRedoLogManager.removeListener();
  }

  public static RedoLogProvider getRedoLogProvider() {
    return (RedoLogProvider) com.zimbra.cs.redolog.RedoLogProvider.getInstance();
  }

  //
//  public class RedoLogManager extends com.zimbra.cs.redolog.RedoLogManager {
//
//    private final com.zimbra.cs.redolog.RedoLogManager redoLogManager;
//
//    @Override
//    protected LogWriter getLogWriter() {
//      redoLogManager
//    }
//
//    @Override
//    public File getLogFile() {
//      throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public File getArchiveDir() {
//      throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public File getRolloverDestDir() {
//      throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public LogWriter getCurrentLogWriter() {
//      throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public LogWriter createLogWriter(
//        com.zimbra.cs.redolog.RedoLogManager redoMgr,
//        File logfile,
//        long fsyncIntervalMS
//    ) {
//      return redoLogManager.createLogWriter(redoMgr, logfile, fsyncIntervalMS);
//    }
//
//    @Override
//    public boolean getInCrashRecovery() {
//      return redoLogManager.getInCrashRecovery();
//    }
//
//    @Override
//    public synchronized void start() {
//      redoLogManager.start();
//    }
//
//    @Override
//    public synchronized void stop() {
//      redoLogManager.stop();
//    }
//
//    @Override
//    public TransactionId getNewTxnId() {
//      return redoLogManager.getNewTxnId();
//    }
//
//    @Override
//    public void log(RedoableOp op, boolean synchronous) {
//      redoLogManager.log(op, synchronous);
//    }
//
//    @Override
//    public void commit(RedoableOp op) {
//      if( Objects.nonNull(redoLogListenerManager) ) {
//        redoLogListenerManager.commit(new org.openzal.zal.redolog.op.RedoableOp(op));
//      }
//      redoLogManager.commit(op);
//    }
//
//    @Override
//    public void abort(RedoableOp op) {
//      redoLogManager.abort(op);
//    }
//
//    @Override
//    public void flush() throws IOException {
//      redoLogManager.flush();
//    }
//
//    @Override
//    public File forceRollover() {
//      return redoLogManager.forceRollover();
//    }
//
//    @Override
//    public File forceRollover(boolean skipCheckpoint) {
//      return redoLogManager.forceRollover();
//    }
//
//    @Override
//    public RolloverManager getRolloverManager() {
//      return redoLogManager.getRolloverManager();
//    }
//
//    @Override
//    public long getCurrentLogSequence() {
//      return redoLogManager.getCurrentLogSequence();
//    }
//
//    @Override
//    public File[] getArchivedLogsFromSequence(long seq) throws IOException {
//      return redoLogManager.getArchivedLogsFromSequence(seq);
//    }
//
//    @Override
//    public File[] getArchivedLogs() throws IOException {
//      return redoLogManager.getArchivedLogs();
//    }
//
//    @Override
//    public Pair<Set<Integer>, CommitId> getChangedMailboxesSince(CommitId cid)
//        throws IOException, MailServiceException {
//      return redoLogManager.getChangedMailboxesSince(cid);
//    }
//
//    public RedoLogManager(com.zimbra.cs.redolog.RedoLogManager redoLogManager) {
//      super(null, null, false);
//      this.redoLogManager = redoLogManager;
//    }
//  }
}
