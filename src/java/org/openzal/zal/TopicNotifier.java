package org.openzal.zal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/* $if ZimbraX == 1 $ */
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import com.zimbra.cs.mailbox.RedissonClientHolder;
/* $endif $ */

import javax.annotation.Nonnull;

public class TopicNotifier
{
  /* $if ZimbraX == 1 $ */
  private class TopicListenerWrapper implements MessageListener<String>
  {
    private final TopicListener mListener;

    private TopicListenerWrapper(@Nonnull TopicListener listener)
    {
      mListener = listener;
    }

    @Override
    public void onMessage(CharSequence charSequence, String message)
    {
      mListener.update(message);
    }
  }

  private static Map<String, Set<Pair<TopicListener, TopicListenerWrapper>>> sListeners = new HashMap<>();
  private static final RedissonClient sRedissonClient = RedissonClientHolder.getInstance().getRedissonClient();
  /* $endif $ */

  public void registerListener(String topic, final TopicListener listener)
  {
    /* $if ZimbraX == 1 $ */
    RTopic rTopic = sRedissonClient.getTopic(topic);
    TopicListenerWrapper topicListener = new TopicListenerWrapper(listener);
    rTopic.addListener(String.class, topicListener);
    Set<Pair<TopicListener, TopicListenerWrapper>> listeners;
    if ( sListeners.containsKey(topic))
    {
      listeners = sListeners.get(topic);
    }
    else
    {
      listeners = new HashSet<>();
    }
    listeners.add(new Pair<>(listener, topicListener));
    sListeners.put(topic, listeners);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void unregisterListener(TopicListener listener)
  {
    /* $if ZimbraX == 1 $ */
    RTopic topic;
    Set<Pair<TopicListener, TopicListenerWrapper>> noUnregisteredSet;
    Map<String, Set<Pair<TopicListener, TopicListenerWrapper>>> newListeners = new HashMap<>();
    for(Map.Entry<String, Set<Pair<TopicListener, TopicListenerWrapper>>> entry : sListeners.entrySet())
    {
      topic = sRedissonClient.getTopic(entry.getKey());
      noUnregisteredSet = new HashSet<>(entry.getValue());
      for(Pair<TopicListener, TopicListenerWrapper> pair : entry.getValue())
      {
        if( pair.getFirst().equals(listener) )
        {
          topic.removeListener(pair.getSecond());
          noUnregisteredSet.remove(pair);
        }
      }
      newListeners.put(entry.getKey(), noUnregisteredSet);
    }
    sListeners.clear();
    sListeners.putAll(newListeners);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void publishOnTopic(String topic, String message)
  {
    /* $if ZimbraX == 1 $ */
    sRedissonClient.getTopic(topic).publish(message);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public Collection<TopicListener> getListeners(String topic)
  {
    /* $if ZimbraX == 1 $ */
    Collection<TopicListener> topicListeners = new HashSet<>();
    for(Pair<TopicListener, TopicListenerWrapper> pair : sListeners.get(topic))
    {
      topicListeners.add(pair.getFirst());
    }
    return topicListeners;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
