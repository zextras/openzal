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

package org.openzal.zal.calendar;

import com.zimbra.common.calendar.ZCalendar;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.calendar.CalendarMailSender;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.openzal.zal.Mime;
import org.openzal.zal.MimeConstants;
import org.openzal.zal.Pair;
import org.openzal.zal.Utils;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;

import javax.annotation.Nullable;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class CalendarMime
{
  private final PlainTextToHtmlConverter mPlainTextToHtmlConverter;

  public CalendarMime( PlainTextToHtmlConverter plainTextToHtmlConverter )
  {
    mPlainTextToHtmlConverter = plainTextToHtmlConverter;
  }

  /**
   * WARNING breaking this method WILL lead to a MAILBOX LOCK and
   * possibile SERVER LOCK due to an internal zimbra mechanism, BE CAREFUL
   */
  public MimeMessage createCalendarMessage(
    Invite inv,
    MimeMessage previousMimeMessage
  )
    throws ZimbraException, IOException, MessagingException
  {
    String subject = inv.getSubject();

    String desc;
    String descHtml;
    if( inv.descInMeta() )
    {
      desc = inv.getDescription();
      descHtml = inv.getDescriptionHtml();
    }
    else
    {
      Pair<String, String> descriptions = extractDescriptionFromMimeMessage(previousMimeMessage, inv.getMailItemId());
      desc = descriptions.getFirst();
      descHtml = descriptions.getSecond();
      if( descHtml == null )
      {
        descHtml = "";
      }
      inv.toZimbra(com.zimbra.cs.mailbox.calendar.Invite.class).setDescription(desc, descHtml);
    }
    ZCalendar.ZVCalendar cal = inv.newToICalendar(true);

    List<BodyPart> bodyPartList = extractAttachmentFromOriginalMime(previousMimeMessage, inv.getMailItemId());

    return createCalendarMessage(subject, desc, descHtml, cal, bodyPartList);
  }

  public MimeMessage createCalendarMessage(
          Invite inv
  )
          throws ZimbraException, IOException, MessagingException
  {
    String subject = inv.getSubject();
    String desc = inv.getDescription();
    String descHtml = inv.getDescriptionHtml();
    ZCalendar.ZVCalendar cal = inv.newToICalendar(true);

    MimeMessage attachment = inv.getAttachment();
    List<BodyPart> bodyPartList;
    if( Objects.nonNull(attachment) )
    {
      bodyPartList = extractAttachmentFromOriginalMime((MimeMultipart) attachment.getContent());
    }
    else
    {
      bodyPartList = Collections.emptyList();
    }
    return createCalendarMessage(subject, desc, descHtml, cal, bodyPartList);
  }

  private List<BodyPart> extractAttachmentFromOriginalMime(MimeMessage mimeMessage, int inviteId) throws MessagingException, IOException
  {
    MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
    MimeMessage subMimeMessage = (MimeMessage) mimeMultipart.getBodyPart(0).getContent();
    for( int n = 0; n < mimeMultipart.getCount(); ++n )
    {
      BodyPart part = mimeMultipart.getBodyPart(n);
      String[] headerInvId = part.getHeader("invId");
      if( headerInvId != null && headerInvId.length > 0 && headerInvId[0].equals(String.valueOf(inviteId)) )
      {
        subMimeMessage = (MimeMessage) part.getContent();
      }
    }
    MimeMultipart subMultipart = (MimeMultipart)subMimeMessage.getContent();
    return extractAttachmentFromOriginalMime(subMultipart);
  }

  private List<BodyPart> extractAttachmentFromOriginalMime(MimeMultipart subMultipart) throws MessagingException
  {
    List<BodyPart> bodyPartList = new LinkedList<>();
    for( int n=0; n < subMultipart.getCount(); ++n )
    {
      BodyPart bodyPart = subMultipart.getBodyPart(n);
      boolean isAttachment = false;

      String contentDispositions = bodyPart.getDisposition();
      if( contentDispositions != null && contentDispositions.equals(Part.ATTACHMENT) )
      {
        isAttachment = true;
      }

      if( bodyPart.isMimeType("application/*") || bodyPart.isMimeType("image/*") )
      {
        isAttachment = true;
      }

      if( isAttachment )
      {
        bodyPartList.add(bodyPart);
      }
    }
    return bodyPartList;
  }

  private Pair<String, String> extractDescriptionFromMimeMessage(MimeMessage mimeMessage, int inviteId) throws MessagingException, IOException
  {
    String descriptionTextPlain = null;
    String descriptionHtml = null;
    try
    {
      MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
      MimeMessage subMimeMessage = (MimeMessage) mimeMultipart.getBodyPart(0).getContent();
      for( int n = 0; n < mimeMultipart.getCount(); ++n )
      {
        BodyPart part = mimeMultipart.getBodyPart(n);
        String[] headerInvId = part.getHeader("invId");
        if( headerInvId != null && headerInvId.length > 0 && headerInvId[0].equals(String.valueOf(inviteId)) )
        {
          subMimeMessage = (MimeMessage) part.getContent();
        }
      }
      descriptionTextPlain = com.zimbra.cs.mailbox.calendar.Invite.getDescription(subMimeMessage, "text/plain");
      descriptionHtml = com.zimbra.cs.mailbox.calendar.Invite.getDescription(subMimeMessage, "text/html");
    }
    catch( Exception e )
    {
      throw new ZimbraException("Unable to retrieve calendar item description", e);
    }
    return new Pair<>(descriptionTextPlain, descriptionHtml);
  }

  private MimeMessage createCalendarMessage(
    @Nullable String subject,
    String desc,
    String descHtml,
    ZCalendar.ZVCalendar cal,
    List<BodyPart> bodyPartList
  )
    throws ZimbraException
  {
    try
    {
      MimeMessage mimeMessage = Mime.buildFixedMimeMessage(Utils.getSmtpSession());
      MimeMultipart alternativeMultipart = createAlternativePart(cal, desc, descHtml);
      MimeMultipart mixedMultipart = createMixedPart(alternativeMultipart, bodyPartList);

      mimeMessage.setContent(mixedMultipart);

      if (subject != null && !subject.isEmpty())
      {
        mimeMessage.setSubject(subject, MimeConstants.P_CHARSET_UTF8);
      }

      mimeMessage.setSentDate(new Date());
      mimeMessage.saveChanges();

      return mimeMessage;
    }
    catch (MessagingException e)
    {
      throw new ZimbraException(
        "MessagingException while re-creating invite mime message", e
      );
    }
  }

  private MimeMultipart createMixedPart(
    MimeMultipart alternativeMultipart,
    List<BodyPart> bodyPartList
  )
    throws MessagingException
  {
    MimeMultipart mixed = new MimeMultipart("mixed");

    MimeBodyPart wrapperOfMultipartAlternative = new MimeBodyPart();
    wrapperOfMultipartAlternative.setContent(alternativeMultipart);

    mixed.addBodyPart(wrapperOfMultipartAlternative);
    for( BodyPart bodyPart : bodyPartList )
    {
      mixed.addBodyPart(bodyPart);
    }

    return mixed;
  }

  private MimeMultipart createAlternativePart(ZCalendar.ZVCalendar cal, String desc, String descHtml)
    throws MessagingException, ZimbraException
  {
    MimeMultipart multipart = new MimeMultipart("alternative");

    cal.addDescription(desc, null);

    MimeBodyPart textPart = new MimeBodyPart();
    textPart.setText(desc, MimeConstants.P_CHARSET_UTF8);
    multipart.addBodyPart(textPart);

    if (descHtml == null || descHtml.isEmpty())
    {
      descHtml = mPlainTextToHtmlConverter.plainText2HTML(desc);
    }

    if (!descHtml.isEmpty())
    {
      MimeBodyPart htmlPart = new MimeBodyPart();
      ContentType ct = new ContentType(MimeConstants.CT_TEXT_HTML);
      ct.setParameter(MimeConstants.P_CHARSET, MimeConstants.P_CHARSET_UTF8);
      htmlPart.setText(descHtml, MimeConstants.P_CHARSET_UTF8);
      htmlPart.setHeader("Content-Type", ct.toString());

      multipart.addBodyPart(htmlPart);
    }

    MimeBodyPart icalPart;
    try {
      icalPart = CalendarMailSender.makeICalIntoMimePart(cal);
    }
    catch (ServiceException ex)
    {
      throw ExceptionWrapper.wrap(ex);
    }
    multipart.addBodyPart(icalPart);

    return multipart;
  }
}
