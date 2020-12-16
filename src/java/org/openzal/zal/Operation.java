package org.openzal.zal;

public enum Operation implements Comparable<Operation>
{
  SKIP(0),
  CHECKPOINT(1),
  COMMIT_TXN(3),
  ABORT_TXN(4),
  ROLLOVER(6),
  CREATE_MAILBOX(7),
  DELETE_MAILBOX(8),
  BACKUP_MAILBOX(9),
  REINDEX_MAILBOX(10),
  PURGE_OLD_MESSAGES(11),
  CREATE_SAVED_SEARCH(12),
  MODIFY_SAVED_SEARCH(13),
  CREATE_TAG(14),
  RENAME_TAG(15),
  COLOR_ITEM(16),
  INDEX_ITEM(17),
  ALTER_ITEM_TAG(18),
  SET_ITEM_TAGS(19),
  MOVE_ITEM(20),
  DELETE_ITEM(21),
  COPY_ITEM(22),
  CREATE_FOLDER_PATH(23),
  RENAME_FOLDER_PATH(24),
  EMPTY_FOLDER(25),
  STORE_INCOMING_BLOB(26),
  CREATE_MESSAGE(27),
  SAVE_DRAFT(28),
  SET_IMAP_UID(29),
  CREATE_CONTACT(30),
  MODIFY_CONTACT(31),
  CREATE_NOTE(32),
  EDIT_NOTE(33),
  REPOSITION_NOTE(34),
  CREATE_MOUNTPOINT(35),
  MODIFY_INVITE_FLAG(36),
  MODIFY_INVITE_PARTSTAT(37),
  CREATE_VOLUME(38),
  MODIFY_VOLUME(39),
  DELETE_VOLUME(40),
  SET_CURRENT_VOLUME(41),
  MOVE_BLOBS(42),
  CREATE_INVITE(43),
  SET_CALENDAR_ITEM(44),
  TRACK_SYNC(45),
  SET_CONFIG(46),
  GRANT_ACCESS(47),
  REVOKE_ACCESS(48),
  SET_FOLDER_URL(49),
  SET_SUBSCRIPTION_DATA(50),
  SET_PERMISSIONS(51),
  SAVE_WIKI(52),
  SAVE_DOCUMENT(53),
  ADD_DOCUMENT_REVISION(54),
  TRACK_IMAP(55),
  IMAP_COPY_ITEM(56),
  ICAL_REPLY(57),
  CREATE_FOLDER(58),
  RENAME_FOLDER(59),
  FIX_CALENDAR_ITEM_TIME_ZONE(60),
  RENAME_ITEM(61),
  RENAME_ITEM_PATH(62),
  CREATE_CHAT(63),
  SAVE_CHAT(64),
  PURGE_IMAP_DELETED(65),
  DISMISS_CALENDAR_ITEM_ALARM(66),
  FIX_CALENDAR_ITEM_END_TIME(67),
  INDEX_DEFERRED_ITEMS(68),
  RENAME_MAILBOX(69),
  FIX_CALENDAR_ITEM_TZ(70),
  DATE_ITEM(71),
  SET_FOLDER_DEFAULT_VIEW(72),
  SET_CUSTOM_DATA(73),
  LOCK_ITEM(74),
  UNLOCK_ITEM(75),
  PURGE_REVISION(76),
  DELETE_ITEM_FROM_DUMPSTER(77),
  FIX_CALENDAR_ITEM_PRIORITY(78),
  RECOVER_ITEM(79),
  ENABLE_SHARED_REMINDER(80),
  DOWNLOAD(81),
  PREVIEW(82),
  SNOOZE_CALENDAR_ITEM_ALARM(83),
  CREATE_COMMENT(84),
  CREATE_LINK(85),
  SET_RETENTION_POLICY(86),
  WATCH(87),
  UNWATCH(88),
  REFRESH_MOUNTPOINT(89),
  EXPIRE_ACCESS(90),
  SET_DISABLE_ACTIVE_SYNC(91),
  SET_WEB_OFFLINE_SYNC_DAYS(92),
  DELETE_CONFIG(93);

  private int operationCode;

  Operation(int operationCode)
  {
    this.operationCode = operationCode;
  }

  public int opCode() {
    return operationCode;
  }

