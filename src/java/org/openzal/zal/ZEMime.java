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

import com.zimbra.cs.mime.Mime;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import java.io.IOException;
import java.util.List;

public class ZEMime
{
  public static List<ZEMPartInfo> getParts(MimeMessage mimeMessage) throws IOException, MessagingException
  {
    return ZimbraListWrapper.wrapMPartInfos(Mime.getParts(mimeMessage));
  }

  public static ZEMPartInfo getTextBody(MimeMessage mimeMessage, boolean preferHtml)
    throws IOException, MessagingException
  {
    return new ZEMPartInfo(Mime.getTextBody(Mime.getParts(mimeMessage), preferHtml));
  }

  public static void recursiveRepairTransferEncoding(MimeMessage mimemessage1) throws IOException, MessagingException
  {
    Mime.recursiveRepairTransferEncoding(mimemessage1);
  }

  public static MimeMultipart getMultipartContent(MimePart multipartPart, String contentType)
    throws IOException, MessagingException
  {
    return Mime.getMultipartContent(multipartPart, contentType);
  }

  public static String getSubject(MimeMessage mimeMessage) throws MessagingException
  {
    return Mime.getSubject(mimeMessage);
  }

  public static MimePart getMimePart(MimePart mimePart, String part) throws IOException, MessagingException
  {
    return Mime.getMimePart(mimePart, part);
  }

  public static MimeMessage buildFixedMimeMessage(Session session)
  {
    return new Mime.FixedMimeMessage(session);
  }
}
