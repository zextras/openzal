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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.io.*;

import com.zimbra.cs.mailbox.calendar.Invite;
import com.zimbra.cs.mailbox.calendar.RecurId;
import com.zimbra.cs.session.Session;
import org.openzal.zal.calendar.ZECalendarItemData;
import org.openzal.zal.calendar.ZEInvite;
import org.openzal.zal.calendar.ZERecurId;
import org.openzal.zal.exceptions.*;
import org.openzal.zal.exceptions.NoSuchItemException;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.lib.ZimbraDatabase;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.db.DbMailbox;
import com.zimbra.cs.db.DbPool;
import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.mime.*;
import com.zimbra.cs.mailbox.*;
import com.zimbra.cs.mailbox.util.*;
import org.openzal.zal.ZEItem.ZECustomMetadata;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.CalendarItem.ReplyInfo;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.account.Identity;

import javax.mail.internet.MimeMessage;
import com.zimbra.cs.service.FileUploadServlet.Upload;

import com.zimbra.cs.mailbox.Flag;
import org.openzal.zal.log.ZimbraLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* $if MajorZimbraVersion == 6 $
import com.zimbra.cs.index.queryparser.ParseException;
import com.zimbra.cs.mime.ParsedMessage;
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
import com.zimbra.common.mailbox.Color;
/* $endif$ */


public class ZEMailbox
{
  final private Mailbox mMbox;

  public static final int ID_AUTO_INCREMENT       = Mailbox.ID_AUTO_INCREMENT;
  public static final int ID_FOLDER_USER_ROOT     = Mailbox.ID_FOLDER_USER_ROOT;
  public static final int ID_FOLDER_INBOX         = Mailbox.ID_FOLDER_INBOX;
  public static final int ID_FOLDER_TRASH         = Mailbox.ID_FOLDER_TRASH;
  public static final int ID_FOLDER_SPAM          = Mailbox.ID_FOLDER_SPAM;
  public static final int ID_FOLDER_SENT          = Mailbox.ID_FOLDER_SENT;
  public static final int ID_FOLDER_DRAFTS        = Mailbox.ID_FOLDER_DRAFTS;
  public static final int ID_FOLDER_CONTACTS      = Mailbox.ID_FOLDER_CONTACTS;
  public static final int ID_FOLDER_TAGS          = Mailbox.ID_FOLDER_TAGS;
  public static final int ID_FOLDER_CONVERSATIONS = Mailbox.ID_FOLDER_CONVERSATIONS;
  public static final int ID_FOLDER_CALENDAR      = Mailbox.ID_FOLDER_CALENDAR;
  public static final int ID_FOLDER_ROOT          = Mailbox.ID_FOLDER_ROOT;
  @Deprecated
  public static final int ID_FOLDER_NOTEBOOK      = Mailbox.ID_FOLDER_NOTEBOOK;
  public static final int ID_FOLDER_AUTO_CONTACTS = Mailbox.ID_FOLDER_AUTO_CONTACTS;
  public static final int ID_FOLDER_IM_LOGS       = Mailbox.ID_FOLDER_IM_LOGS;
  public static final int ID_FOLDER_TASKS         = Mailbox.ID_FOLDER_TASKS;
  public static final int ID_FOLDER_BRIEFCASE     = Mailbox.ID_FOLDER_BRIEFCASE;
  public static final int ID_FOLDER_COMMENTS      = 17;
  public static final int ID_FOLDER_PROFILE       = 18;

  public static final int HIGHEST_SYSTEM_ID = Mailbox.HIGHEST_SYSTEM_ID;
  public static final int FIRST_USER_ID     = Mailbox.FIRST_USER_ID;

  public long getSize()
  {
    return mMbox.getSize();
  }

  public void emptyFolder(ZEOperationContext zContext, int folderId, boolean withDeleteSubFolders)
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

  static class FakeMailbox extends Mailbox
  {
    public FakeMailbox(Account account)
    {
      super(createMailboxMetadata(account));
    }

    private static MailboxData createMailboxMetadata(Account account)
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
      ContactGroup.MigrateContactGroup contactGroup = new ContactGroup.MigrateContactGroup(mMbox);
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

  public ZEMailbox(Object mbox)
  {
    if (mbox == null) throw new IllegalArgumentException("mMbox is null");
    this.mMbox = (Mailbox) mbox;
  }

  public static ZEMailbox createFakeMailbox(ZEAccount realAccount)
  {
    return new ZEMailbox(
      new FakeMailbox(realAccount.toZimbra(Account.class))
    );
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(getMailbox());
  }

  public ZEOperationContext newZimbraAdminContext()
  {
    return new ZEOperationContext(
      new OperationContext(
        new ZEProvisioning(
          Provisioning.getInstance()
        ).getZimbraUser().toZimbra(Account.class)
      )
    );
  }

