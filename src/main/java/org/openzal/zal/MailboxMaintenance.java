package org.openzal.zal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MailboxMaintenance {
  private com.zimbra.cs.mailbox.MailboxMaintenance mMailboxMaintenance;

  public MailboxMaintenance(@Nullable Object mailboxMaintenance)
  {
    if (mailboxMaintenance == null)
    {
      throw new IllegalArgumentException("mailboxMaintenance is null");
    }
    this.mMailboxMaintenance = (com.zimbra.cs.mailbox.MailboxMaintenance) mailboxMaintenance;
  }

  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(mMailboxMaintenance);
  }

}
