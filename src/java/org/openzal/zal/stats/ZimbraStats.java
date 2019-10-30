/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2019 ZeXtras S.r.l.
 *
 * This file is part of ZAL.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZAL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.openzal.zal.stats;

import com.zimbra.common.stats.StatsDumper;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.stats.ActivityTracker;
import com.zimbra.cs.stats.ZimbraPerf;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

public class ZimbraStats
{
  private static final HashMap<StatName, ActivityTracker> sStatMap = new HashMap<>();

  private static final    long          sInterruptPeriod = 10000L;
  private static          ReentrantLock sLock            = new ReentrantLock();
  private static          Timer         sTimer           = null;
  private static volatile boolean       sSuspended       = false;
  private static final    Field         sStatsThreadGroup;

  static
  {
    sStatMap.put(StatName.soap,ZimbraPerf.SOAP_TRACKER);
    sStatMap.put(StatName.imap, ZimbraPerf.IMAP_TRACKER);
    sStatMap.put(StatName.pop, ZimbraPerf.POP_TRACKER);
    sStatMap.put(StatName.ldap, ZimbraPerf.LDAP_TRACKER);
    sStatMap.put(StatName.sync, ZimbraPerf.SYNC_TRACKER);
    sStatMap.put(StatName.sql, ZimbraPerf.SQL_TRACKER);

    try
    {
      sStatsThreadGroup = StatsDumper.class.getDeclaredField("statsGroup");
      sStatsThreadGroup.setAccessible(true);
    }
    catch (NoSuchFieldException e)
    {
      throw new RuntimeException(e);
    }
  }

  public enum StatName
  {
    soap,
    imap,
    pop,
    ldap,
    sync,
    sql
  }

  public static class StatInstance
  {
    private final String mName;
    private final String mCount;
    private final String mAvarage;

    public String getName()
    {
      return mName;
    }

    public String getCount()
    {
      return mCount;
    }

    public String getAvarage()
    {
      return mAvarage;
    }

    public StatInstance(String name, String count, String avarage)
    {
      mName = name;
      mCount = count;
      mAvarage = avarage;
    }
  }

  /**
   * Retrieve only the provided names
   * @return
   */
  public Map<StatName,List<StatInstance>> getAndResetTrackers(List<StatName> names)
  {
    //the format of the line is "command,count,avarage"
    HashMap<StatName, List<StatInstance>> results = new HashMap<>();

    for( StatName name : names )
    {
      ActivityTracker tracker = sStatMap.get(name);
      Collection<String> lines = tracker.getDataLines();
      if( lines == null ) lines = Collections.emptyList();

      ArrayList<StatInstance> resultList = new ArrayList<>(lines.size());
      results.put(name, resultList);

      for(String line : lines)
      {
        String[] fields = line.split(",");
        resultList.add(new StatInstance(fields[0], fields[1], fields[2]));
      }
    }

    return results;
  }

  /**
   * Return zimbra mailbox stats, similar to mailbox.csv stats
   * The returned map could contain double, long, int
   */
  public Map<String, Object> getMailboxStats()
  {
    return ZimbraPerf.getStats();
  }


  /**
   * Suspend zimbra stats writer, leaving the stats in ram to be consumed by some
   * other means.
   *
   * To actually suspend zimbra writer threads an interrupt is sent the thread every 10 second
   * which interrupt their 60 second sleep cycle which trigger a new sleep cycle, an endless sleep
   * cycle, muahaha...
   */
  public void suspend()
  {
    sLock.lock();
    try
    {
      if( sTimer == null )
      {
        sTimer = new Timer();
        sTimer.schedule(
          new TimerTask()
          {
            @Override
            public void run()
            {
              if( sSuspended )
              {
                try
                {
                  ThreadGroup threadGroup = ((ThreadGroup) sStatsThreadGroup.get(null));
                  threadGroup.interrupt();
                }
                catch (IllegalAccessException e)
                {
                  throw new RuntimeException(e);
                }
              }
            }
          },
          0L,
          sInterruptPeriod
        );
      }
    }
    finally
    {
      sLock.unlock();
    }

    //avoid tons of useless log messages from zimbra
    ZimbraLog.perf.setLevel(Log.Level.warn);
    sSuspended = true;
  }

  /**
   * Resume zimbra stats writer
   */
  public void resume()
  {
    sSuspended = false;
  }
}
