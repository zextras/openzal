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

import com.zimbra.cs.mailbox.Flag;
import org.openzal.zal.log.ZimbraLog;
import com.zimbra.cs.mailbox.MailItem;
import org.jetbrains.annotations.NotNull;

public final class ZEFlag extends ZEItem
{
/* $if ZimbraVersion < 8.0.0 $
  public static final int ID_FROM_ME = Flag.ID_FLAG_FROM_ME;
  public static final int ID_ATTACHED = Flag.ID_FLAG_ATTACHED;
  public static final int ID_REPLIED = Flag.ID_FLAG_REPLIED;
  public static final int ID_FORWARDED = Flag.ID_FLAG_FORWARDED;
  public static final int ID_COPIED = Flag.ID_FLAG_COPIED;
  public static final int ID_FLAGGED = Flag.ID_FLAG_FLAGGED;
  public static final int ID_DRAFT = Flag.ID_FLAG_DRAFT;
  public static final int ID_DELETED = Flag.ID_FLAG_DELETED;
  public static final int ID_NOTIFIED = Flag.ID_FLAG_NOTIFIED;
  public static final int ID_UNREAD = Flag.ID_FLAG_UNREAD;
  public static final int ID_HIGH_PRIORITY = Flag.ID_FLAG_HIGH_PRIORITY;
  public static final int ID_LOW_PRIORITY = Flag.ID_FLAG_LOW_PRIORITY;
  public static final int ID_VERSIONED = Flag.ID_FLAG_VERSIONED;
  public static final int ID_INDEXING_DEFERRED = Flag.ID_FLAG_INDEXING_DEFERRED;
  public static final int ID_SUBSCRIBED = Flag.ID_FLAG_SUBSCRIBED;
  public static final int ID_EXCLUDE_FREEBUSY = Flag.ID_FLAG_EXCLUDE_FREEBUSY;
  public static final int ID_CHECKED = Flag.ID_FLAG_CHECKED;
  public static final int ID_NO_INHERIT = Flag.ID_FLAG_NO_INHERIT;
  public static final int ID_INVITE = Flag.ID_FLAG_INVITE;
  public static final int ID_SYNCFOLDER = Flag.ID_FLAG_SYNCFOLDER;
  public static final int ID_SYNC = Flag.ID_FLAG_SYNC;
  public static final int ID_NO_INFERIORS = Flag.ID_FLAG_NO_INFERIORS;
  public static final int ID_GLOBAL = Flag.ID_FLAG_GLOBAL;
  public static final int ID_UNCACHED = Flag.ID_FLAG_UNCACHED;

  public static final int ID_ARCHIVED         = 0;
  public static final int ID_IN_DUMPSTER      = 0;
  public static final int ID_MUTED            = 0;
  public static final int ID_NOTE             = 0;
  public static final int ID_POPPED           = 0;
  public static final int ID_POST             = 0;
  public static final int ID_PRIORITY         = 0;

  public static final int BITMASK_POPPED      = 0;
  public static final int BITMASK_NOTE        = 0;
  public static final int BITMASK_PRIORITY    = 0;
  public static final int BITMASK_POST        = 0;
  public static final int BITMASK_MUTED       = 0;
  public static final int BITMASK_ARCHIVED    = 0;
  public static final int BITMASK_IN_DUMPSTER = 0;

 $else$ */
  public static final int ID_FROM_ME           = Flag.ID_FROM_ME;
  public static final int ID_ATTACHED          = Flag.ID_ATTACHED;
  public static final int ID_REPLIED           = Flag.ID_REPLIED;
  public static final int ID_FORWARDED         = Flag.ID_FORWARDED;
  public static final int ID_COPIED            = Flag.ID_COPIED;
  public static final int ID_FLAGGED           = Flag.ID_FLAGGED;
  public static final int ID_DRAFT             = Flag.ID_DRAFT;
  public static final int ID_DELETED           = Flag.ID_DELETED;
  public static final int ID_NOTIFIED          = Flag.ID_NOTIFIED;
  public static final int ID_UNREAD            = Flag.ID_UNREAD;
  public static final int ID_HIGH_PRIORITY     = Flag.ID_HIGH_PRIORITY;
  public static final int ID_LOW_PRIORITY      = Flag.ID_LOW_PRIORITY;
  public static final int ID_VERSIONED         = Flag.ID_VERSIONED;
  public static final int ID_INDEXING_DEFERRED = Flag.ID_INDEXING_DEFERRED;
  public static final int ID_POPPED            = Flag.ID_POPPED;
  public static final int ID_NOTE              = Flag.ID_NOTE;
  public static final int ID_PRIORITY          = Flag.ID_PRIORITY;
  public static final int ID_POST              = Flag.ID_POST;
  public static final int ID_MUTED             = Flag.ID_MUTED;
  public static final int ID_SUBSCRIBED        = Flag.ID_SUBSCRIBED;
  public static final int ID_EXCLUDE_FREEBUSY  = Flag.ID_EXCLUDE_FREEBUSY;
  public static final int ID_CHECKED           = Flag.ID_CHECKED;
  public static final int ID_NO_INHERIT        = Flag.ID_NO_INHERIT;
  public static final int ID_INVITE            = Flag.ID_INVITE;
  public static final int ID_SYNCFOLDER        = Flag.ID_SYNCFOLDER;
  public static final int ID_SYNC              = Flag.ID_SYNC;
  public static final int ID_NO_INFERIORS      = Flag.ID_NO_INFERIORS;
  public static final int ID_ARCHIVED          = Flag.ID_ARCHIVED;
  public static final int ID_GLOBAL            = Flag.ID_GLOBAL;
  public static final int ID_IN_DUMPSTER       = Flag.ID_IN_DUMPSTER;
  public static final int ID_UNCACHED          = Flag.ID_UNCACHED;

