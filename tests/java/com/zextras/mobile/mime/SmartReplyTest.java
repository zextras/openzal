package com.zextras.mobile.mime;


import com.zextras.mobile.SyncContext;
import com.zextras.mobile.ZESession;
import com.zextras.mobile.easfilter.EasVersion;
import com.zextras.mobile.syncop.SmartReply;
import org.openzal.zal.*;
import org.openzal.zal.exceptions.ZimbraException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SmartReplyTest
{
  private ZEMailbox mMBox;
  private ZEOperationContext mZContext;
  private SyncContext mContext;
  private ZEProvisioningSimulator mProvisioning;

  @Before
  public void setup() throws ZimbraException, IOException, MessagingException
  {
    mProvisioning = new ZEProvisioningSimulator();
    mProvisioning.addUser("sender@example.com");
    ZEAccount account = mProvisioning.getAccountByName("sender@example.com");

    MimeMessage mime = loadBase64File("/mobile/Empty_mime");

    mMBox = mock(ZEMailbox.class);
    mZContext = mock(ZEOperationContext.class);
    mContext = mock(SyncContext.class);

    //when(account.getAttr(anyString(),anyString())).thenReturn("");

    when(mMBox.getAccount()).thenReturn(account);

    ZEMessage zeMessage = mock(ZEMessage.class);
    when(zeMessage.getMimeMessage()).thenReturn(mime);

    when(mMBox.getMessageById((ZEOperationContext) anyObject(), anyInt())).thenReturn(zeMessage);

    ZESession mSession = mock(ZESession.class);
    when(mSession.supportedVersion((EasVersion) anyObject())).thenReturn(false);

    when(mContext.getSession()).thenReturn(mSession);
    when(mContext.getItemId()).thenReturn("0");
  }

  @Test
  public void inlineMergeMessage() throws IOException, MessagingException, ZimbraException
  {
    SmartReply smartReply = new SmartReply(mProvisioning);

    MimeMessage newMime = loadBase64File("/mobile/bugzilla_zimbra_mime");
    newMime = smartReply.inlineMergeMessages(
      mContext,
      mMBox,
      mZContext,
      newMime,
      false
    );

    String excepted = loadBase64File("/mobile/bugzilla_zimbra_mime").getContent().toString() + "\r\n\r\n" + "";

    Multipart multipartMixed = (Multipart)newMime.getDataHandler().getContent();
    Multipart multipartAlternative = (Multipart)multipartMixed.getBodyPart(0).getContent();
    String result = multipartAlternative.getBodyPart(1).getContent().toString();

    assertEquals(
      excepted,
      result
    );
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
