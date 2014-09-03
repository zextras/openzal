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

import java.lang.reflect.*;
import java.io.*;
import java.util.*;

import org.openzal.zal.exceptions.*;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.lib.ZimbraVersion;
import com.zimbra.cs.mailbox.*;

import com.zimbra.cs.mailbox.MailItem;

import org.openzal.zal.log.ZimbraLog;
import org.jetbrains.annotations.NotNull;

/* $if MajorZimbraVersion >= 8 $ */
import com.zimbra.common.mailbox.Color;
/* $else$
import com.zimbra.cs.mailbox.MailItem.Color;
 $endif$ */

public class ZEItem implements Comparable<ZEItem>
{
  final MailItem mMailItem;

  public static final byte TYPE_UNKNOWN              = -1;
  public static final byte TYPE_FOLDER               = 1;
  public static final byte TYPE_SEARCHFOLDER         = 2;
  public static final byte TYPE_TAG                  = 3;
  public static final byte TYPE_CONVERSATION         = 4;
  public static final byte TYPE_MESSAGE              = 5;
  public static final byte TYPE_CONTACT              = 6;
  public static final byte TYPE_DOCUMENT             = 8;
  public static final byte TYPE_NOTE                 = 9;
  public static final byte TYPE_FLAG                 = 10;
  public static final byte TYPE_APPOINTMENT          = 11;
  public static final byte TYPE_VIRTUAL_CONVERSATION = 12;
  public static final byte TYPE_MOUNTPOINT           = 13;
  public static final byte TYPE_WIKI                 = 14; //DEPRECATED
  public static final byte TYPE_TASK                 = 15;
  public static final byte TYPE_CHAT                 = 16;
  public static final byte TYPE_COMMENT              = 17;
  public static final byte TYPE_LINK                 = 18;

  public static final String FN_INDEX_ID = "idx";
  private Long mBitmask;

  public ZEItem(@NotNull Object item)
  {
    if (item == null)
    {
      throw new NullPointerException();
    }
    mMailItem = (MailItem)item;
  }

  public ZEItem(ZEItem item)
  {
    mMailItem = item.mMailItem;
  }

  public int getId()
  {
    return mMailItem.getId();
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mMailItem);
  }

  public byte getType()
  {
    return byteType(mMailItem.getType());
  }

  static byte byteType(Object type)
  {
/* $if MajorZimbraVersion >= 8 $ */
    return ((MailItem.Type)type).toByte();
/* $else$
    return (Byte)type;
/* $endif$ */
  }

  public static <T> T convertType(Class<T> cls, byte type)
  {
/* $if MajorZimbraVersion >= 8 $ */
    return cls.cast(MailItem.Type.of(type));
/* $else$
    return cls.cast(type);
/* $endif$ */
  }

  /* $if MajorZimbraVersion >= 8 $ */
  static MailItem.Type convertType(byte type)
  {
    return MailItem.Type.of(type);
  }
