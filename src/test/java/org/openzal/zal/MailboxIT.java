package org.openzal.zal;

import com.zimbra.cs.mailbox.ZimbraSimulator;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MailboxIT
{
  private ZimbraSimulator mZimbraSimulator;

  @Before
  public void setup() throws Exception {
    mZimbraSimulator = new ZimbraSimulator();
  }

  @After
  public void cleanup() throws Exception {
    mZimbraSimulator.cleanup();
  }

  @Test
  public void reflection_initialization()
  {
    com.zimbra.cs.mailbox.Mailbox.MailboxData data = new com.zimbra.cs.mailbox.Mailbox.MailboxData();
    com.zimbra.cs.mailbox.Mailbox zimbraMbox = new com.zimbra.cs.mailbox.Mailbox(data){};
    Mailbox mbox = new Mailbox(zimbraMbox);
  }
}