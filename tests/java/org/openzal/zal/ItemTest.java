package org.openzal.zal;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ItemTest
{
  @Test
  public void reflection_initialization()
  {
    Item item = new Item(mock(com.zimbra.cs.mailbox.MailItem.class));
  }
}