package com.zextras.newchat.xmpp.decoders;

import com.zextras.newchat.Target;
import com.zextras.newchat.address.SpecificAddress;
import com.zextras.newchat.db.providers.UserProvider;
import com.zextras.newchat.events.Event;
import com.zextras.newchat.events.EventFriendAccepted;
import com.zextras.newchat.events.EventFriendAdded;
import com.zextras.newchat.events.EventStatusChanged;
import com.zextras.newchat.status.FixedStatus;
import com.zextras.newchat.xmpp.encoders.EventFriendAcceptedEncoder;
import com.zextras.newchat.xmpp.encoders.EventFriendAddedEncoder;
import com.zextras.newchat.xmpp.encoders.EventStatusChangedEncoder;
import com.zextras.newchat.xmpp.encoders.XmppEncoderFactory;
import com.zextras.newchat.xmpp.xml.SchemaProvider;
import org.openzal.zal.ZEProvisioningSimulator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PresenceDecoderTest
{
  private XmppEncoderFactory      mXmppEncoderFactory;
  private SchemaProvider          mSchemaProvider;
  private UserProvider            mUserProvider;
  private ZEProvisioningSimulator mZEProvisioningSimulator;

  @Before
  public void setup()
  {
    mSchemaProvider = new SchemaProvider();
    mXmppEncoderFactory = new XmppEncoderFactory(mSchemaProvider);
    mZEProvisioningSimulator = new ZEProvisioningSimulator();
    mUserProvider = Mockito.mock(UserProvider.class);
  }

  @Test
  public void friend_added_event_encode_event_decode() throws XMLStreamException, UnsupportedEncodingException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    SpecificAddress sender = new SpecificAddress("sender@example.com");
    SpecificAddress target = new SpecificAddress("target@example.com");

    EventFriendAdded eventFriendAdded = new EventFriendAdded(
      sender,
      target
    );

    EventFriendAddedEncoder eventFriendAddedEncoder = (EventFriendAddedEncoder) eventFriendAdded.createEncoder(mXmppEncoderFactory);
    PresenceDecoder presenceDecoder = new PresenceDecoder(mSchemaProvider, mZEProvisioningSimulator, mUserProvider);
    eventFriendAddedEncoder.encode(out, target);

    assertEquals("<presence xmlns=\"jabber:client\" id=\"" + eventFriendAdded.getId().toString() + "\" " +
                   "from=\"sender@example.com\" to=\"target@example.com\" type=\"subscribe\"/>", out.toString()
    );

    ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());
    List<Event> list = presenceDecoder.decode(inputStream);

    assertTrue(list.size() == 1);

    EventFriendAdded eventFriendAdded2 = (EventFriendAdded) list.get(0);
    assertEquals(eventFriendAdded.getNickname(),eventFriendAdded2.getNickname());
    assertEquals(eventFriendAdded.getFriendToAdd(),eventFriendAdded2.getFriendToAdd());
    assertEquals(eventFriendAdded.getTarget(),eventFriendAdded2.getTarget());
    assertEquals(eventFriendAdded.getSender(),eventFriendAdded2.getSender());
  }

  @Test
  public void accepted_event_encode_event_decode() throws XMLStreamException, UnsupportedEncodingException
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    SpecificAddress sender = new SpecificAddress("sender@example.com");
    SpecificAddress target = new SpecificAddress("target@example.com");

    EventFriendAccepted eventFriendAccepted = new EventFriendAccepted(
      sender,
      target,
      new Target(target)
    );

    EventFriendAcceptedEncoder eventFriendAddedEncoder = (EventFriendAcceptedEncoder) eventFriendAccepted.createEncoder(mXmppEncoderFactory);
    PresenceDecoder presenceDecoder = new PresenceDecoder(mSchemaProvider, mZEProvisioningSimulator, mUserProvider );
    eventFriendAddedEncoder.encode(out, target);

    assertEquals(
      "<presence xmlns=\"jabber:client\" type=\"subscribed\" from=\"sender@example.com\" to=\"target@example.com\"/>",
      out.toString()
    );

    ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());
    List<Event> list = presenceDecoder.decode(inputStream);

    assertTrue(list.size() == 1);

    EventFriendAccepted eventFriendAccepted2 = (EventFriendAccepted) list.get(0);
    assertEquals(eventFriendAccepted.getTarget(), eventFriendAccepted2.getTarget());
    assertEquals(eventFriendAccepted.getSender(), eventFriendAccepted2.getSender());
    assertEquals(eventFriendAccepted.getTarget(), eventFriendAccepted2.getTarget());
  }

  @Test
  public void available_or_invisible_event_encode_event_decode()
    throws XMLStreamException, UnsupportedEncodingException
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    SpecificAddress sender = new SpecificAddress("sender@example.com");
    SpecificAddress target = new SpecificAddress("target@example.com");

    EventStatusChanged eventStatusChanged = new EventStatusChanged(
      sender,
      new Target(target),
      FixedStatus.Invisible
    );

    EventStatusChangedEncoder eventStatusChangedEncoder = (EventStatusChangedEncoder) eventStatusChanged.createEncoder(mXmppEncoderFactory);
    eventStatusChangedEncoder.encode(out, target);

    PresenceDecoder presenceDecoder = new PresenceDecoder(mSchemaProvider, mZEProvisioningSimulator, mUserProvider );
    ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());
    List<Event> list = presenceDecoder.decode(inputStream);

    assertTrue(list.size() == 1);

    EventStatusChanged eventStatusChanged1 = (EventStatusChanged) list.get(0);
    assertEquals(eventStatusChanged.getTarget(), eventStatusChanged1.getTarget());
    assertEquals(eventStatusChanged.getSender(), eventStatusChanged1.getSender());
    assertEquals(eventStatusChanged.getTarget(), eventStatusChanged1.getTarget());

    /**
     * Available
     * **/
    out = new ByteArrayOutputStream();
    eventStatusChanged = new EventStatusChanged(
      sender,
      new Target(target),
      FixedStatus.Available
    );

    eventStatusChangedEncoder = (EventStatusChangedEncoder) eventStatusChanged.createEncoder(mXmppEncoderFactory);
    eventStatusChangedEncoder.encode(out, target);

    presenceDecoder = new PresenceDecoder(mSchemaProvider, mZEProvisioningSimulator, mUserProvider );
    inputStream = new ByteArrayInputStream(out.toByteArray());
    list = presenceDecoder.decode(inputStream);

    assertTrue(list.size() == 1);

    eventStatusChanged1 = (EventStatusChanged) list.get(0);
    assertEquals(eventStatusChanged.getTarget(), eventStatusChanged1.getTarget());
    assertEquals(eventStatusChanged.getSender(), eventStatusChanged1.getSender());
    assertEquals(eventStatusChanged.getTarget(), eventStatusChanged1.getTarget());
  }
}
