package org.openzal.zal;

public interface AdditionalQuotaProvider
{
  long getAdditionalQuota(Mailbox mailbox);
}
