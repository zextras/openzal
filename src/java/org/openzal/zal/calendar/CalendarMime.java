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

package org.openzal.zal.calendar;

import com.zimbra.common.service.ServiceException;
import org.openzal.zal.Utils;
import org.openzal.zal.ZEMime;
import org.openzal.zal.ZEMimeConstants;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import com.zimbra.cs.mailbox.calendar.CalendarMailSender;

/* $if MajorZimbraVersion <= 7 $
import com.zimbra.cs.mailbox.calendar.TimeZoneMap;
import com.zimbra.cs.mailbox.calendar.ZRecur.ZWeekDay;
import com.zimbra.cs.mailbox.calendar.ZCalendar.ICalTok;
import com.zimbra.cs.mailbox.calendar.ZCalendar;
import com.zimbra.cs.mailbox.calendar.ZCalendar.ZComponent;
import com.zimbra.cs.mailbox.calendar.ZCalendar.ZProperty;
import com.zimbra.cs.mailbox.calendar.ZCalendar.ZVCalendar;
   $else$ */
import com.zimbra.common.calendar.*;
/* $endif$ */


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
    ZEInvite inv,
    MimeMessage previousMimeMessage
  )
    throws ZimbraException, IOException, MessagingException
  {
    String subject = inv.getSubject();
    String desc = inv.getDescription();
    String descHtml = inv.getDescriptionHtml();
    ZCalendar.ZVCalendar cal = inv.newToICalendar(true);

    List<BodyPart> bodyPartList = extractAttachmentFromOriginalMime(previousMimeMessage);

    return createCalendarMessage(subject, desc, descHtml, cal, bodyPartList);
  }

  private List<BodyPart> extractAttachmentFromOriginalMime(MimeMessage mimeMessage) throws MessagingException, IOException
  {
    MimeMultipart mimeMultipart = (MimeMultipart)mimeMessage.getContent();
    MimeMessage subMimeMessage = (MimeMessage)mimeMultipart.getBodyPart(0).getContent();
    MimeMultipart subMultipart = (MimeMultipart)subMimeMessage.getContent();

    List<BodyPart> bodyPartList = new LinkedList<BodyPart>();
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

  private MimeMessage createCalendarMessage(
    String subject,
    String desc,
    String descHtml,
    ZCalendar.ZVCalendar cal,
    List<BodyPart> bodyPartList
  )
    throws ZimbraException
  {
    try
    {
      MimeMessage mimeMessage = ZEMime.buildFixedMimeMessage(Utils.getSmtpSession());
      MimeMultipart alternativeMultipart = createAlternativePart(cal, desc, descHtml);
      MimeMultipart mixedMultipart = createMixedPart(alternativeMultipart, bodyPartList);

      mimeMessage.setContent(mixedMultipart);

      if (subject != null && !subject.isEmpty())
      {
        mimeMessage.setSubject(subject, ZEMimeConstants.P_CHARSET_UTF8);
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
    textPart.setText(desc, ZEMimeConstants.P_CHARSET_UTF8);
    multipart.addBodyPart(textPart);

    MimeBodyPart htmlPart = new MimeBodyPart();
    if (descHtml == null || descHtml.isEmpty())
    {
      descHtml = mPlainTextToHtmlConverter.plainText2HTML(desc);
    }

    ContentType ct = new ContentType(ZEMimeConstants.CT_TEXT_HTML);
    ct.setParameter(ZEMimeConstants.P_CHARSET, ZEMimeConstants.P_CHARSET_UTF8);
    htmlPart.setText(descHtml, ZEMimeConstants.P_CHARSET_UTF8);
    htmlPart.setHeader("Content-Type", ct.toString());

    multipart.addBodyPart(htmlPart);

    MimeBodyPart icalPart;
    try {
  /* $if ZimbraVersion <= 7.1.0 $
      icalPart = CalendarMailSender.makeICalIntoMimePart(null, cal);
  /* $else$ */
      icalPart = CalendarMailSender.makeICalIntoMimePart(cal);
  /* $endif $ */
    }
    catch (ServiceException ex)
    {
      throw ExceptionWrapper.wrap(ex);
    }
    multipart.addBodyPart(icalPart);

    return multipart;
  }
}
