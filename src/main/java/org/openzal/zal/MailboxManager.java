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

import javax.annotation.Nonnull;
import org.openzal.zal.exceptions.ZimbraException;
import java.util.Set;

public interface MailboxManager
{
  int[] getMailboxIds();

  Set<Integer> getMailboxIdsSet();

  Set<Integer> getMailboxGroupSet();

  Mailbox getMailboxById(long mailboxId) throws ZimbraException;

  Mailbox getMailboxById(long mailboxId,boolean skipMailHostCheck) throws ZimbraException;

  Mailbox getMailboxByAccount(Account account) throws ZimbraException;

  Mailbox getMailboxByAccountId(String accountId) throws ZimbraException;

  int getMailboxCount();

  void addListener(MailboxManagerListener listener);

  void removeListener(MailboxManagerListener listener);

  Mailbox getMailboxByAccountId(String accountId, boolean autoCreate) throws ZimbraException;

  MailboxMaintenance beginMaintenance(String accountId, int mailboxId) throws ZimbraException;

  void endMaintenance(MailboxMaintenance maintenance, boolean success, boolean removeFromCache) throws ZimbraException;

  void cleanCache(Mailbox mailbox);

  Mailbox cleanCacheAndGetUpdatedMailbox(Mailbox mailbox);

  Mailbox cleanCacheAndGetUpdatedMailboxById(Mailbox mailbox, boolean skipMailhostCheck);

  void registerAdditionalQuotaProvider(AdditionalQuotaProvider additionalQuotaProvider);

  void removeAdditionalQuotaProvider(AdditionalQuotaProvider additionalQuotaProvider);

  MailboxData getMailboxData(long mailboxId);

  void forceDeleteMailbox(@Nonnull MailboxData data);

  void createMailboxWithSpecificId(Connection connection, Account account, long mailboxId);
}
