package com.zextras.newchat.history;

import com.zextras.chat.properties.ChatProperties;
import com.zextras.newchat.ChatMessage;
import com.zextras.newchat.address.SpecificAddress;
import org.openzal.zal.ZEAccount;
import org.openzal.zal.ZEMailboxManager;
import org.openzal.zal.ZEProvisioning;
import org.openzal.zal.ZEProvisioningSimulator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ImHistoryQueueHandlerTest
{
  private ImHistoryQueueHandler mHandler;
  private LinkedList<ImHistoryItem> mItems;

  @Before
  public void init() throws InterruptedException
  {
    mItems = new LinkedList<ImHistoryItem>();
    ZEProvisioning prov = new ZEProvisioningSimulator();
    Map<String, Object> attrs = new HashMap<String, Object>();
    attrs.put(ZEProvisioning.A_mail, "mailTo");
    ZEAccount account = new ZEProvisioningSimulator.AccountSimulator("account", "1", attrs, null, prov);
    ChatMessage chat = Mockito.mock(ChatMessage.class);
    SpecificAddress sender = new SpecificAddress("mailFrom");
    SpecificAddress recipient = new SpecificAddress("mailTo");

    for (int i = 0; i < 10; i++)
    {
      ImHistoryItem msg = Mockito.mock(ImHistoryItem.class);
      Mockito.when(msg.getAccount()).thenReturn(account);
      Mockito.when(msg.getChat()).thenReturn(chat);
      Mockito.when(chat.getSender()).thenReturn(sender);
      Mockito.when(chat.getMessageTo()).thenReturn(recipient);
      mItems.add(msg);
    }

    ImHistoryQueue queue = Mockito.mock(ImHistoryQueue.class);
    ZEMailboxManager mboxManager = Mockito.mock(ZEMailboxManager.class);

    HistoryMailManagerFactory historyMailManagerFactory = Mockito.mock(HistoryMailManagerFactory.class);
    ChatProperties chatProperties = Mockito.mock(ChatProperties.class);

    mHandler = new ImHistoryQueueHandler(mboxManager, historyMailManagerFactory, chatProperties, queue);
  }

  @Test
  public void build_conversation()
  {
    Collection<Conversation> conversations = mHandler.buildConversations(mItems);
    assertEquals(1, conversations.size());
  }
}
