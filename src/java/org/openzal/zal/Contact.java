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

import com.zimbra.cs.service.formatter.VCard;
import javax.annotation.Nullable;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import javax.annotation.Nonnull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contact extends Item
{
  private final com.zimbra.cs.mailbox.Contact mContact;

  public Contact(@Nonnull Object item)
  {
    super(item);
    mContact = (com.zimbra.cs.mailbox.Contact) item;
  }

  public static class ContactAttachment
  {
    private final com.zimbra.cs.mailbox.Contact.Attachment mAttachment;

    public ContactAttachment(@Nonnull com.zimbra.cs.mailbox.Contact.Attachment attachment)
    {
      if (attachment == null)
      {
        throw new NullPointerException();
      }
      mAttachment = attachment;
    }

    public ContactAttachment(byte[] content, String ctype, String field, String filename)
    {
      mAttachment = new com.zimbra.cs.mailbox.Contact.Attachment(content, ctype, field, filename);
    }

    protected <T> T toZimbra(Class<T> cls)
    {
      return cls.cast(mAttachment);
    }

    public String getContentType()
    {
      return mAttachment.getContentType();
    }

    public int getSize()
    {
      return mAttachment.getSize();
    }

    public byte[] getContent()
      throws IOException
    {
      return mAttachment.getContent();
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ContactAttachment that = (ContactAttachment) o;

      return mAttachment.equals(that.mAttachment);
    }

    @Override
    public int hashCode()
    {
      return mAttachment.hashCode();
    }
  }

  @Nonnull
  public Map<String, String> getFields()
  {
    return new HashMap<String, String>(
      mContact.getFields()
    );
  }

  @Nonnull
  public Map<String, String> getAllFields()
  {
    return mContact.getAllFields();
  }

  public String getSortName()
  {
    try
    {
      return mContact.getSortName();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<String> getEmailAddresses()
  {
    return mContact.getEmailAddresses();
  }

  public List<ContactAttachment> getAttachments()
  {
    return ZimbraListWrapper.wrapAttachments(mContact.getAttachments());
  }

  public String get(String key)
  {
    return mContact.get(key);
  }

  @Override
  public boolean equals(@Nullable Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    Contact zeContact = (Contact) o;

    if (mContact != null ? !mContact.equals(zeContact.mContact) : zeContact.mContact != null)
    {
      return false;
    }

    return true;
  }

  public InputStream toVCardInputStream()
    throws UnsupportedEncodingException
  {
    VCard vCard = VCard.formatContact(mContact);
    String formatted;
    formatted = vCard.getFormatted();
    return new ByteArrayInputStream(formatted.getBytes("UTF-8"));
  }

  @Override
  public int hashCode()
  {
    return mContact != null ? mContact.hashCode() : 0;
  }
}
