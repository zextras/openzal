package com.zextras.newchat;

import org.openzal.zal.*;
import org.openzal.zal.lib.ActualClock;
import org.openzal.zal.lib.Clock;
import com.zextras.newchat.history.Conversation;
import com.zextras.newchat.history.HistoryMailManager;
import org.junit.Before;
import org.junit.Test;

import com.zextras.chat.ConversationBuilder;
import com.zextras.newchat.address.SpecificAddress;
import com.zextras.newchat.db.providers.UserProvider;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


import static org.mockito.Mockito.mock;

public class HistoryMailManagerTest
{
  private ZEAccount               mAccount;
  private ZEOperationContext      mOperationContext;
  private ZEProvisioningSimulator mProvisioning;
  private ChatMessage             mChatMessage;
  private HistoryMailManager      mHistoryMailManager;
  private ZEMailbox               mMbox;
  private ZEChat                  mChat;
  private UserProvider            mUserProvider;

  @Before
  public void setup() throws Exception
  {
    long currentTime = 50000L;
    mChatMessage = new ChatMessage(
      new SpecificAddress("sender@example.com"),
      new SpecificAddress("target@example.com"),
      new Date(50000L)
    );
    mChatMessage.setBody("[10:00] sender@example.com: Hi, how are you? \n Hi, how are you2?");
    mChatMessage.setHtmlBody(
      "<html>\n" +
        "\t<style>\n" +
        "\t\tbody {}\n" +
        "\t\tp {}\n" +
        "\t\t.ZxChat_msg_user { display: table-cell; position: relative; text-align: left; float: left; bottom: 0; }\n" +
        "\t\t.ZxChat_msg_date { display: table-cell; position: relative; text-align: left; float: right; color: #989F9F; bottom: 0; font-weight: normal; }\n" +
        "\t\t.ZxChat_msg_line { display: table-row; font-weight:bold; position: relative; width: 100%; text-align: left;}\n" +
        "\t\t.ZxChat_msg_text { display: table-cell; text-indent: 0px; position: relative; text-align: left; font-family: cursive; font-size: small; font-style: italic; float: center;}\n" +
        "\t\t.ZxChat_msg_container { display: table; width: 100%; }\n" +
        "\t\t.ZxChat_msg_container_first { display: table; border-top: 1px solid #989F9F; width: 100%;}\n" +
        "\t\t.ZxChat_msg_text p, .ZxChat_msg_text h1, .ZxChat_msg_text h2, .ZxChat_msg_text h3, .ZxChat_msg_text h4, .ZxChat_msg_text h5, .ZxChat_msg_text h6, .ZxChat_msg_text ul, .ZxChat_msg_text ol { margin: 0; }\n" +
      "\t\t.ZxChat_msg_text ul, .ZxChat_msg_text ol { padding-left: 2em; }\n" +
      "\t\t.ZxChat_msg_text ul li, .ZxChat_msg_text ol li {}\n" +
      ".ZxChat_msg_date { color: #989F9F; width: 35px; text-align: center; }\n" +
      ".ZxChat_msg_user { font-weight:bold; width: 128px; text-align: right; }\n" +
      ".ZxChat_msg_text { left: 163px; position: absolute; text-align: left; }\n" +
      "\t</style>\n" +
      "\t<body>\n" +
      "\t\t<div class=\"ZxChat_msg_container\">\n" +
      "\t\t\t<div class=\"ZxChat_msg_line\">\t\t\t<div class=\"ZxChat_msg_user\">sender@example.com:</div>\n" +
      "\t\t\t<div class=\"ZxChat_msg_text\">\n" +
      "\t\t\t\tHi, how are you?\n" +
      "\t\t\t</div>\n" +
      "\t\t\t<div class=\"ZxChat_msg_date\">10:00\n" +
      "\t\t\t</div>\n" +
      "\n" +
      "\t\t\t</div>\n" +
      "\t\t\t<div class=\"ZxChat_msg_line\">\t\t\t<div class=\"ZxChat_msg_user\">admin@example.com:</div>\n" +
      "\t\t\t<div class=\"ZxChat_msg_text\">\n" +
      "\t\t\t\tHi, how are you2?\n" +
      "\t\t\t</div>\n" +
      "\t\t\t<div class=\"ZxChat_msg_date\">10:00\n" +
      "\t\t\t</div>\n" +
      "\n" +
      "\t\t\t</div>\n" +
      "\t\t</div>\n" +
      "\t</body>\n" +
      "</html>\t");

    InternetAddress from = new InternetAddress("sender@example.com");
    InternetAddress to = new InternetAddress("target@example.com");

    MimeMessage mmsg = new MimeMessage(javax.mail.Session.getInstance(new Properties()));
    mmsg.setFrom(from);
    mmsg.setRecipient(Message.RecipientType.TO, to);
    mmsg.setSubject("ZxChat - " + from.getPersonal());
    mmsg.setSentDate(new Date(50000L));

    Multipart mp = new MimeMultipart("alternative");
    MimeBodyPart textPart = new MimeBodyPart();
    textPart.setHeader("Content-Type", "text/plain; charset=utf-8");
    textPart.setContent(mChatMessage.getBody(), "text/plain; charset=utf-8");

    MimeBodyPart pixPart = new MimeBodyPart();
    pixPart.setHeader("Content-Type", "text/html; charset=utf-8");
    pixPart.setContent(mChatMessage.getBody(), "text/html; charset=utf-8");

    mp.addBodyPart(textPart);
    mp.addBodyPart(pixPart);

    mmsg.setContent(mp);
    mmsg.setSender(from);
    mmsg.setRecipient(Message.RecipientType.TO, to);
    mmsg.setSentDate(new Date(currentTime));
    mmsg.addHeader("ZxChat-History-Version", "1");

    Clock clock = Mockito.mock(ActualClock.class);
    when(clock.now()).thenReturn(currentTime);

    mChat = mock(ZEChat.class);

    when(mChat.getMimeMessage()).thenReturn(mmsg);
    when(mChat.getSender()).thenReturn("sender@example.com");
    when(mChat.getChangeDate()).thenReturn(50000L);
    when(mChat.getFolderId()).thenReturn(ZEMailbox.ID_FOLDER_IM_LOGS);
    when(mChat.getSubject()).thenReturn("ZxChat - recipient@example.com");
    when(mChat.toChat()).thenReturn(mChat);

    List<ZEItem> itemList = new ArrayList<ZEItem>();
    itemList.add(mChat);

    ZEFolder folder = mock(ZEFolder.class);
    when(folder.getMailboxId()).thenReturn(0);

    mProvisioning = new ZEProvisioningSimulator();
    mProvisioning.addUser("sender1@example.com");
    mAccount = mProvisioning.getAccountByName("sender1@example.com");

    mMbox = mock(ZEMailbox.class);
    when(mMbox.getMessagesByConversation(Mockito.<ZEOperationContext>any(), Mockito.anyInt())).thenReturn(new ArrayList<ZEMessage>());
    when(mMbox.getFolderByName(Mockito.<ZEOperationContext>any(), Mockito.anyString(), Mockito.anyInt())).thenReturn(folder);
    when(mMbox.getAccount()).thenReturn(mAccount);
    when(mMbox.getItemList(Mockito.any(byte.class), Mockito.any(ZEOperationContext.class))).thenReturn(itemList);

    mUserProvider = mock(UserProvider.class);

    mOperationContext = new ZEOperationContext(mAccount);
    mHistoryMailManager = new HistoryMailManager(mMbox, mOperationContext, mUserProvider);
  }

