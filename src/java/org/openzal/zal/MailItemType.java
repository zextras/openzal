/*
 * ZAL - The abstraction layer for Zimbra.
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

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Set;
/* $if MajorZimbraVersion <= 7 $
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.index.MailboxIndex;
/* $endif$ */

public class MailItemType
{
  /* $if ZimbraVersion >= 8.0.0 $ */
  public static MailItemType CONTACT              = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.CONTACT);
  public static          MailItemType UNKNOWN              = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.UNKNOWN);
  public static          MailItemType FOLDER               = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.FOLDER);
  public static          MailItemType SEARCHFOLDER         = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.SEARCHFOLDER);
  public static          MailItemType TAG                  = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.TAG);
  public static          MailItemType CONVERSATION         = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.CONVERSATION);
  public static          MailItemType MESSAGE              = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.MESSAGE);
  public static          MailItemType DOCUMENT             = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.DOCUMENT);
  public static          MailItemType NOTE                 = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.NOTE);
  public static          MailItemType FLAG                 = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.FLAG);
  public static          MailItemType APPOINTMENT          = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.APPOINTMENT);
  public static          MailItemType VIRTUAL_CONVERSATION = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.VIRTUAL_CONVERSATION);
  public static          MailItemType MOUNTPOINT           = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.MOUNTPOINT);
  @Deprecated
  public static          MailItemType WIKI                 = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.WIKI);
  public static          MailItemType TASK                 = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.TASK);
  public static          MailItemType CHAT                 = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.CHAT);
  public static          MailItemType COMMENT              = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.COMMENT);
  public static          MailItemType LINK                 = new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.LINK);
  /* $else $
  public static MailItemType CONTACT              = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_CONTACT);
  public static MailItemType UNKNOWN              = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_UNKNOWN);
  public static MailItemType FOLDER               = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_FOLDER);
  public static MailItemType SEARCHFOLDER         = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_SEARCHFOLDER);
  public static MailItemType TAG                  = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_TAG);
  public static MailItemType CONVERSATION         = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_CONVERSATION);
  public static MailItemType MESSAGE              = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_MESSAGE);
  public static MailItemType DOCUMENT             = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_DOCUMENT);
  public static MailItemType NOTE                 = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_NOTE);
  public static MailItemType FLAG                 = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_FLAG);
  public static MailItemType APPOINTMENT          = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_APPOINTMENT);
  public static MailItemType VIRTUAL_CONVERSATION = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_VIRTUAL_CONVERSATION);
  public static MailItemType MOUNTPOINT           = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_MOUNTPOINT);
  @Deprecated public static MailItemType WIKI     = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_WIKI);
  public static MailItemType TASK                 = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_TASK);
  public static MailItemType CHAT                 = new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_CHAT);
  public static MailItemType COMMENT              = null;
  public static MailItemType LINK                 = null;
  /* $endif $ */

  /* $if ZimbraVersion >= 8.0.0 $ */
  private final com.zimbra.cs.mailbox.MailItem.Type mType;
  /* $else $
  private final byte mType;
  /* $endif $ */


  /* $if ZimbraVersion >= 8.0.0 $ */
  MailItemType(com.zimbra.cs.mailbox.MailItem.Type type)
  /* $else $
  MailItemType(byte type)
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
    Set<com.zimbra.cs.mailbox.MailItem.Type> typeList =
      com.zimbra.cs.mailbox.MailItem.Type.setOf(types.toUpperCase());
    Iterator<com.zimbra.cs.mailbox.MailItem.Type> it = typeList.iterator();

    int n = 0;
    result = new byte[typeList.size()];
    while (it.hasNext())
    {
      result[n] = Item.byteType(it.next());
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

  public static MailItemType of(String itemType)
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.of(itemType));
    /* $else $
    if("message".equals(itemType))
    {
      return new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_MESSAGE);
    }
    else if("contact".equals(itemType))
    {
      return new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_CONTACT);
    }
    else if("all".equals(itemType))
    {
      return new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_UNKNOWN);
    }
    else
    {
      return new MailItemType(com.zimbra.cs.mailbox.MailItem.TYPE_FOLDER);
    }
    /* $endif $ */
  }

  public String toString()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return mType.toString();
    /* $else $
    return com.zimbra.cs.mailbox.MailItem.getNameForType(mType);
    /* $endif $ */
  }

  public static String of(byte type)
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return new MailItemType(com.zimbra.cs.mailbox.MailItem.Type.of(type)).toString();
    /* $else $
    return com.zimbra.cs.mailbox.MailItem.getNameForType(type);
    /* $endif $ */
  }
}
