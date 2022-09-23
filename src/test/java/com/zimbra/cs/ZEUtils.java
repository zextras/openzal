package com.zimbra.cs;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.openzal.zal.Utils;

public class ZEUtils {

  /**
   * Utils::exceptionToString wrapper to manage error caused by services shutdown and cluster unreachable
   * @param e Throwable error
   * @return Error message if throwable is subclass of ShuttingDown Error, string representation of the error otherwise
   */
  public static String exceptionToString(@Nonnull Throwable e)
  {
    Objects.requireNonNull(e);
    StringBuilder sb = new StringBuilder(128);
    Throwable cause = e;

    do{
      Utils.addStackTraceElements(sb, cause);
      cause = cause.getCause();
      if( cause != null )
      {
        sb.append("Caused by: ");
      }
    } while( cause != null );

    return sb.toString();
  }
}
