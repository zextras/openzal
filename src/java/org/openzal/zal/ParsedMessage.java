/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2016 ZeXtras S.r.l.
 *
 * This file is part of ZAL.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZAL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.openzal.zal;

import com.zimbra.common.service.ServiceException;
import java.io.InputStream;
import java.util.List;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;

import javax.annotation.Nonnull;
import javax.mail.internet.MimeMessage;

/**
 * A parsed message is a wrapper of a {@link MimeMessage} that contains the option to perform automatic indexing of
 * possible attachments. It is used with the {@link Mailbox} operations.
 * <h1>Usage examples</h1>
 * An example of editing a message and saving it is:
 * <pre>
 *   Message message = userMailbox.getMessageById(operationContext, Integer.parseInt(messageId.get()));
 *   MimeMessage mimeMessage = message.getMimeMessage();
 *   // Modify the mimeMessage as you wish...
 *   message = userMailbox.saveDraft(
 *    operationContext,
 *    new ParsedMessage(mimeMessage, true), // Save the message again, wrapping it in a ParsedMessage
 *    Integer.parseInt(messageId.get())
 *   );
 * </pre>
 *
 * @see MimeMessage
 * @see Mailbox
 * @see com.zimbra.cs.mime.ParsedMessage
 */
public class ParsedMessage
{
  private com.zimbra.cs.mime.ParsedMessage mParsedMessage;

  protected ParsedMessage(@Nonnull Object parsedMessage)
  {
    if ( parsedMessage == null )
    {
      throw new NullPointerException();
    }
    mParsedMessage = (com.zimbra.cs.mime.ParsedMessage)parsedMessage;
  }

  public ParsedMessage(byte[] rawData, boolean indexAttachments) throws ZimbraException
  {
    try
    {
      mParsedMessage = new com.zimbra.cs.mime.ParsedMessage(rawData, indexAttachments);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  /**
   * Creates a new message from a {@link MimeMessage}, with the option of indexing the passed attachments.
   * This triggers {@link MimeMessage#parse(InputStream)} to set internal headers and DataSources
   *
   * @param msg              the {@link MimeMessage} to wrapper
   * @param indexAttachments flag option to signal the {@link Mailbox} if it needs to index the attachments
   * @throws ZimbraException if something Zimbra-related goes wrong
   */
  public ParsedMessage(MimeMessage msg, boolean indexAttachments) throws ZimbraException
  {
    try
    {
      mParsedMessage = new com.zimbra.cs.mime.ParsedMessage(msg, indexAttachments);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<MPartInfo> getMessageParts()
  {
    return ZimbraListWrapper.wrapMPartInfos(mParsedMessage.getMessageParts());
  }

  public void generateInternalIndexing()
  {
    try
    {
      mParsedMessage.analyzeFully();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
  public MimeMessage getMimeMessage()
  {
    return mParsedMessage.getMimeMessage();
  }

  public boolean hasAttachments()
  {
    return mParsedMessage.hasAttachments();
  }

  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(mParsedMessage);
  }
}

