package com.zimbra.cs.db;

import javax.annotation.Nonnull;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.UnableToObtainDBConnectionException;
import org.openzal.zal.lib.ZimbraConnectionWrapper;
import org.openzal.zal.lib.ZimbraDatabase;

public class ZimbraConnectionProviderSimulator implements ZimbraDatabase.ConnectionProvider
{
  @Nonnull
  @Override
  public org.openzal.zal.Connection getConnection() throws UnableToObtainDBConnectionException
  {
    try {
      DbPool.DbConnection connection = DbPool.getConnection();
      DbConnectionSimulator connectionSimulator = new DbConnectionSimulator(connection.getConnection());
      return new ZimbraConnectionWrapper(connectionSimulator);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.createUnableToObtainDBConnection(e);
    }
  }
}
