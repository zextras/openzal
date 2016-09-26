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

import com.zimbra.common.mime.ContentDisposition;
import com.zimbra.common.mime.ContentType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.internet.ParseException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class MPartInfo // implements Comparable<MPartInfo>
{
  private final com.zimbra.cs.mime.MPartInfo mMPartInfo;

  protected MPartInfo(@NotNull Object mPartInfo)
  {
    if (mPartInfo == null)
    {
      throw new NullPointerException();
    }
    mMPartInfo = (com.zimbra.cs.mime.MPartInfo) mPartInfo;
  }

  @NotNull
  public String getFilename()
  {
    String res = mMPartInfo.getFilename();
    return res == null ? "" : res;
  }

  @NotNull
  public String getDisposition()
  {
    String res = mMPartInfo.getDisposition();
    return res == null ? "" : res;
  }

  @Nullable
  public String getDispositionParameter(String name)
  {
    try
    {
      String headers[] = mMPartInfo.getMimePart().getHeader("Content-Disposition");
      if( headers == null || headers.length == 0 )
      {
        return null;
      }
      return new ContentDisposition(headers[0]).getParameter(name);
    }
    catch (MessagingException e)
    {
      return null;
    }
  }

  public MimePart getMimePart()
  {
    return mMPartInfo.getMimePart();
  }

  @Nullable
  public String getContentTypeParameter(String name)
  {
    return mMPartInfo.getContentTypeParameter(name);
  }

  public int getPartNum()
  {
    return mMPartInfo.getPartNum();
  }

  @Nullable
  public MPartInfo getParent()
  {
    com.zimbra.cs.mime.MPartInfo parent = mMPartInfo.getParent();
    if (parent != null)
    {
      return new MPartInfo(parent);
    }
    return null;
  }

  public List<MPartInfo> getChildren()
  {
    return ZimbraListWrapper.wrapMPartInfos(mMPartInfo.getChildren());
  }

  public boolean isFilterableAttachment()
  {
    return mMPartInfo.isFilterableAttachment();
  }

  public String getContentID()
  {
    return mMPartInfo.getContentID();
  }

  public String getContentType()
  {
    return mMPartInfo.getContentType();
  }

  @Override
  public String toString()
  {
    return mMPartInfo.toString();
  }


  public static boolean equals(Part mp1,Part mp2)
  {
    boolean same = false;

    try
    {
      if (mp1.getCount() == mp2.getCount())
      {
        int i = 0;
        while (i<mp1.getCount() && same)
        {
          BodyPart p1 = mp1.getBodyPart(i);
          BodyPart p2 = mp2.getBodyPart(i);

          String ct1 = p1.getContentType();
          String ct2 = p2.getContentType();

          try {
            //pr("CONTENT-TYPE: " + (new ContentType(ct)).toString());
            new javax.mail.internet.ContentType(ct1);
            new javax.mail.internet.ContentType(ct2);
          } catch (ParseException pex) {
            same = false;
          }
          String filename1 = p1.getFileName();
          String filename2 = p2.getFileName();

          same = filename1 != null && filename2 != null && filename1.equals(filename2);

          if (p1.isMimeType("text/plain") && p2.isMimeType("text/plain")) {
            same = (p1.getContent()).equals(p2.getContent());
          } else if (p1.isMimeType("multipart/*") && p2.isMimeType("multipart/*")) {
            Multipart mmp1 = (Multipart) p1.getContent();
            Multipart mmp2 = (Multipart) p2.getContent();
            int count = mmp1.getCount();
            for (int j = 0; j < count; j++)
              same = equals(mmp1.getBodyPart(j),mmp2.getBodyPart(j));

          } else if (p.isMimeType("message/rfc822")) {
            pr("This is a Nested Message");
            pr("---------------------------");
            mLevel++;
            dumpPart((Part) p.getContent());
            mLevel--;
          } else {
            Object o = p.getContent();
            if (o instanceof String) {
              pr("This is a string");
              pr("---------------------------");
              pr((String) o);
            } else if (o instanceof InputStream) {
              pr("This is just an input stream");
              pr("---------------------------");
              InputStream is = (InputStream) o;
              int c;
              while ((c = is.read()) != -1)
                pr(c);
            } else {
              pr("This is an unknown type");
              pr("---------------------------");
              pr(o.toString());
            }
          }

          i++;
        }
      }
    }
    catch (MessagingException e)
    {
      same = false;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    @Override
  public boolean equals(Object anObject)
  {
    if (anObject instanceof MPartInfo)
    {
      MPartInfo o = (MPartInfo)anObject;

//      sb.append("partName: ").append(mPartName).append(", ");
//      sb.append("contentType: ").append(mContentType).append(", ");
//      sb.append("size: ").append(mSize).append(", ");
//      sb.append("disposition: ").append(mDisposition).append(", ");
//      sb.append("filename: ").append(mFilename).append(", ");
//      sb.append("partNum: ").append(mPartNum).append(", ");
//      sb.append("isFilterableAttachment: ").append(mIsFilterableAttachment);
//      sb.append("isToplevelAttachment: ").append(mIsToplevelAttachment);

      if (    this.getContentType().equals(o.getContentType()) &&
              (this.getContentID() == o.getContentID() || this.getContentID() == null || o.getContentID() == null || this.getContentID().equals(o.getContentID())) &&
              (this.getDisposition() == o.getDisposition() || this.getDisposition() == null || o.getDisposition() == null || this.getDisposition().equals(o.getDisposition())) &&
              (this.getFilename() == o.getFilename() || this.getFilename() == null || o.getFilename() == null || this.getFilename().equals(o.getFilename())) &&
              this.getPartNum() == o.getPartNum() &&
              this.isFilterableAttachment() == o.isFilterableAttachment()
              )
      {
        try
        {
          Object o1 = this.getMimePart().getContent();
          Object o2 = o.getMimePart().getContent();
          if (o1 != o2)
          {
            /*if (o1 instanceof String && o2 instanceof String)
            {
              String s1 = (String) o1;
              String s2 = (String) o2;

              if (s1.isEmpty() && s2.isEmpty())
                return false;
            }
            else*/
            if (o1 instanceof MimeMultipart && o2 instanceof MimeMultipart)
            {
              MimeMultipart mp1 = (MimeMultipart)o1;
              MimeMultipart mp2 = (MimeMultipart)o2;

            }
            else if (o1 instanceof InputStream && o2 instanceof InputStream)
            {
              InputStream is1 = (InputStream) o1;
              InputStream is2 = (InputStream) o2;

              return IOUtils.contentEquals(is1,is2);
            }

            return o1.equals(o2);
          }
        }
        catch (IOException e)
        {
        }
        catch (MessagingException e)
        {
        }
      }
    }

    return false;
  }
}

