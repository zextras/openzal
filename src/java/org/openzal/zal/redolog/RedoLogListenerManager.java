package org.openzal.zal.redolog;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.openzal.zal.Utils;
import org.openzal.zal.log.ZimbraLog;
import org.openzal.zal.redolog.op.RedoableOp;

public class RedoLogListenerManager {
  private final Set<RedoLogListener> listeners;

  public RedoLogListenerManager() {
    listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());
  }

  public void register(RedoLogListener redoLogListner) {
    listeners.add(redoLogListner);
  }

  public void unregister(RedoLogListener redoLogListner) {
    listeners.remove(redoLogListner);
  }

  public void commit(RedoableOp op) {
    listeners.forEach(l -> {
      try {
        l.commit(op);
      } catch (Exception e) {
        ZimbraLog.mailbox.error(Utils.exceptionToString(e));
      }
    });
  }
}
