package org.openzal.zal;

import com.zimbra.cs.mailbox.ZalZimbraSimulator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.*;

public class QuotaProviderIT
{
  private ZalZimbraSimulator mZimbraSimulator;
  private Account                   mUser;

  @Before
  public void setup()
    throws Exception
  {
    mZimbraSimulator = new ZalZimbraSimulator();
    mUser = mZimbraSimulator.getProvisioning().createAccount("user@example.com", "", new HashMap<>());
  }

  @After
  public void cleanup() throws Exception
  {
    mZimbraSimulator.cleanup();
  }

  @Test
  public void register_and_unregister()
  {
    AdditionalQuotaProvider additionalQuotaProvider = mock(AdditionalQuotaProvider.class);
    mZimbraSimulator.getMailboxManager().registerAdditionalQuotaProvider(additionalQuotaProvider);

    Mailbox mailbox = mZimbraSimulator.getMailboxManager().getMailboxByAccount(mUser);
    reset(additionalQuotaProvider);

    mailbox.getSize();
    verify(additionalQuotaProvider, times(1)).getAdditionalQuota(any(Mailbox.class));

    reset(additionalQuotaProvider);
    mZimbraSimulator.getMailboxManager().removeAdditionalQuotaProvider(additionalQuotaProvider);

    mailbox.getSize();
    verifyNoInteractions(additionalQuotaProvider);
  }
}