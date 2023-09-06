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

import com.zimbra.common.account.ZAttrProvisioning;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Provisioning;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.openzal.zal.exceptions.ExceptionWrapper;

public class DomainStatus
{
  public static String DOMAIN_STATUS_ACTIVE = Provisioning.DOMAIN_STATUS_ACTIVE;
  public static String DOMAIN_STATUS_MAINTENANCE = Provisioning.DOMAIN_STATUS_MAINTENANCE;
  public static String DOMAIN_STATUS_LOCKED = Provisioning.DOMAIN_STATUS_LOCKED;
  public static String DOMAIN_STATUS_CLOSED = Provisioning.DOMAIN_STATUS_CLOSED;
  public static String DOMAIN_STATUS_SUSPENDED = Provisioning.DOMAIN_STATUS_SUSPENDED;
  public static String DOMAIN_STATUS_SHUTDOWN = Provisioning.DOMAIN_STATUS_SHUTDOWN;

  private final ZAttrProvisioning.DomainStatus domainStatus;

  protected DomainStatus(@Nonnull Object domainStatus) {
    if (domainStatus == null) {
      throw new NullPointerException();
    }
    this.domainStatus = (ZAttrProvisioning.DomainStatus) domainStatus;
  }

  public DomainStatus(@Nonnull String domainStatusString) {
    try {
      this.domainStatus = ZAttrProvisioning.DomainStatus.fromString(domainStatusString);
    } catch (ServiceException e) {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DomainStatus that = (DomainStatus) o;
    return domainStatus == that.domainStatus;
  }

  @Override
  public int hashCode() {
    return Objects.hash(domainStatus);
  }

  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(domainStatus);
  }

  @Override
  public String toString()
  {
    return domainStatus.toString();
  }
}
