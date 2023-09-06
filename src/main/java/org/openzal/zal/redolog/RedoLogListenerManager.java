package org.openzal.zal.redolog;

import org.openzal.zal.redolog.op.RedoableOp;

public interface RedoLogListenerManager {
  void register(RedoLogListener redoLogListner);
  void unregister(RedoLogListener redoLogListner);
  void commit(RedoableOp op);
}
