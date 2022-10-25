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
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.Pair;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.db.DbMailbox;
import com.zimbra.cs.db.DbPool;
import com.zimbra.cs.fb.FreeBusyQuery;
import com.zimbra.cs.index.SearchParams;
import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.index.ZimbraQueryResults;
import com.zimbra.cs.mailbox.ACL;
import com.zimbra.cs.mailbox.CalendarItem.ReplyInfo;
/* $if ZimbraX == 1 $
import com.zimbra.cs.mailbox.cache.FolderCache;
import com.zimbra.cs.mailbox.cache.LocalTagCache;
import com.zimbra.cs.mailbox.cache.RedisTagCache;
/* $endif $ */
import com.zimbra.cs.mailbox.DeliveryOptions;
import com.zimbra.cs.mailbox.Folder.FolderOptions;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailItem.Type;
import com.zimbra.cs.mailbox.Mailbox.DeleteBlobs;
import com.zimbra.cs.mailbox.calendar.RecurId;
import com.zimbra.cs.mailbox.util.TypedIdList;
import com.zimbra.cs.redolog.RedoLogManager;
import com.zimbra.cs.redolog.op.SetConfig;
import com.zimbra.cs.service.FileUploadServlet.Upload;
import com.zimbra.cs.service.mail.ItemActionHelper;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.session.Session;
import java.util.Objects;
import org.apache.commons.dbutils.DbUtils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openzal.zal.calendar.CalendarItemData;
import org.openzal.zal.calendar.Invite;
import org.openzal.zal.calendar.RecurrenceId;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.InternalServerException;
import org.openzal.zal.exceptions.NoSuchAccountException;
import org.openzal.zal.exceptions.NoSuchCalendarException;
import org.openzal.zal.exceptions.NoSuchConversationException;
import org.openzal.zal.exceptions.NoSuchFolderException;
import org.openzal.zal.exceptions.NoSuchFreeBusyException;
import org.openzal.zal.exceptions.NoSuchItemException;
import org.openzal.zal.exceptions.NoSuchMessageException;
import org.openzal.zal.exceptions.PermissionDeniedException;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.lib.ZimbraConnectionWrapper;
import org.openzal.zal.lib.ZimbraDatabase;
import org.openzal.zal.log.ZimbraLog;

import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openzal.zal.redolog.RedoLogProvider;
import org.openzal.zal.redolog.op.RawSetConfig;

//import com.zimbra.cs.fb.FreeBusy;

/* $if ZimbraVersion != 8.0.1 && ZimbraVersion != 8.0.0 && ZimbraVersion < 8.5.0 $
import com.zimbra.cs.db.DbMailItem.SearchOpts;
/* $endif$ */


public class Mailbox
{
  @Nonnull final private com.zimbra.cs.mailbox.Mailbox mMbox;
  @Nonnull private final MailboxIndex mIndex;

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

  private static final Set<String> CREATE_CALENDAR_ITEM_ALLOWED_METHODS = new HashSet<>(Arrays.asList("PUBLISH", "REQUEST"));

  private static Method sCreateDefaultFlags;

