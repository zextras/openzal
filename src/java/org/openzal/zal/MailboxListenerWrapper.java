package org.openzal.zal;

import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailboxOperation;
import com.zimbra.cs.session.PendingModifications;
import java.util.concurrent.atomic.AtomicBoolean;
import org.openzal.zal.lib.Clock;
import org.openzal.zal.log.ZimbraLog;
import org.openzal.zal.redolog.Redolog;

public abstract class MailboxListenerWrapper
{
  public class MailboxListener extends com.zimbra.cs.mailbox.MailboxListener
  {
    private final AtomicBoolean isRegistered;
    private final Clock         mClock;

    private MailboxListener(Clock clock)
    {
      mClock = clock;
      isRegistered = new AtomicBoolean(false);
    }

    private boolean toBackup(Object what)
    {
      byte type = Item.TYPE_UNKNOWN;

      if( what instanceof MailItem.Type )
      {
        type = ((MailItem.Type) what).toByte();
      }

      if( what instanceof MailItem )
      {
        Item item = new Item(what);
        type = item.getType();
      }

      boolean isRelevant = true;
      switch( type )
      {
        case Item.TYPE_MOUNTPOINT:
        case Item.TYPE_VIRTUAL_CONVERSATION:
          isRelevant = false;
      }

      return isRelevant;
    }