  public static final int BITMASK_POPPED      = Flag.BITMASK_POPPED;
  public static final int BITMASK_NOTE        = Flag.BITMASK_NOTE;
  public static final int BITMASK_PRIORITY    = Flag.BITMASK_PRIORITY;
  public static final int BITMASK_POST        = Flag.BITMASK_POST;
  public static final int BITMASK_MUTED       = Flag.BITMASK_MUTED;
  public static final int BITMASK_ARCHIVED    = Flag.BITMASK_ARCHIVED;
  public static final int BITMASK_IN_DUMPSTER = Flag.BITMASK_IN_DUMPSTER;

  private static Method sFlagOf = null;

  static
  {
    try
    {
      Class partypes[] = new Class[1];
      partypes[0] = int.class;

      sFlagOf = Flag.FlagInfo.class.getDeclaredMethod("of", partypes);
      sFlagOf.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
    }
  }


  static Flag.FlagInfo of(int id)
  {
    try
    {
      Object parameters[] = new Object[1];
      parameters[0] = id;

      return (Flag.FlagInfo) sFlagOf.invoke(null, parameters);
    }
    catch (Throwable ex)
    {
      ZimbraLog.mailbox.warn("Exception: " + Utils.exceptionToString(ex));
    }

    return null;
  }
/* $endif$ */

  public static final int BITMASK_FROM_ME           = Flag.BITMASK_FROM_ME;
  public static final int BITMASK_ATTACHED          = Flag.BITMASK_ATTACHED;
  public static final int BITMASK_REPLIED           = Flag.BITMASK_REPLIED;
  public static final int BITMASK_FORWARDED         = Flag.BITMASK_FORWARDED;
  public static final int BITMASK_COPIED            = Flag.BITMASK_COPIED;
  public static final int BITMASK_FLAGGED           = Flag.BITMASK_FLAGGED;
  public static final int BITMASK_DRAFT             = Flag.BITMASK_DRAFT;
  public static final int BITMASK_DELETED           = Flag.BITMASK_DELETED;
  public static final int BITMASK_NOTIFIED          = Flag.BITMASK_NOTIFIED;
  public static final int BITMASK_UNREAD            = Flag.BITMASK_UNREAD;
  public static final int BITMASK_HIGH_PRIORITY     = Flag.BITMASK_HIGH_PRIORITY;
  public static final int BITMASK_LOW_PRIORITY      = Flag.BITMASK_LOW_PRIORITY;
  public static final int BITMASK_VERSIONED         = Flag.BITMASK_VERSIONED;
  public static final int BITMASK_INDEXING_DEFERRED = Flag.BITMASK_INDEXING_DEFERRED;

  public static final int BITMASK_SUBSCRIBED       = Flag.BITMASK_SUBSCRIBED;
  public static final int BITMASK_EXCLUDE_FREEBUSY = Flag.BITMASK_EXCLUDE_FREEBUSY;
  public static final int BITMASK_CHECKED          = Flag.BITMASK_CHECKED;
  public static final int BITMASK_NO_INHERIT       = Flag.BITMASK_NO_INHERIT;
  public static final int BITMASK_INVITE           = Flag.BITMASK_INVITE;
  public static final int BITMASK_SYNCFOLDER       = Flag.BITMASK_SYNCFOLDER;
  public static final int BITMASK_SYNC             = Flag.BITMASK_SYNC;
  public static final int BITMASK_NO_INFERIORS     = Flag.BITMASK_NO_INFERIORS;

  public static final int BITMASK_GLOBAL   = Flag.BITMASK_GLOBAL;
  public static final int BITMASK_UNCACHED = Flag.BITMASK_UNCACHED;

  private final Flag mFlag;

  public ZEFlag(@NotNull Object item)
  {
    super(item);
    mFlag = (Flag) item;
  }

  public boolean isSystemFlag()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return mFlag.isSystemFlag();
    /* $else $
    return false;
    /* $endif $ */
  }

  public String getName()
  {
    return mFlag.getName();
  }

  public byte getIndex()
  {
    return mFlag.getIndex();
  }

}
