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

import com.zimbra.cs.mime.ExpandMimeMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import java.io.IOException;
import java.util.List;

public class Mime
{
  @NotNull
  public static List<MPartInfo> getParts(MimeMessage mimeMessage) throws IOException, MessagingException
  {
    return ZimbraListWrapper.wrapMPartInfos(com.zimbra.cs.mime.Mime.getParts(mimeMessage));
  }

  @NotNull
  public static MPartInfo getTextBody(MimeMessage mimeMessage, boolean preferHtml)
    throws IOException, MessagingException
  {
    return new MPartInfo(com.zimbra.cs.mime.Mime.getTextBody(com.zimbra.cs.mime.Mime.getParts(mimeMessage),
                                                               preferHtml));
  }

  public static void recursiveRepairTransferEncoding(MimeMessage mimemessage1) throws IOException, MessagingException
  {
    com.zimbra.cs.mime.Mime.recursiveRepairTransferEncoding(mimemessage1);
  }

  @Nullable
  public static MimeMultipart getMultipartContent(MimePart multipartPart, String contentType)
    throws IOException, MessagingException
  {
    return com.zimbra.cs.mime.Mime.getMultipartContent(multipartPart, contentType);
  }

  @Nullable
  public static String getSubject(MimeMessage mimeMessage) throws MessagingException
  {
    return com.zimbra.cs.mime.Mime.getSubject(mimeMessage);
  }

  @Nullable
  public static MimePart getMimePart(MimePart mimePart, String part) throws IOException, MessagingException
  {
    return com.zimbra.cs.mime.Mime.getMimePart(mimePart, part);
  }

  @NotNull
  public static MimeMessage buildFixedMimeMessage(Session session)
  {
    return new com.zimbra.cs.mime.Mime.FixedMimeMessage(session);
  }

/*
  Apply zimbra own modifiers, for example it explode ms-tnef
*/
  public static MimeMessage expandMessage(MimeMessage original) throws MessagingException
  {
    try
    {
      ExpandMimeMessage expandMimeMessage = new ExpandMimeMessage(original);
      expandMimeMessage.expand();
      return expandMimeMessage.getExpanded();
    }
    catch (Throwable ex)
    {
      return original;
    }
  }

  public static boolean isMultipartSigned(String contentType)
  {
    return com.zimbra.cs.mime.Mime.isMultipartSigned(contentType);
  }

  public static boolean isEncrypted(String contentType)
  {
    return com.zimbra.cs.mime.Mime.isEncrypted(contentType);
  }

  public static boolean isPKCS7Signed(String contentType)
  {
    return com.zimbra.cs.mime.Mime.isPKCS7Signed(contentType);
  }
}