/* $else$
  static byte convertType(byte type)
  {
    return type;
  }
 $endif$ */

  public static ZEItem constructItem(ZEMailbox mbox, ZEUnderlyingData data) throws ZimbraException
  {
    try
    {
      return new ZEItem(
        MailItem.constructItem(
          mbox.toZimbra(Mailbox.class),
          data.toZimbra(MailItem.UnderlyingData.class)
        )
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZEMailbox getMailbox()
  {
    return new ZEMailbox(mMailItem.getMailbox());
  }

  public int getMailboxId()
  {
    return (int) mMailItem.getMailboxId();
  }

  public String getDigest()
  {
    return mMailItem.getDigest();
  }

  public int getModifiedSequence()
  {
    return mMailItem.getModifiedSequence();
  }

  public boolean isTagged(int mask)
  {
    return (mMailItem.getFlagBitmask() & mask) != 0;
  }

  public Map<String, Object> getAttrsMap()
  {
    Map<String, Object> zimbraMetadata;
    try
    {
      zimbraMetadata = Utils.decode(encodeZimbraMetadata());
    }
    catch (ZimbraException e)
    {
      zimbraMetadata = new HashMap<String, Object>();
    }
    return zimbraMetadata;
  }

  public ZEMessage toMessage()
  {
    return new ZEMessage(mMailItem);
  }

  public ZEContact toContact()
  {
    return new ZEContact(mMailItem);
  }

  public ZEFolder toFolder()
  {
    return new ZEFolder(mMailItem);
  }

  public ZETag toTag()
  {
    return new ZETag(mMailItem);
  }

  public ZEDocument toDocument()
  {
    return new ZEDocument(mMailItem);
  }

  public ZETask toTask()
  {
    return new ZETask(mMailItem);
  }

  public ZEAppointment toAppointment()
  {
    return new ZEAppointment(mMailItem);
  }

  public ZEFlag toFlag()
  {
    return new ZEFlag(mMailItem);
  }

  public ZENote toNote()
  {
    return new ZENote(mMailItem);
  }

  public ZEChat toChat()
  {
    return new ZEChat(mMailItem);
  }

  public ZESearchFolder toSearchFolder()
  {
    return new ZESearchFolder(mMailItem);
  }

  public ZECalendarItem toCalendarItem()
  {
    return new ZECalendarItem(mMailItem);
  }

  public ZEMountpoint toMountpoint()
  {
    return new ZEMountpoint(mMailItem);
  }

  public ZEComment toComment()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return new ZEComment(mMailItem);
    /* $else$
    throw new UnsupportedOperationException();
    /* $endif$ */
  }

  public ZELink toLink()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return new ZELink(mMailItem);
    /* $else$
    throw new UnsupportedOperationException();
    /* $endif$ */
  }

  public long getTagBitmask()
  {
/* $ if ZimbraVersion >= 8.0.0 $ */
    throw new UnsupportedOperationException();
/* $else$
    return mMailItem.getTagBitmask();
$endif$ */
  }

  public long getBitmask()
  {
/* $ if ZimbraVersion >= 8.0.0 $ */
    return mMailItem.getFlagBitmask();
/* $else$
    return mMailItem.getBitmask();
$endif$ */
  }

  public static class ZECustomMetadata
  {
    private MailItem.CustomMetadata mCustomMetadata;

    public ZECustomMetadata(Object meta)
    {
      mCustomMetadata = (MailItem.CustomMetadata)meta;
    }

    public ZECustomMetadata(String key)
    {
      mCustomMetadata = new MailItem.CustomMetadata(key);
    }

    public <T> T toZimbra(Class<T> cls)
    {
      return cls.cast(mCustomMetadata);
    }

    public String get(String key)
    {
      return mCustomMetadata.get(key);
    }

    public String put(String key, String value)
    {
      return mCustomMetadata.put(key, value);
    }

  }

  public ZECustomMetadata getCustomData(String section) throws ZimbraException
  {
    try
    {
      return new ZECustomMetadata(mMailItem.getCustomData(section));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static class ZEUnderlyingData
  {
    private MailItem.UnderlyingData mUnderlyingData;

    public ZEUnderlyingData()
    {
      mUnderlyingData = new MailItem.UnderlyingData();
    }

    public ZEUnderlyingData(Object data)
    {
      mUnderlyingData = (MailItem.UnderlyingData)data;
    }

    public <T> T toZimbra(Class<T> cls)
    {
      return cls.cast(mUnderlyingData);
    }

    public void setFlag(int flag)
    {
/* $if MajorZimbraVersion >= 8 $ */
      mUnderlyingData.setFlags(flag | mUnderlyingData.getFlags());
/* $else$
      mUnderlyingData.flags |= flag;
  $endif$ */
    }

    public void unsetFlag(int flag)
    {
/* $if MajorZimbraVersion >= 8 $ */
      mUnderlyingData.setFlags((~flag) & mUnderlyingData.getFlags());
/* $else$
      mUnderlyingData.flags &= ~flag;
   $endif$ */
    }

    public boolean isSet(int flag)
    {
/* $if MajorZimbraVersion >= 8 $ */
      return (mUnderlyingData.getFlags() & flag) != 0;
/* $else$
      return (mUnderlyingData.flags & flag) != 0;
  $endif$ */
    }

    public String toString()
    {
      return mUnderlyingData.toString();
    }

    public byte getType()
    {
      return mUnderlyingData.type;
    }

    public String getName()
    {
      return mUnderlyingData.name;
    }

    public long getDate()
    {
      return mUnderlyingData.date;
    }

    public String getMetadata()
    {
      return mUnderlyingData.metadata;
    }
  }

  public ZEUnderlyingData getUnderlyingData()
  {
    return new ZEUnderlyingData(mMailItem.getUnderlyingData());
  }

  public static class ZEColor
  {
    public static final ZEColor BLUE   = new ZEColor(0x0000FFL);
    public static final ZEColor GREEN  = new ZEColor(0x00FF00L);
    public static final ZEColor ORANGE = new ZEColor(0xFF8000L);
    public static final ZEColor PURPLE = new ZEColor(0xBF00FFL);
    public static final ZEColor RED    = new ZEColor(0xFF0000L);
    public static final ZEColor YELLOW = new ZEColor(0xFFFF00L);

    private Color mColor;

    public ZEColor(Object color)
    {
      mColor = (Color)color;
    }

    public ZEColor(long color)
    {
      mColor = new Color(color);
    }

    public ZEColor(String color)
    {
      mColor = new Color(color);
    }

    public <T> T toZimbra(Class<T> cls)
    {
      return cls.cast(mColor);
    }
  }

  public ZEColor getColor()
  {
    return new ZEColor(mMailItem.getRgbColor());
  }

  public String getName()
  {
    String name = mMailItem.getName();
    return name.isEmpty() ? String.valueOf(getId()) : name;
  }

  public String getPath()
    throws ZimbraException
  {
    try
    {
      return mMailItem.getPath();
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public int getFolderId()
  {
    return mMailItem.getFolderId();
  }

  public int getParentId()
  {
    return mMailItem.getParentId();
  }

  public InputStream getContentStream() throws ZimbraException
  {
    try
    {
      return mMailItem.getContentStream();
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public byte[] getContent() throws ZimbraException
  {
    try
    {
      return mMailItem.getContent();
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public long getSize()
  {
    return mMailItem.getSize();
  }

  /* $if MajorZimbraVersion <= 7 $
    public static String getNameForType(byte type)
    {
      return MailItem.getNameForType( convertType(type) );
    }
    $else$ */
  public static String getNameForType(byte type)
  {
    return convertType(type).toString();
  }
/* $endif$ */

  public static String getNameForType(ZEItem item)
  {
    return getNameForType(item.getType());
  }

  private static final HashMap<String, String> sTypeMap = new HashMap<String, String>();

  /* $if MajorZimbraVersion <= 7 $
    static
    {
      sTypeMap.put("mountpoint", "remote folder");
      sTypeMap.put("searchfolder", "search folder");
      sTypeMap.put("mountpoint", "remote folder");
      sTypeMap.put("virtual_conversation","virtual conversation");
    }

    public static byte getTypeForName(String name)
    {
      if( sTypeMap.containsKey(name) ) {
        name = sTypeMap.get(name);
      }
      return byteType(MailItem.getTypeForName(name));
    }
    $else$ */
  static
  {
    sTypeMap.put("remote folder", "mountpoint");
    sTypeMap.put("search folder", "searchfolder");
    sTypeMap.put("virtual conversation", "virtual_conversation");
  }

  public static byte getTypeForName(String name)
  {
    if (sTypeMap.containsKey(name))
    {
      name = sTypeMap.get(name);
    }
    return byteType(MailItem.Type.of(name));
  }
/* $endif$ */

  public boolean inSpam()
  {
    return mMailItem.inSpam();
  }

  public String toString()
  {
    return String.valueOf(mMailItem.getId());
  }

  public boolean inTrash()
    throws NoSuchFolderException
  {
    try
    {
      return mMailItem.inTrash();
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public long getDate()
  {
    return mMailItem.getDate();
  }

  public int getFlagBitmask()
  {
    return mMailItem.getFlagBitmask();
  }

  public void unsetFlag(int flag)
  {
    getUnderlyingData().unsetFlag(flag);
  }

  public boolean isUnread()
  {
    return mMailItem.isUnread();
  }

  private static Method sDeserializeMethod;

  static
  {
    try
    {
      Class partypes[] = new Class[1];
      partypes[0] = Metadata.class;

      sDeserializeMethod = MailItem.UnderlyingData.class.getDeclaredMethod("deserialize", partypes);
      sDeserializeMethod.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
    }
  }

  public static ZEItem.ZEUnderlyingData decodeZimbraMetadata( ZimbraVersion originVersion, final String encodedString ) throws ZimbraException
  {
    try
    {
      Metadata meta = new Metadata(encodedString);
      if (originVersion != null)
      {
        if (ZimbraVersion.current.lessThan(8))
        {
          if (originVersion.isAtLeast(8))
          {
            meta.put("tg", "0");
          }
        }
      }

      ZEItem.ZEUnderlyingData underlyingData = new ZEItem.ZEUnderlyingData();
      Object parameters[] = new Object[1];
      parameters[0] = meta;

      sDeserializeMethod.invoke(
        underlyingData.toZimbra(MailItem.UnderlyingData.class),
        parameters
      );
      return underlyingData;
    }
    catch (Throwable ex)
    {
      throw new RuntimeException(ex);
    }
  }

  private static Method sSerializeMethod;

  static
  {
    try
    {
      Class partypes[] = new Class[0];
      sSerializeMethod = MailItem.UnderlyingData.class.getDeclaredMethod("serialize", partypes);
      sSerializeMethod.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
    }
  }

  public String encodeZimbraMetadata()
  {
    try
    {
      ZEItem.ZEUnderlyingData underlyingData = getUnderlyingData();
      Object parameters[] = new Object[0];
      Metadata meta = (Metadata) sSerializeMethod.invoke(
        underlyingData.toZimbra(MailItem.UnderlyingData.class),
        parameters
      );
      return meta.toString();
    }
    catch (Throwable ex)
    {
      ZimbraLog.mailbox.warn("Exception: " + Utils.exceptionToString(ex));
      return null;
    }
  }

  public String[] getTags()
  {
    /* $if MajorZimbraVersion >= 8 $ */
      return mMailItem.getTags();
    /* $else$
      throw new UnsupportedOperationException();
     $endif$ */
  }

  public String getSubject()
  {
    return mMailItem.getSubject();
  }

  public ZEAcl getEffectiveACL()
  {
/* $if MajorZimbraVersion >= 8 $ */
    return new ZEAcl(mMailItem.getEffectiveACL());
/* $else$
      throw new UnsupportedOperationException();
   $endif$ */
  }

  public long getChangeDate()
  {
    return mMailItem.getChangeDate();
  }

  @Override
  public int compareTo(ZEItem item)
  {
    return mMailItem.compareTo(item.toZimbra(MailItem.class));
  }
}
