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

import com.zimbra.cs.mailbox.MailItem;
import javax.annotation.Nonnull;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParsedContact
{
  @Nonnull private final com.zimbra.cs.mime.ParsedContact mParsedContact;

  protected ParsedContact(Object parsedContact)
  {
    mParsedContact = (com.zimbra.cs.mime.ParsedContact) parsedContact;
  }

  protected <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(mParsedContact);
  }

  public ParsedContact(Contact contact)
  {
    try
    {
      mParsedContact = new com.zimbra.cs.mime.ParsedContact(
        (com.zimbra.cs.mailbox.Contact) contact.toZimbra(MailItem.class)
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ParsedContact(Map<String, String> map, @Nonnull List<Contact.ContactAttachment> list)
  {
    List<com.zimbra.cs.mailbox.Contact.Attachment> attachments = new ArrayList<com.zimbra.cs.mailbox.Contact.Attachment>();
    for (Contact.ContactAttachment attachment : list)
    {
      attachments.add(attachment.toZimbra(com.zimbra.cs.mailbox.Contact.Attachment.class));
    }
    try
    {
      mParsedContact = new com.zimbra.cs.mime.ParsedContact(map, attachments);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ParsedContact(Map<String, String> fields)
  {
    try
    {
      mParsedContact = new com.zimbra.cs.mime.ParsedContact(fields);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ParsedContact(Map<String, String> fields, byte[] content)
  {
    try
    {
      mParsedContact = new com.zimbra.cs.mime.ParsedContact(fields, content);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<Contact.ContactAttachment> getAttachments()
  {
    return ZimbraListWrapper.wrapAttachments(mParsedContact.getAttachments());
  }

  public Map<String,String> getFields()
  {
    return mParsedContact.getFields();
  }
}

