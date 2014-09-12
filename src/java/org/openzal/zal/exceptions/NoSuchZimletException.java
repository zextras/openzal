package org.openzal.zal.exceptions;

public class NoSuchZimletException extends ZimbraException
{
  public NoSuchZimletException(Exception exception) {super(exception);}
  public NoSuchZimletException(String msg) {super(msg);}
}
