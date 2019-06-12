package org.openzal.zal;

public enum Operation implements Comparable<Operation>
{
  MAILBOX_CREATED(0),
  ACCOUNT_SCAN(1),
  MAILBOX_DELETED(2),
  ITEM_SCAN(2),
  SKIP(3);

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
