package Model;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.*;

public class ChannelPoolGenerator {
  private Connection connection;
  private BlockingQueue<Channel> pool;
  private final static int capacity = 50;

  public ChannelPoolGenerator() throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("52.39.250.165");
    factory.setPassword("guest");
    factory.setUsername("guest");

    try {
      this.connection = factory.newConnection();
    } catch (IOException | TimeoutException e) {
      System.err.println("Something Went Wrong in Connection");
      e.printStackTrace();
    }

    this.pool = new LinkedBlockingQueue<>();

    for (int i = 0; i < this.capacity; i++) {
      try {
        Channel channel = this.connection.createChannel();
        this.pool.add(channel);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public Channel takeChannel() throws InterruptedException {
    return this.pool.take();
  }

  public void add(Channel channel) {
    this.pool.offer(channel);
  }
}