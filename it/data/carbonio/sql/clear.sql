--
-- ***** BEGIN LICENSE BLOCK *****
-- Zimbra Collaboration Suite Server
-- Copyright (C) 2011, 2013, 2014, 2016 Synacor, Inc.
--
-- This program is free software: you can redistribute it and/or modify it under
-- the terms of the GNU General Public License as published by the Free Software Foundation,
-- version 2 of the License.
--
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
-- without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
-- See the GNU General Public License for more details.
-- You should have received a copy of the GNU General Public License along with this program.
-- If not, see <https://www.gnu.org/licenses/>.
-- ***** END LICENSE BLOCK *****
--

SET DATABASE REFERENTIAL INTEGRITY FALSE;

DELETE FROM *{DATABASE_NAME}.tagged_item;
DELETE FROM *{DATABASE_NAME}.tag;
DELETE FROM *{DATABASE_NAME}.mail_item;
DELETE FROM *{DATABASE_NAME}.mail_item_dumpster;
DELETE FROM *{DATABASE_NAME}.revision;
DELETE FROM *{DATABASE_NAME}.revision_dumpster;
DELETE FROM *{DATABASE_NAME}.open_conversation;
DELETE FROM *{DATABASE_NAME}.appointment;
DELETE FROM *{DATABASE_NAME}.appointment_dumpster;
DELETE FROM *{DATABASE_NAME}.tombstone;
DELETE FROM *{DATABASE_NAME}.pop3_message;
DELETE FROM *{DATABASE_NAME}.imap_folder;
DELETE FROM *{DATABASE_NAME}.imap_message;
DELETE FROM *{DATABASE_NAME}.data_source_item;
DELETE FROM *{DATABASE_NAME}.search_history;
DELETE FROM *{DATABASE_NAME}.searches;

DELETE FROM ZIMBRA.mailbox;
DELETE FROM ZIMBRA.current_volumes;
DELETE FROM ZIMBRA.volume;
DELETE FROM ZIMBRA.deleted_account;
DELETE FROM ZIMBRA.mailbox_metadata;
DELETE FROM ZIMBRA.out_of_office;
DELETE FROM ZIMBRA.config;
DELETE FROM ZIMBRA.table_maintenance;
DELETE FROM ZIMBRA.service_status;
DELETE FROM ZIMBRA.scheduled_task;
DELETE FROM ZIMBRA.mobile_devices;
DELETE FROM ZIMBRA.pending_acl_push;

INSERT INTO volume (id, type, name, path, file_bits, file_group_bits, mailbox_bits, mailbox_group_bits, compress_blobs, compression_threshold)
  VALUES (1, 1, 'message1', '/tmp/test/store', 12, 8, 12, 8, 0, 4096);
INSERT INTO volume (id, type, name, path, file_bits, file_group_bits, mailbox_bits, mailbox_group_bits, compress_blobs, compression_threshold)
  VALUES (2, 10, 'index1', 'tmp/test/index', 12, 8, 12, 8, 0, 4096);
INSERT INTO volume (id, type, name, path, file_bits, file_group_bits, mailbox_bits, mailbox_group_bits, compress_blobs, compression_threshold)
  VALUES (3, 2, 'message2', '/tmp/test/store2', 12, 8, 12, 8, 0, 4096);
INSERT INTO current_volumes (message_volume_id, index_volume_id, next_mailbox_id, secondary_message_volume_id) VALUES (1, 2, 1, 3);

SET DATABASE REFERENTIAL INTEGRITY TRUE;