  @Test
  public void updateChatMessageWithExistingChat() throws Exception
  {
    HistoryMailManager history = spy(mHistoryMailManager);
    doReturn(new Date(5000L)).when(history).getCurrentDate();

    SpecificAddress recipient = new SpecificAddress("recipient@example.com");
    SpecificAddress accountAddress = new SpecificAddress(mAccount.getMail());

    Relationship relationship = new Relationship(0, recipient, Relationship.RelationshipType.ACCEPTED, "nickname!!", "zimbra");
    User user = Mockito.mock(User.class);
    when( user.hasRelationship(recipient) ).thenReturn(true);
    when( user.getRelationship(recipient) ).thenReturn(relationship);

    when(mUserProvider.getUser(accountAddress)).thenReturn( user );

    Conversation conversation = new Conversation(mAccount, recipient);
    conversation.add(mChatMessage);
    history.updateHistoryMail(conversation);

    ArgumentCaptor<ConversationBuilder> captor = ArgumentCaptor.forClass(ConversationBuilder.class);
    verify(history).createMessageWriter(captor.capture());


    ConversationBuilder conversationBuilder = captor.getValue();

    assertEquals("<html>\n" +
      "\t<style>\n" +
      "\t\tbody {}\n" +
      "\t\tp {}\n" +
      "\t\t.ZxChat_msg_user { display: table-cell; position: relative; bottom: 0; }\n" +
      "\t\t.ZxChat_msg_date { display: table-cell; width: 5%; position: relative; color: #989F9F; bottom: 0; font-weight: normal; padding-right:5px;}\n" +
      "\t\t.ZxChat_msg_line { display: table-row; position: relative; width: 100%; }\n" +
      "\t\t.ZxChat_msg_text { display: table-cell; width: 85%; text-indent: 0px; position: relative; padding-left: 10px;}\n" +
      "\t\t.ZxChat_msg_container { display: table; width: 100%; }\n" +
      "\t\t.ZxChat_msg_container_first { display: table; border-top: 1px solid #989F9F; width: 100%;}\n" +
      "\t\t.ZxChat_msg_text p, .ZxChat_msg_text h1, .ZxChat_msg_text h2, .ZxChat_msg_text h3, .ZxChat_msg_text h4, .ZxChat_msg_text h5, .ZxChat_msg_text h6, .ZxChat_msg_text ul, .ZxChat_msg_text ol { margin: 0; }\n" +
      "\t\t.ZxChat_msg_text ul, .ZxChat_msg_text ol { padding-left: 2em; }\n" +
      "\t\t.ZxChat_msg_text ul li, .ZxChat_msg_text ol li {}\n" +
      ".ZxChat_msg_date { color: #989F9F; text-align: left; }\n" +
      ".ZxChat_msg_user { font-weight:bold; text-align: right; }\n" +
      ".ZxChat_msg_text { text-align: left; }\n" +
      "\t</style>\n" +
      "\t<body>\n" +
      "\t\t<div class=\"ZxChat_msg_container_first\">\n" +
      "\t\t\t<div class=\"ZxChat_msg_line\">\t\t\t<div class=\"ZxChat_msg_date\">01:00\n" +
      "\t\t\t</div>\n" +
      "\t\t\t<div class=\"ZxChat_msg_user\">Me:</div>\n" +
      "\t\t\t<div class=\"ZxChat_msg_text\">\n" +
      "\t\t\t\t Hi, how are you? <br> Hi, how are you2?\n" +
      "\t\t\t</div>\n" +
      "\n" +
      "\t\t\t</div>\n" +
      "\t\t</div>\n" +
      "\t</body>\n" +
      "</html>",
      conversationBuilder.getHTMLMessage().replaceAll("sender@example.com", "Me")
    );
  }


