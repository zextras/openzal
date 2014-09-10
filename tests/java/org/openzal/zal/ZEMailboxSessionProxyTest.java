package org.openzal.zal;

import com.zimbra.cs.session.Session;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;

public class ZEMailboxSessionProxyTest
{
  @Test
  public void add_listener_to_map()
  {
    CopyOnWriteArrayList<Session> listeners = new CopyOnWriteArrayList<Session>();

    ZEListener listener = Mockito.mock(ZEListener.class);
    ZEMailboxSessionProxy sessionProxy = new ZEMailboxSessionProxy(1, "listener", "accountId", listener);

    listeners.add(sessionProxy.toZimbra(Session.class));

    assertEquals(1, listeners.size());
    assertTrue(listeners.contains(sessionProxy.toZimbra(Session.class)));
  }

  @Test
  public void remove_listener_from_map()
  {
    CopyOnWriteArrayList<Session> listeners = new CopyOnWriteArrayList<Session>();

    ZEListener listener = Mockito.mock(ZEListener.class);
    ZEMailboxSessionProxy sessionProxy = new ZEMailboxSessionProxy(1, "listener", "accountId", listener);

    listeners.add(sessionProxy.toZimbra(Session.class));

    Object listenerObject = null;
    for (Session listenerSession : listeners)
    {
      if (listenerSession.getSessionId().equals("listener"))
      {
        listenerObject = listenerSession;
      }
    }
    assertNotNull("Map should contain a Session with getSessionId() = \"listener\"", listenerObject);

    listeners.remove(new ZEMailboxSessionProxy(listenerObject).toZimbra(Session.class));

    assertEquals(0, listeners.size());
  }
}