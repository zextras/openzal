package com.zimbra.cs.redolog;

import com.zimbra.common.util.Pair;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.redolog.logger.LogWriter;
import com.zimbra.cs.redolog.op.RedoableOp;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import org.openzal.zal.redolog.RedoLogListenerManager;

public class ProxyRedoLogManager extends com.zimbra.cs.redolog.RedoLogManager {

  private final com.zimbra.cs.redolog.RedoLogManager redoLogManager;
  private final AtomicReference<RedoLogListenerManager> redoLogListenerManagerRef;

  @Override
  protected void logOnly(RedoableOp op, boolean synchronous) {
    redoLogManager.logOnly(op, synchronous);
  }

  @Override
  protected boolean isRolloverNeeded(boolean immediate) {
    return redoLogManager.isRolloverNeeded(immediate);
  }

  @Override
  protected File rollover(boolean force, boolean skipCheckpoint) {
    return redoLogManager.rollover(force, skipCheckpoint);
  }

  @Override
  protected void resetActiveOps() {
    redoLogManager.resetActiveOps();
  }

  @Override
  protected WriteLock acquireExclusiveLock() throws InterruptedException {
    return redoLogManager.acquireExclusiveLock();
  }

  @Override
  protected void releaseExclusiveLock(WriteLock exclusiveLock) {
    redoLogManager.releaseExclusiveLock(exclusiveLock);
  }

  @Override
  protected void signalFatalError(Throwable e) {
    redoLogManager.signalFatalError(e);
  }

  @Override
  protected LogWriter getLogWriter() {
    return redoLogManager.getLogWriter();
  }

  @Override
  public File getLogFile() {
    return redoLogManager.getLogFile();
  }

  @Override
  public File getArchiveDir() {
    return redoLogManager.getArchiveDir();
  }

  @Override
  public File getRolloverDestDir() {
    return redoLogManager.getRolloverDestDir();
  }

  @Override
  public LogWriter getCurrentLogWriter() {
    return redoLogManager.getCurrentLogWriter();
  }

  @Override
  public LogWriter createLogWriter(
      com.zimbra.cs.redolog.RedoLogManager redoMgr,
      File logfile,
      long fsyncIntervalMS
  ) {
    return redoLogManager.createLogWriter(redoMgr, logfile, fsyncIntervalMS);
  }

  @Override
  public boolean getInCrashRecovery() {
    return redoLogManager.getInCrashRecovery();
  }

  @Override
  public synchronized void start() {
    redoLogManager.start();
  }

  @Override
  public synchronized void stop() {
    redoLogManager.stop();
  }

  @Override
  public TransactionId getNewTxnId() {
    return redoLogManager.getNewTxnId();
  }

  @Override
  public void log(RedoableOp op, boolean synchronous) {
    redoLogManager.log(op, synchronous);
  }

  @Override
  public void commit(RedoableOp op) {
    RedoLogListenerManager redoLogListenerManager = redoLogListenerManagerRef.get();
    if( Objects.nonNull(redoLogListenerManager) ) {
      redoLogListenerManager.commit(new org.openzal.zal.redolog.op.RedoableOp(op));
    }
    redoLogManager.commit(op);
  }

  @Override
  public void abort(RedoableOp op) {
    redoLogManager.abort(op);
  }

  @Override
  public void flush() throws IOException {
    redoLogManager.flush();
  }

  @Override
  public File forceRollover() {
    return redoLogManager.forceRollover();
  }

  @Override
  public File forceRollover(boolean skipCheckpoint) {
    return redoLogManager.forceRollover();
  }

  @Override
  public RolloverManager getRolloverManager() {
    return redoLogManager.getRolloverManager();
  }

  @Override
  public long getCurrentLogSequence() {
    return redoLogManager.getCurrentLogSequence();
  }

  @Override
  public File[] getArchivedLogsFromSequence(long seq) throws IOException {
    return redoLogManager.getArchivedLogsFromSequence(seq);
  }

  @Override
  public File[] getArchivedLogs() throws IOException {
    return redoLogManager.getArchivedLogs();
  }

  @Override
  public Pair<Set<Integer>, CommitId> getChangedMailboxesSince(CommitId cid)
      throws IOException, MailServiceException {
    return redoLogManager.getChangedMailboxesSince(cid);
  }

  public ProxyRedoLogManager(com.zimbra.cs.redolog.RedoLogManager redoLogManager) {
    super(null, null, false);
    this.redoLogManager = redoLogManager;
    redoLogListenerManagerRef = new AtomicReference<>();
  }

  public void setListener(RedoLogListenerManager redoLogListenerManager) {
    redoLogListenerManagerRef.set(redoLogListenerManager);
  }

  public void removeListener() {
    redoLogListenerManagerRef.set(null);
  }
}
