package com.zextras.newchat.history;

import com.zextras.lib.log.ZELog;
import com.zextras.newchat.ChatMessage;
import com.zextras.newchat.address.SpecificAddress;
import org.openzal.zal.ZEAccount;
import org.openzal.zal.ZEProvisioningSimulator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ImHistoryQueueTest
{
  ImHistoryQueue mImHistoryQueue;
  ZEAccount mAccount;
  Handler mHandler;
  static int mI;
  static Conversation mConversation;
  List<ChatMessage> mMessages = new LinkedList<ChatMessage>();

  @Before
  public void init()
  {
    mConversation = new Conversation(mAccount, new SpecificAddress("account"));
    ZEProvisioningSimulator prov = new ZEProvisioningSimulator();
    mAccount = new ZEProvisioningSimulator.AccountSimulator("account", "1", null, null, prov);
    ZELog log = Mockito.mock(ZELog.class);
    ImHistoryQueueHandlerFactory factory = Mockito.mock(ImHistoryQueueHandlerFactory.class);
    mHandler = new Handler();
    Mockito.when(factory.create(mImHistoryQueue)).thenReturn(Mockito.mock(ImHistoryQueueHandler.class));
    mImHistoryQueue = new ImHistoryQueue(factory);
  }

  @Test
  public void history_handler_get_messages() throws InterruptedException
  {
    Thread thread = new Thread(mHandler);
    thread.start();
    for (mI = 0; mI < 10; mI++)
    {
      ChatMessage message = Mockito.mock(ChatMessage.class);
      Mockito.when(message.getBody()).thenReturn(String.valueOf(mI));
      mMessages.add(message);
      mImHistoryQueue.addMessage(message, mAccount);
      Thread.sleep(50);
    }

    thread.join();
    assertEquals(mMessages, mConversation.getMessages());
  }

  class Handler implements Runnable
  {
    @Override
    public void run() {
      try {
        while (mConversation.getMessages().size() < 10) {
          List<ImHistoryItem> items = mImHistoryQueue.getMessages();
          for (ImHistoryItem item : items)
          {
            mConversation.add(item.getChat());
          }
        }
      } catch (InterruptedException e) {
      }
    }
  }
}
