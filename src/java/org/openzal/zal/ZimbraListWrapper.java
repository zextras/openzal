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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openzal.zal.calendar.Invite;
import org.openzal.zal.soap.SoapElement;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.accesscontrol.RightCommand;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.mailbox.ACL;
import com.zimbra.cs.volume.Volume;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ZimbraListWrapper
{
  @NotNull
  protected static List<Cos> wrapCoses(@Nullable List<com.zimbra.cs.account.Cos> coses)
  {
    if (coses == null || coses.size() == 0)
    {
      return Collections.emptyList();
    }
    List<Cos> list = new ArrayList<Cos>(coses.size());
    for (com.zimbra.cs.account.Cos cos : coses)
    {
      list.add(new Cos(cos));
    }

    return list;
  }

  @NotNull
  protected static List<DataSource> wrapDataSources(@Nullable List<com.zimbra.cs.account.DataSource> dataSources)
  {
    if (dataSources == null || dataSources.size() == 0)
    {
      return Collections.emptyList();
    }
    List<DataSource> list = new ArrayList<DataSource>(dataSources.size());
    for (com.zimbra.cs.account.DataSource dataSource : dataSources)
    {
      list.add(new DataSource(dataSource));
    }

    return list;
  }

  @NotNull
  public static List<Identity> wrapIdentities(@Nullable List<com.zimbra.cs.account.Identity> identities)
  {
    if (identities == null || identities.size() == 0)
    {
      return Collections.emptyList();
    }
    List<Identity> list = new ArrayList<Identity>(identities.size());
    for (com.zimbra.cs.account.Identity identity : identities)
    {
      list.add(new Identity(identity));
    }

    return list;
  }

  @NotNull
  public static List<DistributionList> wrapDistributionLists(@Nullable List<com.zimbra.cs.account.DistributionList> distributionLists)
  {
    if (distributionLists == null || distributionLists.size() == 0)
    {
      return Collections.emptyList();
    }
    List<DistributionList> list = new ArrayList<DistributionList>(distributionLists.size());
    for (com.zimbra.cs.account.DistributionList distributionList : distributionLists)
    {
      list.add(new DistributionList(distributionList));
    }

    return list;
  }

  @NotNull
  public static List<Signature> wrapSignatures(@Nullable List<com.zimbra.cs.account.Signature> signatures)
  {
    if (signatures == null || signatures.size() == 0)
    {
      return Collections.emptyList();
    }
    List<Signature> list = new ArrayList<Signature>(signatures.size());
    for (com.zimbra.cs.account.Signature signature : signatures)
    {
      list.add(new Signature(signature));
    }

    return list;
  }

  @NotNull
  public static List<Account> wrapAccounts(@Nullable List accounts)
  {
    if (accounts == null || accounts.size() == 0)
    {
      return Collections.emptyList();
    }
    List<Account> list = new ArrayList<Account>(accounts.size());
    for (Object account : accounts)
    {
      list.add(new Account((com.zimbra.cs.account.Account) account));
    }

    return list;
  }

  @NotNull
  public static List<Domain> wrapDomain(@Nullable List<com.zimbra.cs.account.Domain> domains)
  {
    if (domains == null || domains.size() == 0)
    {
      return Collections.emptyList();
    }
    List<Domain> list = new ArrayList<Domain>(domains.size());
    for (com.zimbra.cs.account.Domain signature : domains)
    {
      list.add(new Domain(signature));
    }

    return list;
  }

  @NotNull
  public static List<Server> wrapServers(@Nullable List<com.zimbra.cs.account.Server> servers)
  {
    if (servers == null || servers.size() == 0)
    {
      return Collections.emptyList();
    }
    List<Server> list = new ArrayList<Server>(servers.size());
    for (com.zimbra.cs.account.Server signature : servers)
    {
      list.add(new Server(signature));
    }

    return list;
  }

  @NotNull
  public static List<CalendarResource> wrapCalendarResources(@Nullable List calendarResources)
  {
    if (calendarResources == null || calendarResources.size() == 0)
    {
      return Collections.emptyList();
    }
    List<CalendarResource> list = new ArrayList<CalendarResource>(calendarResources.size());
    for (Object calendarResource : calendarResources)
    {
      list.add(new CalendarResource((com.zimbra.cs.account.CalendarResource) calendarResource));
    }

    return list;
  }

  @NotNull
  public static List<Zimlet> wrapZimlets(@Nullable List zimlets)
  {
    if (zimlets == null || zimlets.size() == 0)
    {
      return Collections.emptyList();
    }
    List<Zimlet> list = new ArrayList<Zimlet>(zimlets.size());
    for (Object calendarResource : zimlets)
    {
      list.add(new Zimlet((com.zimbra.cs.account.Zimlet) calendarResource));
    }

    return list;
  }

  @NotNull
  public static List<XMPPComponent> wrapXmppComponents(@Nullable List<com.zimbra.cs.account.XMPPComponent> xmppComponents)
  {
    if (xmppComponents == null || xmppComponents.size() == 0)
    {
      return Collections.emptyList();
    }
    List<XMPPComponent> list = new ArrayList<XMPPComponent>(xmppComponents.size());
    for (com.zimbra.cs.account.XMPPComponent xmppComponent : xmppComponents)
    {
      list.add(new XMPPComponent(xmppComponent));
    }

    return list;
  }

  @NotNull
  public static List<UCService> wrapUCServices(@Nullable List ucServices)
  {
    if (ucServices == null || ucServices.size() == 0)
    {
      return Collections.emptyList();
    }

    List<UCService> list = new ArrayList<UCService>(ucServices.size());
    for (Object ucService : ucServices)
    {
      list.add(new UCService(ucService));
    }

    return list;
  }


  @NotNull
  public static List<MPartInfo> wrapMPartInfos(@Nullable List<com.zimbra.cs.mime.MPartInfo> mPartInfos)
  {
    if (mPartInfos == null || mPartInfos.size() == 0)
    {
      return Collections.emptyList();
    }
    List<MPartInfo> list = new ArrayList<MPartInfo>(mPartInfos.size());
    for (com.zimbra.cs.mime.MPartInfo mPartInfo : mPartInfos)
    {
      list.add(new MPartInfo(mPartInfo));
    }

    return list;
  }

  @NotNull
  public static List<Contact.ContactAttachment> wrapAttachments(@Nullable List<com.zimbra.cs.mailbox.Contact.Attachment> attachments)
  {
    if (attachments == null || attachments.size() == 0)
    {
      return Collections.emptyList();
    }
    List<Contact.ContactAttachment> list = new ArrayList<Contact.ContactAttachment>();

    for (com.zimbra.cs.mailbox.Contact.Attachment attachment : attachments)
    {
      list.add(new Contact.ContactAttachment(attachment));
    }
    return list;
  }

  @NotNull
  public static List<ProvisioningImp.CountAccountByCos> wrapCountAccountByCosList(
    @Nullable List<com.zimbra.cs.account.Provisioning.CountAccountResult.CountAccountByCos> countAccountByCosList
  )
  {
    if (countAccountByCosList == null || countAccountByCosList.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ProvisioningImp.CountAccountByCos> list =
      new ArrayList<ProvisioningImp.CountAccountByCos>(countAccountByCosList.size());

    for (com.zimbra.cs.account.Provisioning.CountAccountResult.CountAccountByCos countAccountByCos : countAccountByCosList)
    {
      list.add(new ProvisioningImp.CountAccountByCos(countAccountByCos));
    }

    return list;
  }

  @NotNull
  public static List<AccountQuotaInfo> wrapAccountQuotaInfos(@Nullable List<com.zimbra.soap.admin.type.AccountQuotaInfo> accountQuotas)
  {
    if (accountQuotas == null || accountQuotas.size() == 0)
    {
      return Collections.emptyList();
    }

    List<AccountQuotaInfo> list =
      new ArrayList<AccountQuotaInfo>(accountQuotas.size());

    for (com.zimbra.soap.admin.type.AccountQuotaInfo accountQuota : accountQuotas)
    {
      list.add(new AccountQuotaInfo(
        accountQuota.getId(),
        accountQuota.getName(),
        accountQuota.getQuotaLimit(),
        accountQuota.getQuotaUsed())
      );
    }

    return list;
  }

  @NotNull
  public static List<SoapElement> wrapElements(@Nullable List<Element> elements)
  {
    if (elements == null || elements.size() == 0)
    {
      return Collections.emptyList();
    }
    List<SoapElement> list =
      new ArrayList<SoapElement>(elements.size());

    for (Element element : elements)
    {
      list.add(new SoapElement(element));
    }

    return list;
  }

  @NotNull
  public static Collection<QuotaUsage> wrapQuotaUsages(@Nullable List<SoapProvisioning.QuotaUsage> quotaUsages)
  {
    if (quotaUsages == null || quotaUsages.size() == 0)
    {
      return Collections.emptyList();
    }
    List<QuotaUsage> list =
      new ArrayList<QuotaUsage>(quotaUsages.size());

    for (SoapProvisioning.QuotaUsage quotaUsage : quotaUsages)
    {
      list.add(new QuotaUsage(quotaUsage));
    }

    return list;
  }

  @NotNull
  public static Set<ACE> wrapACEs(@Nullable Set<RightCommand.ACE> aces)
  {
    if (aces == null || aces.size() == 0)
    {
      return Collections.emptySet();
    }
    Set<ACE> set = new HashSet<ACE>();

    for (RightCommand.ACE ace : aces)
    {
      set.add(new ACE(ace));
    }

    return set;
  }

  @NotNull
  public static List<CalendarItem> wrapCalendarItems(@Nullable List<com.zimbra.cs.mailbox.CalendarItem> zimbraCalendarItems)
  {
    if (zimbraCalendarItems == null || zimbraCalendarItems.size() == 0)
    {
      return Collections.emptyList();
    }
    List<CalendarItem> list = new ArrayList<CalendarItem>();

    for (com.zimbra.cs.mailbox.CalendarItem calendarItem : zimbraCalendarItems)
    {
      list.add(new CalendarItem(calendarItem));
    }

    return list;
  }

  @NotNull
  public static List<Grant> wrapGrants(@Nullable List<ACL.Grant> grants)
  {
    if (grants == null || grants.size() == 0)
    {
      return Collections.emptyList();
    }

    List<Grant> grantList = new ArrayList<Grant>();
    for (ACL.Grant grant : grants)
    {
      grantList.add(new Grant(grant));
    }
    return grantList;
  }

  @NotNull
  public static List<StoreVolume> wrapVolumes(@Nullable List<Volume> list)
  {
    if (list == null || list.size() == 0)
    {
      return Collections.emptyList();
    }

    List<StoreVolume> newList = new ArrayList<StoreVolume>(list.size());
    for( Volume vol : list )
    {
      newList.add(new StoreVolume(vol));
    }
    return newList;
  }

  @NotNull
  public static List<Invite> wrapInvites(@Nullable List<com.zimbra.cs.mailbox.calendar.Invite> inviteList)
  {
    if (inviteList == null || inviteList.size() == 0)
    {
      return Collections.emptyList();
    }

    List<Invite> newList = new ArrayList<Invite>(inviteList.size());
    for( com.zimbra.cs.mailbox.calendar.Invite invite : inviteList )
    {
      newList.add(new Invite(invite));
    }
    return newList;
  }
}
