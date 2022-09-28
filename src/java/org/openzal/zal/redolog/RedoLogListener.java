package org.openzal.zal.redolog;

import org.openzal.zal.redolog.op.RedoableOp;

public interface RedoLogListener {
  void commit(RedoableOp op);
}
