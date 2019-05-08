package org.openzal.zal;

import com.zimbra.cs.mailbox.RedissonClientHolder;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;

import javax.annotation.Nonnull;

public class TopicNotifier
{
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

  private static final RedissonClient sRedissonClient = RedissonClientHolder.getInstance().getRedissonClient();;
  private static final Map<String, Set<Pair<TopicListener, TopicListenerWrapper>>> sListeners = new HashMap<>();

  public void registerListener(String topic, final TopicListener listener)
  {
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
  }

  public void unregisterListener(TopicListener listener)
  {
    RTopic topic;
    Pair<TopicListener, TopicListenerWrapper> topicListenerPair;
    Map.Entry<String, Set<Pair<TopicListener, TopicListenerWrapper>>> entry;
    Iterator<Pair<TopicListener, TopicListenerWrapper>> topicListenerIteratorPair;
    Iterator<Map.Entry<String, Set<Pair<TopicListener, TopicListenerWrapper>>>> topicListenerEntriesIterator = sListeners
      .entrySet().iterator();
    while( topicListenerEntriesIterator.hasNext())
    {
      entry = topicListenerEntriesIterator.next();
      topic = sRedissonClient.getTopic(entry.getKey());
      topicListenerIteratorPair = entry.getValue().iterator();
      while( topicListenerIteratorPair.hasNext())
      {
        topicListenerPair = topicListenerIteratorPair.next();
        if( topicListenerPair.getFirst().equals(listener) )
        {
          topic.removeListener(topicListenerPair.getSecond());
          entry.getValue().remove(topicListenerPair);
        }
      }
    }
  }

  public void publishOnTopic(String topic, String message)
  {
    sRedissonClient.getTopic(topic).publish(message);
  }

  public Collection<TopicListener> getListeners(String topic)
  {
    Collection<TopicListener> topicListeners = new HashSet<>();
    for(Pair<TopicListener, TopicListenerWrapper> pair : sListeners.get(topic))
    {
      topicListeners.add(pair.getFirst());
    }
    return topicListeners;
  }
}
