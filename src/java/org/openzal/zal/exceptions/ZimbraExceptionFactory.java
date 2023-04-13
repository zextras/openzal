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

package org.openzal.zal.exceptions;

import javax.annotation.Nonnull;

public class ZimbraExceptionFactory
{
  protected static ZimbraException wrapAccountServiceException(
    @Nonnull com.zimbra.cs.account.AccountServiceException accountServiceException
  )
  {
    String code = accountServiceException.getCode();
    if (code.equals(com.zimbra.cs.account.AccountServiceException.NO_SUCH_ACCOUNT))
    {
      return new NoSuchAccountException(accountServiceException);
    }
    else if (code.equals(com.zimbra.cs.account.AccountServiceException.MAINTENANCE_MODE))
    {
      return new MaintenanceModeAccountException(accountServiceException);
    }
    else if (code.equals(com.zimbra.cs.account.AccountServiceException.CHANGE_PASSWORD))
    {
      return new ChangePasswordAccountException(accountServiceException);
    }
    else if (code.equals("account.NO_SUCH_DOMAIN"))
    {
      return new NoSuchDomainException(accountServiceException);
    }
    else if (code.equals(com.zimbra.cs.account.AccountServiceException.NO_SUCH_SERVER))
    {
      return new NoSuchServerException(accountServiceException);
    }

    return new ZimbraException(accountServiceException);
  }

  protected static ZimbraException wrapServiceException(
    com.zimbra.common.service.ServiceException serviceException
  )
  {
    String code = serviceException.getCode();
    if (code.equals(com.zimbra.common.service.ServiceException.PERM_DENIED))
    {
      return new PermissionDeniedException(serviceException);
    }
    else if (code.equals(com.zimbra.common.service.ServiceException.INVALID_REQUEST))
    {
      return new InvalidRequestException(serviceException);
    }
    else if (code.equals(com.zimbra.common.service.ServiceException.ALREADY_IN_PROGRESS))
    {
      return new AlreadyInProgressException(serviceException);
    }

    return new ZimbraException(serviceException);
  }

  protected static ZimbraException wrapMailServiceException(
    com.zimbra.cs.mailbox.MailServiceException mailServiceException
  )
  {
    String code = mailServiceException.getCode();
    if (code.equals(com.zimbra.cs.mailbox.MailServiceException.NO_SUCH_FOLDER))
    {
      return new NoSuchFolderException(mailServiceException);
    }
    else if (code.equals(com.zimbra.cs.mailbox.MailServiceException.NO_SUCH_ITEM))
    {
      return new NoSuchItemException(mailServiceException);
    }
    else if (code.equals(com.zimbra.cs.mailbox.MailServiceException.NO_SUCH_MSG))
    {
      return new NoSuchMessageException(mailServiceException);
    }
    else if (code.equals(com.zimbra.cs.mailbox.MailServiceException.NO_SUCH_CONV))
    {
      return new NoSuchConversationException(mailServiceException);
    }
    else if (code.equals(com.zimbra.cs.mailbox.MailServiceException.NO_SUCH_BLOB))
    {
      return new NoSuchBlobException(mailServiceException);
    }
    else if (code.equals(com.zimbra.cs.mailbox.MailServiceException.QUOTA_EXCEEDED))
    {
      return new QuotaExceededException(mailServiceException);
    }
    else if (code.equals(com.zimbra.cs.mailbox.MailServiceException.ALREADY_EXISTS))
    {
      return new AlreadyExistsException(mailServiceException);
    }
    else if (code.equals(com.zimbra.cs.mailbox.MailServiceException.PERM_DENIED))
    {
      return new PermissionDeniedException(mailServiceException);
    }

    return new ZimbraException(mailServiceException);
  }

  static ZimbraException wrapVolumeServiceException(
    com.zimbra.cs.volume.VolumeServiceException volumeServiceException
  )
  {
    return new VolumeServiceException(volumeServiceException);
  }

  public static ZimbraException wrap(Throwable ex)
  {
    return new ZimbraException(ex);
  }

  public static UnableToSanitizeFolderNameException createUnableToSanitizeFolder(String folderName, ZimbraException ex)
  {
    return new UnableToSanitizeFolderNameException(folderName, ex);
  }

  public static UnableToSanitizeFolderNameException createUnableToSanitizeFolder(String folderName)
  {
    return new UnableToSanitizeFolderNameException(folderName);
  }

  public static UnableToFindDistributionListException createUnableToFindDistributionList(String list, Throwable t)
  {
    return new UnableToFindDistributionListException(list, t);
  }

  public static UnableToFindDistributionListException createUnableToFindDistributionList(String list)
  {
    return new UnableToFindDistributionListException(list);
  }

  public static UnableToObtainDBConnectionException createUnableToObtainDBConnection(
    com.zimbra.common.service.ServiceException e
  )
  {
    throw new UnableToObtainDBConnectionException(e);
  }
}
