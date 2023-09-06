package org.openzal.zal;

import com.zimbra.cs.convert.ConversionException;

public class TemporaryIndexingError extends ConversionException {

  public TemporaryIndexingError(String msg, Throwable t) {
    this(msg, t, true);
  }

  public TemporaryIndexingError(String msg, Throwable t, boolean temp) {
    super(msg, t, temp);
  }

  public TemporaryIndexingError(String msg) {
    this(msg, true);
  }

  public TemporaryIndexingError(String msg, boolean temp) {
    super(msg, temp);
  }

}
