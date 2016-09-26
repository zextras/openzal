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

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Mime
{
  @NotNull
  public static List<MPartInfo> getParts(MimeMessage mimeMessage) throws IOException, MessagingException
  {
    List<MPartInfo> parts = ZimbraListWrapper.wrapMPartInfos(com.zimbra.cs.mime.Mime.getParts(mimeMessage));
//    if (partsDuplicated(parts))
//    {
//      throw new RuntimeException("Duplicated parts");
//    }


//    List<MPartInfo> bodyParts = new ArrayList<MPartInfo>();
//
//    Iterator<MPartInfo> it = parts.iterator();
//    while (it.hasNext())
//    {
//      MPartInfo info = it.next();
//      if (info.getMimePart().getContent() instanceof MimeMultipart)
//      {
//        //MimeMultipart mp = Mime.getMultipartContent(info.getMimePart(),"");
//        //bodyParts.add(mp.);
//        MimeMultipart mp = (MimeMultipart)info.getMimePart().getContent();
//        BodyPart part = mp.getBodyPart(0);
//
//        //bodyParts.add()
//        //ParsedMessage pm = new ParsedMessage( new MimeMessage(mp), true );
////        MimeMessage mime = new MimeMessage((Session)null);
////        mime.setContent(mp);
////
////        bodyParts = Mime.getParts(mime);
//        )
//      }
    //}

    return parts;
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

  public static boolean partsDuplicated(List<MPartInfo> parts)
  {
    boolean duplicated = false;
    Iterator<MPartInfo> it1 = parts.iterator();
    while (it1.hasNext() && !duplicated)
    {
      Iterator<MPartInfo> it2 = parts.iterator();
      MPartInfo ref = it1.next();
      boolean b = false;

      while (it2.hasNext() && !b) // skip until it2 reaches it
      {
        MPartInfo check = it2.next();
        b = ref == check;
      }

      while (it2.hasNext() && !duplicated)
      {
        MPartInfo check = it2.next();
        duplicated = ref.equals(check);
      }
    }

    return duplicated;
  }

  public static List<MPartInfo> partsDeDuplication(List<MPartInfo> parts)
  {
    List<MPartInfo> partsCleaned = new ArrayList<MPartInfo>();
    List<MPartInfo> partsDuplicated = new ArrayList<MPartInfo>();

    Iterator<MPartInfo> it1 = parts.iterator();
    while (it1.hasNext())
    {
      Iterator<MPartInfo> it2 = parts.iterator();
      MPartInfo ref = it1.next();
      boolean b = false;
      boolean duplicated = false;

      while (it2.hasNext() && !b) // skip until it2 reaches it
      {
        MPartInfo check = it2.next();
        b = ref == check;
      }

      while (it2.hasNext() && !duplicated)
      {
        MPartInfo check = it2.next();
        duplicated = ref.equals(check);
      }

      if (!duplicated)
      {
        if (!partsDuplicated.contains(ref))
          partsCleaned.add(ref);
      }
      else
        partsDuplicated.add(ref);
    }

    return partsCleaned;
  }
}
