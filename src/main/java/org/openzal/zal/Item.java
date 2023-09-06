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

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.ACL;
import com.zimbra.cs.mailbox.MailItem;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.NoSuchFolderException;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.lib.ZimbraVersion;
import org.openzal.zal.log.ZimbraLog;
import org.openzal.zal.lucene.document.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class Item implements Comparable<Item>
{
  final MailItem mMailItem;

  public static final byte TYPE_UNKNOWN              = -1;
  public static final byte TYPE_FOLDER               = 1;
  public static final byte TYPE_SEARCHFOLDER         = 2;
  public static final byte TYPE_TAG                  = 3;
  public static final byte TYPE_CONVERSATION         = 4;
  public static final byte TYPE_MESSAGE              = 5;
  public static final byte TYPE_CONTACT              = 6;
  public static final byte TYPE_FLAG                 = 10;
  public static final byte TYPE_APPOINTMENT          = 11;
  public static final byte TYPE_VIRTUAL_CONVERSATION = 12;
  public static final byte TYPE_MOUNTPOINT           = 13;
  public static final byte TYPE_CHAT                 = 16;

  // FIXME clone of Item.UnderlyingData.FIELD_INDEX_ID
  public static final String FN_INDEX_ID = "idx";

  public Item(Object item)
  {
    mMailItem = (MailItem) Objects.requireNonNull(item);
  }

  public Item(@Nonnull Item item)
  {
    mMailItem = item.mMailItem;
    Objects.requireNonNull(mMailItem);
  }

  public int getId()
  {
    return mMailItem.getId();
  }

  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(mMailItem);
  }

  public byte getType()
  {
    return byteType(mMailItem.getType());
  }

  static byte byteType(@Nonnull Object type)
  {
    return ((MailItem.Type)type).toByte();
  }

  public static <T> T convertType(@Nonnull Class<T> cls, byte type)
  {
    return cls.cast(MailItem.Type.of(type));
  }

  static MailItem.Type convertType(byte type)
  {
    return MailItem.Type.of(type);
  }

  public int getVersion()
  {
    return mMailItem.getVersion();
  }

  @Nonnull
  public static Item constructItem(@Nonnull Mailbox mbox, @Nonnull UnderlyingData data) throws ZimbraException
  {
    try
    {
      return new Item(
        MailItem.constructItem(
          mbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
          data.toZimbra(MailItem.UnderlyingData.class)
        )
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
  public static Item constructItem(@Nonnull Mailbox mbox, @Nonnull UnderlyingData data, boolean skipCache)
    throws ZimbraException
  {
    try
    {
      return new Item(
        MailItem.constructItem(
          mbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
          data.toZimbra(MailItem.UnderlyingData.class),
          skipCache
        )
      );
    }
    catch( com.zimbra.common.service.ServiceException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
  public Mailbox getMailbox()
  {
    return new Mailbox(mMailItem.getMailbox());
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

  public int getSavedSequence()
  {
    return mMailItem.getSavedSequence();
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

  @Nonnull
  public Message toMessage()
  {
    return new Message(mMailItem);
  }

  @Nonnull
  public Contact toContact()
  {
    return new Contact(mMailItem);
  }

  @Nonnull
  public Folder toFolder()
  {
    return new Folder(mMailItem);
  }

  @Nonnull
  public Tag toTag()
  {
    return new Tag(mMailItem);
  }

  @Nonnull
  public Task toTask()
  {
    return new Task(mMailItem);
  }

  @Nonnull
  public Appointment toAppointment()
  {
    return new Appointment(mMailItem);
  }

  @Nonnull
  public Flag toFlag()
  {
    return new Flag(mMailItem);
  }

  @Nonnull
  public Chat toChat()
  {
    return new Chat(mMailItem);
  }

  @Nonnull
  public SearchFolder toSearchFolder()
  {
    return new SearchFolder(mMailItem);
  }

  @Nonnull
  public CalendarItem toCalendarItem()
  {
    return new CalendarItem(mMailItem);
  }

  @Nonnull
  public Mountpoint toMountpoint()
  {
    return new Mountpoint(mMailItem);
  }

  @Nullable
  public MailboxBlob getBlob()
  {
    try
    {
      com.zimbra.cs.store.MailboxBlob blob = mMailItem.getBlob();
      if (blob == null)
      {
        return null;
      }
      return new MailboxBlobWrap(blob);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
  public String getBlobPath() throws IOException
  {
    try
    {
      return mMailItem.getBlob().getLocalBlob().getPath();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static class CustomMetadata
  {
    private MailItem.CustomMetadata mCustomMetadata;

    public CustomMetadata(Object meta)
    {
      mCustomMetadata = (MailItem.CustomMetadata)meta;
    }

    public CustomMetadata(String key)
    {
      mCustomMetadata = new MailItem.CustomMetadata(key);
    }

    public <T> T toZimbra(@Nonnull Class<T> cls)
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

    public boolean containsKey(String key)
    {
      return mCustomMetadata.containsKey(key);
    }

    public String remove(String key)
    {
      return mCustomMetadata.remove(key);
    }

    public int size()
    {
      return mCustomMetadata.size();
    }

    public Set<String> keySet()
    {
      return mCustomMetadata.keySet();
    }
  }

  @Nullable
  public CustomMetadata getCustomData(String section) throws ZimbraException
  {
    try
    {
      MailItem.CustomMetadata customData = mMailItem.getCustomData(section);
      if (customData == null)
      {
        return null;
      }

      return new CustomMetadata(customData);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static class UnderlyingData
  {
    public static final String FN_ID           = "id";
    public static final String FN_TYPE         = "tp";
    public static final String FN_PARENT_ID    = "pid";
    public static final String FN_FOLDER_ID    = "fid";
    public static final String FN_PREV_FOLDER  = "pfid";
    public static final String FN_INDEX_ID     = "idx";
    public static final String FN_IMAP_ID      = "imap";
    public static final String FN_LOCATOR      = "loc";
    public static final String FN_BLOB_DIGEST  = "dgst";
    public static final String FN_DATE         = "dt";
    public static final String FN_SIZE         = "sz";
    public static final String FN_UNREAD_COUNT = "uc";
    public static final String FN_FLAGS        = "fg";
    public static final String FN_TAGS         = "tg";
    public static final String FN_SUBJECT      = "sbj";
    public static final String FN_NAME         = "nm";
    public static final String FN_METADATA     = "meta";
    public static final String FN_MOD_METADATA = "modm";
    public static final String FN_MOD_CONTENT  = "modc";
    public static final String FN_DATE_CHANGED = "dc";

    private MailItem.UnderlyingData mUnderlyingData;

    public UnderlyingData()
    {
      mUnderlyingData = new MailItem.UnderlyingData();
    }

    public UnderlyingData(Object data)
    {
      mUnderlyingData = (MailItem.UnderlyingData)data;
    }

    public UnderlyingData(Metadata metadata)
    {
      this();
      try
      {
        deserialize(metadata);
      }
      catch( ServiceException e )
      {
        throw ExceptionWrapper.wrap(e);
      }
    }

    public void deserialize(Metadata metadata)
      throws ServiceException
    {
      mUnderlyingData.deserialize(metadata.toZimbra(com.zimbra.cs.mailbox.Metadata.class));
    }

    public <T> T toZimbra(@Nonnull Class<T> cls)
    {
      return cls.cast(mUnderlyingData);
    }

    public void setFlag(int flag)
    {
      mUnderlyingData.setFlags(flag | mUnderlyingData.getFlags());
    }

    public void unsetFlag(int flag)
    {
      mUnderlyingData.setFlags((~flag) & mUnderlyingData.getFlags());
    }

    public boolean isSet(int flag)
    {
      return (mUnderlyingData.getFlags() & flag) != 0;
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

    public int getParentId()
    {
      return mUnderlyingData.parentId;
    }

    public int getFolderId()
    {
      return mUnderlyingData.folderId;
    }

    public int getIndexId()
    {
      return mUnderlyingData.indexId;
    }

    public int getImapId()
    {
      return mUnderlyingData.imapId;
    }

    public long getSize()
    {
      return mUnderlyingData.size;
    }

    public String getLocator()
    {
      return mUnderlyingData.locator;
    }


    public String getBlobDigest()
    {
      return mUnderlyingData.getBlobDigest();
    }

    public int getUnreadCount()
    {
      return mUnderlyingData.unreadCount;
    }

    public int getFlags()
    {
      return mUnderlyingData.getFlags();
    }

    public String[] getTags()
    {
      return mUnderlyingData.getTags();
    }

    public String getSubject()
    {
      return mUnderlyingData.getSubject();
    }

    public int getModMetadata()
    {
      return mUnderlyingData.modMetadata;
    }

    public int getChangeDate()
    {
      return mUnderlyingData.dateChanged;
    }

    public int getModContent()
    {
      return mUnderlyingData.modContent;
    }

    public String getUuid()
    {
      return mUnderlyingData.uuid;
    }
  }

  @Nonnull
  public UnderlyingData getUnderlyingData()
  {
    return new UnderlyingData(mMailItem.getUnderlyingData());
  }

  public static class Color
  {
    public static final Color BLUE   = new Color(0x0000FFL);
    public static final Color GREEN  = new Color(0x00FF00L);
    public static final Color ORANGE = new Color(0xFF8000L);
    public static final Color PURPLE = new Color(0xBF00FFL);
    public static final Color RED    = new Color(0xFF0000L);
    public static final Color YELLOW = new Color(0xFFFF00L);

    private com.zimbra.common.mailbox.Color mColor;

    public Color(Object color)
    {
      mColor = (com.zimbra.common.mailbox.Color) color;
    }

    public Color(long color)
    {
      mColor = new com.zimbra.common.mailbox.Color(color);
    }

    public Color(String color)
    {
      mColor = new com.zimbra.common.mailbox.Color(color);
    }

    public <T> T toZimbra(@Nonnull Class<T> cls)
    {
      return cls.cast(mColor);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Color color = (Color) o;
      return Objects.equals(mColor, color.mColor);
    }

    @Override
    public int hashCode() {
      return Objects.hash(mColor);
    }
  }

  @Nonnull
  public Color getColor()
  {
    return new Color(mMailItem.getRgbColor());
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

  public InputStream getContentStream()
    throws ZimbraException
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

  public byte[] getContent()
    throws ZimbraException
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

  public static String getNameForType(byte type)
  {
    return convertType(type).toString();
  }

  public static String getNameForType(@Nonnull Item item)
  {
    return getNameForType(item.getType());
  }

  private static final HashMap<String, String> sTypeMap = new HashMap<String, String>();

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

  public boolean inSpam()
  {
    return mMailItem.inSpam();
  }

  @Nonnull
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
      partypes[0] = com.zimbra.cs.mailbox.Metadata.class;

      sDeserializeMethod = MailItem.UnderlyingData.class.getDeclaredMethod("deserialize", partypes);
      sDeserializeMethod.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  @Nonnull
  public static UnderlyingData decodeZimbraMetadata(@Nullable ZimbraVersion originVersion, final String encodedString)
    throws ZimbraException
  {
    try
    {
      Metadata meta = new Metadata(encodedString);

      UnderlyingData underlyingData = new UnderlyingData();
      Object parameters[] = new Object[1];
      parameters[0] = meta.toZimbra(com.zimbra.cs.mailbox.Metadata.class);

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
      throw new RuntimeException(ex);
    }
  }

  @Nullable
  public String encodeZimbraMetadata()
  {
    try
    {
      UnderlyingData underlyingData = getUnderlyingData();
      Object parameters[] = new Object[0];
      com.zimbra.cs.mailbox.Metadata meta = (com.zimbra.cs.mailbox.Metadata) sSerializeMethod.invoke(
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

  private static Method sEncodeMetadata;

  static
  {
    try
    {
      Class partypes[] = {com.zimbra.cs.mailbox.Metadata.class};

      sEncodeMetadata = MailItem.class.getDeclaredMethod("encodeMetadata", partypes);
      sEncodeMetadata.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  @Nullable
  public String encodeSubmetadataForItemType()
  {
    try
    {
      Object parameters[] = { new com.zimbra.cs.mailbox.Metadata() };
      com.zimbra.cs.mailbox.Metadata meta = (com.zimbra.cs.mailbox.Metadata) sEncodeMetadata.invoke(
        mMailItem,
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
    return mMailItem.getTags();
  }

  public String getSubject()
  {
    return mMailItem.getSubject();
  }

  @Nonnull
  public Acl getEffectiveACL()
  {
    ACL acl = mMailItem.getEffectiveACL();
    if( acl == null )
    {
      return new Acl();
    }
    else
    {
      return new Acl(acl);
    }
  }

  public long getChangeDate()
  {
    return mMailItem.getChangeDate();
  }

  @Override
  public int compareTo(@Nonnull Item item)
  {
    return mMailItem.compareTo(item.toZimbra(MailItem.class));
  }

  @Nullable
  public String getUuid()
  {
    return mMailItem.getUuid();
  }

  public List<Document> generateIndexData()
  {
    return new ArrayList<>();
  }
}
