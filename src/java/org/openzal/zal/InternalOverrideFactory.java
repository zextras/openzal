package org.openzal.zal;

import com.zimbra.cs.store.file.InternalOverrideBlob;

public class InternalOverrideFactory
{
  public static Object wrapBlob(Blob blob)
  {
    return InternalOverrideBlob.wrap(blob);
  }

  public static Object wrapMailboxBlob(MailboxBlob blob)
  {
    return InternalOverrideMailboxBlob.wrap(blob);
  }
}
