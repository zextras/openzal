package org.openzal.zal;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;

public class ZEMailboxManagerListenerWrapperTest
{
  @Test
  public void equals()
  {
    ZEMailboxManagerListener listener = Mockito.mock(ZEMailboxManagerListener.class);

    ZEMailboxManagerListenerWrapper firstWrapper = new ZEMailboxManagerListenerWrapper(listener);
    ZEMailboxManagerListenerWrapper secondWrapper = new ZEMailboxManagerListenerWrapper(listener);

    assertEquals(firstWrapper, secondWrapper);
  }

  @Test
  public void not_equals()
  {
    ZEMailboxManagerListener firstListener = Mockito.mock(ZEMailboxManagerListener.class);
    ZEMailboxManagerListener secondListener = Mockito.mock(ZEMailboxManagerListener.class);

    ZEMailboxManagerListenerWrapper firstWrapper = new ZEMailboxManagerListenerWrapper(firstListener);
    ZEMailboxManagerListenerWrapper secondWrapper = new ZEMailboxManagerListenerWrapper(secondListener);

    assertNotEquals(firstWrapper, secondWrapper);
  }

  @Test
  public void add_listener()
  {
    ZEMailboxManagerListener firstListener = Mockito.mock(ZEMailboxManagerListener.class);
    ZEMailboxManagerListener secondListener = Mockito.mock(ZEMailboxManagerListener.class);

    CopyOnWriteArrayList<ZEMailboxManagerListenerWrapper> listeners =
      new CopyOnWriteArrayList<ZEMailboxManagerListenerWrapper>();

    assertFalse(listeners.contains(new ZEMailboxManagerListenerWrapper(firstListener)));
    assertFalse(listeners.contains(new ZEMailboxManagerListenerWrapper(secondListener)));

    listeners.add(new ZEMailboxManagerListenerWrapper(firstListener));
    assertTrue(listeners.contains(new ZEMailboxManagerListenerWrapper(firstListener)));
    assertFalse(listeners.contains(new ZEMailboxManagerListenerWrapper(secondListener)));

    listeners.add(new ZEMailboxManagerListenerWrapper(secondListener));
    assertTrue(listeners.contains(new ZEMailboxManagerListenerWrapper(firstListener)));
    assertTrue(listeners.contains(new ZEMailboxManagerListenerWrapper(secondListener)));

    assertEquals(2, listeners.size());
  }

  @Test
  public void remove_listener()
  {
    ZEMailboxManagerListener firstListener = Mockito.mock(ZEMailboxManagerListener.class);
    ZEMailboxManagerListener secondListener = Mockito.mock(ZEMailboxManagerListener.class);

    CopyOnWriteArrayList<ZEMailboxManagerListenerWrapper> listeners =
      new CopyOnWriteArrayList<ZEMailboxManagerListenerWrapper>();

    listeners.add(new ZEMailboxManagerListenerWrapper(firstListener));
    listeners.add(new ZEMailboxManagerListenerWrapper(secondListener));

    assertTrue(listeners.remove(new ZEMailboxManagerListenerWrapper(firstListener)));
    assertFalse(listeners.remove(new ZEMailboxManagerListenerWrapper(firstListener)));

    assertFalse(listeners.contains(new ZEMailboxManagerListenerWrapper(firstListener)));
    assertTrue(listeners.contains(new ZEMailboxManagerListenerWrapper(secondListener)));

    assertEquals(1, listeners.size());
  }
}