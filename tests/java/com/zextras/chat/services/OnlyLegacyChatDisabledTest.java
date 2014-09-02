package com.zextras.chat.services;

import com.zextras.lib.switches.SwitchConditionNotification;
import com.zimbra.cs.account.Provisioning;
import org.openzal.zal.ZEServer;
import org.openzal.zal.lib.ZimbraVersion;
import org.openzal.zal.ZEProvisioning;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class OnlyLegacyChatDisabledTest
{
  private ZEProvisioning              mProvisioning;
  private ZEServer                    mServer;
  private boolean                     mXmppEnabled;
  private SwitchConditionNotification mConditionNotification;

  @Before
  public void setup() throws Exception
  {
    mXmppEnabled = true;
    ZEProvisioning provisioning = mock(ZEProvisioning.class);
    when(provisioning.toZimbra(Provisioning.class)).thenReturn(mock(Provisioning.class));
    mServer = new ZEServer(
      null,
      null,
      null,
      null,
      provisioning
    ) {
      @Override
      public boolean isXMPPEnabled()
      {
        return mXmppEnabled;
      }
    };
    mProvisioning = mock(ZEProvisioning.class);
    when(mProvisioning.getLocalServer()).thenReturn(mServer);
    mConditionNotification = new SwitchConditionNotification();
  }

  @Test
  public void on_to_off_always_returns_true() throws Exception
  {
    OnlyLegacyChatDisabled onlyLegacyChatDisabled = new OnlyLegacyChatDisabled(
      new ZimbraVersion(4, 0, 0),
      mProvisioning
    );

    assertTrue(onlyLegacyChatDisabled.onToOff());
    verifyNoMoreInteractions(mProvisioning);
  }

  @Test
  public void zimbra_8_always_returns_true()
  {
    OnlyLegacyChatDisabled onlyLegacyChatDisabled = new OnlyLegacyChatDisabled(
      new ZimbraVersion(8,0,0),
      mProvisioning
    );

    assertTrue(onlyLegacyChatDisabled.offToOn(mConditionNotification));
    verifyNoMoreInteractions(mProvisioning);
  }

  @Test
  public void zimbra_7_enabled_false_returns_true()
  {
    OnlyLegacyChatDisabled onlyLegacyChatDisabled = new OnlyLegacyChatDisabled(
      new ZimbraVersion(7,0,0),
      mProvisioning
    );

    mXmppEnabled = false;
    assertTrue(onlyLegacyChatDisabled.offToOn(mConditionNotification));
  }

  @Test
  public void zimbra_6_enabled_true_returns_false()
  {
    OnlyLegacyChatDisabled onlyLegacyChatDisabled = new OnlyLegacyChatDisabled(
      new ZimbraVersion(7,0,0),
      mProvisioning
    );

    mXmppEnabled = true;
    assertFalse(onlyLegacyChatDisabled.offToOn(mConditionNotification));
  }
}
