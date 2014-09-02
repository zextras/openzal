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

import com.zimbra.cs.mailbox.MailItem;

import java.util.Iterator;
import java.util.Set;
/* $if MajorZimbraVersion <= 7 $
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.index.MailboxIndex;
/* $endif$ */

public class ZEMailItemType
{
  /* $if ZimbraVersion >= 8.0.0 $ */
  public static ZEMailItemType CONTACT              = new ZEMailItemType(MailItem.Type.CONTACT);
  public static ZEMailItemType UNKNOWN              = new ZEMailItemType(MailItem.Type.UNKNOWN);
  public static ZEMailItemType FOLDER               = new ZEMailItemType(MailItem.Type.FOLDER);
  public static ZEMailItemType SEARCHFOLDER         = new ZEMailItemType(MailItem.Type.SEARCHFOLDER);
  public static ZEMailItemType TAG                  = new ZEMailItemType(MailItem.Type.TAG);
  public static ZEMailItemType CONVERSATION         = new ZEMailItemType(MailItem.Type.CONVERSATION);
  public static ZEMailItemType MESSAGE              = new ZEMailItemType(MailItem.Type.MESSAGE);
  public static ZEMailItemType DOCUMENT             = new ZEMailItemType(MailItem.Type.DOCUMENT);
  public static ZEMailItemType NOTE                 = new ZEMailItemType(MailItem.Type.NOTE);
  public static ZEMailItemType FLAG                 = new ZEMailItemType(MailItem.Type.FLAG);
  public static ZEMailItemType APPOINTMENT          = new ZEMailItemType(MailItem.Type.APPOINTMENT);
  public static ZEMailItemType VIRTUAL_CONVERSATION = new ZEMailItemType(MailItem.Type.VIRTUAL_CONVERSATION);
  public static ZEMailItemType MOUNTPOINT           = new ZEMailItemType(MailItem.Type.MOUNTPOINT);
  @Deprecated public static ZEMailItemType WIKI     = new ZEMailItemType(MailItem.Type.WIKI);
  public static ZEMailItemType TASK                 = new ZEMailItemType(MailItem.Type.TASK);
  public static ZEMailItemType CHAT                 = new ZEMailItemType(MailItem.Type.CHAT);
  public static ZEMailItemType COMMENT              = new ZEMailItemType(MailItem.Type.COMMENT);
  public static ZEMailItemType LINK                 = new ZEMailItemType(MailItem.Type.LINK);
  /* $else $
  public static ZEMailItemType CONTACT              = new ZEMailItemType(MailItem.TYPE_CONTACT);
  public static ZEMailItemType UNKNOWN              = new ZEMailItemType(MailItem.TYPE_UNKNOWN);
  public static ZEMailItemType FOLDER               = new ZEMailItemType(MailItem.TYPE_FOLDER);
  public static ZEMailItemType SEARCHFOLDER         = new ZEMailItemType(MailItem.TYPE_SEARCHFOLDER);
  public static ZEMailItemType TAG                  = new ZEMailItemType(MailItem.TYPE_TAG);
  public static ZEMailItemType CONVERSATION         = new ZEMailItemType(MailItem.TYPE_CONVERSATION);
  public static ZEMailItemType MESSAGE              = new ZEMailItemType(MailItem.TYPE_MESSAGE);
  public static ZEMailItemType DOCUMENT             = new ZEMailItemType(MailItem.TYPE_DOCUMENT);
  public static ZEMailItemType NOTE                 = new ZEMailItemType(MailItem.TYPE_NOTE);
  public static ZEMailItemType FLAG                 = new ZEMailItemType(MailItem.TYPE_FLAG);
  public static ZEMailItemType APPOINTMENT          = new ZEMailItemType(MailItem.TYPE_APPOINTMENT);
  public static ZEMailItemType VIRTUAL_CONVERSATION = new ZEMailItemType(MailItem.TYPE_VIRTUAL_CONVERSATION);
  public static ZEMailItemType MOUNTPOINT           = new ZEMailItemType(MailItem.TYPE_MOUNTPOINT);
  @Deprecated public static ZEMailItemType WIKI     = new ZEMailItemType(MailItem.TYPE_WIKI);
  public static ZEMailItemType TASK                 = new ZEMailItemType(MailItem.TYPE_TASK);
  public static ZEMailItemType CHAT                 = new ZEMailItemType(MailItem.TYPE_CHAT);
  public static ZEMailItemType COMMENT              = null;
  public static ZEMailItemType LINK                 = null;
  /* $endif $ */

  /* $if ZimbraVersion >= 8.0.0 $ */
  private final MailItem.Type mType;
  /* $else $
  private final byte mType;
  /* $endif $ */


  /* $if ZimbraVersion >= 8.0.0 $ */
  ZEMailItemType(MailItem.Type type)
  /* $else $
  ZEMailItemType(byte type)
  /* $endif $ */
  {
    mType = type;
  }

  public byte toByte()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return mType.toByte();
    /* $else $
    return mType;
    /* $endif $ */
  }

  public static byte[] typeArrayFrom(String types)
  {
    byte[] result;
    /* $if ZimbraVersion >= 8.0.0 $ */
    Set<MailItem.Type> typeList = MailItem.Type.setOf(types.toUpperCase());
    Iterator<MailItem.Type> it = typeList.iterator();

    int n=0;
    result = new byte[typeList.size()];
    while( it.hasNext() )
    {
      result[n] = ZEItem.byteType(it.next());
      n++;
    }
    /* $else $
    try
    {
      result = MailboxIndex.parseTypesString(types);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $endif $ */
    return result;
  }

  public static ZEMailItemType of(String itemType)
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return new ZEMailItemType(MailItem.Type.of(itemType));
    /* $else $
    if("message".equals(itemType))
    {
      return new ZEMailItemType(MailItem.TYPE_MESSAGE);
    }
    else if("contact".equals(itemType))
    {
      return new ZEMailItemType(MailItem.TYPE_CONTACT);
    }
    else if("all".equals(itemType))
    {
      return new ZEMailItemType(MailItem.TYPE_UNKNOWN);
    }
    else
    {
      return new ZEMailItemType(MailItem.TYPE_FOLDER);
    }
    /* $endif $ */
  }

  public String toString()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return mType.toString();
    /* $else $
    return MailItem.getNameForType(mType);
    /* $endif $ */
  }

  public static String of(byte type)
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return new ZEMailItemType(MailItem.Type.of(type)).toString();
    /* $else $
    return MailItem.getNameForType(type);
    /* $endif $ */
  }
}
