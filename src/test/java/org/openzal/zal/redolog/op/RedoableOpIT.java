package org.openzal.zal.redolog.op;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class RedoableOpIT
{
  @Test
  public void reflection_initialization()
  {
    RedoableOp redoableOp = new RedoableOp(mock(com.zimbra.cs.redolog.op.RedoableOp.class));
  }
}