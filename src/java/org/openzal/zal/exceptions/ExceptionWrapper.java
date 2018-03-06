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

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.mailbox.MailServiceException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ExceptionWrapper
{
  private interface ExceptionWrapperCreator
  {

    ZimbraException create(Exception exception);
  }

  @NotNull
  private static Map<String, ExceptionWrapperCreator> mExceptionMap = new HashMap<String, ExceptionWrapperCreator>();

  private static final String INTERNAL_SERVER_EXCEPTION = "internalServerException";

  private static final String VOLUME_SERVICE_EXCEPTION = "volumeServiceException";
  private static final String DEFAULT                  = "unknownException";
  private static final String LDAP_EXCEPTION           = "ldapException";
  private static final String EXTENSION_EXCEPTION      = "extensionException";
  private static final String ZIMLET_EXCEPTION         = "zimletException";
  private static final String AUTH_TOKEN_EXCEPTION     = "authTokenException";

  static
  {
    mExceptionMap.put(
      com.zimbra.cs.account.AccountServiceException.NO_SUCH_ACCOUNT, new ExceptionWrapperCreator()
      {
        @Override
        public ZimbraException create(Exception exception)
        {
          return new NoSuchAccountException(exception);
        }
      }
    );
    mExceptionMap.put(
      com.zimbra.cs.account.AccountServiceException.TOO_MANY_IDENTITIES, new ExceptionWrapperCreator()
      {
        @Override
        public ZimbraException create(Exception exception)
        {
          return new TooManyIdentitiesException(exception);
        }
      }
    );
    mExceptionMap.put(
      com.zimbra.cs.account.AccountServiceException.DATA_SOURCE_EXISTS, new ExceptionWrapperCreator()
      {
        @Override
        public ZimbraException create(Exception exception)
        {
          return new DataSourceExistsException(exception);
        }
      }
    );
    mExceptionMap.put(
      com.zimbra.cs.account.AccountServiceException.IDENTITY_EXISTS, new ExceptionWrapperCreator()
      {
        @Override
        public ZimbraException create(Exception exception)
      {
        return new IdentityExistsException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.account.AccountServiceException.TOO_MANY_DATA_SOURCES, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new TooManyDataSourcesException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.account.AccountServiceException.NO_SUCH_SIGNATURE, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new NoSuchSignatureException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.account.AccountServiceException.MAINTENANCE_MODE, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new MaintenanceModeAccountException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.account.AccountServiceException.CHANGE_PASSWORD, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new ChangePasswordAccountException(exception);
      }
    });
    /* $if ZimbraVersion >= 8.7.2 $ */
    mExceptionMap.put(Constants.ERROR_CODE_NO_SUCH_DOMAIN, new ExceptionWrapperCreator()
    /* $else $
    mExceptionMap.put(com.zimbra.cs.account.AccountServiceException.NO_SUCH_DOMAIN, new ExceptionWrapperCreator()
    /* $endif $ */
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new NoSuchDomainException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.account.AccountServiceException.NO_SUCH_SERVER, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new NoSuchServerException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.account.AccountServiceException.NO_SUCH_ALIAS, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new NoSuchAliasException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.common.service.ServiceException.PERM_DENIED, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new PermissionDeniedException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.common.service.ServiceException.INVALID_REQUEST, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new InvalidRequestException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.common.service.ServiceException.ALREADY_IN_PROGRESS, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new AlreadyInProgressException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.mailbox.MailServiceException.NO_SUCH_ITEM, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new NoSuchItemException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.mailbox.MailServiceException.NO_SUCH_TAG, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new NoSuchItemException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.mailbox.MailServiceException.NO_SUCH_FOLDER, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new NoSuchFolderException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.mailbox.MailServiceException.NO_SUCH_MSG, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new NoSuchMessageException(exception);
      }
    });
    mExceptionMap.put( MailServiceException.NO_SUCH_APPT, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new NoSuchCalendarException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.mailbox.MailServiceException.NO_SUCH_BLOB, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new NoSuchBlobException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.mailbox.MailServiceException.QUOTA_EXCEEDED, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new QuotaExceededException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.mailbox.MailServiceException.ALREADY_EXISTS, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new AlreadyExistsException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.mailbox.MailServiceException.PERM_DENIED, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new PermissionDeniedException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.account.AccountServiceException.AUTH_FAILED, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new AuthFailedException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.account.AccountServiceException.NO_SUCH_GRANT, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new NoSuchGrantException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.mailbox.MailServiceException.NO_SUCH_ITEM, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new NoSuchItemException(exception);
      }
    });
    mExceptionMap.put(MailServiceException.WRONG_HOST, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new WrongHostException(exception);
      }
    });
    mExceptionMap.put(LDAP_EXCEPTION, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new LdapException(exception);
      }
    });
    mExceptionMap.put(AUTH_TOKEN_EXCEPTION, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new AuthTokenException(exception);
      }
    });
    mExceptionMap.put(VOLUME_SERVICE_EXCEPTION, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new VolumeServiceException(exception);
      }
    });
    mExceptionMap.put(INTERNAL_SERVER_EXCEPTION, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new InternalServerException(exception);
      }
    });
    mExceptionMap.put(ZIMLET_EXCEPTION, new ExceptionWrapperCreator() {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new ZimbraException(exception);
      }
    });
    mExceptionMap.put(DEFAULT, new ExceptionWrapperCreator()
    {
      @Override
      public ZimbraException create(Exception exception)
      {
        return new ZimbraException(exception);
      }
    });
    mExceptionMap.put(com.zimbra.cs.mailbox.MailServiceException.IMMUTABLE_OBJECT, new ExceptionWrapperCreator()
      {
        @Override
        public ZimbraException create(Exception exception)
        {
          return new ImmutableChangeAttempt(exception);
        }
      }
    );
    mExceptionMap.put(com.zimbra.cs.mailbox.MailServiceException.NO_SUCH_MBOX, new ExceptionWrapperCreator()
      {
        @Override
        public ZimbraException create(Exception exception)
        {
          return new NoSuchMailboxException(exception);
        }
      }
    );
  }

  public static ZimbraException wrap(Exception exception)
  {
    try
    {
      throw exception;
    }
    catch (AccountServiceException.AuthFailedServiceException authFailedServiceException)
    {
      return mExceptionMap.get(com.zimbra.cs.account.AccountServiceException.AUTH_FAILED).create(authFailedServiceException);
    }
    catch(com.zimbra.cs.ldap.LdapException ldapException)
    {
      return mExceptionMap.get(LDAP_EXCEPTION).create(ldapException);
    }
    catch(com.zimbra.cs.extension.ExtensionException extensionException)
    {
      return mExceptionMap.get(EXTENSION_EXCEPTION).create(extensionException);
    }
    catch(com.zimbra.cs.zimlet.ZimletException zimletException)
    {
      return mExceptionMap.get(ZIMLET_EXCEPTION).create(zimletException);
    }
    catch(com.zimbra.cs.account.AuthTokenException authTokenException)
    {
      return mExceptionMap.get(AUTH_TOKEN_EXCEPTION).create(authTokenException);
    }
    catch(ServiceException serviceException)
    {
      String code = serviceException.getCode();
      if (mExceptionMap.containsKey(code))
      {
        return mExceptionMap.get(code).create(serviceException);
      }
    }
    catch (Throwable ignored) {}

    return mExceptionMap.get(DEFAULT).create(exception);
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

  public static NoSuchZimletException createNoSuchZimletException(
    com.zimbra.common.service.ServiceException e
  )
  {
    throw new NoSuchZimletException(e);
  }

  public static NoSuchZimletException createNoSuchZimletException(
    String msg
  )
  {
    throw new NoSuchZimletException(msg);
  }
}
