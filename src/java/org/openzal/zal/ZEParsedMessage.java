/*
 * ZAL - An abstraction layer for Zimbra.
 * Copyright (C) 2014 ZeXtras S.r.l.
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

import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;
import com.zimbra.cs.mime.ParsedMessage;
import org.jetbrains.annotations.NotNull;

import javax.mail.internet.MimeMessage;
import java.util.List;

public class ZEParsedMessage
{
  private com.zimbra.cs.mime.ParsedMessage mParsedMessage;

  protected ZEParsedMessage(@NotNull Object parsedMessage)
  {
    if ( parsedMessage == null )
    {
      throw new NullPointerException();
    }
    mParsedMessage = (com.zimbra.cs.mime.ParsedMessage)parsedMessage;
  }

  public ZEParsedMessage(byte[] rawData, boolean indexAttachments) throws ZimbraException
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

  public ZEParsedMessage(MimeMessage msg, boolean indexAttachments) throws ZimbraException
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

  public List<ZEMPartInfo> getMessageParts()
  {
    return ZimbraListWrapper.wrapMPartInfos(mParsedMessage.getMessageParts());
  }

  public MimeMessage getMimeMessage()
  {
    return mParsedMessage.getMimeMessage();
  }

  public boolean hasAttachments()
  {
    return mParsedMessage.hasAttachments();
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mParsedMessage);
  }
}

