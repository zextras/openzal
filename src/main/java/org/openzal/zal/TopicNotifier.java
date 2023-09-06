package org.openzal.zal;

import java.util.Collection;

import javax.annotation.Nonnull;

public class TopicNotifier
{
  public void registerListener(String topic, final TopicListener listener)
  {
    throw new UnsupportedOperationException();
  }

  public void unregisterListener(TopicListener listener)
  {
    throw new UnsupportedOperationException();
  }

  public void publishOnTopic(String topic, String message)
  {
    throw new UnsupportedOperationException();
  }

  public Collection<TopicListener> getListeners(String topic)
  {
    throw new UnsupportedOperationException();
  }
}
