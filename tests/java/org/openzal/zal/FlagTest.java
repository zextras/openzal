package org.openzal.zal;

import org.junit.Test;

public class FlagTest
{

/*
  Cannot mock this class, using a constant still trigger the static{} block
*/
  @Test
  public void reflection_initialization()
  {
    int test = Flag.BITMASK_FROM_ME;
  }
}