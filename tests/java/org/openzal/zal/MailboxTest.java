package org.openzal.zal;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class MailboxTest
{
  @Test
  public void reflection_initialization()
  {
    Mailbox mbox = new Mailbox(mock(com.zimbra.cs.mailbox.Mailbox.class));
  }
}