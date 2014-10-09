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

import java.lang.reflect.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.io.*;

import com.zimbra.cs.mailbox.calendar.RecurId;
import com.zimbra.cs.session.Session;
import org.openzal.zal.calendar.CalendarItemData;
import org.openzal.zal.calendar.Invite;
import org.openzal.zal.calendar.RecurrenceId;
import org.openzal.zal.exceptions.*;
import org.openzal.zal.exceptions.NoSuchItemException;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.lib.ZimbraConnectionWrapper;
import org.openzal.zal.lib.ZimbraDatabase;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.db.DbMailbox;
import com.zimbra.cs.db.DbPool;
import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.mailbox.*;
import com.zimbra.cs.mailbox.util.*;
import com.zimbra.cs.mailbox.CalendarItem.ReplyInfo;
import com.zimbra.cs.service.util.ItemId;

import javax.mail.internet.MimeMessage;

import com.zimbra.cs.service.FileUploadServlet.Upload;

import org.openzal.zal.log.ZimbraLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* $if MajorZimbraVersion == 6 $
import com.zimbra.cs.index.queryparser.ParseException;
$endif$ $*/

/* $if MajorZimbraVersion <= 7 $
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import com.zimbra.cs.mailbox.MailItem.Color;
  $endif$ $*/

/* $if ZimbraVersion >= 7.2.2 && ZimbraVersion != 8.0.1 && ZimbraVersion != 8.0.0 && ZimbraVersion < 8.5.0 $
import com.zimbra.cs.db.DbMailItem.SearchOpts;
/* $endif$ */

/* $if MajorZimbraVersion > 7$ */
/* $endif$ */


public class Mailbox
{
  @NotNull final private com.zimbra.cs.mailbox.Mailbox mMbox;

  public static final int ID_AUTO_INCREMENT       = com.zimbra.cs.mailbox.Mailbox.ID_AUTO_INCREMENT;
  public static final int ID_FOLDER_USER_ROOT     = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_USER_ROOT;
  public static final int ID_FOLDER_INBOX         = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_INBOX;
  public static final int ID_FOLDER_TRASH         = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_TRASH;
  public static final int ID_FOLDER_SPAM          = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_SPAM;
  public static final int ID_FOLDER_SENT          = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_SENT;
  public static final int ID_FOLDER_DRAFTS        = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_DRAFTS;
  public static final int ID_FOLDER_CONTACTS      = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_CONTACTS;
  public static final int ID_FOLDER_TAGS          = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_TAGS;
  public static final int ID_FOLDER_CONVERSATIONS = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_CONVERSATIONS;
  public static final int ID_FOLDER_CALENDAR      = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_CALENDAR;
  public static final int ID_FOLDER_ROOT          = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_ROOT;
  @Deprecated
  public static final int ID_FOLDER_NOTEBOOK      = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_NOTEBOOK;
  public static final int ID_FOLDER_AUTO_CONTACTS = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_AUTO_CONTACTS;
  public static final int ID_FOLDER_IM_LOGS       = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_IM_LOGS;
  public static final int ID_FOLDER_TASKS         = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_TASKS;
  public static final int ID_FOLDER_BRIEFCASE     = com.zimbra.cs.mailbox.Mailbox.ID_FOLDER_BRIEFCASE;
  public static final int ID_FOLDER_COMMENTS      = 17;
  public static final int ID_FOLDER_PROFILE       = 18;

  private static final int HIGHEST_SYSTEM_ID = com.zimbra.cs.mailbox.Mailbox.HIGHEST_SYSTEM_ID;
  public static final  int FIRST_USER_ID     = com.zimbra.cs.mailbox.Mailbox.FIRST_USER_ID;

  public long getSize()
  {
    return mMbox.getSize();
  }

  public static int getHighestSystemId()
  {
    return HIGHEST_SYSTEM_ID;
  }

