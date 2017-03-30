package org.openzal.zal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

  @NotNull
  public com.zimbra.cs.mailbox.MailboxMaintenance getMailboxMaintenance()
  {
    return mMailboxMaintenance;
  }

  public <T> T toZimbra(@NotNull Class<T> cls)
  {
    return cls.cast(getMailboxMaintenance());
  }

}
