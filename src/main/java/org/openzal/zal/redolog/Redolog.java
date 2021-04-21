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

package org.openzal.zal.redolog;

import javax.annotation.Nonnull;
import org.openzal.zal.redolog.op.RedoableOp;

import com.zimbra.cs.mailbox.MailboxOperation;

public class Redolog
{
  public static int getOpCode( @Nonnull RedoableOp op )
  {
    return op.getOpCode();
  }

  public static String getOpClassName( RedoableOp op )
  {
    MailboxOperation mop = MailboxOperation.fromInt(getOpCode(op));
    return mop.name();
  }

  public static String toString( RedoableOp op )
  {
    return Redolog.getOpClassName(op)+" " +op.getTransactionId().toString();
  }

  public static final int OP_CHECKPOINT = 1;
  public static final int OP_COMMIT_TXN = 3;
  public static final int OP_ABORT_TXN = 4;
  public static final int OP_ROLLOVER = 6;
  public static final int OP_CREATE_MAILBOX = 7;
  public static final int OP_DELETE_MAILBOX = 8;
  public static final int OP_BACKUP_MAILBOX = 9;
  public static final int OP_REINDEX_MAILBOX = 10;
  public static final int OP_PURGE_OLD_MESSAGES = 11;
  public static final int OP_CREATE_SAVED_SEARCH = 12;
  public static final int OP_MODIFY_SAVED_SEARCH = 13;
  public static final int OP_CREATE_TAG = 14;
  public static final int OP_RENAME_TAG = 15;
  public static final int OP_COLOR_ITEM = 16;
  public static final int OP_INDEX_ITEM = 17;
  public static final int OP_ALTER_ITEM_TAG = 18;
  public static final int OP_SET_ITEM_TAGS = 19;
  public static final int OP_MOVE_ITEM = 20;
  public static final int OP_DELETE_ITEM = 21;
  public static final int OP_COPY_ITEM = 22;
  public static final int OP_CREATE_FOLDER_PATH = 23;
  public static final int OP_RENAME_FOLDER_PATH = 24;
  public static final int OP_EMPTY_FOLDER = 25;
  public static final int OP_STORE_INCOMING_BLOB = 26;
  public static final int OP_CREATE_MESSAGE = 27;
  public static final int OP_SAVE_DRAFT = 28;
  public static final int OP_SET_IMAP_UID = 29;
  public static final int OP_CREATE_CONTACT = 30;
  public static final int OP_MODIFY_CONTACT = 31;
  public static final int OP_CREATE_NOTE = 32;
  public static final int OP_EDIT_NOTE = 33;
  public static final int OP_REPOSITION_NOTE = 34;
  public static final int OP_CREATE_MOUNTPOINT = 35;
  public static final int OP_MODIFY_INVITE_FLAG = 36;
  public static final int OP_MODIFY_INVITE_PARTSTAT = 37;
  public static final int OP_CREATE_VOLUME = 38;
  public static final int OP_MODIFY_VOLUME = 39;
  public static final int OP_DELETE_VOLUME = 40;
  public static final int OP_SET_CURRENT_VOLUME = 41;
  public static final int OP_MOVE_BLOBS = 42;
  public static final int OP_CREATE_INVITE = 43;
  public static final int OP_SET_CALENDAR_ITEM = 44;
  public static final int OP_TRACK_SYNC = 45;
  public static final int OP_SET_CONFIG = 46;
  public static final int OP_GRANT_ACCESS = 47;
  public static final int OP_REVOKE_ACCESS = 48;
  public static final int OP_SET_FOLDER_URL = 49;
  public static final int OP_SET_SUBSCRIPTION_DATA = 50;
  public static final int OP_SET_PERMISSIONS = 51;
  public static final int OP_SAVE_WIKI = 52;
  public static final int OP_SAVE_DOCUMENT = 53;
  public static final int OP_ADD_DOCUMENT_REVISION = 54;
  public static final int OP_TRACK_IMAP = 55;
  public static final int OP_IMAP_COPY_ITEM = 56;
  public static final int OP_ICAL_REPLY = 57;
  public static final int OP_CREATE_FOLDER = 58;
  public static final int OP_RENAME_FOLDER = 59;
  public static final int OP_FIX_CALENDAR_ITEM_TIME_ZONE = 60;
  public static final int OP_RENAME_ITEM = 61;
  public static final int OP_RENAME_ITEM_PATH = 62;
  public static final int OP_CREATE_CHAT = 63;
  public static final int OP_SAVE_CHAT = 64;
  public static final int OP_PURGE_IMAP_DELETED = 65;
  public static final int OP_DISMISS_CALENDAR_ITEM_ALARM = 66;
  public static final int OP_FIX_CALENDAR_ITEM_END_TIME = 67;
  public static final int OP_INDEX_DEFERRED_ITEMS = 68;
  public static final int OP_RENAME_MAILBOX = 69;
  public static final int OP_FIX_CALENDAR_ITEM_TZ = 70;
  public static final int OP_DATE_ITEM = 71;
  public static final int OP_SET_FOLDER_DEFAULT_VIEW = 72;
  public static final int OP_SET_CUSTOM_DATA = 73;
  public static final int OP_LOCK_ITEM = 74;
  public static final int OP_UNLOCK_ITEM = 75;
  public static final int OP_PURGE_REVISION = 76;
  public static final int OP_DELETE_ITEM_FROM_DUMPSTER = 77;
  public static final int OP_FIX_CALENDAR_ITEM_PRIORITY = 78;
  public static final int OP_RECOVER_ITEM = 79;
  public static final int OP_ENABLE_SHARED_REMINDER = 80;
  public static final int OP_DOWNLOAD = 81;
  public static final int OP_PREVIEW = 82;
  public static final int OP_SNOOZE_CALENDAR_ITEM_ALARM = 83;
  public static final int OP_CREATE_COMMENT = 84;
  public static final int OP_CREATE_LINK = 85;
  public static final int OP_SET_RETENTION_POLICY = 86;
  public static final int OP_WATCH = 87;
  public static final int OP_UNWATCH = 88;
  public static final int OP_REFRESH_MOUNTPOINT = 89;
  public static final int OP_EXPIRE_ACCESS = 90;
  public static final int OP_SET_DISABLE_ACTIVE_SYNC = 91;
  public static final int OP_SET_WEB_OFFLINE_SYNC_DAYS = 92;
  public static final int OP_DELETE_CONFIG = 93;
}