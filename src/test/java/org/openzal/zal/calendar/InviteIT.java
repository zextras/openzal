package org.openzal.zal.calendar;

import com.zimbra.common.calendar.ParsedDateTime;
import com.zimbra.common.calendar.ParsedDuration;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.calendar.Alarm;
import org.junit.Test;
import org.mockito.Mockito;

import java.text.ParseException;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class InviteIT
{
  @Test
  public void reflection_test()
  {
    Invite invite = new Invite(mock(com.zimbra.cs.mailbox.calendar.Invite.class));
  }

  @Test
  public void alarm_with_absolute_trigger() throws ServiceException, ParseException
  {
    com.zimbra.cs.mailbox.calendar.Invite zimbraInvite =
      mock(com.zimbra.cs.mailbox.calendar.Invite.class);
    ParsedDateTime parsedDateTime = ParsedDateTime.parseUtcOnly("2014-09-10T09:29:21.000Z");
    Alarm alarm = new Alarm(
      Alarm.Action.DISPLAY, Alarm.TriggerType.ABSOLUTE, null,
      null, parsedDateTime, null, 0, null, null, null, null
      /* $if ZimbraVersion >= 8.0.4 $ */
      , null
      /* $endif $ */
    );

    Mockito.when(zimbraInvite.hasAlarm()).thenReturn(true);
    Mockito.when(zimbraInvite.getAlarms()).thenReturn(Collections.singletonList(alarm));

    Invite zeInvite = new Invite(zimbraInvite);

    assertTrue(zeInvite.hasAlarm());
  }

  @Test
  public void alarm_with_relative_start_trigger() throws ServiceException, ParseException
  {
    com.zimbra.cs.mailbox.calendar.Invite zimbraInvite =
      mock(com.zimbra.cs.mailbox.calendar.Invite.class);
    Alarm alarm = new Alarm(
      Alarm.Action.DISPLAY, Alarm.TriggerType.RELATIVE, Alarm.TriggerRelated.START,
      ParsedDuration.parse(true, 1, 1, 1, 1, 1), ParsedDateTime.parseUtcOnly("2014-09-10T09:29:21.000Z"), null, 0, null, null, null, null
      /* $if ZimbraVersion >= 8.0.4 $ */
      , null
      /* $endif $ */
    );

    Mockito.when(zimbraInvite.hasAlarm()).thenReturn(true);
    Mockito.when(zimbraInvite.getAlarms()).thenReturn(Collections.singletonList(alarm));
    Mockito.when(zimbraInvite.getStartTime()).thenReturn(ParsedDateTime.parseUtcOnly("2014-09-10T09:29:21.000Z"));

    Invite zeInvite = new Invite(zimbraInvite);

    assertTrue(zeInvite.hasAlarm());
  }

  @Test
  public void alarm_with_relative_start_trigger_and_no_start_date() throws ServiceException, ParseException
  {
    com.zimbra.cs.mailbox.calendar.Invite zimbraInvite =
      mock(com.zimbra.cs.mailbox.calendar.Invite.class);
    Alarm alarm = new Alarm(
      Alarm.Action.DISPLAY, Alarm.TriggerType.RELATIVE, Alarm.TriggerRelated.START,
      ParsedDuration.parse(true, 1, 1, 1, 1, 1), ParsedDateTime.parseUtcOnly("2014-09-10T09:29:21.000Z"), null, 0, null, null, null, null
      /* $if ZimbraVersion >= 8.0.4 $ */
      , null
      /* $endif $ */
    );

    Mockito.when(zimbraInvite.hasAlarm()).thenReturn(true);
    Mockito.when(zimbraInvite.getAlarms()).thenReturn(Collections.singletonList(alarm));

    Invite zeInvite = new Invite(zimbraInvite);

    assertFalse(zeInvite.hasAlarm());
  }

  @Test
  public void alarm_with_relative_end_trigger() throws ServiceException, ParseException
  {
    com.zimbra.cs.mailbox.calendar.Invite zimbraInvite =
      mock(com.zimbra.cs.mailbox.calendar.Invite.class);
    Alarm alarm = new Alarm(
      Alarm.Action.DISPLAY, Alarm.TriggerType.RELATIVE, Alarm.TriggerRelated.END,
      ParsedDuration.parse(true, 1, 1, 1, 1, 1), ParsedDateTime.parseUtcOnly("2014-09-10T09:29:21.000Z"), null, 0, null, null, null, null
      /* $if ZimbraVersion >= 8.0.4 $ */
      , null
      /* $endif $ */
    );

    Mockito.when(zimbraInvite.hasAlarm()).thenReturn(true);
    Mockito.when(zimbraInvite.getAlarms()).thenReturn(Collections.singletonList(alarm));
    Mockito.when(zimbraInvite.getEffectiveEndTime()).thenReturn(ParsedDateTime.parseUtcOnly("2014-09-10T09:29:21.000Z"));

    Invite zeInvite = new Invite(zimbraInvite);

    assertTrue(zeInvite.hasAlarm());
  }

  @Test
  public void alarm_with_relative_end_trigger_and_no_end_date() throws ServiceException, ParseException
  {
    com.zimbra.cs.mailbox.calendar.Invite zimbraInvite =
      mock(com.zimbra.cs.mailbox.calendar.Invite.class);
    Alarm alarm = new Alarm(
      Alarm.Action.DISPLAY, Alarm.TriggerType.RELATIVE, Alarm.TriggerRelated.END,
      ParsedDuration.parse(true, 1, 1, 1, 1, 1), ParsedDateTime.parseUtcOnly("2014-09-10T09:29:21.000Z"), null, 0, null, null, null, null
      /* $if ZimbraVersion >= 8.0.4 $ */
      , null
      /* $endif $ */
    );

    Mockito.when(zimbraInvite.hasAlarm()).thenReturn(true);
    Mockito.when(zimbraInvite.getAlarms()).thenReturn(Collections.singletonList(alarm));

    Invite zeInvite = new Invite(zimbraInvite);

    assertFalse(zeInvite.hasAlarm());
  }

  @Test
  public void no_alarm() throws ServiceException, ParseException
  {
    com.zimbra.cs.mailbox.calendar.Invite zimbraInvite =
      mock(com.zimbra.cs.mailbox.calendar.Invite.class);
    ParsedDateTime parsedDateTime = ParsedDateTime.parseUtcOnly("2014-09-10T09:29:21.000Z");
    Alarm alarm = new Alarm(Alarm.Action.DISPLAY, Alarm.TriggerType.ABSOLUTE, null, null, parsedDateTime, null, 0, null, null, null, null
      /* $if ZimbraVersion >= 8.0.4 $ */
    , null
      /* $endif $ */
    );

    Mockito.when(zimbraInvite.hasAlarm()).thenReturn(false);
    Mockito.when(zimbraInvite.getAlarms()).thenReturn(Collections.singletonList(alarm));

    Invite zeInvite = new Invite(zimbraInvite);

    assertFalse(zeInvite.hasAlarm());
  }
}