  @Test
  public void createNewChatMessageHistory() throws Exception
  {
    HistoryMailManager history = spy(mHistoryMailManager);
    doReturn(null).when(history).findMessage(Mockito.<Date>any(), Mockito.<SpecificAddress>any());

    SpecificAddress recipient = new SpecificAddress("recipient@example.com");
    SpecificAddress accountAddress = new SpecificAddress(mAccount.getMail());

    Relationship relationship = new Relationship(0, recipient, Relationship.RelationshipType.ACCEPTED, "nickname!!", "zimbra");
    User user = Mockito.mock(User.class);
    when( user.hasRelationship(recipient) ).thenReturn(true);
    when( user.getRelationship(recipient) ).thenReturn(relationship);

    when(mUserProvider.getUser(accountAddress)).thenReturn( user );

    Conversation conversation = new Conversation(mAccount, recipient);
    conversation.add(mChatMessage);
    history.updateHistoryMail(conversation);

    ArgumentCaptor<ConversationBuilder> captor = ArgumentCaptor.forClass(ConversationBuilder.class);
    verify(history).createMessageWriter(captor.capture());


    ConversationBuilder conversationBuilder = captor.getValue();

    assertEquals("<html>\n" +
      "\t<style>\n" +
      "\t\tbody {}\n" +
      "\t\tp {}\n" +
      "\t\t.ZxChat_msg_user { display: table-cell; position: relative; bottom: 0; }\n" +
      "\t\t.ZxChat_msg_date { display: table-cell; width: 5%; position: relative; color: #989F9F; bottom: 0; font-weight: normal; padding-right:5px;}\n" +
      "\t\t.ZxChat_msg_line { display: table-row; position: relative; width: 100%; }\n" +
      "\t\t.ZxChat_msg_text { display: table-cell; width: 85%; text-indent: 0px; position: relative; padding-left: 10px;}\n" +
      "\t\t.ZxChat_msg_container { display: table; width: 100%; }\n" +
      "\t\t.ZxChat_msg_container_first { display: table; border-top: 1px solid #989F9F; width: 100%;}\n" +
      "\t\t.ZxChat_msg_text p, .ZxChat_msg_text h1, .ZxChat_msg_text h2, .ZxChat_msg_text h3, .ZxChat_msg_text h4, .ZxChat_msg_text h5, .ZxChat_msg_text h6, .ZxChat_msg_text ul, .ZxChat_msg_text ol { margin: 0; }\n" +
      "\t\t.ZxChat_msg_text ul, .ZxChat_msg_text ol { padding-left: 2em; }\n" +
      "\t\t.ZxChat_msg_text ul li, .ZxChat_msg_text ol li {}\n" +
      ".ZxChat_msg_date { color: #989F9F; text-align: left; }\n" +
      ".ZxChat_msg_user { font-weight:bold; text-align: right; }\n" +
      ".ZxChat_msg_text { text-align: left; }\n" +
      "\t</style>\n" +
      "\t<body>\n" +
      "\t\t<div class=\"ZxChat_msg_container\">\n" +
      "\t\t\t<div class=\"ZxChat_msg_line\">\t\t\t<div class=\"ZxChat_msg_date\">01:00\n" +
      "\t\t\t</div>\n" +
      "\t\t\t<div class=\"ZxChat_msg_user\">Me:</div>\n" +
      "\t\t\t<div class=\"ZxChat_msg_text\">\n" +
      "\t\t\t\t Hi, how are you? <br> Hi, how are you2?\n" +
      "\t\t\t</div>\n" +
      "\n" +
      "\t\t\t</div>\n" +
      "\t\t</div>\n" +
      "\t</body>\n" +
      "</html>",
      conversationBuilder.getHTMLMessage().replaceAll("sender@example.com", "Me")
    );
  }

}