  static
  {
    try
    {
      sCreateDefaultFlags = com.zimbra.cs.mailbox.Mailbox.class.getDeclaredMethod("createDefaultFlags");
      sCreateDefaultFlags.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  public MailboxMaintenance getMaintenance()
  {
    return new MailboxMaintenance(mMbox.getMaintenance());
  }

  public long getSize()
  {
    return mMbox.getSize();
  }

  public static int getHighestSystemId()
  {
    return HIGHEST_SYSTEM_ID;
  }

  public void emptyFolder(@Nonnull OperationContext zContext, int folderId, boolean withDeleteSubFolders)
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

  public void deleteRevision(@Nonnull OperationContext zContext, int itemId, int revision)
  {
    try
    {
      mMbox.purgeRevision(
        zContext.getOperationContext(),
        itemId,
        revision,
        false
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public MailboxData getMailboxData()
  {
    return new MailboxData(
      getId(),
      getSchemaGroupId(),
      getAccountId(),
      getIndexVolume()
    );
  }

  public void deleteFromDumpster(OperationContext newOperationContext, int[] ids) {
    try {
      mMbox.deleteFromDumpster(newOperationContext.getOperationContext(), ids);
    } catch( ServiceException e ) {
      throw ExceptionWrapper.wrap(e);
    }
  }

  static class FakeMailbox extends com.zimbra.cs.mailbox.Mailbox
  {
    public FakeMailbox(@Nonnull com.zimbra.cs.account.Account account)
    {
      super(createMailboxMetadata(account));
    }

    public FakeMailbox(long id, String accountId, int schemaGroupId)
    {
      super(createMailboxMetadata((int)id, accountId, schemaGroupId));
    }

    @Nonnull
    private static MailboxData createMailboxMetadata(
      @Nonnull
        com.zimbra.cs.account.Account account
    )
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

    @Nonnull
    private static MailboxData createMailboxMetadata(int id, String accountId, int schemaGroupId)
    {
      com.zimbra.cs.mailbox.Mailbox.MailboxData data = new com.zimbra.cs.mailbox.Mailbox.MailboxData();
      data.id = id;
      data.schemaGroupId = schemaGroupId;
      data.accountId = accountId;
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
  }

  public Mailbox(@Nullable Object mbox)
  {
    if (mbox == null)
    {
      throw new IllegalArgumentException("mMbox is null");
    }
    this.mMbox = (com.zimbra.cs.mailbox.Mailbox) mbox;

    mIndex = new MailboxIndex(this, this.mMbox.index);
  }

  @Nonnull
  public static Mailbox createFakeMailbox(@Nonnull Account realAccount)
  {
    return new Mailbox(
      new FakeMailbox(realAccount.toZimbra(com.zimbra.cs.account.Account.class))
    );
  }

  @Nonnull
  public static Mailbox createFakeMailbox(long id, String accountId, int schemaGroupId)
  {
    return new Mailbox(
      new FakeMailbox(id, accountId, schemaGroupId)
    );
  }

  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(getMailbox());
  }

  @Nonnull
  public OperationContext newZimbraAdminContext()
  {
    return new OperationContext(
      new com.zimbra.cs.mailbox.OperationContext(
        new ProvisioningImp(
          com.zimbra.cs.account.Provisioning.getInstance()
        ).getZimbraUser().toZimbra(com.zimbra.cs.account.Account.class)
      ,true)
    );
  }

  @Nonnull
  public com.zimbra.cs.mailbox.Mailbox getMailbox()
  {
    return mMbox;
  }

  @Nonnull
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
    /* $if ZimbraX == 1 $
    return null;
    /* $else $ */
    return mMbox.getListener(listenerName);
    /* $endif $ */
  }

  public void registerListener(@Nonnull Listener listener)
  {
    /* $if ZimbraX == 1 $
    return;
    /* $else $ */
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
    /* $endif $ */
  }

  public void unregisterListener(@Nonnull Listener listener)
  {
    /* $if ZimbraX == 1 $
    return;
    /* $else $ */
    mMbox.removeListener(listener.getStoreContext().toZimbra(Session.class));
    /* $endif $ */
  }

  public void unregisterListener(@Nonnull MailboxSessionProxy session)
  {
    /* $if ZimbraX == 1 $
    return;
    /* $else $ */
    mMbox.removeListener(session.toZimbra(Session.class));
    /* $endif $ */
  }

  private static int getMailboxSyncCutoff(@Nonnull com.zimbra.cs.mailbox.Mailbox mMbox)
  {
    return mMbox.getSyncCutoff();
  }

  public boolean isTombstoneValid(int sequence)
  {
    int mboxSequence = getMailboxSyncCutoff(mMbox);
    return mboxSequence > 0 && sequence >= mboxSequence;
  }

  @Nonnull
  public Item getItemById(@Nonnull OperationContext zContext, int id, byte type)
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

  @Nonnull
  public Item getItemByUuId(@Nonnull OperationContext zContext, String uuid, byte type, boolean fromDumpster)
    throws NoSuchItemException
  {
    MailItem item;
    try
    {
      item = mMbox.getItemByUuid(zContext.getOperationContext(), uuid, Item.convertType(type),fromDumpster);
    }
    catch (com.zimbra.common.service.ServiceException serviceException)
    {
      throw ExceptionWrapper.wrap(serviceException);
    }
    return new Item(item);
  }

  @Nonnull
  public Document getDocumentById(@Nonnull OperationContext zContext, int id)
    throws NoSuchItemException
  {
    MailItem item;
    try
    {
      item = mMbox.getDocumentById(zContext.getOperationContext(), id);
    }
    catch (com.zimbra.common.service.ServiceException serviceException)
    {
      throw ExceptionWrapper.wrap(serviceException);
    }
    return new Document(item);
  }

  @Nonnull
  public Document getDocumentByUuid(@Nonnull OperationContext zContext, String uuid)
    throws NoSuchItemException
  {
    MailItem item;
    try
    {
      item = mMbox.getDocumentByUuid(zContext.getOperationContext(), uuid);
    }
    catch (com.zimbra.common.service.ServiceException serviceException)
    {
      throw ExceptionWrapper.wrap(serviceException);
    }
    return new Document(item);
  }


  @Nonnull
  public Item getItemByIdFromDumpster(@Nonnull OperationContext zContext, int id, byte type)
    throws NoSuchItemException
  {
    MailItem item;
    try
    {
      item = mMbox.getItemById(zContext.getOperationContext(), id, Item.convertType(type), true);
    }
    catch (com.zimbra.common.service.ServiceException serviceException)
    {
      throw ExceptionWrapper.wrap(serviceException);
    }
    return new Item(item);
  }

  @Nonnull
  public Item getItemRevisionById(@Nonnull OperationContext zContext, int id, byte type, int revision)
    throws NoSuchItemException
  {
    MailItem item;
    try
    {
      item = mMbox.getItemRevision(zContext.getOperationContext(), id, Item.convertType(type), revision);
    }
    catch (com.zimbra.common.service.ServiceException serviceException)
    {
      throw ExceptionWrapper.wrap(serviceException);
    }
    if (item == null)
    {
      throw new NoSuchItemException(id+"-"+revision);
    }
    return new Item(item);
  }

  @Nullable private static Method sLoadRevisionsMethod = null;

  static
  {
    try
    {
      sLoadRevisionsMethod = com.zimbra.cs.mailbox.MailItem.class.getDeclaredMethod("loadRevisions");
      sLoadRevisionsMethod.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  @Nonnull
  public Item getItemRevisionByIdFromDumpster(@Nonnull OperationContext zContext, int id, byte type, int revision)
    throws NoSuchItemException
  {
    MailItem item;
    try
    {
      item = mMbox.getItemRevision(zContext.getOperationContext(), id, Item.convertType(type), revision, true);
    }
    catch (com.zimbra.common.service.ServiceException serviceException)
    {
      throw ExceptionWrapper.wrap(serviceException);
    }
    if (item == null)
    {
      throw new NoSuchItemException(id+"-"+revision);
    }
    return new Item(item);
  }

  public List<Item> getAllRevisionsIncludeDumpster(@Nonnull OperationContext zContext, int id, byte type)
  {
    List<Item> revisions = new ArrayList<>();
    try
    {
      revisions.addAll(getAllRevisions(zContext, id, type, false));
    }
    catch( Exception ignored ) {}
    try
    {
      revisions.addAll(getAllRevisions(zContext, id, type, true));
    }
    catch( Exception ignored ) {}
    return revisions;
  }

  @Nonnull
  public List<Item> getAllRevisions(@Nonnull OperationContext zContext, int id, byte type)
  {
    return getAllRevisions(zContext, id, type, false);
  }

  @Nonnull
  public List<Item> getAllRevisions(@Nonnull OperationContext zContext, int id, byte type, boolean inDumpster)
  {
    try
    {
      if (inDumpster)
      {
        beginTransaction("getAllRevisions", zContext);
        try
        {
          Item item = getItemByIdFromDumpster(zContext, id, type);

          MailItem mailItem = item.toZimbra(MailItem.class);
          List<Item> revisions;
          Object revisionsObject = sLoadRevisionsMethod.invoke(mailItem);
          if (revisionsObject == null)
          {
            revisions = Collections.singletonList(item);
          }
          else
          {
            List<MailItem> zimbraRevisions = (List<MailItem>) revisionsObject;
            revisions = new ArrayList<Item>(zimbraRevisions.size());
            for (MailItem zimbraItem : zimbraRevisions)
            {
              revisions.add(new Item(zimbraItem));
            }
            revisions.add(item);
          }

          return revisions;
        }
        finally
        {
          endTransaction(true);
        }
      }
      else
      {
        List<MailItem> mailItems = mMbox.getAllRevisions(zContext.getOperationContext(), id, Item.convertType(type));

        List<Item> items = new ArrayList<Item>(mailItems.size());
        for (MailItem mailItem : mailItems)
        {
          items.add(new Item(mailItem));
        }

        return items;
      }
    }
    catch (com.zimbra.common.service.ServiceException serviceException)
    {
      throw ExceptionWrapper.wrap(serviceException);
    }
    catch (IllegalAccessException | InvocationTargetException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  public Message getMessageById(@Nonnull OperationContext zContext, int id)
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

  @Nonnull
  public List<Message> getMessagesByConversation(@Nonnull OperationContext zContext, int id)
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

  public void setDate(@Nonnull OperationContext octxt, int itemId, byte type, long date)
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

  @Nonnull
  public List<Tag> getModifiedTags(@Nonnull OperationContext octxt, int lastSync)
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

  public List<Integer> getModifiedItems(@Nonnull OperationContext zContext, int sequence)
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
  /* $endif$ */
    }
    catch (com.zimbra.common.service.ServiceException serviceException)
    {
      throw ExceptionWrapper.wrap(serviceException);
    }
  }

  @Nonnull
  public Folder getFolderByName(@Nonnull OperationContext zContext, String name, int parentId)
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

  @Nonnull
  public Folder getFolderByPath(@Nonnull OperationContext zContext, String path)
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

  @Nonnull
  public List<Folder> getFolderList(@Nonnull OperationContext zContext)
      throws NoSuchFolderException
  {
    List<Folder> folderList = new ArrayList<>(0);
    try
    {
      for (com.zimbra.cs.mailbox.Folder folder : mMbox
          .getFolderList(zContext.getOperationContext(), SortBy.NONE)) {
        folderList.add(new Folder(folder));
      }

    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return folderList;
  }

  @Nonnull
  public Item getItemByPath(@Nonnull OperationContext zContext, String path)
    throws NoSuchItemException
  {
    MailItem mailItem;
    try
    {
      mailItem = mMbox.getItemByPath(zContext.getOperationContext(), path);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Folder(mailItem);
  }

  @Nonnull
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

  @Nonnull
  public Folder getFolderById(@Nonnull OperationContext zContext, int id)
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

  @Nonnull
  public Mountpoint getMountpointById(@Nonnull OperationContext octxt, int id)
  {
    MailItem mountpoint;
    try
    {
      mountpoint = mMbox.getMountpointById(octxt.getOperationContext(), id);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Mountpoint(mountpoint);
  }

  @Nonnull
  public CalendarItem getCalendarItemById(@Nonnull OperationContext octxt, int id)
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

  @Nonnull
  public CalendarItem getCalendarItemByUid(@Nonnull OperationContext octxt, String uid)
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
      throw new NoSuchCalendarException(uid);
    }

    return new CalendarItem(mailItem);
  }

    @Nonnull
    public FreeBusy getFreeBusy(@Nonnull OperationContext octxt, long start, long end)
    throws NoSuchItemException
    {
      com.zimbra.cs.fb.FreeBusy freeBusy;
      try
      {
        freeBusy = mMbox.getFreeBusy(octxt.getOperationContext(),start,end,FreeBusyQuery.CALENDAR_FOLDER_ALL);
      }
      catch (com.zimbra.common.service.ServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }

      if (freeBusy == null)
      {
        throw new NoSuchFreeBusyException(start, end);
      }

      return new FreeBusy(freeBusy);
    }

  public void copyCalendarReplyInfo(
    @Nonnull CalendarItem fromCalendarItem,
    CalendarItem toCalendarItem,
    @Nonnull OperationContext zContext
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

  public void rename(@Nonnull OperationContext zContext, int id, byte type, String name, int folderId)
    throws ZimbraException
  {
    try
    {
      mMbox.rename(zContext.getOperationContext(), id, Item.convertType(type), name, folderId);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void renameMailbox(OperationContext operationContext, String oldName, String newName) {
    try {
      mMbox.renameMailbox(operationContext.getOperationContext(), oldName, newName);
    } catch (com.zimbra.common.service.ServiceException e) {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void delete(@Nonnull OperationContext octxt, int itemId, byte type)
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

  public void delete(@Nonnull OperationContext octxt, int[] itemIds, byte type)
    throws ZimbraException
  {
    try
    {
      mMbox.delete(octxt.getOperationContext(), itemIds, Item.convertType(type), null);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void setPermissions(@Nonnull OperationContext zContext, int folderId, @Nonnull Acl acl)
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

  public void setColor(@Nonnull OperationContext octxt, int[] itemIds, byte type, @Nonnull Item.Color color)
    throws ZimbraException
  {
    try
    {
      mMbox.setColor(
        octxt.getOperationContext(),
        itemIds,
        Item.convertType(type),
        color.toZimbra(com.zimbra.common.mailbox.Color.class)
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
  public CalendarItem setCalendarItem(
    @Nonnull OperationContext octxt, int folderId, int flags, String tags[],
                                      @Nonnull CalendarItemData defaultInv,
                                      @Nonnull List<CalendarItemData> exceptions,
                                      List<ReplyInfo> replies, long nextAlarm
  )
    throws ZimbraException
  {
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
      com.zimbra.cs.mailbox.Mailbox.SetCalendarItemData calendarItemData = defaultInv.toZimbra(com.zimbra.cs.mailbox.Mailbox.SetCalendarItemData.class);
      boolean patchCalendarItemMethod = !CREATE_CALENDAR_ITEM_ALLOWED_METHODS.contains(calendarItemData.invite.getMethod());
      String oldMethod = calendarItemData.invite.getMethod();
      if (patchCalendarItemMethod) {
        calendarItemData.invite.setMethod("PUBLISH");
        com.zimbra.cs.mailbox.CalendarItem calendarItem = calendarItemData.invite.getCalendarItem();
        String cid = String.format("Message Id: %s from account id %s",
            calendarItem.getId(),
            calendarItem.getAccount().getId()
        );
        ZimbraLog.extensions.warn(String.format("Setting metadata method to 'PUBLISH', '%s' is not supported for calendar item %s", oldMethod, cid));
      }

      com.zimbra.cs.mailbox.CalendarItem inviteCalendarItem = calendarItemData.invite.getCalendarItem();
      List<ReplyInfo> newReplies = replies;
      if (calendarItemData.invite != null && calendarItemData.invite.getCalendarItem() != null) {
        newReplies = replies == null ? calendarItemData.invite.getCalendarItem().getAllReplies() : null;
      }

      CalendarItem result = new CalendarItem(
          mMbox.setCalendarItem(
              octxt.getOperationContext(),
              folderId,
              flags,
              tags,
              calendarItemData,
              zimbraExceptions,
              newReplies,
              nextAlarm
          )
      );
      return result;
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public
  @Nullable
  Metadata getConfig(@Nonnull OperationContext octxt, String section)
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

  public void setConfig(@Nonnull OperationContext octxt, String section, @Nonnull Metadata config)
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

  public void removeConfig(@Nonnull OperationContext octxt, String section)
    throws ZimbraException
  {
    try
    {
      mMbox.setConfig(
        octxt.getOperationContext(),
        section,
        null
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void alterTag(@Nonnull OperationContext octxt, int itemId, byte type, int tagId, boolean addTag)
    throws ZimbraException
  {
    try
    {
      mMbox.alterTag(octxt.getOperationContext(), itemId, Item.convertType(type), Flag.of(tagId), addTag, null);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void alterTag(@Nonnull OperationContext octxt, int itemId, byte type, String tagName, boolean addTag)
    throws ZimbraException
  {
    try
    {
      mMbox.alterTag(octxt.getOperationContext(), itemId, Item.convertType(type), tagName, addTag, null);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void setTags(@Nonnull OperationContext octxt, int itemId, byte type, @Nullable Collection<String> tags)
    throws ZimbraException
  {
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
  }

  public void setFlags(@Nonnull OperationContext octxt, int itemId, byte type, int flags)
    throws ZimbraException
  {
    try
    {
      MailItem item = mMbox.getItemById(octxt.getOperationContext(), itemId, Item.convertType(Item.TYPE_UNKNOWN));
      mMbox.setTags(octxt.getOperationContext(), itemId, Item.convertType(type), flags,
        item.getTags()
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void modifyContact(@Nonnull OperationContext octxt, int contactId, @Nonnull ParsedContact pc)
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

  @Nullable
  public ZimbraItemId sendMimeMessage(
    @Nonnull OperationContext octxt, Boolean saveToSent, MimeMessage mm,
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
      newItemId = mMbox.getMailSender().sendMimeMessage(
        octxt.getOperationContext(), mMbox, saveToSent, mm,
        uploads, itemId, replyType,
        null, replyToSender
      );

      if( newItemId == null ) {
        return null;
      }
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

  public void move(@Nonnull OperationContext octxt, int itemId, byte type, int targetId)
          throws ZimbraException
  {
    try
    {
      if (!canWrite(octxt,itemId) || !canWrite(octxt,targetId))
      {
        throw new PermissionDeniedException("Missing write permissions for " + octxt.getAccount().getName() + " on " + mMbox.getAccount().getMail() + " mailbox");
      }
      mMbox.move(octxt.getOperationContext(), itemId, Item.convertType(type), targetId);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void move(@Nonnull OperationContext octxt, int[] itemIds, byte type, int targetId) throws ZimbraException
  {
    try
    {
      mMbox.move(octxt.getOperationContext(), itemIds, Item.convertType(type), targetId, null);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<Item> copy(@Nonnull OperationContext octxt, int[] itemIds, byte type, int targetId) throws ZimbraException
  {
    try
    {
      List<MailItem> copiedItems = mMbox.copy(octxt.getOperationContext(), itemIds, Item.convertType(type), targetId);
      List<Item> result = new ArrayList<>(copiedItems.size());
      for( MailItem mailItem : copiedItems ) {
        result.add(new Item(mailItem));
      }
      return result;
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void createFolderPath(@Nonnull OperationContext octxt, String path) throws ZimbraException
  {
    /* $if ZimbraVersion >= 8.8.10 $ */
    try
    {
      mMbox.createFolderForMsgs(octxt.getOperationContext(), path);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $endif$ */
  }



  public int move(@Nonnull Account dstAccount,@Nonnull OperationContext octxt, int itemId, byte type, int targetId)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.service.util.ItemId zimbraItemId = new com.zimbra.cs.service.util.ItemId(
              dstAccount.getId(),
              targetId
      );
      ItemActionHelper op = ItemActionHelper.MOVE(octxt.getOperationContext(),
                                                  mMbox,
                                                  SoapProtocol.Soap12,
                                                  Arrays.asList(itemId),
                                                  Item.convertType(type),
                                                  null,
                                                  zimbraItemId);
      List<String> createdIds;
      /* $if ZimbraVersion >= 8.8.2 $ */
      createdIds = op.getResult().getSuccessIds();
      /* $else $
      createdIds = op.getCreatedIds();
      /* $endif $ */
      if (createdIds == null)
      {
        return itemId;
      }
      if (createdIds.size() != 1)
      {
        throw new NoSuchItemException(Integer.toString(itemId));
      }
      com.zimbra.cs.service.util.ItemId newZimbraItemId = new com.zimbra.cs.service.util.ItemId(createdIds.get(0),(String)null);
      return newZimbraItemId.getId();
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<CalendarItem> getCalendarItemsForRange(
    @Nonnull OperationContext octxt, byte type, long start,
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
    @Nonnull OperationContext octxt, byte type, long start,
                                          long end, int folderId, boolean descending
  )
    throws ZimbraException
  {
    List<Integer> itemIds;

    try
    {
      /* $if ZimbraVersion == 8.0.0 |! ZimbraVersion == 8.0.1 $
      itemIds = mMbox.getItemListByDates(octxt.getOperationContext(), Item.convertType(type),
                                      start, end, folderId, descending);
      /* $elseif ZimbraVersion < 8.5.0 $
      DbMailItem.SearchOpts options = new DbMailItem.SearchOpts(start, end, descending);
      itemIds = mMbox.getItemIdList(octxt.getOperationContext(), Item.convertType(type),
                                   folderId, options);
      /* $else $ */
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
      /* $endif */
      return itemIds;
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<Integer> listItemIds(@Nonnull OperationContext octxt, byte type, int folderId)
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

  @Nonnull
  public Iterator<Map.Entry<Byte, List<Integer>>> getItemIds(@Nonnull OperationContext octxt, int folderId)
    throws ZimbraException
  {
    try
    {
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
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean canRead(OperationContext octxt, int itemId)
  {
    short rights;

    try
    {
      rights = mMbox.getEffectivePermissions(octxt.getOperationContext(), itemId, com.zimbra.cs.mailbox.MailItem.Type.UNKNOWN);
    }
    catch (ServiceException e)
    {
      return false;
    }

    return (rights & Acl.RIGHT_ADMIN) != 0 || (rights & Acl.RIGHT_READ) != 0;
  }

  public boolean canWrite(OperationContext octxt, int itemId)
  {
    short rights;

    try
    {
      rights = mMbox.getEffectivePermissions(octxt.getOperationContext(), itemId, com.zimbra.cs.mailbox.MailItem.Type.UNKNOWN);
    }
    catch (ServiceException e)
    {
      return false;
    }

    return (rights & Acl.RIGHT_ADMIN) != 0 || (rights & Acl.RIGHT_WRITE) != 0;
  }

  public void modifyPartStat(
    @Nonnull OperationContext octxt, int calItemId,
                             @Nullable RecurrenceId recurId, String cnStr,
                             String addressStr, String cutypeStr,
                             String roleStr, String partStatStr,
                             Boolean rsvp, int seqNo, long dtStamp
  )
    throws ZimbraException
  {
    try
    {
      if (! canWrite(octxt, calItemId))
      {
        throw new PermissionDeniedException("Missing write permissions for " + octxt.getAccount().getName() + " on " + mMbox.getAccount().getMail() + " mailbox");
      }


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

  @Nonnull
  public Tag getTagById(@Nonnull OperationContext octxt, int itemId)
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

  @Nullable
  public Tag getTagByName(@Nonnull OperationContext octxt, String name)
    throws NoSuchItemException
  {
    try
    {
      com.zimbra.cs.mailbox.Tag tagByName = mMbox.getTagByName(octxt.getOperationContext(), name);
      if( Objects.isNull(tagByName) )
      {
        return null;
      }
      return new Tag(tagByName);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void setCustomData(@Nonnull OperationContext octxt, int itemId, byte type, @Nonnull Item.CustomMetadata custom)
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

  @Nonnull
  public ZimbraItemId nextItemId()
  {
    return new ZimbraItemId(mMbox.getAccountId(), 0);
  }

  @Nonnull
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

  public int getLastItemId()
  {
    return mMbox.getLastItemId();
  }

  public void clearItemCache() throws ZimbraException
  {
    /* $if ZimbraX == 1 $
    try
    {
    /* $endif $ */
      mMbox.purge(Item.convertType(Item.TYPE_UNKNOWN));
    /* $if ZimbraX == 1 $
    }
    catch( ServiceException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $endif $ */
  }

  public void clearCache(byte type) throws ZimbraException
  {
    /* $if ZimbraX == 1 $
    try
    {
    /* $endif $ */
      mMbox.purge(Item.convertType(type));
    /* $if ZimbraX == 1 $
    }
    catch( ServiceException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $endif $ */
  }


  @Nonnull
  public QueryResults search(
    @Nonnull OperationContext octxt,
    String queryString,
    @Nonnull byte[] types,
    @Nonnull SortedBy sortBy,
    int chunkSize
  )
    throws ZimbraException
  {
    return search(octxt, queryString, types, sortBy, chunkSize, 0, false, false);
  }

  @Nonnull
  public QueryResults search(
    @Nonnull OperationContext octxt,
    String queryString,
    @Nonnull byte[] types,
    @Nonnull SortedBy sortBy,
    int chunkSize,
    int offset,
    boolean onlyIds
  )
          throws ZimbraException
  {
    return search(octxt, queryString, types, sortBy, chunkSize, offset, onlyIds, false);
  }

  public QueryResults search(
    OperationContext operationContext,
    org.openzal.zal.SearchParams  searchParams
  ) {
    ZimbraQueryResults result;
    try {
      result = mMbox.index.search(
          SoapProtocol.Soap12,
          operationContext.getOperationContext(),
          searchParams.toZimbra(SearchParams.class)
      );
    } catch (ServiceException e) {
      throw ExceptionWrapper.wrap(e);
    }

    return new QueryResults(
        result
    );
  }

  @Nonnull
  public QueryResults search(
    @Nonnull OperationContext octxt,
    String queryString,
    @Nonnull byte[] types,
    @Nonnull SortedBy sortBy,
    int chunkSize,
    int offset,
    boolean onlyIds,
    boolean inDumpster
  )
    throws ZimbraException
  {
    try
    {
      Set<MailItem.Type> typeList = new HashSet(types.length);
      for (byte type : types)
      {
        typeList.add(Item.convertType(type));
      }

      SearchParams.Fetch fetchMode = onlyIds ? SearchParams.Fetch.IDS : SearchParams.Fetch.NORMAL;

      com.zimbra.cs.index.SearchParams params = new com.zimbra.cs.index.SearchParams();
      params.setQueryString(queryString);
      params.setTimeZone(null);
      params.setLocale(null);
      params.setTypes(typeList);
      params.setSortBy(sortBy.toZimbra(SortBy.class));
      params.setPrefetch(true);
      params.setFetchMode(fetchMode);
      params.setInDumpster(inDumpster);
      params.setLimit(chunkSize + offset);
      params.setOffset(offset);

      ZimbraQueryResults result = mMbox.index.search(
        SoapProtocol.Soap12,
        octxt.getOperationContext(),
        params
      );

      if( offset >= 1 )
        result.skipToHit(offset-1);

      return new QueryResults(
        result
      );
    }
    catch (Exception e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
  public List<Item> getItemList(byte type, @Nonnull OperationContext zContext)
    throws ZimbraException
  {
    List<Item> result = new LinkedList<Item>();

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
              Item item = rawGetItem(data);
              if(item != null)
              {
                result.add(item);
              }
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

    return result;
  }

  @Nonnull
  public Folder createFolder(
    @Nonnull OperationContext octxt, String name, int parentId,
                             byte attrs, byte defaultView, int flags,
                             @Nonnull Item.Color color, String url
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
        Item.convertType(defaultView == Item.TYPE_WIKI ? Item.TYPE_DOCUMENT : defaultView),
        flags,
        color.toZimbra(com.zimbra.common.mailbox.Color.class),
        url
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Folder(folder);
  }

  @Nonnull
  public Folder createFolder(
      OperationContext operationContext,
      String path
  ) {
    MailItem folder;
    try {
      FolderOptions fopts = new FolderOptions();
      folder = mMbox.createFolder(operationContext.getOperationContext(), path, fopts);
    } catch (com.zimbra.common.service.ServiceException e) {
      throw ExceptionWrapper.wrap(e);
    }
    return new Folder(folder);
  }

  public void setFolderRetentionPolicy(@Nonnull OperationContext octxt, int folderId, RetentionPolicy retentionPolicy)
      throws ZimbraException {
    try {
      mMbox.setRetentionPolicy(
          octxt.getOperationContext(),
          folderId,
          Type.FOLDER,
          retentionPolicy.toZimbra(com.zimbra.soap.mail.type.RetentionPolicy.class)
      );
    } catch (ServiceException e) {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
  public SearchFolder createSearchFolder(
    @Nonnull OperationContext octxt, int folderId, String name,
                                         String query, String types, String sort,
                                         int flags, @Nonnull Item.Color color
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
        color.toZimbra(com.zimbra.common.mailbox.Color.class)
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new SearchFolder(item);
  }

  @Nonnull
  public SearchFolder getSearchFolderById(@Nonnull OperationContext zContext, int id) throws NoSuchFolderException {
    MailItem folder;

    try
    {
      folder = mMbox.getSearchFolderById(zContext.getOperationContext(), id);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new SearchFolder(folder);
  }

  public void modifySearchFolder(OperationContext zContext, int id, String query, String types, String sort) {
    try
    {
      mMbox.modifySearchFolder(zContext.getOperationContext(), id, query, types, sort);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
  public Tag createTag(@Nonnull OperationContext octxt, String name, @Nonnull Item.Color color)
    throws ZimbraException
  {
    MailItem tag;
    try
    {
      tag = mMbox.createTag(
        octxt.getOperationContext(),
        name,
        color.toZimbra(com.zimbra.common.mailbox.Color.class)
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Tag(tag);
  }

  @Nonnull
  public Message addMessage(
    @Nonnull OperationContext octxt, InputStream in, int sizeHint, Long receivedDate,
                            int folderId, boolean noIcal,
                            int flags, Collection<String> tags, int conversationId, String rcptEmail,
                            @Nullable Item.CustomMetadata customData
  )
    throws IOException, ZimbraException
  {
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
  }

  @Nonnull
  public Message simpleAddMessage(
    @Nonnull OperationContext octxt,
    InputStream in,
    int folderId
  )
    throws IOException, ZimbraException
  {
    MailItem message;
    try
    {
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
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Message(message);
  }

  @Nonnull
  public Message saveDraft(@Nonnull OperationContext octxt,@Nonnull ParsedMessage parsedMessage, int id)
    throws IOException, ZimbraException
  {
    try
    {
      return new Message(mMbox.saveDraft(octxt.getOperationContext(), parsedMessage.toZimbra(com.zimbra.cs.mime.ParsedMessage.class), id));
    }
    catch( com.zimbra.common.service.ServiceException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
  public Contact getContactById(OperationContext octxt,int id)
  {
    try
    {
      return new Contact(mMbox.getContactById(octxt.getOperationContext(),id));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
  public List<Contact> getContacts(OperationContext octxt, int folderId)
  {
    List<Contact> contacts = new ArrayList<>();
    try
    {
      List<com.zimbra.cs.mailbox.Contact> contactList = mMbox.getContactList(octxt.getOperationContext(), folderId);
      for( com.zimbra.cs.mailbox.Contact contact : contactList )
      {
        contacts.add(new Contact(contact));
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return contacts;
  }

  @Nonnull
  public Contact createContact(OperationContext octxt, ParsedContact pc, int folderId)
  {
    return createContact(octxt, pc, folderId, Collections.<String>emptyList());
  }

  @Nonnull
  public Contact createContact(@Nonnull OperationContext octxt, @Nonnull ParsedContact pc, int folderId, @Nonnull Collection<String> tags)
    throws ZimbraException
  {
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
  }

  @Nonnull
  public Document createDocument(
    @Nonnull OperationContext octxt,
    int folderId,
    @Nonnull ParsedDocument pd,
    byte type, int flags
  )
    throws IOException, ZimbraException
  {
    MailItem document;
    try
    {
      document = mMbox.createDocument(
        octxt.getOperationContext(),
        folderId,
        pd.toZimbra(com.zimbra.cs.mime.ParsedDocument.class),
        Item.convertType(type),
        flags
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Document(document);
  }

  @Nonnull
  public Document simpleCreateDocument(
    @Nonnull OperationContext octxt,
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

  @Nonnull
  public Document addDocumentRevision(
    @Nonnull OperationContext octxt,
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
      document = mMbox.addDocumentRevision(
        octxt.getOperationContext(),
        docId,
        author,
        name,
        description,
        data
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Document(document);
  }

  @Nonnull
  public Document addDocumentRevision(
    @Nonnull OperationContext octxt,
    int docId,
    ParsedDocument parsedDocument
  )
    throws ZimbraException, IOException
  {
    MailItem document;
    try
    {
      document = mMbox.addDocumentRevision(
        octxt.getOperationContext(),
        docId,
        parsedDocument.toZimbra(com.zimbra.cs.mime.ParsedDocument.class)
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Document(document);
  }

  @Nonnull
  public Document createDocument(
    @Nonnull OperationContext octxt, int folderId,
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
        description,
        descEnabled,
        data,
        Item.convertType(Item.TYPE_DOCUMENT)
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Document(document);
  }

  @Nonnull
  public Note createNote(
    @Nonnull OperationContext octxt, String content,
                         @Nonnull Note.Rectangle rectangle, @Nonnull Item.Color color,
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
        color.toZimbra(com.zimbra.common.mailbox.Color.class),
        folderId);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Note(note);
  }

  public int addInvite(
    @Nonnull OperationContext octxt, @Nonnull Invite inv,
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
      ).calItemId;
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void addInvite(@Nonnull OperationContext octxt, @Nonnull Invite inv, int folderId)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.mime.ParsedMessage parsedMessage = null;
      mMbox.addInvite(
        octxt.getOperationContext(),
        inv.toZimbra(com.zimbra.cs.mailbox.calendar.Invite.class),
        folderId,
        null,
        true,
        false,
        true
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void addInvite(@Nonnull OperationContext octxt, @Nonnull Invite inv, int folderId, @Nullable MimeMessage mimeMessage)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.mime.ParsedMessage parsedMessage = null;
      mMbox.addInvite(
        octxt.getOperationContext(),
        inv.toZimbra(com.zimbra.cs.mailbox.calendar.Invite.class),
        folderId,
        mimeMessage != null ? new com.zimbra.cs.mime.ParsedMessage(mimeMessage, false) : null,
        true,
        true,
        true
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
  public Mountpoint createMountpoint(
    @Nonnull OperationContext octxt, int folderId,
                                     String name, String ownerId,
                                     int remoteId, String remoteUuid,
                                     byte view, int flags,
                                     @Nonnull Item.Color color
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
        color.toZimbra(com.zimbra.common.mailbox.Color.class),
        false
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Mountpoint(mountPoint);
  }


  @Nonnull
  public Chat createChat(
    @Nonnull OperationContext octxt,
                         @Nonnull ParsedMessage pm,
                         int folderId, int flags,
                         @Nonnull Tags tags
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
        tags.getTags()
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return new Chat(chat);
  }

  @Nonnull
  public Chat createChat(
    @Nonnull OperationContext octxt,
                         @Nonnull ParsedMessage pm,
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

  @Nonnull
  public Comment createComment(
    @Nonnull OperationContext octxt,
                               int parentId,
                               String text,
                               String creator
  )
    throws ZimbraException
  {
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
  }

  @Nonnull
  public Link createLink(
    @Nonnull OperationContext octxt,
                         int parentId,
                         String name,
                         String ownerId,
                         int remoteId
  )
    throws ZimbraException
  {
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
  }

  @Nullable private static Method sEndTransactionMethod = null;

  static
  {
    /* $if ZimbraX == 0 $ */
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
    /* $endif $ */
  }

  @Nullable private static Method sBeginTransactionMethod = null;

  static
  {
    /* $if ZimbraX == 0 $ */
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
    /* $endif $ */
  }

  public void beginTransaction(String name, @Nonnull OperationContext context)
  {
    beginTransaction(name, context.getOperationContext());
  }

  private final void beginTransaction(String name, com.zimbra.cs.mailbox.OperationContext zContext)
    throws ZimbraException
  {
    /* $if ZimbraX == 1 $
    return;
    /* $else $ */
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
    /* $endif $ */
  }

  public final void endTransaction(boolean success)
    throws ZimbraException
  {
    /* $if ZimbraX == 1 $
    return;
    /* $else $ */
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
    /* $endif $ */
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
  @Nullable
  private final Item rawGetItem(@Nonnull Item.UnderlyingData data)
    throws InternalServerException
  {
    Object parameters[] = new Object[1];
    parameters[0] = data.toZimbra(MailItem.UnderlyingData.class);

    try
    {
      MailItem item = (MailItem) sRawGetItem.invoke(mMbox, parameters);
      if(item != null)
      {
        return new Item(item);
      }
      else
      {
        return null;
      }
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

      sGetAllFlags = com.zimbra.cs.mailbox.Flag.class.getDeclaredMethod("allOf", partypes);
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
      Map<Object, com.zimbra.cs.mailbox.Tag> tagsMap;
      /* $if ZimbraX == 1 $
      RedisTagCache tagCache = (RedisTagCache) sTagCache.get(mMbox);
      Collection<com.zimbra.cs.mailbox.Tag> tags = tagCache.values();
      tagsMap = new HashMap<Object, com.zimbra.cs.mailbox.Tag>(tags.size());
      for (com.zimbra.cs.mailbox.Tag tag : tags)
      {
        tagsMap.put(tag.getTagId(), tag);
      }
      /* $else $ */
      tagsMap = (Map<Object, com.zimbra.cs.mailbox.Tag>) sTagCache.get(mMbox);
      /* $endif $ */
      return tagsMap;
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

  private static Field sFolderCacheMap;

  static
  {
    /* $if ZimbraX == 0 $ */
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
    /* $endif $ */
  }

  /*
   * Warning: unsynchronized private access to mailbox
   */
  @Nullable
  private final Collection<Folder> getFolderCache()
  {
    try
    {
      Collection<com.zimbra.cs.mailbox.Folder> folders = Collections.emptyList();
      /* $if ZimbraX == 1 $
      FolderCache folderCache = (FolderCache) sFolderCache.get(mMbox);
      if(folderCache != null)
      {
        folders = folderCache.values();
      }
      /* $else $*/
      folders = (Collection<com.zimbra.cs.mailbox.Folder>) (((Map<Integer, com.zimbra.cs.mailbox.Folder>) sFolderCacheMap
        .get(sFolderCache.get(mMbox))).values());

      /* $endif $  */
      ArrayList<Folder> newList = new ArrayList<Folder>(folders.size());

      for (Object folder : folders)
      {
        if ( folder != null)
        {
          newList.add(new Folder(folder));
        }
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
  public static Mailbox getByAccount(@Nonnull Account account)
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
  public static Mailbox getByAccount(@Nonnull Account account, boolean autocreate)
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

  @Nonnull
  @Deprecated
  public static Mailbox getByItem(@Nonnull Item item)
  {
    return item.getMailbox();
  }

  @Deprecated
  public static Map<String, Integer> getMapAccountsAndMailboxes(@Nonnull Connection conn)
    throws ZimbraException
  {
    Map<String, Integer> accountsAndMailboxes;
    try
    {
      accountsAndMailboxes = DbMailbox.listMailboxes(conn.toZimbra(DbPool.DbConnection.class));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return accountsAndMailboxes;
  }

  @Nonnull
  public Connection getOperationConnection()
    throws ZimbraException
  {
    DbPool.DbConnection connection;
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

  public void updateChat(@Nonnull OperationContext operationContext, @Nonnull ParsedMessage parsedMessage, int id)
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

  public void reindexItem(@Nonnull Item item)
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

  public String rawGetConfig(
    @Nonnull String key
  )
    throws SQLException, ZimbraException
  {
    String query = "SELECT metadata FROM zimbra.mailbox_metadata WHERE mailbox_id=? AND section=? LIMIT 1";
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    try
    {
      connection = ZimbraDatabase.legacyGetConnection();
      statement = connection.prepareStatement(query);
      statement.setInt(1, getId());
      statement.setString(2, key);

      resultSet = statement.executeQuery();
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
      DbUtils.closeQuietly(resultSet);
      DbUtils.closeQuietly(statement);
      if (connection != null)
      {
        connection.close();
      }
    }
  }

  public static final long MAX_METADATA_SIZE = 16777215;

  public void rawSetConfig(
    @Nonnull String section,
    @Nullable String metadata
  )
    throws SQLException, ZimbraException
  {
    if (metadata != null && metadata.length() > MAX_METADATA_SIZE)
    {
      throw new SQLException("metadata is too big to be saved");
    }

    Connection connection = null;
    PreparedStatement replaceStatement = null;
    try
    {
      connection = ZimbraDatabase.legacyGetConnection();
      if (metadata != null) {
        String query = "REPLACE INTO zimbra.mailbox_metadata (mailbox_id,section,metadata) VALUES(?,?,?)";
        replaceStatement = connection.prepareStatement(query);
        replaceStatement.setInt(1, getId());
        replaceStatement.setString(2, section);
        replaceStatement.setString(3, metadata);
      } else {
        String query = "DELETE FROM zimbra.mailbox_metadata WHERE mailbox_id = ? AND section = ?";
        replaceStatement = connection.prepareStatement(query);
        replaceStatement.setInt(1, getId());
        replaceStatement.setString(2, section);
      }

      replaceStatement.executeUpdate();
      connection.commit();
      RedoLogProvider.getRedoLogProvider().getRedoLogManager().commit(new RawSetConfig(getId(), section, metadata));
    }
    finally
    {
      DbUtils.closeQuietly(replaceStatement);
      if (connection != null)
      {
        connection.close();
      }
    }
  }

  public void deleteMailbox()
  {
    try
    {
      mMbox.deleteMailbox();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void deleteMailboxButStore()
  {
    try
    {
      mMbox.deleteMailbox(DeleteBlobs.NEVER);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void recalculateFolderAndTagCounts()
  {
    try
    {
      mMbox.recalculateFolderAndTagCounts();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
  public MailboxIndex getIndex()
  {
    return mIndex;
  }

  public void startReIndex()
  {
    try
    {
      /* $if ZimbraX == 1 $
      mMbox.index.startReIndex(mMbox.getOperationContext());
      /* $else $ */
      mMbox.index.startReIndex();
      /* $endif $ */
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void deleteIndex() throws IOException, ZimbraException
  {
    /* $if ZimbraX == 1 $
    try
    {
    /* $endif $ */
      mMbox.index.deleteIndex();
    /* $if ZimbraX == 1 $
    }
    catch( ServiceException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $endif $ */
  }
  public void suspendIndexing()
  {
    /* $if ZimbraVersion >= 8.7.0 && ZimbraX == 0 $ */
    mMbox.suspendIndexing();
    /* $elseif ZimbraX == 1 $
    return;
    /* $endif $ */
  }

  public void resumeIndexing()
  {
    /* $if ZimbraX == 1 $
    return;
    /* $elseif ZimbraVersion >= 8.7.0 $ */
    mMbox.resumeIndexing();
    /* $endif $ */
  }

  public boolean isReIndexInProgress()
  {
    return mMbox.index.isReIndexInProgress() || mMbox.index.isCompactIndexInProgress();
  }

  public short getIndexVolume() {
    return mMbox.getIndexVolume();
  }

  public boolean isInMaintenanceMode()
  {
    com.zimbra.cs.mailbox.MailboxMaintenance maintenace = mMbox.getMaintenance();
    return maintenace != null;
  }

  public void checkSizeChange(long newSize) throws ZimbraException
  {
    /* $if ZimbraVersion >= 8.8.10 && ZimbraX == 0 $ */
    try
    {
      mMbox.checkSizeChange(newSize);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $elseif ZimbraX == 1 $
    return;
    /* $endif $ */
  }

  public void createDefaultFlags()  throws ZimbraException
  {
    try
    {
      beginTransaction("createDefaultFlags", newOperationContext());
      sCreateDefaultFlags.invoke(mMbox);
      endTransaction(true);
    }
    catch( Exception e )
    {
      endTransaction(false);
      if( e instanceof ServiceException )
      {
        throw ExceptionWrapper.wrap(e);
      }
      throw new RuntimeException(e);
    }
  }

  public void purgeImapDeleted(OperationContext operationContext) {
    try{
      mMbox.purgeImapDeleted(operationContext.getOperationContext());
    } catch( ServiceException e ) {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void setFolderUrl(OperationContext operationContext, int folderId, String url) {
    try {
      mMbox.setFolderUrl(operationContext.getOperationContext(), folderId, url);
    } catch (ServiceException e) {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void setActiveSyncDisabled(OperationContext octxt, int folderId, boolean disableActiveSync) {
    try {
      mMbox.setActiveSyncDisabled(octxt.getOperationContext(), folderId, disableActiveSync);
    } catch (ServiceException e) {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
