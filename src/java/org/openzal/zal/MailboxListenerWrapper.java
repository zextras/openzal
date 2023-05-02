package org.openzal.zal;

import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailboxOperation;
import com.zimbra.cs.session.PendingModifications;
import java.util.concurrent.atomic.AtomicBoolean;
import org.openzal.zal.ItemChange.ChangeType;
import org.openzal.zal.lib.Clock;
import org.openzal.zal.log.ZimbraLog;

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

    private boolean isNotVirtualConversation(Object what)
    {
      boolean isRelevant = false;

      byte type = Item.TYPE_UNKNOWN;

      if( what instanceof MailItem.Type )
      {
        type = ((MailItem.Type) what).toByte();
        isRelevant = true;
      }

      if( what instanceof MailItem )
      {
        Item item = new Item(what);
        type = item.getType();
        isRelevant = true;
      }

      switch( type )
      {
        case Item.TYPE_VIRTUAL_CONVERSATION:
          isRelevant = false;
      }

      return isRelevant;
    }

    @Override
    public void notify(ChangeNotification notification)
    {
      try
      {
        notifyInternal(notification);
      }
      catch (Exception ex)
      {
        ZimbraLog.mailbox.warn("Exception in notify: "+Utils.exceptionToString(ex));
      }
    }

    public void notifyInternal(ChangeNotification notification)
    {
      if( isRegistered.get() && notification.mods.hasNotifications() )
      {
        MailboxOperation op = notification.op;
        if( op == null ) {
          return;
        }

        Operation operation = map(op.getCode());
        Account account = new Account(notification.mailboxAccount);

        if( operation != Operation.SKIP )
        {
          ItemChange itemChange;
          if( notification.mods.created != null )
          {
            for( PendingModifications.ModificationKey mod : (notification.mods.created).keySet() )
            {
              Object whatObj = notification.mods.created.get(mod);
              if( isNotVirtualConversation(whatObj) )
              {
                Item what = new Item(whatObj);
                itemChange = new ItemChange(
                  ChangeType.CREATED,
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
              if(isNotVirtualConversation(change.what))
              {
                try
                {
                  Item item = new Item(change.what);
                  itemChange = new ItemChange(
                    ChangeType.MODIFIED,
                    item.getFolderId(),
                    item.getId(),
                    item.getModifiedSequence(),
                    item.getDate()
                  );
                }
                catch( Exception e )
                {
                  ZimbraLog.mailbox.debug(" Unable to obtain modification information");
                  itemChange = new ItemChange(
                    ChangeType.MODIFIED,
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
              if( isNotVirtualConversation(change.what ))
              {
                itemChange = new ItemChange(
                  ChangeType.DELETED,
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

  public abstract Operation map(int opCode);

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
