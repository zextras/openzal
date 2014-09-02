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

import com.zimbra.cs.mime.MPartInfo;
import org.jetbrains.annotations.NotNull;
import javax.mail.internet.MimePart;
import java.util.List;

public class ZEMPartInfo
{
  private final com.zimbra.cs.mime.MPartInfo mMPartInfo;

  protected ZEMPartInfo(@NotNull Object mPartInfo)
  {
    if ( mPartInfo == null )
    {
      throw new NullPointerException();
    }
    mMPartInfo = (com.zimbra.cs.mime.MPartInfo)mPartInfo;
  }

  public String getFilename()
  {
    return mMPartInfo.getFilename();
  }

  public String getDisposition()
  {
    return mMPartInfo.getDisposition();
  }

  public MimePart getMimePart()
  {
    return mMPartInfo.getMimePart();
  }

  public String getContentTypeParameter(String name)
  {
    return mMPartInfo.getContentTypeParameter(name);
  }

  public int getPartNum()
  {
    return mMPartInfo.getPartNum();
  }

  public ZEMPartInfo getParent()
  {
    MPartInfo parent = mMPartInfo.getParent();
    if (parent != null)
    {
      return new ZEMPartInfo(parent);
    }
    return null;
  }

  public List<ZEMPartInfo> getChildren()
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
}

