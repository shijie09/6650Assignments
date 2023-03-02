package Model;

import io.swagger.client.model.SwipeDetails;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.RandomStringUtils;

public class EventGenerator implements Runnable {
  private static final int startSwiperId = 1;
  private static final int endSwiperId = 5000;
  private static final int startSwipeeId = 1;
  private static final int endSwipeeId = 1000000;
  private static final int commentLength = 256;
  private static final String stopSwiperId = "5001";
  private BlockingQueue<Event> queue;
  private int eventTotalNumber;


  public EventGenerator(BlockingQueue<Event> queue, int eventTotalNumber) {
    this.queue = queue;
    this.eventTotalNumber = eventTotalNumber;
  }
  @Override
  public void run(){
    for (int i = 0; i < this.eventTotalNumber; i++){
      Integer swperID = ThreadLocalRandom.current().nextInt(startSwiperId, endSwiperId + 1);
      Integer swpeeID = ThreadLocalRandom.current().nextInt(startSwipeeId, endSwipeeId  + 1);
      Integer leftOrRight = ThreadLocalRandom.current().nextInt(0,2);
      String comment = RandomStringUtils.random(commentLength,true, true);

      SwipeDetails swipeDetails = new SwipeDetails();
      swipeDetails.setSwiper(swperID.toString());
      swipeDetails.setSwipee(swpeeID.toString());
      swipeDetails.setComment(comment);
      Event event = new Event(leftOrRight == 0? "left":"right", swipeDetails);
      this.queue.offer(event);
    }
    String stopSwiperId = this.stopSwiperId ;
    Integer stopSwipeeId  = ThreadLocalRandom.current().nextInt(startSwipeeId, endSwipeeId + 1);
    Integer stopLeftOrRight = ThreadLocalRandom.current().nextInt(0,2);
    String stopComment = RandomStringUtils.random(commentLength,true, true);
    SwipeDetails stopSwipeDetails = new SwipeDetails();
    stopSwipeDetails.setSwiper(stopSwiperId);
    stopSwipeDetails.setSwipee(stopSwipeeId.toString());
    stopSwipeDetails.setComment(stopComment);
    Event event = new Event(stopLeftOrRight == 0? "left":"right", stopSwipeDetails);
    this.queue.offer(event);

  }




}
