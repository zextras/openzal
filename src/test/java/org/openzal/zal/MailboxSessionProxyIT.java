package org.openzal.zal;

import com.zimbra.cs.session.Session;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.openzal.zal.lib.ZimbraVersion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MailboxSessionProxyIT
{
  @Test
  public void add_listener_to_map()
  {
    CopyOnWriteArrayList<Session> listeners = new CopyOnWriteArrayList<Session>();

    Listener listener = Mockito.mock(Listener.class);
    MailboxSessionProxy sessionProxy = new MailboxSessionProxy(1, "listener", "accountId", listener);

    listeners.add(sessionProxy.toZimbra(Session.class));

    assertEquals(1, listeners.size());
    assertTrue(listeners.contains(sessionProxy.toZimbra(Session.class)));
  }

  @Test
  public void remove_listener_from_map()
  {
    CopyOnWriteArrayList<Session> listeners = new CopyOnWriteArrayList<Session>();

    Listener listener = Mockito.mock(Listener.class);
    MailboxSessionProxy sessionProxy = new MailboxSessionProxy(1, "listener", "accountId", listener);

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

    listeners.remove(new MailboxSessionProxy(listenerObject).toZimbra(Session.class));

    assertEquals(0, listeners.size());
  }
}