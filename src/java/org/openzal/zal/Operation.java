package org.openzal.zal;

public enum Operation implements Comparable<Operation>
{
  SKIP(0),
  MAILBOX_CREATED(1),
  ACCOUNT_SCAN(2),
  MAILBOX_DELETED(3),
  ITEM_SCAN(3),
  BLOB_MOVED(4),
  ACCOUNT_INFO(3),
  ;

  private int priority;

  Operation(int priority)
  {
    this.priority = priority;
  }

  public static int compare(Operation operation1, Operation operation2)
  {
    return Integer.compare(operation1.priority, operation2.priority);
  }
}
