package org.openzal.zal.calendar;

/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.common.calendar.ParsedDateTime;
import com.zimbra.common.calendar.ParsedDuration;
/* $else $
import com.zimbra.cs.mailbox.calendar.ParsedDateTime;
import com.zimbra.cs.mailbox.calendar.ParsedDuration;
/* $endif $ */
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.calendar.Alarm;
import com.zimbra.cs.mailbox.calendar.Invite;
import org.junit.Test;
import org.mockito.Mockito;

import java.text.ParseException;
import java.util.Collections;

import static org.junit.Assert.*;

public class ZEInviteTest
{
  @Test
  public void alarm_with_absolute_trigger() throws ServiceException, ParseException
  {
    Invite invite = Mockito.mock(Invite.class);
    ParsedDateTime parsedDateTime = ParsedDateTime.parseUtcOnly("");
    Alarm alarm = new Alarm(
      Alarm.Action.DISPLAY, Alarm.TriggerType.ABSOLUTE, null,
      null, parsedDateTime, null, 0, null, null, null, null
      /* $if ZimbraVersion >= 8.0.4 $ */
      , null
      /* $endif $ */
    );

    Mockito.when(invite.hasAlarm()).thenReturn(true);
    Mockito.when(invite.getAlarms()).thenReturn(Collections.singletonList(alarm));

    ZEInvite zeInvite = new ZEInvite(invite);

    assertTrue(zeInvite.hasAlarm());
  }

  @Test
  public void alarm_with_relative_start_trigger() throws ServiceException, ParseException
  {
    Invite invite = Mockito.mock(Invite.class);
    Alarm alarm = new Alarm(
      Alarm.Action.DISPLAY, Alarm.TriggerType.RELATIVE, Alarm.TriggerRelated.START,
      ParsedDuration.parse(true, 1, 1, 1, 1, 1), ParsedDateTime.parseUtcOnly(""), null, 0, null, null, null, null
      /* $if ZimbraVersion >= 8.0.4 $ */
      , null
      /* $endif $ */
    );

    Mockito.when(invite.hasAlarm()).thenReturn(true);
    Mockito.when(invite.getAlarms()).thenReturn(Collections.singletonList(alarm));
    Mockito.when(invite.getStartTime()).thenReturn(ParsedDateTime.parseUtcOnly(""));

    ZEInvite zeInvite = new ZEInvite(invite);

    assertTrue(zeInvite.hasAlarm());
  }

  @Test
  public void alarm_with_relative_start_trigger_and_no_start_date() throws ServiceException, ParseException
  {
    Invite invite = Mockito.mock(Invite.class);
    Alarm alarm = new Alarm(
      Alarm.Action.DISPLAY, Alarm.TriggerType.RELATIVE, Alarm.TriggerRelated.START,
      ParsedDuration.parse(true, 1, 1, 1, 1, 1), ParsedDateTime.parseUtcOnly(""), null, 0, null, null, null, null
      /* $if ZimbraVersion >= 8.0.4 $ */
      , null
      /* $endif $ */
    );

    Mockito.when(invite.hasAlarm()).thenReturn(true);
    Mockito.when(invite.getAlarms()).thenReturn(Collections.singletonList(alarm));

    ZEInvite zeInvite = new ZEInvite(invite);

    assertFalse(zeInvite.hasAlarm());
  }

  @Test
  public void alarm_with_relative_end_trigger() throws ServiceException, ParseException
  {
    Invite invite = Mockito.mock(Invite.class);
    Alarm alarm = new Alarm(
      Alarm.Action.DISPLAY, Alarm.TriggerType.RELATIVE, Alarm.TriggerRelated.END,
      ParsedDuration.parse(true, 1, 1, 1, 1, 1), ParsedDateTime.parseUtcOnly(""), null, 0, null, null, null, null
      /* $if ZimbraVersion >= 8.0.4 $ */
      , null
      /* $endif $ */
    );

    Mockito.when(invite.hasAlarm()).thenReturn(true);
    Mockito.when(invite.getAlarms()).thenReturn(Collections.singletonList(alarm));
    Mockito.when(invite.getEndTime()).thenReturn(ParsedDateTime.parseUtcOnly(""));

    ZEInvite zeInvite = new ZEInvite(invite);

    assertTrue(zeInvite.hasAlarm());
  }

  @Test
  public void alarm_with_relative_end_trigger_and_no_end_date() throws ServiceException, ParseException
  {
    Invite invite = Mockito.mock(Invite.class);
    Alarm alarm = new Alarm(
      Alarm.Action.DISPLAY, Alarm.TriggerType.RELATIVE, Alarm.TriggerRelated.END,
      ParsedDuration.parse(true, 1, 1, 1, 1, 1), ParsedDateTime.parseUtcOnly(""), null, 0, null, null, null, null
      /* $if ZimbraVersion >= 8.0.4 $ */
      , null
      /* $endif $ */
    );

    Mockito.when(invite.hasAlarm()).thenReturn(true);
    Mockito.when(invite.getAlarms()).thenReturn(Collections.singletonList(alarm));

    ZEInvite zeInvite = new ZEInvite(invite);

    assertFalse(zeInvite.hasAlarm());
  }

  @Test
  public void no_alarm() throws ServiceException, ParseException
  {
    Invite invite = Mockito.mock(Invite.class);
    ParsedDateTime parsedDateTime = ParsedDateTime.parseUtcOnly("");
    Alarm alarm = new Alarm(Alarm.Action.DISPLAY, Alarm.TriggerType.ABSOLUTE, null, null, parsedDateTime, null, 0, null, null, null, null
      /* $if ZimbraVersion >= 8.0.4 $ */
    , null
      /* $endif $ */
    );

    Mockito.when(invite.hasAlarm()).thenReturn(false);
    Mockito.when(invite.getAlarms()).thenReturn(Collections.singletonList(alarm));

    ZEInvite zeInvite = new ZEInvite(invite);

    assertFalse(zeInvite.hasAlarm());
  }
}