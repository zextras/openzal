package org.openzal.zal.soap;

import org.junit.Test;

import static org.junit.Assert.*;

public class InternalOverrideDocumentHandlerIT
{
  @Test
  public void reflection_initialization()
  {
    InternalOverrideDocumentHandler handler = new InternalOverrideDocumentHandler(null,null);
  }
}