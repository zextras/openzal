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

import org.openzal.zal.calendar.ZEInvite;
import org.openzal.zal.soap.ZEElement;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.CalendarResource;
import com.zimbra.cs.account.Cos;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.DistributionList;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Identity;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.Signature;
import com.zimbra.cs.account.XMPPComponent;
import com.zimbra.cs.account.Zimlet;
import com.zimbra.cs.account.accesscontrol.RightCommand;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.mailbox.ACL;
import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.calendar.Invite;
import com.zimbra.cs.mime.MPartInfo;
/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.cs.volume.Volume;
import com.zimbra.soap.admin.type.AccountQuotaInfo;
import com.zimbra.cs.account.UCService;
/* $else $
import com.zimbra.cs.store.file.Volume;
/* $endif $ */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ZimbraListWrapper
{
  protected static List<ZECos> wrapCoses(List<Cos> coses)
  {
    if (coses == null || coses.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZECos> list = new ArrayList<ZECos>(coses.size());
    for (Cos cos : coses)
    {
      list.add(new ZECos(cos));
    }

    return list;
  }

  protected static List<ZEDataSource> wrapDataSources(List<DataSource> dataSources)
  {
    if (dataSources == null || dataSources.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZEDataSource> list = new ArrayList<ZEDataSource>(dataSources.size());
    for (DataSource dataSource : dataSources)
    {
      list.add(new ZEDataSource(dataSource));
    }

    return list;
  }

  public static List<ZEIdentity> wrapIdentities(List<Identity> identities)
  {
    if (identities == null || identities.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZEIdentity> list = new ArrayList<ZEIdentity>(identities.size());
    for (Identity identity : identities)
    {
      list.add(new ZEIdentity(identity));
    }

    return list;
  }

  public static List<ZEDistributionList> wrapDistributionLists(List<DistributionList> distributionLists)
  {
    if (distributionLists == null || distributionLists.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZEDistributionList> list = new ArrayList<ZEDistributionList>(distributionLists.size());
    for (DistributionList distributionList : distributionLists)
    {
      list.add(new ZEDistributionList(distributionList));
    }

    return list;
  }

  public static List<ZESignature> wrapSignatures(List<Signature> signatures)
  {
    if (signatures == null || signatures.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZESignature> list = new ArrayList<ZESignature>(signatures.size());
    for (Signature signature : signatures)
    {
      list.add(new ZESignature(signature));
    }

    return list;
  }

  public static List<ZEAccount> wrapAccounts(List accounts)
  {
    if (accounts == null || accounts.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZEAccount> list = new ArrayList<ZEAccount>(accounts.size());
    for (Object account : accounts)
    {
      list.add(new ZEAccount((Account) account));
    }

    return list;
  }

  public static List<ZEDomain> wrapDomain(List<Domain> domains)
  {
    if (domains == null || domains.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZEDomain> list = new ArrayList<ZEDomain>(domains.size());
    for (Domain signature : domains)
    {
      list.add(new ZEDomain(signature));
    }

    return list;
  }

  public static List<ZEServer> wrapServers(List<Server> servers)
  {
    if (servers == null || servers.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZEServer> list = new ArrayList<ZEServer>(servers.size());
    for (Server signature : servers)
    {
      list.add(new ZEServer(signature));
    }

    return list;
  }

  public static List<ZECalendarResource> wrapCalendarResources(List calendarResources)
  {
    if (calendarResources == null || calendarResources.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZECalendarResource> list = new ArrayList<ZECalendarResource>(calendarResources.size());
    for (Object calendarResource : calendarResources)
    {
      list.add(new ZECalendarResource((CalendarResource) calendarResource));
    }

    return list;
  }

  public static List<ZEZimlet> wrapZimlets(List zimlets)
  {
    if (zimlets == null || zimlets.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZEZimlet> list = new ArrayList<ZEZimlet>(zimlets.size());
    for (Object calendarResource : zimlets)
    {
      list.add(new ZEZimlet((Zimlet) calendarResource));
    }

    return list;
  }

  public static List<ZEXMPPComponent> wrapXmppComponents(List<XMPPComponent> xmppComponents)
  {
    if (xmppComponents == null || xmppComponents.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZEXMPPComponent> list = new ArrayList<ZEXMPPComponent>(xmppComponents.size());
    for (XMPPComponent xmppComponent : xmppComponents)
    {
      list.add(new ZEXMPPComponent(xmppComponent));
    }

    return list;
  }

  /* $if ZimbraVersion >= 8.0.0 $ */
  public static List<ZEUCService> wrapUCServices(List<UCService> ucServices)
  {
    if (ucServices == null || ucServices.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZEUCService> list = new ArrayList<ZEUCService>(ucServices.size());
    for (UCService ucService : ucServices)
    {
      list.add(new ZEUCService(ucService));
    }

    return list;
  }
  /* $endif $ */

  public static List<ZEMPartInfo> wrapMPartInfos(List<MPartInfo> mPartInfos)
  {
    if (mPartInfos == null || mPartInfos.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZEMPartInfo> list = new ArrayList<ZEMPartInfo>(mPartInfos.size());
    for (MPartInfo mPartInfo : mPartInfos)
    {
      list.add(new ZEMPartInfo(mPartInfo));
    }

    return list;
  }

  public static List<ZEContact.ContactAttachment> wrapAttachments(List<Contact.Attachment> attachments)
  {
    if (attachments == null || attachments.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZEContact.ContactAttachment> list = new ArrayList<ZEContact.ContactAttachment>();

    for (Contact.Attachment attachment : attachments)
    {
      list.add(new ZEContact.ContactAttachment(attachment));
    }
    return list;
  }

  public static List<ZEProvisioning.ZECountAccountByCos> wrapCountAccountByCosList(
    List<Provisioning.CountAccountResult.CountAccountByCos> countAccountByCosList
  )
  {
    if (countAccountByCosList == null || countAccountByCosList.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZEProvisioning.ZECountAccountByCos> list =
      new ArrayList<ZEProvisioning.ZECountAccountByCos>(countAccountByCosList.size());

    for (Provisioning.CountAccountResult.CountAccountByCos countAccountByCos : countAccountByCosList)
    {
      list.add(new ZEProvisioning.ZECountAccountByCos(countAccountByCos));
    }

    return list;
  }

  /* $if ZimbraVersion >= 8.0.0 $ */
  public static List<ZEAccountQuotaInfo> wrapAccountQuotaInfos(List<AccountQuotaInfo> accountQuotas)
  /* $else $
  public static List<ZEAccountQuotaInfo> wrapAccountQuotaInfos(List<Object> accountQuotas)
  /* $endif $ */
  {
    if (accountQuotas == null || accountQuotas.size() == 0)
    {
      return Collections.emptyList();
    }
    /* $if ZimbraVersion >= 8.0.0 $ */
    List<ZEAccountQuotaInfo> list =
      new ArrayList<ZEAccountQuotaInfo>(accountQuotas.size());

    for (AccountQuotaInfo accountQuota : accountQuotas)
    {
      list.add(new ZEAccountQuotaInfo(
        accountQuota.getId(),
        accountQuota.getName(),
        accountQuota.getQuotaLimit(),
        accountQuota.getQuotaUsed())
      );
    }

    return list;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public static List<ZEElement> wrapElements(List<Element> elements)
  {
    if (elements == null || elements.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZEElement> list =
      new ArrayList<ZEElement>(elements.size());

    for (Element element : elements)
    {
      list.add(new ZEElement(element));
    }

    return list;
  }

  public static Collection<ZEQuotaUsage> wrapQuotaUsages(List<SoapProvisioning.QuotaUsage> quotaUsages)
  {
    if (quotaUsages == null || quotaUsages.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZEQuotaUsage> list =
      new ArrayList<ZEQuotaUsage>(quotaUsages.size());

    for (SoapProvisioning.QuotaUsage quotaUsage : quotaUsages)
    {
      list.add(new ZEQuotaUsage(quotaUsage));
    }

    return list;
  }

  public static Set<ZEACE> wrapACEs(Set<RightCommand.ACE> aces)
  {
    if (aces == null || aces.size() == 0)
    {
      return Collections.emptySet();
    }
    Set<ZEACE> set = new HashSet<ZEACE>();

    for (RightCommand.ACE ace : aces)
    {
      set.add(new ZEACE(ace));
    }

    return set;
  }

  public static List<ZECalendarItem> wrapCalendarItems(List<CalendarItem> zimbraCalendarItems)
  {
    if (zimbraCalendarItems == null || zimbraCalendarItems.size() == 0)
    {
      return Collections.emptyList();
    }
    List<ZECalendarItem> list = new ArrayList<ZECalendarItem>();

    for (CalendarItem calendarItem : zimbraCalendarItems)
    {
      list.add(new ZECalendarItem(calendarItem));
    }

    return list;
  }

  public static List<ZEGrant> wrapGrants(List<ACL.Grant> grants)
  {
    if (grants == null || grants.size() == 0)
    {
      return Collections.emptyList();
    }

    List<ZEGrant> grantList = new ArrayList<ZEGrant>();
    for (ACL.Grant grant : grants)
    {
      grantList.add(new ZEGrant(grant));
    }
    return grantList;
  }

  public static List<ZEVolume> wrapVolumes(List<Volume> list)
  {
    if (list == null || list.size() == 0)
    {
      return Collections.emptyList();
    }

    List<ZEVolume> newList = new ArrayList<ZEVolume>(list.size());
    for( Volume vol : list )
    {
      newList.add(new ZEVolume(vol));
    }
    return newList;
  }

  public static List<ZEInvite> wrapInvites(List<Invite> inviteList)
  {
    if (inviteList == null || inviteList.size() == 0)
    {
      return Collections.emptyList();
    }

    List<ZEInvite> newList = new ArrayList<ZEInvite>(inviteList.size());
    for( Invite invite : inviteList )
    {
      newList.add(new ZEInvite(invite));
    }
    return newList;
  }
}
