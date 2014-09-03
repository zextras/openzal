package com.zextras.newchat;

import org.openzal.zal.*;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

public class ChatMessageWriterTest
{
  private ZEMailbox               mMbox;
  private String                  mHtml;
  private String                  mPlainText;
  private Date                    mConversationDate;
  private ZEOperationContext      mOperationContext;
  private ZEAccount               mAccount;
  private ZEProvisioningSimulator mProvisioning;
  private ChatMessageWriter       mChatMessageWriter;
  private ZEMailbox               mZimbraMbox;

  @Before
  public void Setup() throws IOException {
    mProvisioning = new ZEProvisioningSimulator();
    mProvisioning.addUser("user@example.com");
    mAccount = mProvisioning.getAccountByName("user@example.com");
    mMbox = mock(ZEMailbox.class);

    mHtml = "<html><body>Hello!!</body></html>";
    mPlainText = "Hello!!";
    mConversationDate = new Date(500000L);

    when(mMbox.getAccount()).thenReturn(mAccount);
    when(mMbox.createChat(Mockito.any(ZEOperationContext.class),
                          Mockito.any(ZEParsedMessage.class),
                          Mockito.anyInt(),
                          Mockito.anyInt())).thenReturn(null);

    mOperationContext = new ZEOperationContext(mAccount);
    mChatMessageWriter = new ChatMessageWriter(mMbox, mOperationContext, mHtml, mPlainText, mConversationDate);
  }

  @Test
  public void updateChat() throws IOException, MessagingException
  {
    MimeMessage mime = loadBase64File("/mobile/history_mime_01");

    ZEChat chat = mock(ZEChat.class);

    when(chat.getMimeMessage()).thenReturn(mime);
    mChatMessageWriter.updateExistingChat(chat);

    ArgumentCaptor<ZEParsedMessage> captor = ArgumentCaptor.forClass(ZEParsedMessage.class);
    verify(mMbox).updateChat(Mockito.any(ZEOperationContext.class), captor.capture(), Mockito.anyInt());

    ZEParsedMessage pm = captor.getValue();
    MimeMultipart multipart = (MimeMultipart) pm.getMimeMessage().getContent();

    assertEquals(mPlainText, multipart.getBodyPart(0).getContent().toString());
    assertEquals(mHtml,multipart.getBodyPart(1).getContent().toString());
    assertTrue(pm.getMimeMessage().getContentType().startsWith("multipart/alternative") );
  }

  @Test
  public void makeChat() throws IOException, MessagingException
  {

    mChatMessageWriter.createNewChat("tommy@example.com", "1");

    ArgumentCaptor<ZEParsedMessage> captor = ArgumentCaptor.forClass(ZEParsedMessage.class);
    verify(mMbox).createChat(Mockito.any(ZEOperationContext.class),
                             captor.capture(),
                             Mockito.anyInt(),
                             Mockito.anyInt());

    ZEParsedMessage pm = captor.getValue();
    MimeMultipart multipart = (MimeMultipart) pm.getMimeMessage().getContent();

    assertEquals(mPlainText,multipart.getBodyPart(0).getContent().toString());
    assertEquals(mHtml,multipart.getBodyPart(1).getContent().toString());
  }


  private MimeMessage loadBase64File(String s) throws IOException, MessagingException
  {
    File file = new File("it/data",s);
    FileInputStream stream = null;
    try
    {
      stream = new FileInputStream(file);
      return new MimeMessage(null, stream);
    }
    finally
    {
      IOUtils.closeQuietly(stream);
    }
  }
}