  public static Operation fromOperationCode(int code) {
    switch (code) {
      case 1:
        return CHECKPOINT;
      case 3:
        return COMMIT_TXN;
      case 4:
        return ABORT_TXN;
      case 6:
        return ROLLOVER;
      case 7:
        return CREATE_MAILBOX;
      case 8:
        return DELETE_MAILBOX;
      case 9:
        return BACKUP_MAILBOX;
      case 10:
        return REINDEX_MAILBOX;
      case 11:
        return PURGE_OLD_MESSAGES;
      case 12:
        return CREATE_SAVED_SEARCH;
      case 13:
        return MODIFY_SAVED_SEARCH;
      case 14:
        return CREATE_TAG;
      case 15:
        return RENAME_TAG;
      case 16:
        return COLOR_ITEM;
      case 17:
        return INDEX_ITEM;
      case 18:
        return ALTER_ITEM_TAG;
      case 19:
        return SET_ITEM_TAGS;
      case 20:
        return MOVE_ITEM;
      case 21:
        return DELETE_ITEM;
      case 22:
        return COPY_ITEM;
      case 23:
        return CREATE_FOLDER_PATH;
      case 24:
        return RENAME_FOLDER_PATH;
      case 25:
        return EMPTY_FOLDER;
      case 26:
        return STORE_INCOMING_BLOB;
      case 27:
        return CREATE_MESSAGE;
      case 28:
        return SAVE_DRAFT;
      case 29:
        return SET_IMAP_UID;
      case 30:
        return CREATE_CONTACT;
      case 31:
        return MODIFY_CONTACT;
      case 32:
        return CREATE_NOTE;
      case 33:
        return EDIT_NOTE;
      case 34:
        return REPOSITION_NOTE;
      case 35:
        return CREATE_MOUNTPOINT;
      case 36:
        return MODIFY_INVITE_FLAG;
      case 37:
        return MODIFY_INVITE_PARTSTAT;
      case 38:
        return CREATE_VOLUME;
      case 39:
        return MODIFY_VOLUME;
      case 40:
        return DELETE_VOLUME;
      case 41:
        return SET_CURRENT_VOLUME;
      case 42:
        return MOVE_BLOBS;
      case 43:
        return CREATE_INVITE;
      case 44:
        return SET_CALENDAR_ITEM;
      case 45:
        return TRACK_SYNC;
      case 46:
        return SET_CONFIG;
      case 47:
        return GRANT_ACCESS;
      case 48:
        return REVOKE_ACCESS;
      case 49:
        return SET_FOLDER_URL;
      case 50:
        return SET_SUBSCRIPTION_DATA;
      case 51:
        return SET_PERMISSIONS;
      case 52:
        return SAVE_WIKI;
      case 53:
        return SAVE_DOCUMENT;
      case 54:
        return ADD_DOCUMENT_REVISION;
      case 55:
        return TRACK_IMAP;
      case 56:
        return IMAP_COPY_ITEM;
      case 57:
        return ICAL_REPLY;
      case 58:
        return CREATE_FOLDER;
      case 59:
        return RENAME_FOLDER;
      case 60:
        return FIX_CALENDAR_ITEM_TIME_ZONE;
      case 61:
        return RENAME_ITEM;
      case 62:
        return RENAME_ITEM_PATH;
      case 63:
        return CREATE_CHAT;
      case 64:
        return SAVE_CHAT;
      case 65:
        return PURGE_IMAP_DELETED;
      case 66:
        return DISMISS_CALENDAR_ITEM_ALARM;
      case 67:
        return FIX_CALENDAR_ITEM_END_TIME;
      case 68:
        return INDEX_DEFERRED_ITEMS;
      case 69:
        return RENAME_MAILBOX;
      case 70:
        return FIX_CALENDAR_ITEM_TZ;
      case 71:
        return DATE_ITEM;
      case 72:
        return SET_FOLDER_DEFAULT_VIEW;
      case 73:
        return SET_CUSTOM_DATA;
      case 74:
        return LOCK_ITEM;
      case 75:
        return UNLOCK_ITEM;
      case 76:
        return PURGE_REVISION;
      case 77:
        return DELETE_ITEM_FROM_DUMPSTER;
      case 78:
        return FIX_CALENDAR_ITEM_PRIORITY;
      case 79:
        return RECOVER_ITEM;
      case 80:
        return ENABLE_SHARED_REMINDER;
      case 81:
        return DOWNLOAD;
      case 82:
        return PREVIEW;
      case 83:
        return SNOOZE_CALENDAR_ITEM_ALARM;
      case 84:
        return CREATE_COMMENT;
      case 85:
        return CREATE_LINK;
      case 86:
        return SET_RETENTION_POLICY;
      case 87:
        return WATCH;
      case 88:
        return UNWATCH;
      case 89:
        return REFRESH_MOUNTPOINT;
      case 90:
        return EXPIRE_ACCESS;
      case 91:
        return SET_DISABLE_ACTIVE_SYNC;
      case 92:
        return SET_WEB_OFFLINE_SYNC_DAYS;
      case 93:
        return DELETE_CONFIG;
      case 0:
      default:
        return SKIP;
    }
  }
}
