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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import javax.mail.MessagingException;
import javax.mail.internet.MimePart;
import java.util.List;


public class MPartInfo
{
  private final com.zimbra.cs.mime.MPartInfo mMPartInfo;

  protected MPartInfo(@Nonnull Object mPartInfo)
  {
    if (mPartInfo == null)
    {
      throw new NullPointerException();
    }
    mMPartInfo = (com.zimbra.cs.mime.MPartInfo) mPartInfo;
  }

  @Nonnull
  public String getFilename()
  {
    String res = mMPartInfo.getFilename();
    return res == null ? "" : res;
  }

  @Nonnull
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
}