  public ZEOperationContext newContext()
  {
    try
    {
      return new ZEOperationContext(
        new OperationContext(mMbox)
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public Mailbox getMailbox()
  {
    return mMbox;
  }

  public ZEAccount getAccount()
    throws NoSuchAccountException
  {
    try
    {
      return new ZEAccount(mMbox.getAccount());
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

  public void registerListener(ZEListener listener)
  {
    try
    {
      mMbox.addListener(listener.getStoreContext().toZimbra(Session.class));
    } catch (com.zimbra.common.service.ServiceException e)
    {
      ZimbraLog.mailbox.warn("Error adding listener to mailbox " +
                               mMbox.getId() + ": " +
                               e.getMessage());
    }
  }

  public void unregisterListener(ZEListener listener)
  {
    mMbox.removeListener(listener.getStoreContext().toZimbra(Session.class));
  }

  public void unregisterListener(ZEMailboxSessionProxy session)
  {
    mMbox.removeListener(session.toZimbra(Session.class));
  }

  /* $if MajorZimbraVersion < 8 $
  public static final String sCurrentChangeName = "mCurrentChange";

  public static Field sMailboxChange = null;
  public static Field sMailboxChangeSync = null;
  public static Field sMailboxData = null;
  static
  {
    try
    {
      sMailboxChange = Mailbox.class.getDeclaredField(sCurrentChangeName);
      sMailboxChange.setAccessible(true);

      sMailboxData = Mailbox.class.getDeclaredField("mData");
      sMailboxData.setAccessible(true);

      Class mailboxChangeClass = null;
      for( Class cls : Mailbox.class.getDeclaredClasses() )
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
    }
  }

  public final static int getMailboxSyncCutoff( Mailbox mMbox )
  {
    try
    {
      Object obj = sMailboxChange.get( mMbox );

      Integer sync = (Integer)sMailboxChangeSync.get(obj);
      if( sync == null )
      {
        return ((Mailbox.MailboxData)sMailboxData.get(mMbox)).trackSync;
      }
      else
      {
        return sync;
      }
    }
    catch( Throwable ex )
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Exception: "+Utils.exceptionToString(ex));
      return 0;
    }
  }
  $endif$ */

  public boolean isTombstoneValid( int sequence )
  {
    int mboxSequence;

  /* $if MajorZimbraVersion >= 8 $*/
    mboxSequence = mMbox.getSyncCutoff();
  /* $else$
    mboxSequence = getMailboxSyncCutoff(mMbox);
  $endif$ */

    return mboxSequence > 0 && sequence >= mboxSequence;
  }

  public ZEItem getItemById( ZEOperationContext zContext, int id, byte type )
    throws NoSuchItemException
  {
    MailItem item;
    try
    {
      item = mMbox.getItemById(zContext.getOperationContext(), id, ZEItem.convertType(type));
    }
    catch(com.zimbra.common.service.ServiceException serviceException)
    {
      throw ExceptionWrapper.wrap(serviceException);
    }
    return new ZEItem( item );
  }

  public ZEMessage getMessageById( ZEOperationContext zContext, int id )
    throws NoSuchMessageException
  {
    try
    {
      Message message = mMbox.getMessageById(zContext.getOperationContext(), id);
      return new ZEMessage(message);
    }
    catch (Exception exception)
    {
      throw ExceptionWrapper.wrap(exception);
    }
  }

  public List<ZEMessage> getMessagesByConversation( ZEOperationContext zContext, int id )
    throws NoSuchConversationException
  {
    List<Message> list;
    try
    {
      list = mMbox.getMessagesByConversation(zContext.getOperationContext(), id);
    }
    catch (Exception exception)
    {
      throw ExceptionWrapper.wrap(exception);
    }
    List<ZEMessage> newList = new ArrayList<ZEMessage>(list.size());

    for( Message item : list ){
      newList.add( new ZEMessage(item) );
    }

    return newList;
  }

  public void setDate(ZEOperationContext octxt, int itemId, byte type, long date)
  {
    try
    {
      mMbox.setDate(octxt.getOperationContext(), itemId, ZEItem.convertType(type), date);
    }
    catch(com.zimbra.common.service.ServiceException serviceException)
    {
      throw ExceptionWrapper.wrap(serviceException);
    }
  }

  public List<ZETag> getModifiedTags(ZEOperationContext octxt, int lastSync)
  {
    List<Tag> list;
    try
    {
      list = mMbox.getModifiedTags(octxt.getOperationContext(),lastSync);
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    List<ZETag> newList = new ArrayList<ZETag>(list.size());

    for( MailItem item : list ){
      newList.add( new ZETag(item) );
    }
    return newList;
  }

  public List<Integer> getModifiedItems( ZEOperationContext zContext, int sequence )
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

  public List<Integer> listTombstones( int sequence )
  {
    try
    {
  /* $if ZimbraVersion >= 8.0.7 $ */
      return mMbox.getTombstones(sequence).getAllIds();
  /* $else$
      return mMbox.listTombstones( sequence );
     $endif$ */
    }
    catch(com.zimbra.common.service.ServiceException serviceException)
    {
      throw ExceptionWrapper.wrap(serviceException);
    }
  }

  public ZEFolder getFolderByName( ZEOperationContext zContext, String name, int parentId )
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

    return new ZEFolder(folder);
  }

  public ZEFolder getFolderByPath( ZEOperationContext zContext, String path )
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

    return new ZEFolder(folder);
  }

  public List<ZEFolder> getModifiedFolders( int sequence )
    throws NoSuchFolderException
  {
    List<Folder> folderList;
    try
    {
      folderList = mMbox.getModifiedFolders(sequence);
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    List<ZEFolder> newFolderList = new ArrayList(folderList.size());

    for( Folder folder : folderList ){
      newFolderList.add( new ZEFolder(folder) );
    }

    return newFolderList;
  }
  /*
  public ZEFolder getFolderById( int id )
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
    return new ZEFolder(folder);
  }
  */

  public ZEFolder getFolderById( ZEOperationContext zContext, int id )
    throws NoSuchFolderException
  {
    MailItem folder;
    try
    {
      folder = mMbox.getFolderById(zContext.getOperationContext(), id);
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new ZEFolder(folder);
  }

  public ZECalendarItem getCalendarItemById(ZEOperationContext octxt, int id) throws NoSuchCalendarException
  {
    try
    {
      return new ZECalendarItem(mMbox.getCalendarItemById(octxt.getOperationContext(),id));
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZECalendarItem getCalendarItemByUid(ZEOperationContext octxt, String uid) throws NoSuchCalendarException
  {
    MailItem mailItem;
    try
    {
      mailItem = mMbox.getCalendarItemByUid(octxt.getOperationContext(), uid);
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    if ( mailItem == null )
    {
      return null;
    }

    return new ZECalendarItem(mailItem);
  }

  public void copyCalendarReplyInfo(
    ZECalendarItem fromCalendarItem,
    ZECalendarItem toCalendarItem,
    ZEOperationContext zContext
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

  public void rename( ZEOperationContext zContext, int id, byte type, String name, int folderId)
    throws ZimbraException
  {
    /* $if ZimbraVersion >= 7 $ */
    try
    {
      mMbox.rename(zContext.getOperationContext(), id, ZEItem.convertType(type), name, folderId);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
      throw new UnsupportedOperationException();
       $endif$ */
  }

  public void delete(ZEOperationContext octxt, int itemId, byte type)
    throws ZimbraException
  {
    try
    {
      mMbox.delete(octxt.getOperationContext(), itemId, ZEItem.convertType(type));
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void setPermissions(ZEOperationContext zContext, int folderId, ZEAcl acl)
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

  public void setColor(ZEOperationContext octxt, int[] itemIds, byte type, ZEItem.ZEColor color)
    throws ZimbraException
  {
    try
    {
      mMbox.setColor(octxt.getOperationContext(), itemIds, ZEItem.convertType(type), color.toZimbra(Color.class));
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZECalendarItem setCalendarItem(ZEOperationContext octxt, int folderId, int flags, long tags,
                                      ZECalendarItemData defaultInv,
                                      List<ZECalendarItemData> exceptions,
                                      List<ReplyInfo> replies, long nextAlarm)
    throws ZimbraException
  {
    /* $if MajorZimbraVersion <= 7 $
    Mailbox.SetCalendarItemData[] zimbraExceptions = new Mailbox.SetCalendarItemData[exceptions.size()];
    for (int i=0; i<exceptions.size(); i++)
    {
      zimbraExceptions[i] = exceptions.get(i).toZimbra(Mailbox.SetCalendarItemData.class);
    }

    try
    {
      return new ZECalendarItem(mMbox.setCalendarItem(
        octxt.getOperationContext(),
        folderId,
        flags,
        tags,
        defaultInv.toZimbra(Mailbox.SetCalendarItemData.class),
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

  public ZECalendarItem setCalendarItem(ZEOperationContext octxt, int folderId, int flags, String tags[],
                                      ZECalendarItemData defaultInv,
                                      List<ZECalendarItemData> exceptions,
                                      List<ReplyInfo> replies, long nextAlarm)
    throws ZimbraException
  {
    /* $if MajorZimbraVersion >= 8 $ */
    Mailbox.SetCalendarItemData[] zimbraExceptions = new Mailbox.SetCalendarItemData[exceptions.size()];
    for (int i=0; i<exceptions.size(); i++)
    {
      zimbraExceptions[i] = exceptions.get(i).toZimbra(Mailbox.SetCalendarItemData.class);
    }

    try
    {
      return new ZECalendarItem(
        mMbox.setCalendarItem(
          octxt.getOperationContext(),
          folderId,
          flags,
          tags,
          defaultInv.toZimbra(Mailbox.SetCalendarItemData.class),
          zimbraExceptions,
          replies,
          nextAlarm
        )
      );
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public @Nullable ZEMetadata getConfig(ZEOperationContext octxt, String section) throws ZimbraException
  {
    try
    {
      Metadata metadata = mMbox.getConfig(octxt.getOperationContext(), section);
      if( metadata == null ) {
        return null;
      }
      return new ZEMetadata(metadata);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void setConfig(ZEOperationContext octxt, String section, ZEMetadata config) throws ZimbraException
  {
    try
    {
      mMbox.setConfig(
        octxt.getOperationContext(),
        section,
        config.toZimbra(Metadata.class));
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void alterTag(ZEOperationContext octxt, int itemId, byte type, int tagId, boolean addTag)
    throws ZimbraException
  {
    try
    {
  /* $if MajorZimbraVersion <= 7 $
        mMbox.alterTag(octxt.getOperationContext(), itemId, ZEItem.convertType(type), tagId, addTag, null);
     $else$ */
        mMbox.alterTag(octxt.getOperationContext(), itemId, ZEItem.convertType(type), ZEFlag.of(tagId), addTag, null);
  /* $endif$ */
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void setTags(ZEOperationContext octxt, int itemId, byte type, Collection<String> tags )
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
      item = mMbox.getItemById(octxt.getOperationContext(),itemId, ZEItem.convertType(ZEItem.TYPE_UNKNOWN));
      mMbox.setTags(
        octxt.getOperationContext(),
        itemId,
        ZEItem.convertType(type),
        item.getFlagBitmask(),
        tagsArray
      );
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
       $endif $ */
  }

  public void setTags(ZEOperationContext octxt, int itemId, byte type, long tags )
  {
    /* $if ZimbraVersion < 8.0.0 $
    MailItem item;
    try
    {
      item = mMbox.getItemById(octxt.getOperationContext(),itemId, ZEItem.convertType(ZEItem.TYPE_UNKNOWN));
      mMbox.setTags(octxt.getOperationContext(),itemId,ZEItem.convertType(type), item.getFlagBitmask(),tags);
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $ */
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void setFlags(ZEOperationContext octxt, int itemId, byte type, int flags )
    throws ZimbraException
  {
    try
    {
      MailItem item = mMbox.getItemById(octxt.getOperationContext(), itemId, ZEItem.convertType(ZEItem.TYPE_UNKNOWN));
      mMbox.setTags(octxt.getOperationContext(), itemId, ZEItem.convertType(type), flags,
  /* $if MajorZimbraVersion >= 8 $ */
  item.getTags()
  /* $else$
                   item.getTagBitmask()
    $endif$ */
      );
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void modifyContact(ZEOperationContext octxt, int contactId, ZEParsedContact pc)
    throws ZimbraException
  {
    try
    {
      mMbox.modifyContact(
        octxt.getOperationContext(),
        contactId,
        pc.toZimbra(ParsedContact.class)
      );
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZimbraItemId sendMimeMessage(ZEOperationContext octxt, Boolean saveToSent, MimeMessage mm,
                                List<Upload> uploads,
                                ZimbraItemId origMsgId, String replyType,
                                boolean replyToSender)
    throws ZimbraException
  {

    if( origMsgId != null )
    {
      ItemId itemId = new ItemId(origMsgId.getAccountId().toString(), origMsgId.getItemId());
      ItemId newItemId;
      try
      {
  /* $if MajorZimbraVersion == 6 $
        newItemId = mMbox.getMailSender().sendMimeMessage( octxt.getOperationContext(), mMbox, saveToSent, mm,
                                                   (List<InternetAddress>)null,
                                                   uploads, itemId, replyType,
                                                   null, true, replyToSender );
    $endif$ */

  /* $if MajorZimbraVersion == 7 $
        newItemId = mMbox.getMailSender().sendMimeMessage( octxt.getOperationContext(), mMbox, saveToSent, mm,
                                                   (List<InternetAddress>)null,
                                                   uploads, itemId, replyType,
                                                   null, replyToSender );
    $endif$ */

  /* $if MajorZimbraVersion == 8 $ */
        newItemId = mMbox.getMailSender().sendMimeMessage(octxt.getOperationContext(), mMbox, saveToSent, mm,
                                                    uploads, itemId, replyType, null, replyToSender);
  /* $endif$ */
        return new ZimbraItemId(newItemId.getAccountId(), newItemId.getId());
      }
      catch(com.zimbra.common.service.ServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
    }
    return null;
  }

  public boolean attachmentsIndexingEnabled() throws ZimbraException
  {
    try
    {
      return mMbox.attachmentsIndexingEnabled();
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void move(ZEOperationContext octxt, int itemId, byte type, int targetId)
    throws ZimbraException
  {
    try
    {
      mMbox.move(octxt.getOperationContext(), itemId, ZEItem.convertType(type), targetId);
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<ZECalendarItem> getCalendarItemsForRange(ZEOperationContext octxt, byte type, long start,
                                                     long end, int folderId, int[] excludeFolders)
    throws ZimbraException
  {
    try
    {
      List<CalendarItem> zimbraCalendarItems = mMbox.getCalendarItemsForRange(
        octxt.getOperationContext(),
        ZEItem.convertType(type),
        start,
        end,
        folderId,
        excludeFolders
      );

      return ZimbraListWrapper.wrapCalendarItems(zimbraCalendarItems);
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
  public List<Integer> getItemListByDates(ZEOperationContext octxt, byte type, long start,
                                          long end, int folderId, boolean descending)
    throws ZimbraException
  {
    List<Integer> itemIds;

    try
    {
/* $if ZimbraVersion < 7.2.2 |! ZimbraVersion == 8.0.0 |! ZimbraVersion == 8.0.1 $
    itemIds = mMbox.getItemListByDates(octxt.getOperationContext(), ZEItem.convertType(type),
                                      start, end, folderId, descending);
  $elseif ZimbraVersion < 8.5.0 $

      DbMailItem.SearchOpts options = new DbMailItem.SearchOpts(start, end, descending);
      itemIds = mMbox.getItemIdList(octxt.getOperationContext(), ZEItem.convertType(type),
                                   folderId, options);
   $else $ */
    DbMailItem.QueryParams options = new DbMailItem.QueryParams();
    options.setFolderIds(Collections.singletonList(folderId));
    options.setIncludedTypes(Collections.singletonList(ZEItem.convertType(type)));
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
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<Integer> listItemIds(ZEOperationContext octxt, byte type, int folderId)
    throws NoSuchItemException
  {
    try
    {
      return mMbox.listItemIds(octxt.getOperationContext(),ZEItem.convertType(type),folderId);
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public Iterator<Map.Entry<Byte, List<Integer>>> getItemIds(ZEOperationContext octxt, int folderId)
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
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void modifyPartStat(ZEOperationContext octxt, int calItemId,
                                          ZERecurId recurId, String cnStr,
                                          String addressStr, String cutypeStr,
                                          String roleStr, String partStatStr,
                                          Boolean rsvp, int seqNo, long dtStamp)
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
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZETag getTagById(ZEOperationContext octxt, int itemId)
    throws NoSuchItemException
  {
    MailItem tag;
    try
    {
      tag = mMbox.getTagById(octxt.getOperationContext(),itemId);
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    return new ZETag(tag);
  }

  public ZETag getTagByName(ZEOperationContext octxt, String name)
      throws NoSuchItemException
  {
    try
    {
/* $if MajorZimbraVersion >= 8 $ */
      return new ZETag(mMbox.getTagByName(octxt.getOperationContext(), name));
/* $else$
      return new ZETag(mMbox.getTagByName(name));
   $endif */
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void setCustomData(ZEOperationContext octxt, int itemId, byte type, ZECustomMetadata custom)
    throws ZimbraException
  {
    try
    {
      mMbox.setCustomData(
        octxt.getOperationContext(),
        itemId,
        ZEItem.convertType(type),
        custom.toZimbra(MailItem.CustomMetadata.class)
      );
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZimbraItemId nextItemId()
  {
    return new ZimbraItemId(mMbox.getAccountId(), 0);
  }

  public ZEOperationContext newOperationContext() throws ZimbraException
  {
    try
    {
      return new ZEOperationContext( new OperationContext(mMbox) );
    }
    catch(com.zimbra.common.service.ServiceException e)
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
    catch(com.zimbra.common.service.ServiceException e)
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
    mMbox.purge(ZEItem.convertType(ZEItem.TYPE_UNKNOWN));
  }

  public ZEQueryResults search(ZEOperationContext octxt, String queryString, byte[] types, ZESortBy sortBy, int chunkSize)
    throws IOException, ZimbraException
  {
    try
    {
/* $if MajorZimbraVersion >= 8 $ */
      Set<MailItem.Type> typeList = new HashSet(types.length);
      for (byte type : types)
      {
        typeList.add(ZEItem.convertType(type));
      }
      return new ZEQueryResults(
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
      return new ZEQueryResults(
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
    catch(Exception e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<ZEItem> getItemList( byte type, ZEOperationContext zContext )
    throws ZimbraException
  {
    List<ZEItem> result = new LinkedList<ZEItem>();

/* $if MajorZimbraVersion <= 7 $
    synchronized( mMbox )
    {
 $endif$ */
      beginTransaction("ZxGetItemList", zContext.getOperationContext());
      try
      {
        if( type == ZEItem.TYPE_FOLDER || type == ZEItem.TYPE_SEARCHFOLDER || type == ZEItem.TYPE_MOUNTPOINT )
        {
          final Collection<ZEFolder> folderCache = getFolderCache();
          for( ZEFolder subfolder : folderCache )
          {
            if( subfolder.getType() == type || type == ZEItem.TYPE_FOLDER )
            {
              result.add( subfolder );
            }
          }
        }
        else if( type == ZEItem.TYPE_TAG )
        {
          final Map<Object,Tag> tagCache = getTagCache();
          for( Map.Entry<Object, Tag> entry : tagCache.entrySet() )
          {
            if( entry.getKey() instanceof String ) {
              result.add( new ZEItem(entry.getValue()) );
            }
          }
        }
        else if( type == ZEItem.TYPE_FLAG )
        {
          for( MailItem item : getAllFlags() ) {
            result.add( new ZEItem(item) );
          }
          return result;
        }
        else
        {
          List<ZEItem.ZEUnderlyingData> dataList = ZimbraDatabase.getByType(this, type, SortBy.NONE);

          if( dataList == null ) {
            return Collections.emptyList();
          }

          for( ZEItem.ZEUnderlyingData data : dataList )
          {
            if (data != null)
            {
              try
              {
                result.add( rawGetItem(data) );
              }
              catch( Throwable ex )
              {
                ZimbraLog.mailbox.debug("getItemList(): skipping item: "+Utils.exceptionToString(ex) );
              }
            }
          }
        }
      }
      finally {
        endTransaction(true);
      }
/* $if MajorZimbraVersion <= 7 $
    }
 $endif$ */

    return result;
  }

  public ZEFolder createFolder(ZEOperationContext octxt, String name, int parentId,
                                          byte attrs, byte defaultView, int flags,
                                          ZEItem.ZEColor color, String url)
    throws ZimbraException
  {
    MailItem folder;
    try
    {
      folder = mMbox.createFolder(octxt.getOperationContext(),
                              name,
                              parentId,
                              attrs,
  /* $if MajorZimbraVersion >= 7 $ */
                              ZEItem.convertType(defaultView == ZEItem.TYPE_WIKI ? ZEItem.TYPE_DOCUMENT : defaultView),
  /* $else$
                              ZEItem.convertType(defaultView),
    $endif $ */
                              flags,
                              color.toZimbra(Color.class),
                              url
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new ZEFolder(folder);
  }

  public ZESearchFolder createSearchFolder(ZEOperationContext octxt, int folderId, String name,
                                         String query, String types, String sort,
                                         int flags, ZEItem.ZEColor color)
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
        color.toZimbra(Color.class));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new ZESearchFolder(item);
  }

  public ZETag createTag(ZEOperationContext octxt, String name, ZEItem.ZEColor color)
    throws ZimbraException
  {
    MailItem tag;
    try
    {
      tag = mMbox.createTag(
        octxt.getOperationContext(),
        name,
        color.toZimbra(Color.class)
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new ZETag(tag);
  }

  public ZEMessage addMessage(ZEOperationContext octxt, InputStream in, int sizeHint, Long receivedDate,
                              int folderId, boolean noIcal,
                              int flags, long tags, int conversationId, String rcptEmail,
                              ZECustomMetadata customData)
    throws IOException, ZimbraException
  {
    /* $if ZimbraVersion <= 7 $
    return addMessage(octxt, in, sizeHint, receivedDate, folderId, noIcal, flags, Tag.bitmaskToTags(tags),
                      conversationId, rcptEmail, customData);
    /* $else $ */
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public ZEMessage addMessage(ZEOperationContext octxt, InputStream in, int sizeHint, Long receivedDate,
                              int folderId, boolean noIcal,
                              int flags, String tags, int conversationId, String rcptEmail,
                              ZECustomMetadata customData)
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

    return new ZEMessage(message);
    /* $else $ */
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public ZEMessage addMessage(ZEOperationContext octxt, InputStream in, int sizeHint, Long receivedDate,
                            int folderId, boolean noIcal,
                            int flags, Collection<String> tags, int conversationId, String rcptEmail,
                            ZECustomMetadata customData)
    throws IOException, ZimbraException
{
    /* $if MajorZimbraVersion >= 8 $ */
    DeliveryOptions opts = new DeliveryOptions();
    opts.setFolderId(folderId);
    opts.setNoICal(noIcal);
    opts.setFlags( flags );
    opts.setTags( tags );
    opts.setConversationId( conversationId );
    opts.setRecipientEmail( rcptEmail );
    opts.setDraftInfo( null );
    if( customData != null ) {
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

    return new ZEMessage(message);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public ZEMessage simpleAddMessage(
    ZEOperationContext octxt,
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
    return new ZEMessage(message);
  }


  public ZEContact createContact(ZEOperationContext octxt, ZEParsedContact pc, int folderId, String tags)
    throws ZimbraException
  {
    /* $if MajorZimbraVersion <= 7 $
    MailItem contact;
    try
    {
      contact = mMbox.createContact(
        octxt.getOperationContext(),
        pc.toZimbra(ParsedContact.class),
        folderId,
        tags
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new ZEContact(contact);
    /* $else $ */
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public ZEContact createContact(ZEOperationContext octxt, ZEParsedContact pc, int folderId, Collection<String> tags)
    throws ZimbraException
  {
    /* $if MajorZimbraVersion >= 8 $ */
    MailItem contact;
    try
    {
      contact = mMbox.createContact(
        octxt.getOperationContext(),
        pc.toZimbra(ParsedContact.class),
        folderId,
        tags.toArray(new String[tags.size()])
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new ZEContact(contact);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public ZEDocument createDocument(
    ZEOperationContext octxt,
    int folderId,
    ZEParsedDocument pd,
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
        pd.toZimbra(ParsedDocument.class),
        ZEItem.convertType(type),
        flags
      );
/* $else$
      document = mMbox.createDocument(
        octxt.getOperationContext(),
        folderId,
        pd.toZimbra(ParsedDocument.class),
        ZEItem.convertType(type)
      );
  $endif$  */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new ZEDocument(document);
  }

  public ZEDocument simpleCreateDocument(
    ZEOperationContext octxt,
    int folderId,
    FileInputStream fileInputStream,
    String filename,
    String mimetype,
    long timestamp
  )
    throws IOException, ZimbraException
  {
    ZEParsedDocument document = new ZEParsedDocument(
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
      ZEItem.TYPE_DOCUMENT,
      0
    );
  }

  public ZEDocument addDocumentRevision(
    ZEOperationContext octxt,
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

    return new ZEDocument(document);
  }

  public ZEDocument createDocument(ZEOperationContext octxt, int folderId,
                                 String filename, String mimeType,
                                 String author, String description,
                                 boolean descEnabled,
                                 InputStream data, byte type )
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
        ZEItem.convertType(ZEItem.TYPE_DOCUMENT)
  /* $else$
        ZEItem.convertType(type)
    $endif$ */
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new ZEDocument(document);
  }

  public ZENote createNote(ZEOperationContext octxt, String content,
                         ZENote.ZERectangle rectangle, ZEItem.ZEColor color,
                         int folderId)
    throws ZimbraException
  {
    MailItem note;
    try
    {
      note = mMbox.createNote(
        octxt.getOperationContext(),
        content,
        rectangle.getZimbraRectangle(),
        color.toZimbra(Color.class),
        folderId);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new ZENote(note);
  }

  public int addInvite(ZEOperationContext octxt, ZEInvite inv,
                                 int folderId, ZEParsedMessage pm,
                                 boolean preserveExistingAlarms,
                                 boolean discardExistingInvites,
                                 boolean addRevision )
    throws ZimbraException
  {
    try
    {
      return mMbox.addInvite(octxt.getOperationContext(), inv.toZimbra(Invite.class),
                            folderId, pm.toZimbra(ParsedMessage.class),
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
  public ZEMountpoint createMountpoint(ZEOperationContext octxt, int folderId,
                                     String name, String ownerId,
                                     int remoteId, String remoteUuid,
                                     byte view, int flags,
                                     ZEItem.ZEColor color)
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
                                         color.toZimbra(Color.class)
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new ZEMountpoint(mountPoint);
  }
  $else$ */
  public ZEMountpoint createMountpoint(ZEOperationContext octxt, int folderId,
                                 String name, String ownerId,
                                 int remoteId, String remoteUuid,
                                 byte view, int flags,
                                 ZEItem.ZEColor color)
    throws ZimbraException
  {
    MailItem mountPoint;
    try
    {
      mountPoint = mMbox.createMountpoint(octxt.getOperationContext(), folderId,
                                         name, ownerId,
                                         remoteId,
                                         remoteUuid,
                                         ZEItem.convertType(view),
                                         flags, color.toZimbra(Color.class),
                                         false
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new ZEMountpoint(mountPoint);
  }
/* $endif$ */


  public ZEChat createChat(ZEOperationContext octxt,
                         ZEParsedMessage pm,
                         int folderId, int flags,
                         ZETags tags)
    throws ZimbraException, IOException
  {
    MailItem chat;
    try
    {
      /* $if MajorZimbraVersion >= 8 $ */
      chat = mMbox.createChat(
        octxt.getOperationContext(),
        pm.toZimbra(ParsedMessage.class),
        folderId,
        flags,
        tags.getTags()
      );
      /* $else $
      chat = mMbox.createChat(
        octxt.getOperationContext(),
        pm.toZimbra(ParsedMessage.class),
        folderId,flags,
        Tag.bitmaskToTags(tags.getLongTags())
        );
      /* $endif $ */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new ZEChat(chat);
  }

  public ZEChat createChat(ZEOperationContext octxt,
                           ZEParsedMessage pm,
                           int folderId, int flags)
      throws ZimbraException, IOException
  {
    MailItem chat;
    try
    {
      chat = mMbox.createChat(
        octxt.getOperationContext(),
        pm.toZimbra(ParsedMessage.class),
        folderId,
        flags,
        null);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new ZEChat(chat);
  }

  public ZEComment createComment( ZEOperationContext octxt,
                               int parentId,
                               String text,
                               String creator )
    throws ZimbraException
  {
    /* $if MajorZimbraVersion >= 8 $ */
    MailItem comment;
    try
    {
      comment = mMbox.createComment(octxt.getOperationContext(),parentId,text,creator);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new ZEComment(comment);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif$ */
  }

  public ZELink createLink( ZEOperationContext octxt,
                            int parentId,
                            String name,
                            String ownerId,
                            int remoteId )
    throws ZimbraException
  {
    /* $if MajorZimbraVersion >= 8 $ */
    MailItem link;
    try
    {
      link = mMbox.createLink(octxt.getOperationContext(),parentId,name,ownerId,remoteId);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new ZELink(link);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif$ */
  }

  private static Method sEndTransactionMethod = null;
  static
  {
    try
    {
      Class partypes[] = new Class[1];
      partypes[0] = boolean.class;

      sEndTransactionMethod = Mailbox.class.getDeclaredMethod("endTransaction", partypes);
      sEndTransactionMethod.setAccessible(true);
    }
    catch( Throwable ex ){
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: "+Utils.exceptionToString(ex));
    }
  }

  private static Method sBeginTransactionMethod = null;
  static
  {
    try
    {
      Class partypes[] = new Class[2];
      partypes[0] = String.class;
      partypes[1] = OperationContext.class;

      sBeginTransactionMethod = Mailbox.class.getDeclaredMethod("beginTransaction", partypes);
      sBeginTransactionMethod.setAccessible(true);
    }
    catch( Throwable ex ){
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: "+Utils.exceptionToString(ex));
    }
  }

  public void beginTransaction(String name, ZEOperationContext context)
  {
    beginTransaction(name, context.getOperationContext());
  }

  private final void beginTransaction( String name, OperationContext zContext )
    throws ZimbraException
  {
    try
    {
      Object parameters[] = new Object[2];
      parameters[0] = name;
      parameters[1] = zContext;

      sBeginTransactionMethod.invoke(mMbox, parameters );
    }
    catch( Exception ex )
    {
      throw ExceptionWrapper.wrap(ex);
    }
  }

  public final void endTransaction( boolean success )
    throws ZimbraException
  {
    Object parameters[] = new Object[1];
    parameters[0] = success;

    try
    {
      sEndTransactionMethod.invoke(mMbox, parameters );
    }
    catch( Exception ex )
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

      sRawGetItem = Mailbox.class.getDeclaredMethod("getItem", partypes);
      sRawGetItem.setAccessible(true);
    }
    catch( Throwable ex ){
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: "+Utils.exceptionToString(ex));
    }
  }

/*
 * Warning: unsynchronized private access to mailbox
 */
  private final ZEItem rawGetItem( ZEItem.ZEUnderlyingData data )
    throws InternalServerException
  {
    Object parameters[] = new Object[1];
    parameters[0] = data.toZimbra(MailItem.UnderlyingData.class);

    try
    {
      return new ZEItem((MailItem)sRawGetItem.invoke(mMbox, parameters ));
    }
    catch( Throwable ex )
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
      partypes[0] = Mailbox.class;

/* $if MajorZimbraVersion < 8 $
      sGetAllFlags = Flag.class.getDeclaredMethod("getAllFlags", partypes);
  $else$ */
      sGetAllFlags = Flag.class.getDeclaredMethod("allOf", partypes);
/* $endif$ */
      sGetAllFlags.setAccessible(true);
    }
    catch( Throwable ex ){
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: "+Utils.exceptionToString(ex));
    }
  }

/*
 * Warning: unsynchronized private access to mailbox
 */
  private final List<Flag> getAllFlags()
    throws ZimbraException
  {
    Object parameters[] = new Object[1];
    parameters[0] = mMbox;

    try
    {
      return (List<Flag>)sGetAllFlags.invoke( null, parameters );
    }
    catch( Throwable ex )
    {
      return null;
    }
  }

  public final static boolean ACLIsEmpty( ZEAcl acl )
  {
    if( acl == null ) return true;
    return acl.isEmpty();
  }

  private static Field sTagCache;
  static
  {
    try {
      sTagCache = Mailbox.class.getDeclaredField("mTagCache");
      sTagCache.setAccessible(true);
    }
    catch( Throwable ex ) {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: "+Utils.exceptionToString(ex));
    }
  }

/*
 * Warning: unsynchronized private access to mailbox
 */
  private final Map<Object,Tag> getTagCache()
  {
    try {
      return (Map<Object,Tag>)sTagCache.get(mMbox);
    }
    catch( Throwable ex ) {
      return null;
    }
  }

  private static Field sFolderCache;
  static
  {
    try {
      sFolderCache = Mailbox.class.getDeclaredField("mFolderCache");
      sFolderCache.setAccessible(true);
    }
    catch( Throwable ex ) {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: "+Utils.exceptionToString(ex));
    }
  }

/* $if MajorZimbraVersion >= 8 $ */
  private static Field sFolderCacheMap;
  static
  {
    try {
      Class cls = null;
      Class subClasses[] = Mailbox.class.getDeclaredClasses();

      for( int n=0; n < subClasses.length; ++n )
      {
        if( subClasses[n].getName().equals("com.zimbra.cs.mailbox.Mailbox$FolderCache") )
        {
          cls = subClasses[n];
          break;
        }
      }
      if (cls == null)
      {
        ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " +
                                   "com.zimbra.cs.mailbox.Mailbox$FolderCache not found");
      }
      else
      {
        //do not avoid the exception
        sFolderCacheMap = cls.getDeclaredField("mapById");
        sFolderCacheMap.setAccessible(true);
      }
    }
    catch( Throwable ex ) {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: "+Utils.exceptionToString(ex));
    }
  }
/* $endif$ */

/*
 * Warning: unsynchronized private access to mailbox
 */
  private final Collection<ZEFolder> getFolderCache()
  {
    try {

/* $if MajorZimbraVersion >= 8 $ */
      Collection<Folder> list = (Collection<Folder>)(((Map<Integer,Folder>)sFolderCacheMap.get(sFolderCache.get(mMbox))).values());
/* $else$
      Collection<Folder> list = (Collection<Folder>)((Map<Integer,Folder>)sFolderCache.get(mMbox)).values();
  $endif$ */

       ArrayList<ZEFolder> newList = new ArrayList<ZEFolder>(list.size());

       for( Folder folder : list ){
         newList.add( new ZEFolder(folder) );
       }

       return newList;
    }
    catch( Throwable ex ) {
      ZimbraLog.mailbox.error("Exception: "+Utils.exceptionToString(ex));
      return null;
    }
  }

  public static ZEMailbox getByAccount( ZEAccount account ) throws ZimbraException
  {
    return getByAccount(account, true);
  }

  public static ZEMailbox getByAccountId( String id ) throws ZimbraException
  {
    return getByAccountId(id, true);
  }

  public static ZEMailbox getByAccountId( String id, boolean autocreate )
    throws ZimbraException
  {
    Mailbox mbox;
    try
    {
      mbox = MailboxManager.getInstance().getMailboxByAccountId(
          id,
          autocreate ? MailboxManager.FetchMode.AUTOCREATE : MailboxManager.FetchMode.DO_NOT_AUTOCREATE
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    if( mbox != null ){
      return new ZEMailbox(mbox);
    }

    return null;
  }

  @Deprecated
  public static ZEMailbox getByAccount( ZEAccount account, boolean autocreate )
    throws ZimbraException
  {
    Mailbox mbox;
    try
    {
      mbox = MailboxManager.getInstance().getMailboxByAccount(account.toZimbra(Account.class));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    if( mbox != null ) {
      return new ZEMailbox(mbox);
    }
    return null;
  }

  @Deprecated
  public static ZEMailbox getById( long mboxId )
    throws ZimbraException
  {
    return getById((int) mboxId);
  }

  @Deprecated
  public static ZEMailbox getById( int mboxId )
    throws ZimbraException
  {
    Mailbox mbox;
    try
    {
      mbox = MailboxManager.getInstance().getMailboxById( mboxId );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    if( mbox != null ) {
      return new ZEMailbox(mbox);
    }
    return null;
  }

  @Deprecated
/* $if MajorZimbraVersion < 7 $
  public static long mailboxId( int mboxId )
  {
    return (long)mboxId;
  }
$else$ */
  public static int mailboxId( int mboxId )
  {
    return mboxId;
  }
/* $endif$ */

  @Deprecated
  public static ZEMailbox getByItem( ZEItem item )
  {
    return item.getMailbox();
  }

  @Deprecated
  public static Map<String, Integer> getMapAccountsAndMailboxes(Connection conn) throws ZimbraException
  {
    Map<String, Integer> accountsAndMailboxes = new HashMap<String, Integer>();
    try
    {
      /* $if MajorZimbraVersion < 7 $
      Map<String, Long> result = DbMailbox.listMailboxes(conn.getProxiedConnection());
      for (Map.Entry<String, Long> entry : result.entrySet())
      {
        accountsAndMailboxes.put(entry.getKey(), entry.getValue().intValue());
      }
      /* $else $ */
      accountsAndMailboxes = DbMailbox.listMailboxes(conn.getProxiedConnection());
      /* $endif $ */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return accountsAndMailboxes;
  }

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

  public int getSchemaGroupId() {
    //leave the cast
    return (int) mMbox.getSchemaGroupId();
  }

  public void updateChat(ZEOperationContext operationContext, ZEParsedMessage parsedMessage, int id)
    throws IOException, ZimbraException
  {
    try
    {
      mMbox.updateChat(
        operationContext.getOperationContext(),
        parsedMessage.toZimbra(ParsedMessage.class),
        id
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

/* $if MajorZimbraVersion >= 8 $ */
  public void reindexItem(ZEItem item) throws ZimbraException
  {
    List list = new ArrayList<Integer>();

    list.add( item.getId() );
    try
    {
      mMbox.index.startReIndexById( list );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

/* $else$
  public void reindexItem(ZEItem item) throws ZimbraException {}
  $endif$ */

  public String rawGetConfig(
    @NotNull String key
  ) throws SQLException, ZimbraException
  {
    String query = "SELECT metadata FROM "+DbMailbox.qualifyZimbraTableName(mMbox, "mailbox_metadata")+" WHERE mailbox_id=? AND section=? LIMIT 1";
    Connection connection = null;
    try
    {
      connection = ZimbraDatabase.legacyGetConnection();
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, getId());
      statement.setString(2, key);

      ResultSet resultSet = statement.executeQuery();
      if( resultSet.next() )
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
      if( connection != null ){
        connection.close();
      }
    }
  }

  public static final  long   MAX_METADATA_SIZE = 16777215;

  public void rawSetConfig(
    @NotNull String section,
    @NotNull String metadata
  ) throws SQLException, ZimbraException
  {
    if( metadata.length() > MAX_METADATA_SIZE )
    {
      throw new SQLException("metadata is too big to be saved");
    }

    String updateQuery = "UPDATE "+DbMailbox.qualifyZimbraTableName(mMbox, "mailbox_metadata")+" SET metadata=? WHERE mailbox_id=? AND section=? LIMIT 1";
    Connection connection = null;
    try
    {
      connection = ZimbraDatabase.legacyGetConnection();

      PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
      updateStatement.setString(1, metadata);
      updateStatement.setInt(2, getId());
      updateStatement.setString(3, section);

      int res = updateStatement.executeUpdate();
      if( res == 0 )
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
      if( connection != null ){
        connection.close();
      }
    }
  }
}
