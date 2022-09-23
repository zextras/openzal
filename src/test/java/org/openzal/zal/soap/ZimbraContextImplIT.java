package org.openzal.zal.soap;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ZimbraContextImplIT
{
  @Test
  public void reflection_initialization()
  {
    ZimbraContextImpl zimbraContext = new ZimbraContextImpl(new HashMap<String, Object>());
  }
}