    @Override
    public void notify(ChangeNotification notification)
    {
     /* $if ZimbraX == 1 $
      return;
     /* $else $ */
      if( isRegistered.get() && notification.mods.hasNotifications() )
      {
        MailboxOperation op = notification.op;
        Operation operation = Operation.SKIP;
        Account account = new Account(notification.mailboxAccount);

        switch( op.getCode() )
        {
          // to be managed with MailboxManager$Listener
          case Redolog.OP_CREATE_MAILBOX:
          case Redolog.OP_DELETE_MAILBOX:
            break;
          case Redolog.OP_CREATE_SAVED_SEARCH:
          case Redolog.OP_MODIFY_SAVED_SEARCH:
          case Redolog.OP_CREATE_TAG:
          case Redolog.OP_RENAME_TAG:
          case Redolog.OP_CREATE_CONTACT:
          case Redolog.OP_MODIFY_CONTACT:
          case Redolog.OP_EDIT_NOTE:
          case Redolog.OP_REPOSITION_NOTE:
          case Redolog.OP_CREATE_NOTE:
          case Redolog.OP_CREATE_LINK:
          case Redolog.OP_MODIFY_INVITE_PARTSTAT:
          case Redolog.OP_CREATE_INVITE:
          case Redolog.OP_FIX_CALENDAR_ITEM_TIME_ZONE:
          case Redolog.OP_SET_CALENDAR_ITEM:
          case Redolog.OP_GRANT_ACCESS:   // TODO: Find out what that operations mean
          case Redolog.OP_REVOKE_ACCESS:  // TODO: Find out what that operations mean
          case Redolog.OP_SET_SUBSCRIPTION_DATA:
          case Redolog.OP_SET_PERMISSIONS:
          case Redolog.OP_SET_FOLDER_URL:
          case Redolog.OP_ADD_DOCUMENT_REVISION:
          case Redolog.OP_SAVE_CHAT:
          case Redolog.OP_CREATE_CHAT:
          case Redolog.OP_DATE_ITEM:
          case Redolog.OP_FIX_CALENDAR_ITEM_TZ:
          case Redolog.OP_FIX_CALENDAR_ITEM_END_TIME:
          case Redolog.OP_DISMISS_CALENDAR_ITEM_ALARM:
          case Redolog.OP_SET_FOLDER_DEFAULT_VIEW:
          case Redolog.OP_SET_CUSTOM_DATA:
          case Redolog.OP_SNOOZE_CALENDAR_ITEM_ALARM:
          case Redolog.OP_CREATE_COMMENT:
          case Redolog.OP_FIX_CALENDAR_ITEM_PRIORITY:
          case Redolog.OP_MOVE_ITEM:
          case Redolog.OP_COLOR_ITEM:
          case Redolog.OP_SET_ITEM_TAGS:
          case Redolog.OP_ALTER_ITEM_TAG:
          case Redolog.OP_DELETE_ITEM:
          case Redolog.OP_COPY_ITEM:
          case Redolog.OP_CREATE_FOLDER_PATH:
          case Redolog.OP_RENAME_FOLDER_PATH:
          case Redolog.OP_SAVE_DRAFT:
          case Redolog.OP_CREATE_MESSAGE:
          case Redolog.OP_SAVE_WIKI:
          case Redolog.OP_SAVE_DOCUMENT:
          case Redolog.OP_IMAP_COPY_ITEM:
          case Redolog.OP_CREATE_FOLDER:
          case Redolog.OP_CREATE_MOUNTPOINT:
          case Redolog.OP_RENAME_FOLDER:
          case Redolog.OP_RENAME_ITEM:
          case Redolog.OP_RENAME_ITEM_PATH:
            operation = Operation.ITEM_SCAN;
            break;
          case Redolog.OP_RENAME_MAILBOX:
          case Redolog.OP_EMPTY_FOLDER:
            operation = Operation.ACCOUNT_SCAN;
            break;

          //ignore
          case Redolog.OP_PURGE_OLD_MESSAGES:
          case Redolog.OP_REINDEX_MAILBOX:
          case Redolog.OP_SET_IMAP_UID:
          case Redolog.OP_INDEX_DEFERRED_ITEMS:
          case Redolog.OP_PURGE_IMAP_DELETED:
          case Redolog.OP_TRACK_SYNC:
          case Redolog.OP_TRACK_IMAP:
          case Redolog.OP_CREATE_VOLUME:
          case Redolog.OP_MODIFY_VOLUME:
          case Redolog.OP_DELETE_VOLUME:
          case Redolog.OP_SET_CURRENT_VOLUME:
          case Redolog.OP_MOVE_BLOBS:
          case Redolog.OP_ROLLOVER:
          case Redolog.OP_STORE_INCOMING_BLOB:
            // ignore?
          case Redolog.OP_ICAL_REPLY:
            // obsolete
          case Redolog.OP_BACKUP_MAILBOX:
          case Redolog.OP_MODIFY_INVITE_FLAG:
            //Mailbox.setConfig( "section", metadata );
            //not backupped yet, so ignore it...
          case Redolog.OP_SET_CONFIG:
          default:
            break;
        }

        if( operation != Operation.SKIP )
        {
          ItemChange itemChange;
          if( notification.mods.created != null )
          {
            for( PendingModifications.ModificationKey mod : (notification.mods.created).keySet() )
            {
              Object whatObj = notification.mods.created.get(mod);
              if( toBackup(whatObj) )
              {
                Item what = new Item(whatObj);
                itemChange = new ItemChange(
                  false,
                  what.getFolderId(),
                  what.getId(),
                  what.getModifiedSequence(),
                  what.getDate()
                );
                notifyChanges(operation, account, itemChange);
              }
            }
          }
          if( notification.mods.modified != null )
          {
            for( PendingModifications.ModificationKey mod : (notification.mods.modified).keySet() )
            {
              PendingModifications.Change change = notification.mods.modified.get(mod);
              if(toBackup(change.what))
              {
                try
                {
                  Item item = new Item(change.what);
                  itemChange = new ItemChange(
                    false,
                    item.getFolderId(),
                    item.getId(),
                    item.getModifiedSequence(),
                    item.getDate()
                  );
                }
                catch( Exception e )
                {
                  ZimbraLog.mailbox.debug(" Unable to obtain modification informations");
                  itemChange = new ItemChange(
                    false,
                    0,
                    mod.getItemId(),
                    0,
                    mClock.getCurrentTime().getTimeInMillis()
                  );
                }
                notifyChanges(operation, account, itemChange);
              }
            }
          }

          if( notification.mods.deleted != null )
          {
            for( PendingModifications.ModificationKey mod : notification.mods.deleted.keySet() )
            {
              PendingModifications.Change change = notification.mods.deleted.get(mod);
              if( toBackup( change.what ))
              {
                itemChange = new ItemChange(
                  true,
                  0,
                  mod.getItemId(),
                  0,
                  mClock.getCurrentTime().getTimeInMillis()
                );

                notifyChanges(operation, account, itemChange);
              }
            }
          }
        }
      }
      /* $endif $ */
    }

    private void register()
    {
      if( isRegistered.get() )
      {
        return;
      }
      MailboxListener.register(this);
      isRegistered.set(true);
    }

    private void unregister()
    {
      isRegistered.set(false);
    }
  }

  private final MailboxListener mMailboxListener;

  public MailboxListenerWrapper(Clock clock)
  {
    mMailboxListener = new MailboxListener(clock);
  }

  public void register()
  {
    mMailboxListener.register();
  }

  public void unregister()
  {
    mMailboxListener.unregister();
  }

  public abstract void notifyChanges(Operation redologOperation, Account account, ItemChange itemChange);
}