  public void emptyFolder(@NotNull OperationContext zContext, int folderId, boolean withDeleteSubFolders)
  {
    try
    {
      mMbox.emptyFolder(zContext.getOperationContext(), folderId, withDeleteSubFolders);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  static class FakeMailbox extends com.zimbra.cs.mailbox.Mailbox
  {
    public FakeMailbox(@NotNull com.zimbra.cs.account.Account account)
    {
      super(createMailboxMetadata(account));
    }

    @NotNull
    private static MailboxData createMailboxMetadata(@NotNull com.zimbra.cs.account.Account account)
    {
      MailboxData data = new MailboxData();
      data.id = -1;
      data.schemaGroupId = -1;
      data.accountId = account.getId();
      data.size = 0L;
      data.contacts = 0;
      data.indexVolumeId = 0;
      data.lastBackupDate = 0;
      data.lastItemId = 0;
      data.lastChangeId = 0;
      data.lastChangeDate = 0;
      data.lastWriteDate = 0;
      data.recentMessages = -1;
      data.trackSync = -1;
      data.trackImap = false;
      data.configKeys = new HashSet<String>();

      return data;
    }
  }

  public void migrateContactGroup()
  {
/* $if MajorZimbraVersion >= 8 $ */
    try
    {
      com.zimbra.cs.mailbox.ContactGroup.MigrateContactGroup contactGroup =
        new com.zimbra.cs.mailbox.ContactGroup.MigrateContactGroup(mMbox);
      contactGroup.handle();
    }
    catch (com.zimbra.common.service.ServiceException ex)
    {
      throw ExceptionWrapper.wrap(ex);
    }
/* $else$
      throw new UnsupportedOperationException();
   $endif$ */
  }

  public Mailbox(@Nullable Object mbox)
  {
    if (mbox == null)
    {
      throw new IllegalArgumentException("mMbox is null");
    }
    this.mMbox = (com.zimbra.cs.mailbox.Mailbox) mbox;
  }

  @NotNull
  public static Mailbox createFakeMailbox(@NotNull Account realAccount)
  {
    return new Mailbox(
      new FakeMailbox(realAccount.toZimbra(com.zimbra.cs.account.Account.class))
    );
  }

  public <T> T toZimbra(@NotNull Class<T> cls)
  {
    return cls.cast(getMailbox());
  }

  @NotNull
  public OperationContext newZimbraAdminContext()
  {
    return new OperationContext(
      new com.zimbra.cs.mailbox.OperationContext(
        new Provisioning(
          com.zimbra.cs.account.Provisioning.getInstance()
        ).getZimbraUser().toZimbra(com.zimbra.cs.account.Account.class)
      )
    );
  }

  @NotNull
  public OperationContext newContext()
  {
    try
    {
      return new OperationContext(
        new com.zimbra.cs.mailbox.OperationContext(mMbox)
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @NotNull
  public com.zimbra.cs.mailbox.Mailbox getMailbox()
  {
    return mMbox;
  }

  @NotNull
  public Account getAccount()
    throws NoSuchAccountException
  {
    try
    {
      return new Account(mMbox.getAccount());
    }
    catch (com.zimbra.common.service.ServiceException serviceException)
    {
      throw ExceptionWrapper.wrap(serviceException);
    }
  }

  public String getAccountId()
  {
    return mMbox.getAccountId();
  }

  public int getId()
  {
    return (int) mMbox.getId();
  }

  public boolean hasListener(String listenerName)
  {
    return getListener(listenerName) != null;
  }

  public Object getListener(String listenerName)
  {
    return mMbox.getListener(listenerName);
  }

  public void registerListener(@NotNull Listener listener)
  {
    try
    {
      mMbox.addListener(listener.getStoreContext().toZimbra(Session.class));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      ZimbraLog.mailbox.warn("Error adding listener to mailbox " +
                               mMbox.getId() + ": " +
                               e.getMessage());
    }
  }

  public void unregisterListener(@NotNull Listener listener)
  {
    mMbox.removeListener(listener.getStoreContext().toZimbra(Session.class));
  }

  public void unregisterListener(@NotNull MailboxSessionProxy session)
  {
    mMbox.removeListener(session.toZimbra(Session.class));
  }

  /* $if MajorZimbraVersion < 8 $
  static final String sCurrentChangeName = "mCurrentChange";

  private static Field sMailboxChange = null;
  private static Field sMailboxChangeSync = null;
  private static Field sMailboxData = null;
  static
  {
    try
    {
      sMailboxChange = com.zimbra.cs.mailbox.Mailbox.class.getDeclaredField(sCurrentChangeName);
      sMailboxChange.setAccessible(true);

      sMailboxData = com.zimbra.cs.mailbox.Mailbox.class.getDeclaredField("mData");
      sMailboxData.setAccessible(true);

      Class mailboxChangeClass = null;
      for( Class cls : com.zimbra.cs.mailbox.Mailbox.class.getDeclaredClasses() )
      {
        if( cls.getName().equals("com.zimbra.cs.mailbox.Mailbox$MailboxChange") )
        {
          mailboxChangeClass = cls;
          break;
        }
      }
      sMailboxChangeSync = mailboxChangeClass.getDeclaredField("sync");
      sMailboxChangeSync.setAccessible(true);
    }
    catch( Throwable ex ){
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: "+Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  private static int getMailboxSyncCutoff(com.zimbra.cs.mailbox.Mailbox mMbox)
  {
    try
    {
      Object obj = sMailboxChange.get( mMbox );

      Integer sync = (Integer)sMailboxChangeSync.get(obj);
      if( sync == null )
      {
        return ((com.zimbra.cs.mailbox.Mailbox.MailboxData)sMailboxData.get(mMbox)).trackSync;
      }
      else
      {
        return sync;
      }
    }
    catch( Throwable ex )
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Exception: "+Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }
  $else$ */
  private static int getMailboxSyncCutoff(@NotNull com.zimbra.cs.mailbox.Mailbox mMbox)
  {
    return mMbox.getSyncCutoff();
  }
/*  $endif$ */

  public boolean isTombstoneValid(int sequence)
  {
    int mboxSequence = getMailboxSyncCutoff(mMbox);
    return mboxSequence > 0 && sequence >= mboxSequence;
  }

  @NotNull
  public Item getItemById(@NotNull OperationContext zContext, int id, byte type)
    throws NoSuchItemException
  {
    MailItem item;
    try
    {
      item = mMbox.getItemById(zContext.getOperationContext(), id, Item.convertType(type));
    }
    catch (com.zimbra.common.service.ServiceException serviceException)
    {
      throw ExceptionWrapper.wrap(serviceException);
    }
    return new Item(item);
  }

  @NotNull
  public Message getMessageById(@NotNull OperationContext zContext, int id)
    throws NoSuchMessageException
  {
    try
    {
      com.zimbra.cs.mailbox.Message message = mMbox.getMessageById(zContext.getOperationContext(), id);
      return new Message(message);
    }
    catch (Exception exception)
    {
      throw ExceptionWrapper.wrap(exception);
    }
  }

  @NotNull
  public List<Message> getMessagesByConversation(@NotNull OperationContext zContext, int id)
    throws NoSuchConversationException
  {
    List<com.zimbra.cs.mailbox.Message> list;
    try
    {
      list = mMbox.getMessagesByConversation(zContext.getOperationContext(), id);
    }
    catch (Exception exception)
    {
      throw ExceptionWrapper.wrap(exception);
    }
    List<Message> newList = new ArrayList<Message>(list.size());

    for (com.zimbra.cs.mailbox.Message item : list)
    {
      newList.add(new Message(item));
    }

    return newList;
  }

  public void setDate(@NotNull OperationContext octxt, int itemId, byte type, long date)
  {
    try
    {
      mMbox.setDate(octxt.getOperationContext(), itemId, Item.convertType(type), date);
    }
    catch (com.zimbra.common.service.ServiceException serviceException)
    {
      throw ExceptionWrapper.wrap(serviceException);
    }
  }

  @NotNull
  public List<Tag> getModifiedTags(@NotNull OperationContext octxt, int lastSync)
  {
    List<com.zimbra.cs.mailbox.Tag> list;
    try
    {
      list = mMbox.getModifiedTags(octxt.getOperationContext(), lastSync);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    List<Tag> newList = new ArrayList<Tag>(list.size());

    for (MailItem item : list)
    {
      newList.add(new Tag(item));
    }
    return newList;
  }

  public List<Integer> getModifiedItems(@NotNull OperationContext zContext, int sequence)
  {
    try
    {
      return mMbox.getModifiedItems(zContext.getOperationContext(), sequence).getFirst();
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<Integer> listTombstones(int sequence)
  {
    try
    {
  /* $if ZimbraVersion >= 8.0.7 $ */
      return mMbox.getTombstones(sequence).getAllIds();
  /* $else$
      return mMbox.listTombstones( sequence );
     $endif$ */
    }
    catch (com.zimbra.common.service.ServiceException serviceException)
    {
      throw ExceptionWrapper.wrap(serviceException);
    }
  }

  @NotNull
  public Folder getFolderByName(@NotNull OperationContext zContext, String name, int parentId)
    throws NoSuchFolderException
  {
    MailItem folder;
    try
    {
      folder = mMbox.getFolderByName(zContext.getOperationContext(), parentId, name);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Folder(folder);
  }

  @NotNull
  public Folder getFolderByPath(@NotNull OperationContext zContext, String path)
    throws NoSuchFolderException
  {
    MailItem folder;
    try
    {
      folder = mMbox.getFolderByPath(zContext.getOperationContext(), path);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Folder(folder);
  }

  @NotNull
  public List<Folder> getModifiedFolders(int sequence)
    throws NoSuchFolderException
  {
    List<com.zimbra.cs.mailbox.Folder> folderList;
    try
    {
      folderList = mMbox.getModifiedFolders(sequence);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    List<Folder> newFolderList = new ArrayList(folderList.size());

    for (com.zimbra.cs.mailbox.Folder folder : folderList)
    {
      newFolderList.add(new Folder(folder));
    }

    return newFolderList;
  }
  /*
  public Folder getFolderById( int id )
    throws ServiceException
  {
    MailItem folder;
    try
    {
      folder = mMbox.getFolderById(id);
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    return new Folder(folder);
  }
  */

  @NotNull
  public Folder getFolderById(@NotNull OperationContext zContext, int id)
    throws NoSuchFolderException
  {
    MailItem folder;
    try
    {
      folder = mMbox.getFolderById(zContext.getOperationContext(), id);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Folder(folder);
  }

  @NotNull
  public CalendarItem getCalendarItemById(@NotNull OperationContext octxt, int id)
    throws NoSuchCalendarException
  {
    try
    {
      return new CalendarItem(mMbox.getCalendarItemById(octxt.getOperationContext(), id));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  public CalendarItem getCalendarItemByUid(@NotNull OperationContext octxt, String uid)
    throws NoSuchCalendarException
  {
    MailItem mailItem;
    try
    {
      mailItem = mMbox.getCalendarItemByUid(octxt.getOperationContext(), uid);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    if (mailItem == null)
    {
      return null;
    }

    return new CalendarItem(mailItem);
  }

  public void copyCalendarReplyInfo(
    @NotNull CalendarItem fromCalendarItem,
    CalendarItem toCalendarItem,
    @NotNull OperationContext zContext
  )
  {
    synchronized (mMbox)
    {
      beginTransaction("ZxCalendarRepliesRestore", zContext);
      try
      {
        fromCalendarItem.copyReplyInfoTo(toCalendarItem);
      }
      finally
      {
        endTransaction(true);
      }
    }
  }

  public void rename(@NotNull OperationContext zContext, int id, byte type, String name, int folderId)
    throws ZimbraException
  {
    /* $if ZimbraVersion >= 7 $ */
    try
    {
      mMbox.rename(zContext.getOperationContext(), id, Item.convertType(type), name, folderId);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
      throw new UnsupportedOperationException();
       $endif$ */
  }

  public void delete(@NotNull OperationContext octxt, int itemId, byte type)
    throws ZimbraException
  {
    try
    {
      mMbox.delete(octxt.getOperationContext(), itemId, Item.convertType(type));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void setPermissions(@NotNull OperationContext zContext, int folderId, @NotNull Acl acl)
    throws ZimbraException
  {
    try
    {
      mMbox.setPermissions(zContext.getOperationContext(), folderId, acl.toZimbra(ACL.class));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void setColor(@NotNull OperationContext octxt, int[] itemIds, byte type, @NotNull Item.Color color)
    throws ZimbraException
  {
    try
    {
      mMbox.setColor(
        octxt.getOperationContext(),
        itemIds,
        Item.convertType(type),
    /* $if MajorZimbraVersion <= 7 $
        color.toZimbra(com.zimbra.cs.mailbox.MailItem.Color.class)
       $else$ */
        color.toZimbra(com.zimbra.common.mailbox.Color.class)
    /* $endif$ */
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @NotNull
  public CalendarItem setCalendarItem(OperationContext octxt, int folderId, int flags, long tags,
                                      CalendarItemData defaultInv,
                                      List<CalendarItemData> exceptions,
                                      List<ReplyInfo> replies, long nextAlarm
  )
    throws ZimbraException
  {
    /* $if MajorZimbraVersion <= 7 $
    com.zimbra.cs.mailbox.Mailbox.SetCalendarItemData[] zimbraExceptions =
      new com.zimbra.cs.mailbox.Mailbox.SetCalendarItemData[exceptions.size()];
    for (int i=0; i<exceptions.size(); i++)
    {
      zimbraExceptions[i] = exceptions.get(i).toZimbra(com.zimbra.cs.mailbox.Mailbox.SetCalendarItemData.class);
    }

    try
    {
      return new CalendarItem(mMbox.setCalendarItem(
        octxt.getOperationContext(),
        folderId,
        flags,
        tags,
        defaultInv.toZimbra(com.zimbra.cs.mailbox.Mailbox.SetCalendarItemData.class),
        zimbraExceptions,
        replies,
        nextAlarm
      ));
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $ */
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @NotNull
  public CalendarItem setCalendarItem(
    @NotNull OperationContext octxt, int folderId, int flags, String tags[],
                                      @NotNull CalendarItemData defaultInv,
                                      @NotNull List<CalendarItemData> exceptions,
                                      List<ReplyInfo> replies, long nextAlarm
  )
    throws ZimbraException
  {
    /* $if MajorZimbraVersion >= 8 $ */
    com.zimbra.cs.mailbox.Mailbox.SetCalendarItemData[] zimbraExceptions = null;
    if( exceptions.size() > 0 )
    {
      zimbraExceptions = new com.zimbra.cs.mailbox.Mailbox.SetCalendarItemData[exceptions.size()];
      for (int i = 0; i < exceptions.size(); i++)
      {
        zimbraExceptions[i] = exceptions.get(i).toZimbra(com.zimbra.cs.mailbox.Mailbox.SetCalendarItemData.class);
      }
    }

    try
    {
      return new CalendarItem(
        mMbox.setCalendarItem(
          octxt.getOperationContext(),
          folderId,
          flags,
          tags,
          defaultInv.toZimbra(com.zimbra.cs.mailbox.Mailbox.SetCalendarItemData.class),
          zimbraExceptions,
          replies,
          nextAlarm
        )
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public
  @Nullable
  Metadata getConfig(@NotNull OperationContext octxt, String section)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.mailbox.Metadata metadata = mMbox.getConfig(octxt.getOperationContext(), section);
      if (metadata == null)
      {
        return null;
      }
      return new Metadata(metadata);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void setConfig(@NotNull OperationContext octxt, String section, @NotNull Metadata config)
    throws ZimbraException
  {
    try
    {
      mMbox.setConfig(
        octxt.getOperationContext(),
        section,
        config.toZimbra(com.zimbra.cs.mailbox.Metadata.class));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void alterTag(@NotNull OperationContext octxt, int itemId, byte type, int tagId, boolean addTag)
    throws ZimbraException
  {
    try
    {
  /* $if MajorZimbraVersion <= 7 $
        mMbox.alterTag(octxt.getOperationContext(), itemId, Item.convertType(type), tagId, addTag, null);
     $else$ */
      mMbox.alterTag(octxt.getOperationContext(), itemId, Item.convertType(type), Flag.of(tagId), addTag, null);
  /* $endif$ */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void setTags(@NotNull OperationContext octxt, int itemId, byte type, @Nullable Collection<String> tags)
    throws ZimbraException
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    String[] tagsArray;
    if (tags == null)
    {
      tagsArray = null;
    }
    else
    {
      tagsArray = tags.toArray(new String[tags.size()]);
    }

    MailItem item;
    try
    {
      item = mMbox.getItemById(octxt.getOperationContext(), itemId, Item.convertType(Item.TYPE_UNKNOWN));
      mMbox.setTags(
        octxt.getOperationContext(),
        itemId,
        Item.convertType(type),
        item.getFlagBitmask(),
        tagsArray
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
       $endif $ */
  }

  public void setTags(OperationContext octxt, int itemId, byte type, long tags)
  {
    /* $if ZimbraVersion < 8.0.0 $
    MailItem item;
    try
    {
      item = mMbox.getItemById(octxt.getOperationContext(), itemId, Item.convertType(Item.TYPE_UNKNOWN));
      mMbox.setTags(octxt.getOperationContext(), itemId, Item.convertType(type), item.getFlagBitmask(), tags);
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $ */
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void setFlags(@NotNull OperationContext octxt, int itemId, byte type, int flags)
    throws ZimbraException
  {
    try
    {
      MailItem item = mMbox.getItemById(octxt.getOperationContext(), itemId, Item.convertType(Item.TYPE_UNKNOWN));
      mMbox.setTags(octxt.getOperationContext(), itemId, Item.convertType(type), flags,
  /* $if MajorZimbraVersion >= 8 $ */
        item.getTags()
  /* $else$
        item.getTagBitmask()
    $endif$ */
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void modifyContact(@NotNull OperationContext octxt, int contactId, @NotNull ParsedContact pc)
    throws ZimbraException
  {
    try
    {
      mMbox.modifyContact(
        octxt.getOperationContext(),
        contactId,
        pc.toZimbra(com.zimbra.cs.mime.ParsedContact.class)
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @NotNull
  public ZimbraItemId sendMimeMessage(
    @NotNull OperationContext octxt, Boolean saveToSent, MimeMessage mm,
                                      List<Upload> uploads,
                                      @Nullable ZimbraItemId origMsgId, String replyType,
                                      boolean replyToSender
  )
    throws ZimbraException
  {

    ItemId itemId = null;
    ItemId newItemId;

    if (origMsgId != null)
    {
      itemId = new ItemId(origMsgId.getAccountId().toString(), origMsgId.getItemId());
    }

    try
    {
  /* $if MajorZimbraVersion == 6 $
      newItemId = mMbox.getMailSender().sendMimeMessage(
        octxt.getOperationContext(), mMbox,
        saveToSent, mm,
        (List<InternetAddress>)null,
        uploads, itemId, replyType,
        null, true, replyToSender
      );
    $endif$ */

  /* $if MajorZimbraVersion == 7 $
      newItemId = mMbox.getMailSender().sendMimeMessage(
        octxt.getOperationContext(), mMbox, saveToSent, mm,
        (List<InternetAddress>)null,
        uploads, itemId, replyType,
        null, replyToSender
      );
    $endif$ */

  /* $if MajorZimbraVersion == 8 $ */
      newItemId = mMbox.getMailSender().sendMimeMessage(
        octxt.getOperationContext(), mMbox, saveToSent, mm,
        uploads, itemId, replyType,
        null, replyToSender
      );
  /* $endif$ */
      return new ZimbraItemId(newItemId.getAccountId(), newItemId.getId());
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean attachmentsIndexingEnabled()
    throws ZimbraException
  {
    try
    {
      return mMbox.attachmentsIndexingEnabled();
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void move(@NotNull OperationContext octxt, int itemId, byte type, int targetId)
    throws ZimbraException
  {
    try
    {
      mMbox.move(octxt.getOperationContext(), itemId, Item.convertType(type), targetId);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<CalendarItem> getCalendarItemsForRange(
    @NotNull OperationContext octxt, byte type, long start,
                                                     long end, int folderId, int[] excludeFolders
  )
    throws ZimbraException
  {
    try
    {
      List<com.zimbra.cs.mailbox.CalendarItem> zimbraCalendarItems = mMbox.getCalendarItemsForRange(
        octxt.getOperationContext(),
        Item.convertType(type),
        start,
        end,
        folderId,
        excludeFolders
      );

      return ZimbraListWrapper.wrapCalendarItems(zimbraCalendarItems);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<Integer> getItemListByDates(
    @NotNull OperationContext octxt, byte type, long start,
                                          long end, int folderId, boolean descending
  )
    throws ZimbraException
  {
    List<Integer> itemIds;

    try
    {
/* $if ZimbraVersion < 7.2.2 |! ZimbraVersion == 8.0.0 |! ZimbraVersion == 8.0.1 $
    itemIds = mMbox.getItemListByDates(octxt.getOperationContext(), Item.convertType(type),
                                      start, end, folderId, descending);
  $elseif ZimbraVersion < 8.5.0 $

      DbMailItem.SearchOpts options = new DbMailItem.SearchOpts(start, end, descending);
      itemIds = mMbox.getItemIdList(octxt.getOperationContext(), Item.convertType(type),
                                   folderId, options);
   $else $ */
      DbMailItem.QueryParams options = new DbMailItem.QueryParams();
      options.setFolderIds(Collections.singletonList(folderId));
      options.setIncludedTypes(Collections.singletonList(Item.convertType(type)));
      options.setDateAfter((int) (start / 1000L));
      options.setDateBefore((int) (end / 1000L));
      if (descending)
      {
        options.setOrderBy(Collections.singletonList("date DESC"));
      }
      else
      {
        options.setOrderBy(Collections.singletonList("date ASC"));
      }
      itemIds = mMbox.getItemIdList(octxt.getOperationContext(), options);
/* $endif$ */
      return itemIds;
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<Integer> listItemIds(@NotNull OperationContext octxt, byte type, int folderId)
    throws NoSuchItemException
  {
    try
    {
      return mMbox.listItemIds(octxt.getOperationContext(), Item.convertType(type), folderId);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @NotNull
  public Iterator<Map.Entry<Byte, List<Integer>>> getItemIds(@NotNull OperationContext octxt, int folderId)
    throws ZimbraException
  {
    try
    {
    /* $if MajorZimbraVersion >= 8 $ */
      Map<Byte, List<Integer>> map = new HashMap<Byte, List<Integer>>();
      Iterator<Map.Entry<MailItem.Type, List<TypedIdList.ItemInfo>>> iterator
        = mMbox.getItemIds(octxt.getOperationContext(), folderId).iterator();
      while (iterator.hasNext())
      {
        Map.Entry<MailItem.Type, List<TypedIdList.ItemInfo>> entry = iterator.next();
        List<Integer> list = new ArrayList<Integer>();
        for (TypedIdList.ItemInfo item : entry.getValue())
        {
          list.add(item.getId());
        }
        map.put(entry.getKey().toByte(), list);
      }
      return map.entrySet().iterator();
    /* $else$
    TypedIdList typedIdList = mMbox.getItemIds(octxt.getOperationContext(), folderId);
      return typedIdList.iterator();
       $endif $ */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void modifyPartStat(
    @NotNull OperationContext octxt, int calItemId,
                             @Nullable RecurrenceId recurId, String cnStr,
                             String addressStr, String cutypeStr,
                             String roleStr, String partStatStr,
                             Boolean rsvp, int seqNo, long dtStamp
  )
    throws ZimbraException
  {
    try
    {
      mMbox.modifyPartStat(
        octxt.getOperationContext(),
        calItemId,
        recurId == null ? null : recurId.toZimbra(RecurId.class),
        cnStr,
        addressStr,
        cutypeStr,
        roleStr,
        partStatStr,
        rsvp,
        seqNo,
        dtStamp);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @NotNull
  public Tag getTagById(@NotNull OperationContext octxt, int itemId)
    throws NoSuchItemException
  {
    MailItem tag;
    try
    {
      tag = mMbox.getTagById(octxt.getOperationContext(), itemId);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    return new Tag(tag);
  }

  @NotNull
  public Tag getTagByName(@NotNull OperationContext octxt, String name)
    throws NoSuchItemException
  {
    try
    {
/* $if MajorZimbraVersion >= 8 $ */
      return new Tag(mMbox.getTagByName(octxt.getOperationContext(), name));
/* $else$
      return new Tag(mMbox.getTagByName(name));
   $endif */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void setCustomData(@NotNull OperationContext octxt, int itemId, byte type, @NotNull Item.CustomMetadata custom)
    throws ZimbraException
  {
    try
    {
      mMbox.setCustomData(
        octxt.getOperationContext(),
        itemId,
        Item.convertType(type),
        custom.toZimbra(MailItem.CustomMetadata.class)
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @NotNull
  public ZimbraItemId nextItemId()
  {
    return new ZimbraItemId(mMbox.getAccountId(), 0);
  }

  @NotNull
  public OperationContext newOperationContext()
    throws ZimbraException
  {
    try
    {
      return new OperationContext(new com.zimbra.cs.mailbox.OperationContext(mMbox));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void beginTrackingSync()
    throws ZimbraException
  {
    try
    {
      mMbox.beginTrackingSync();
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public int getLastChangeID()
  {
    return mMbox.getLastChangeID();
  }

  public void clearItemCache()
  {
    mMbox.purge(Item.convertType(Item.TYPE_UNKNOWN));
  }

  @NotNull
  public QueryResults search(
    @NotNull OperationContext octxt,
                               String queryString,
                               @NotNull byte[] types,
                               @NotNull SortedBy sortBy,
                               int chunkSize
  )
    throws IOException, ZimbraException
  {
    try
    {
/* $if MajorZimbraVersion >= 8 $ */
      Set<MailItem.Type> typeList = new HashSet(types.length);
      for (byte type : types)
      {
        typeList.add(Item.convertType(type));
      }
      return new QueryResults(
        mMbox.index.search(
          octxt.getOperationContext(),
          queryString,
          typeList,
          sortBy.toZimbra(SortBy.class),
          chunkSize,
          false
        )
      );
/* $else$
      return new QueryResults(
        mMbox.search(
          octxt.getOperationContext(),
          queryString,
          types,
          sortBy.toZimbra(SortBy.class),
          chunkSize
        )
      );
   $endif$ */
    }
    catch (Exception e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @NotNull
  public List<Item> getItemList(byte type, @NotNull OperationContext zContext)
    throws ZimbraException
  {
    List<Item> result = new LinkedList<Item>();

/* $if MajorZimbraVersion <= 7 $
    synchronized( mMbox )
    {
 $endif$ */
    beginTransaction("ZxGetItemList", zContext.getOperationContext());
    try
    {
      if (type == Item.TYPE_FOLDER || type == Item.TYPE_SEARCHFOLDER || type == Item.TYPE_MOUNTPOINT)
      {
        final Collection<Folder> folderCache = getFolderCache();
        for (Folder subfolder : folderCache)
        {
          if (subfolder.getType() == type || type == Item.TYPE_FOLDER)
          {
            result.add(subfolder);
          }
        }
      }
      else if (type == Item.TYPE_TAG)
      {
        final Map<Object, com.zimbra.cs.mailbox.Tag> tagCache = getTagCache();
        for (Map.Entry<Object, com.zimbra.cs.mailbox.Tag> entry : tagCache.entrySet())
        {
          if (entry.getKey() instanceof String)
          {
            result.add(new Item(entry.getValue()));
          }
        }
      }
      else if (type == Item.TYPE_FLAG)
      {
        for (MailItem item : getAllFlags())
        {
          result.add(new Item(item));
        }
        return result;
      }
      else
      {
        List<Item.UnderlyingData> dataList = ZimbraDatabase.getByType(this, type, SortBy.NONE);

        if (dataList == null)
        {
          return Collections.emptyList();
        }

        for (Item.UnderlyingData data : dataList)
        {
          if (data != null)
          {
            try
            {
              result.add(rawGetItem(data));
            }
            catch (Throwable ex)
            {
              ZimbraLog.extensions.debug("getItemList(): skipping item: " + Utils.exceptionToString(ex));
            }
          }
        }
      }
    }
    finally
    {
      endTransaction(true);
    }
/* $if MajorZimbraVersion <= 7 $
    }
 $endif$ */

    return result;
  }

  @NotNull
  public Folder createFolder(
    @NotNull OperationContext octxt, String name, int parentId,
                             byte attrs, byte defaultView, int flags,
                             @NotNull Item.Color color, String url
  )
    throws ZimbraException
  {
    MailItem folder;
    try
    {
      folder = mMbox.createFolder(
        octxt.getOperationContext(),
        name,
        parentId,
        attrs,
  /* $if MajorZimbraVersion >= 7 $ */
        Item.convertType(defaultView == Item.TYPE_WIKI ? Item.TYPE_DOCUMENT : defaultView),
  /* $else$
        Item.convertType(defaultView),
    $endif $ */
        flags,
  /* $if MajorZimbraVersion <= 7 $
        color.toZimbra(com.zimbra.cs.mailbox.MailItem.Color.class),
     $else$ */
        color.toZimbra(com.zimbra.common.mailbox.Color.class),
  /* $endif$ */
        url
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Folder(folder);
  }

  @NotNull
  public SearchFolder createSearchFolder(
    @NotNull OperationContext octxt, int folderId, String name,
                                         String query, String types, String sort,
                                         int flags, @NotNull Item.Color color
  )
    throws ZimbraException
  {
    MailItem item;
    try
    {
      item = mMbox.createSearchFolder(
        octxt.getOperationContext(),
        folderId,
        name,
        query,
        types,
        sort,
        flags,
    /* $if MajorZimbraVersion <= 7 $
        color.toZimbra(com.zimbra.cs.mailbox.MailItem.Color.class)
       $else$ */
        color.toZimbra(com.zimbra.common.mailbox.Color.class)
    /* $endif$ */
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new SearchFolder(item);
  }

  @NotNull
  public Tag createTag(@NotNull OperationContext octxt, String name, @NotNull Item.Color color)
    throws ZimbraException
  {
    MailItem tag;
    try
    {
      tag = mMbox.createTag(
        octxt.getOperationContext(),
        name,
    /* $if MajorZimbraVersion <= 7 $
        color.toZimbra(com.zimbra.cs.mailbox.MailItem.Color.class)
       $else$ */
        color.toZimbra(com.zimbra.common.mailbox.Color.class)
    /* $endif$ */
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Tag(tag);
  }

  @NotNull
  public Message addMessage(OperationContext octxt, InputStream in, int sizeHint, Long receivedDate,
                            int folderId, boolean noIcal,
                            int flags, long tags, int conversationId, String rcptEmail,
                            Item.CustomMetadata customData
  )
    throws IOException, ZimbraException
  {
    /* $if ZimbraVersion <= 7 $
    return addMessage(octxt, in,
                      sizeHint, receivedDate,
                      folderId, noIcal,
                      flags, com.zimbra.cs.mailbox.Tag.bitmaskToTags(tags),
                      conversationId, rcptEmail,
                      customData);
    /* $else $ */
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @NotNull
  public Message addMessage(OperationContext octxt, InputStream in, int sizeHint, Long receivedDate,
                            int folderId, boolean noIcal,
                            int flags, String tags, int conversationId, String rcptEmail,
                            Item.CustomMetadata customData
  )
    throws IOException, ZimbraException
  {
    /* $if MajorZimbraVersion <= 7 $
    MailItem message;
    try
    {
      message = mMbox.addMessage(octxt.getOperationContext(),
                             in,
                             sizeHint,
                             receivedDate,
                             folderId,
                             noIcal,
                             flags,
                             tags,
                             conversationId,
                             rcptEmail,
                             customData != null ? customData.toZimbra(MailItem.CustomMetadata.class) : null,
                             null
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Message(message);
    /* $else $ */
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @NotNull
  public Message addMessage(
    @NotNull OperationContext octxt, InputStream in, int sizeHint, Long receivedDate,
                            int folderId, boolean noIcal,
                            int flags, Collection<String> tags, int conversationId, String rcptEmail,
                            @Nullable Item.CustomMetadata customData
  )
    throws IOException, ZimbraException
  {
    /* $if MajorZimbraVersion >= 8 $ */
    DeliveryOptions opts = new DeliveryOptions();
    opts.setFolderId(folderId);
    opts.setNoICal(noIcal);
    opts.setFlags(flags);
    opts.setTags(tags);
    opts.setConversationId(conversationId);
    opts.setRecipientEmail(rcptEmail);
    opts.setDraftInfo(null);
    if (customData != null)
    {
      opts.setCustomMetadata(customData.toZimbra(MailItem.CustomMetadata.class));
    }
    MailItem message;
    try
    {
      message = mMbox.addMessage(octxt.getOperationContext(),
                                 in,
                                 sizeHint,
                                 receivedDate,
                                 opts,
                                 null
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Message(message);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @NotNull
  public Message simpleAddMessage(
    @NotNull OperationContext octxt,
    InputStream in,
    int folderId
  )
    throws IOException, ZimbraException
  {
    MailItem message;
    try
    {
/* $if ZimbraVersion >= 8.0.0 $ */
      DeliveryOptions opts = new DeliveryOptions();
      opts.setFolderId(folderId);
      opts.setDraftInfo(null);
      message = mMbox.addMessage(
        octxt.getOperationContext(),
        in,
        0L,
        null,
        opts,
        null
      );
/* $else$

      message = mMbox.addMessage(
        octxt.getOperationContext(),
        in,
        0,
        null,
        folderId,
        false,
        0,
        null,
        -1,
        null,
        null,
        null
      );

   $endif$ */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    return new Message(message);
  }


  @NotNull
  public Contact createContact(OperationContext octxt, ParsedContact pc, int folderId, String tags)
    throws ZimbraException
  {
    /* $if MajorZimbraVersion <= 7 $
    MailItem contact;
    try
    {
      contact = mMbox.createContact(
        octxt.getOperationContext(),
        pc.toZimbra(com.zimbra.cs.mime.ParsedContact.class),
        folderId,
        tags
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Contact(contact);
    /* $else $ */
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @NotNull
  public Contact createContact(@NotNull OperationContext octxt, @NotNull ParsedContact pc, int folderId, @NotNull Collection<String> tags)
    throws ZimbraException
  {
    /* $if MajorZimbraVersion >= 8 $ */
    MailItem contact;
    try
    {
      contact = mMbox.createContact(
        octxt.getOperationContext(),
        pc.toZimbra(com.zimbra.cs.mime.ParsedContact.class),
        folderId,
        tags.toArray(new String[tags.size()])
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Contact(contact);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @NotNull
  public Document createDocument(
    @NotNull OperationContext octxt,
    int folderId,
    @NotNull ParsedDocument pd,
    byte type, int flags
  )
    throws IOException, ZimbraException
  {
    MailItem document;
    try
    {
/* $if MajorZimbraVersion >= 8 $ */
      document = mMbox.createDocument(
        octxt.getOperationContext(),
        folderId,
        pd.toZimbra(com.zimbra.cs.mime.ParsedDocument.class),
        Item.convertType(type),
        flags
      );
/* $else$
      document = mMbox.createDocument(
        octxt.getOperationContext(),
        folderId,
        pd.toZimbra(com.zimbra.cs.mime.ParsedDocument.class),
        Item.convertType(type)
      );
  $endif$  */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Document(document);
  }

  @NotNull
  public Document simpleCreateDocument(
    @NotNull OperationContext octxt,
    int folderId,
    FileInputStream fileInputStream,
    String filename,
    String mimetype,
    long timestamp
  )
    throws IOException, ZimbraException
  {
    ParsedDocument document = new ParsedDocument(
      fileInputStream,
      filename,
      mimetype,
      System.currentTimeMillis(),
      "",
      ""
    );

    return createDocument(
      octxt,
      folderId,
      document,
      Item.TYPE_DOCUMENT,
      0
    );
  }

  @NotNull
  public Document addDocumentRevision(
    @NotNull OperationContext octxt,
    int docId,
    String author,
    String name,
    String description,
    InputStream data
  )
    throws ZimbraException
  {
    MailItem document;
    try
    {
/* $if ZimbraVersion >= 7.0.0$ */
      document = mMbox.addDocumentRevision(
        octxt.getOperationContext(),
        docId,
        author,
        name,
        description,
        data
      );
/* $else$
      document = mMbox.addDocumentRevision(
        octxt.getOperationContext(),
        docId,
        data,
        author,
        name
      );
 $endif$ */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Document(document);
  }

  @NotNull
  public Document createDocument(
    @NotNull OperationContext octxt, int folderId,
                                 String filename, String mimeType,
                                 String author, String description,
                                 boolean descEnabled,
                                 InputStream data, byte type
  )
    throws ZimbraException
  {
    MailItem document;
    try
    {
      document = mMbox.createDocument(
        octxt.getOperationContext(),
        folderId,
        filename,
        mimeType,
        author,
  /* $if MajorZimbraVersion >= 7 $ */
  description,
  /* $endif$ */
  /* $if ZimbraVersion >= 7.0.1 $ */
  descEnabled,
  /* $endif$ */
  data,
  /* $if MajorZimbraVersion >= 7 $ */
  Item.convertType(Item.TYPE_DOCUMENT)
  /* $else$
        Item.convertType(type)
    $endif$ */
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Document(document);
  }

  @NotNull
  public Note createNote(
    @NotNull OperationContext octxt, String content,
                         @NotNull Note.Rectangle rectangle, @NotNull Item.Color color,
                         int folderId
  )
    throws ZimbraException
  {
    MailItem note;
    try
    {
      note = mMbox.createNote(
        octxt.getOperationContext(),
        content,
        rectangle.toZimbra(com.zimbra.cs.mailbox.Note.Rectangle.class),
    /* $if MajorZimbraVersion <= 7 $
        color.toZimbra(com.zimbra.cs.mailbox.MailItem.Color.class),
       $else$ */
        color.toZimbra(com.zimbra.common.mailbox.Color.class),
    /* $endif$ */
        folderId);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Note(note);
  }

  public int addInvite(
    @NotNull OperationContext octxt, @NotNull Invite inv,
                       int folderId, @Nullable ParsedMessage pm,
                       boolean preserveExistingAlarms,
                       boolean discardExistingInvites,
                       boolean addRevision
  )
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.mime.ParsedMessage parsedMessage = null;
      if (pm != null)
      {
        parsedMessage = pm.toZimbra(com.zimbra.cs.mime.ParsedMessage.class);
      }
      return mMbox.addInvite(
        octxt.getOperationContext(),
        inv.toZimbra(com.zimbra.cs.mailbox.calendar.Invite.class),
        folderId, parsedMessage,
        preserveExistingAlarms,
        discardExistingInvites,
        addRevision
      )
/* $if MajorZimbraVersion >= 7 $ */
        .calItemId
/* $else$
      [0]
  $endif$ */
        ;
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  /* $if MajorZimbraVersion <= 7 $
    public Mountpoint createMountpoint(OperationContext octxt, int folderId,
                                       String name, String ownerId,
                                       int remoteId, String remoteUuid,
                                       byte view, int flags,
                                       Item.Color color)
      throws ZimbraException
    {
      MailItem mountPoint;
      try
      {
        mountPoint = mMbox.createMountpoint(octxt.getOperationContext(),
                                           folderId,
                                           name,
                                           ownerId,
                                           remoteId,
                                           view,
                                           flags,
                                           color.toZimbra(com.zimbra.cs.mailbox.MailItem.Color.class)

        );
      }
      catch (com.zimbra.common.service.ServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }

      return new Mountpoint(mountPoint);
    }
    $else$ */
  @NotNull
  public Mountpoint createMountpoint(
    @NotNull OperationContext octxt, int folderId,
                                     String name, String ownerId,
                                     int remoteId, String remoteUuid,
                                     byte view, int flags,
                                     @NotNull Item.Color color
  )
    throws ZimbraException
  {
    MailItem mountPoint;
    try
    {
      mountPoint = mMbox.createMountpoint(
        octxt.getOperationContext(), folderId,
        name, ownerId,
        remoteId,
        remoteUuid,
        Item.convertType(view),
        flags,
    /* $if MajorZimbraVersion <= 7 $
        color.toZimbra(com.zimbra.cs.mailbox.MailItem.Color.class),
       $else$ */
        color.toZimbra(com.zimbra.common.mailbox.Color.class),
    /* $endif$ */
        false
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Mountpoint(mountPoint);
  }
/* $endif$ */


  @NotNull
  public Chat createChat(
    @NotNull OperationContext octxt,
                         @NotNull ParsedMessage pm,
                         int folderId, int flags,
                         @NotNull Tags tags
  )
    throws ZimbraException, IOException
  {
    MailItem chat;
    try
    {
      /* $if MajorZimbraVersion >= 8 $ */
      chat = mMbox.createChat(
        octxt.getOperationContext(),
        pm.toZimbra(com.zimbra.cs.mime.ParsedMessage.class),
        folderId,
        flags,
        tags.getTags()
      );
      /* $else $
      chat = mMbox.createChat(
        octxt.getOperationContext(),
        pm.toZimbra(com.zimbra.cs.mime.ParsedMessage.class),
        folderId,flags,
        com.zimbra.cs.mailbox.Tag.bitmaskToTags(tags.getLongTags())
        );
      /* $endif $ */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Chat(chat);
  }

  @NotNull
  public Chat createChat(
    @NotNull OperationContext octxt,
                         @NotNull ParsedMessage pm,
                         int folderId, int flags
  )
    throws ZimbraException, IOException
  {
    MailItem chat;
    try
    {
      chat = mMbox.createChat(
        octxt.getOperationContext(),
        pm.toZimbra(com.zimbra.cs.mime.ParsedMessage.class),
        folderId,
        flags,
        null);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Chat(chat);
  }

  @NotNull
  public Comment createComment(
    @NotNull OperationContext octxt,
                               int parentId,
                               String text,
                               String creator
  )
    throws ZimbraException
  {
    /* $if MajorZimbraVersion >= 8 $ */
    MailItem comment;
    try
    {
      comment = mMbox.createComment(octxt.getOperationContext(), parentId, text, creator);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Comment(comment);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif$ */
  }

  @NotNull
  public Link createLink(
    @NotNull OperationContext octxt,
                         int parentId,
                         String name,
                         String ownerId,
                         int remoteId
  )
    throws ZimbraException
  {
    /* $if MajorZimbraVersion >= 8 $ */
    MailItem link;
    try
    {
      link = mMbox.createLink(octxt.getOperationContext(), parentId, name, ownerId, remoteId);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Link(link);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif$ */
  }

  @Nullable private static Method sEndTransactionMethod = null;

  static
  {
    try
    {
      Class partypes[] = new Class[1];
      partypes[0] = boolean.class;

      sEndTransactionMethod = com.zimbra.cs.mailbox.Mailbox.class.getDeclaredMethod("endTransaction", partypes);
      sEndTransactionMethod.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  @Nullable private static Method sBeginTransactionMethod = null;

  static
  {
    try
    {
      Class partypes[] = new Class[2];
      partypes[0] = String.class;
      partypes[1] = com.zimbra.cs.mailbox.OperationContext.class;

      sBeginTransactionMethod = com.zimbra.cs.mailbox.Mailbox.class.getDeclaredMethod("beginTransaction", partypes);
      sBeginTransactionMethod.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  public void beginTransaction(String name, @NotNull OperationContext context)
  {
    beginTransaction(name, context.getOperationContext());
  }

  private final void beginTransaction(String name, com.zimbra.cs.mailbox.OperationContext zContext)
    throws ZimbraException
  {
    try
    {
      Object parameters[] = new Object[2];
      parameters[0] = name;
      parameters[1] = zContext;

      sBeginTransactionMethod.invoke(mMbox, parameters);
    }
    catch (Exception ex)
    {
      throw ExceptionWrapper.wrap(ex);
    }
  }

  public final void endTransaction(boolean success)
    throws ZimbraException
  {
    Object parameters[] = new Object[1];
    parameters[0] = success;

    try
    {
      sEndTransactionMethod.invoke(mMbox, parameters);
    }
    catch (Exception ex)
    {
      throw ExceptionWrapper.wrap(ex);
    }
  }

  private static Method sRawGetItem;

  static
  {
    try
    {
      Class partypes[] = new Class[1];
      partypes[0] = MailItem.UnderlyingData.class;

      sRawGetItem = com.zimbra.cs.mailbox.Mailbox.class.getDeclaredMethod("getItem", partypes);
      sRawGetItem.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  /*
   * Warning: unsynchronized private access to mailbox
   */
  @NotNull
  private final Item rawGetItem(@NotNull Item.UnderlyingData data)
    throws InternalServerException
  {
    Object parameters[] = new Object[1];
    parameters[0] = data.toZimbra(MailItem.UnderlyingData.class);

    try
    {
      return new Item((MailItem) sRawGetItem.invoke(mMbox, parameters));
    }
    catch (Throwable ex)
    {
      throw ExceptionWrapper.wrap(new RuntimeException(ex));
    }
  }

  private static Method sGetAllFlags;

  static
  {
    try
    {
      Class partypes[] = new Class[1];
      partypes[0] = com.zimbra.cs.mailbox.Mailbox.class;

/* $if MajorZimbraVersion < 8 $
      sGetAllFlags = com.zimbra.cs.mailbox.Flag.class.getDeclaredMethod("getAllFlags", partypes);
  $else$ */
      sGetAllFlags = com.zimbra.cs.mailbox.Flag.class.getDeclaredMethod("allOf", partypes);
/* $endif$ */
      sGetAllFlags.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  /*
   * Warning: unsynchronized private access to mailbox
   */
  @Nullable
  private final List<com.zimbra.cs.mailbox.Flag> getAllFlags()
    throws ZimbraException
  {
    Object parameters[] = new Object[1];
    parameters[0] = mMbox;

    try
    {
      return (List<com.zimbra.cs.mailbox.Flag>) sGetAllFlags.invoke(null, parameters);
    }
    catch (Throwable ex)
    {
      return null;
    }
  }

  public final static boolean ACLIsEmpty(@Nullable Acl acl)
  {
    if (acl == null)
    {
      return true;
    }
    return acl.isEmpty();
  }

  private static Field sTagCache;

  static
  {
    try
    {
      sTagCache = com.zimbra.cs.mailbox.Mailbox.class.getDeclaredField("mTagCache");
      sTagCache.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  /*
   * Warning: unsynchronized private access to mailbox
   */
  @Nullable
  private final Map<Object, com.zimbra.cs.mailbox.Tag> getTagCache()
  {
    try
    {
      return (Map<Object, com.zimbra.cs.mailbox.Tag>) sTagCache.get(mMbox);
    }
    catch (Throwable ex)
    {
      return null;
    }
  }

  private static Field sFolderCache;

  static
  {
    try
    {
      sFolderCache = com.zimbra.cs.mailbox.Mailbox.class.getDeclaredField("mFolderCache");
      sFolderCache.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  /* $if MajorZimbraVersion >= 8 $ */
  private static Field sFolderCacheMap;

  static
  {
    try
    {
      Class cls = null;
      Class subClasses[] = com.zimbra.cs.mailbox.Mailbox.class.getDeclaredClasses();

      for (int n = 0; n < subClasses.length; ++n)
      {
        if (subClasses[n].getName().equals("com.zimbra.cs.mailbox.Mailbox$FolderCache"))
        {
          cls = subClasses[n];
          break;
        }
      }
      if (cls == null)
      {
        ZimbraLog.extensions.fatal(
          "ZAL Reflection Initialization Exception: " +
            "com.zimbra.cs.mailbox.Mailbox$FolderCache not found"
        );
        throw new RuntimeException();
      }
      else
      {
        //do not avoid the exception
        sFolderCacheMap = cls.getDeclaredField("mapById");
        sFolderCacheMap.setAccessible(true);
      }
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }
/* $endif$ */

  /*
   * Warning: unsynchronized private access to mailbox
   */
  @Nullable
  private final Collection<Folder> getFolderCache()
  {
    try
    {

/* $if MajorZimbraVersion >= 8 $ */
      Collection<com.zimbra.cs.mailbox.Folder> list = (Collection<com.zimbra.cs.mailbox.Folder>) (((Map<Integer, com.zimbra.cs.mailbox.Folder>) sFolderCacheMap
        .get(sFolderCache.get(mMbox))).values());
/* $else$
      Collection<Folder> list = (Collection<Folder>)((Map<Integer,Folder>)sFolderCache.get(mMbox)).values();
  $endif$ */

      ArrayList<Folder> newList = new ArrayList<Folder>(list.size());

      for (Object folder : list)
      {
        newList.add(new Folder(folder));
      }

      return newList;
    }
    catch (Throwable ex)
    {
      ZimbraLog.mailbox.error("Exception: " + Utils.exceptionToString(ex));
      return null;
    }
  }

  @Nullable
  public static Mailbox getByAccount(@NotNull Account account)
    throws ZimbraException
  {
    return getByAccount(account, true);
  }

  @Nullable
  public static Mailbox getByAccountId(String id)
    throws ZimbraException
  {
    return getByAccountId(id, true);
  }

  @Nullable
  public static Mailbox getByAccountId(String id, boolean autocreate)
    throws ZimbraException
  {
    com.zimbra.cs.mailbox.Mailbox mbox;
    try
    {
      mbox = com.zimbra.cs.mailbox.MailboxManager.getInstance().getMailboxByAccountId(
        id,
        autocreate ? com.zimbra.cs.mailbox.MailboxManager.FetchMode.AUTOCREATE :
          com.zimbra.cs.mailbox.MailboxManager.FetchMode.DO_NOT_AUTOCREATE
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    if (mbox != null)
    {
      return new Mailbox(mbox);
    }

    return null;
  }

  @Nullable
  @Deprecated
  public static Mailbox getByAccount(@NotNull Account account, boolean autocreate)
    throws ZimbraException
  {
    com.zimbra.cs.mailbox.Mailbox mbox;
    try
    {
      mbox = com.zimbra.cs.mailbox.MailboxManager.getInstance().getMailboxByAccount(
        account.toZimbra(com.zimbra.cs.account.Account.class)
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    if (mbox != null)
    {
      return new Mailbox(mbox);
    }
    return null;
  }

  @Nullable
  @Deprecated
  public static Mailbox getById(long mboxId)
    throws ZimbraException
  {
    return getById((int) mboxId);
  }

  @Nullable
  @Deprecated
  public static Mailbox getById(int mboxId)
    throws ZimbraException
  {
    com.zimbra.cs.mailbox.Mailbox mbox;
    try
    {
      mbox = com.zimbra.cs.mailbox.MailboxManager.getInstance().getMailboxById(mboxId);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    if (mbox != null)
    {
      return new Mailbox(mbox);
    }
    return null;
  }

  @NotNull
  @Deprecated
  public static Mailbox getByItem(@NotNull Item item)
  {
    return item.getMailbox();
  }

  @Deprecated
  public static Map<String, Integer> getMapAccountsAndMailboxes(@NotNull Connection conn)
    throws ZimbraException
  {
    Map<String, Integer> accountsAndMailboxes = new HashMap<String, Integer>();
    try
    {
  /* $if ZimbraVersion >= 8.0.0 $*/
      accountsAndMailboxes = DbMailbox.listMailboxes(conn.toZimbra(DbPool.DbConnection.class));
  /* $else $
    /* $if ZimbraVersion >= 7.0.0 $
          accountsAndMailboxes = DbMailbox.listMailboxes(conn.toZimbra(DbPool.Connection.class));
    /* $else$
      Map<String, Long> result = DbMailbox.listMailboxes(conn.toZimbra(DbPool.Connection.class));
      for (Map.Entry<String, Long> entry : result.entrySet())
      {
        accountsAndMailboxes.put(entry.getKey(), entry.getValue().intValue());
      }
      /* $endif $ */
  /* $endif $ */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return accountsAndMailboxes;
  }

  @NotNull
  public Connection getOperationConnection()
    throws ZimbraException
  {
    /* $if MajorZimbraVersion < 8 $
    DbPool.Connection connection;
    $else$ */
    DbPool.DbConnection connection;
    /* $endif$ */
    try
    {
      connection = mMbox.getOperationConnection();
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    return new ZimbraConnectionWrapper(connection);
  }

  public int getSchemaGroupId()
  {
    //leave the cast
    return (int) mMbox.getSchemaGroupId();
  }

  public void updateChat(@NotNull OperationContext operationContext, @NotNull ParsedMessage parsedMessage, int id)
    throws IOException, ZimbraException
  {
    try
    {
      mMbox.updateChat(
        operationContext.getOperationContext(),
        parsedMessage.toZimbra(com.zimbra.cs.mime.ParsedMessage.class),
        id
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public void reindexItem(@NotNull Item item)
    throws ZimbraException
  {
    List list = new ArrayList<Integer>();

    list.add(item.getId());
    try
    {
      mMbox.index.startReIndexById(list);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

/* $else$
  public void reindexItem(Item item) throws ZimbraException {}
  $endif$ */

  public String rawGetConfig(
    @NotNull String key
  )
    throws SQLException, ZimbraException
  {
    String query = "SELECT metadata FROM " + DbMailbox.qualifyZimbraTableName(
      mMbox,
      "mailbox_metadata"
    ) + " WHERE mailbox_id=? AND section=? LIMIT 1";
    Connection connection = null;
    try
    {
      connection = ZimbraDatabase.legacyGetConnection();
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, getId());
      statement.setString(2, key);

      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        return resultSet.getString(1);
      }
      else
      {
        return "";
      }
    }
    finally
    {
      if (connection != null)
      {
        connection.close();
      }
    }
  }

  public static final long MAX_METADATA_SIZE = 16777215;

  public void rawSetConfig(
    @NotNull String section,
    @NotNull String metadata
  )
    throws SQLException, ZimbraException
  {
    if (metadata.length() > MAX_METADATA_SIZE)
    {
      throw new SQLException("metadata is too big to be saved");
    }

    String updateQuery = "UPDATE " + DbMailbox.qualifyZimbraTableName(
      mMbox,
      "mailbox_metadata"
    ) + " SET metadata=? WHERE mailbox_id=? AND section=? LIMIT 1";
    Connection connection = null;
    try
    {
      connection = ZimbraDatabase.legacyGetConnection();

      PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
      updateStatement.setString(1, metadata);
      updateStatement.setInt(2, getId());
      updateStatement.setString(3, section);

      int res = updateStatement.executeUpdate();
      if (res == 0)
      {
        //REPLACE works only on mysql
        String insertQuery = "REPLACE INTO zimbra.mailbox_metadata (mailbox_id,section,metadata) VALUES(?,?,?)";

        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.setInt(1, getId());
        insertStatement.setString(2, section);
        insertStatement.setString(3, metadata);

        insertStatement.executeUpdate();
      }

      connection.commit();
    }
    finally
    {
      if (connection != null)
      {
        connection.close();
      }
    }
  }
}
