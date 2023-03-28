import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Consumer2 implements Runnable{
  private final static String QUEUE_NAME = "HAPPYPOOL1";
  private Connection connection;
  private ProcessSwipees processSwipees;
  public Consumer2(Connection connection, ProcessSwipees processSwipees){
    this.connection = connection;
    this.processSwipees = processSwipees;
  }

  @Override
  public void run() {
      Channel channel;
      try{
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
          String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
          String[] messageParts = message.split("/");
//          System.out.println("Consumer Received :" + message);
          String swiper = messageParts[1];
          String swipee = messageParts[2];
          String action = messageParts[0];
          processSwipees.swipe(action, swiper, swipee);
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

        };
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });

      } catch (IOException e) {
        e.printStackTrace();
      }

    }

}

