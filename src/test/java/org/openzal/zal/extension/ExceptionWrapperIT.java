package org.openzal.zal.extension;

import com.zimbra.cs.mailbox.ZalZimbraSimulator;
import java.util.HashMap;
import org.junit.Rule;
import org.junit.Test;
import org.openzal.zal.Account;
import org.openzal.zal.Mailbox;
import org.openzal.zal.OperationContext;
import org.openzal.zal.exceptions.NoSuchItemException;

public class ExceptionWrapperIT
{
  @Rule
  public ZalZimbraSimulator mZimbraSimulator = new ZalZimbraSimulator();

  @Test (expected = NoSuchItemException.class)
  public void wrap_new_no_suchitem_exception() throws Exception
  {
    Account account = mZimbraSimulator.getProvisioning().createAccount("test", "iddddd", new HashMap<String, Object>());
    Mailbox mbox = mZimbraSimulator.getMailboxManager().getMailboxByAccount(account);
    OperationContext octxt = mbox.newOperationContext();
    mbox.getTagById(octxt,10);
  }
}