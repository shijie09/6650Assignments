package Model;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Processor implements Runnable{
  private String urlBase;
  private AtomicInteger successReq;
  private AtomicInteger failReq;
  private int totalReq;
  private BlockingQueue<Event> events;
  private CountDownLatch countDownLatch;
  private List<Record> records;
  public Processor(String urlBase, AtomicInteger successReq,
      AtomicInteger failReq, int totalReq, BlockingQueue<Event> events,
      CountDownLatch countDownLatch, List<Record> records) {
    this.urlBase = urlBase;
    this.successReq = successReq;
    this.failReq = failReq;
    this.totalReq = totalReq;
    this.events = events;
    this.countDownLatch = countDownLatch;
    this.records = records;
  }

  @Override
  public void run() {
    ApiClient apiClient = new ApiClient();
    SwipeApi swipeApi = new SwipeApi(apiClient);
    swipeApi.getApiClient().setBasePath(this.urlBase);
    int countOfSuccess = 0;
    int countOfFail = 0;

    for (int i = 0; i < this.totalReq; i++) {
      Event event = this.events.poll();
      if (doSwipe(swipeApi, event)) {
        countOfSuccess += 1;
      } else {
        countOfFail += 1;
      }
    }

    successReq.getAndAdd(countOfSuccess);
    failReq.getAndAdd(countOfFail);
    countDownLatch.countDown();

  }
  private boolean doSwipe(SwipeApi swipeApi, Event event) {
    int times = 0;
    while(times < 5) {
      try {
        long start = System.currentTimeMillis();
        ApiResponse<Void> res = swipeApi.swipeWithHttpInfo(event.getCurBody(), event.getLeftOrRight());
        if(res.getStatusCode() == 201) {
          long end = System.currentTimeMillis();
          System.out.println(end - start);
          Record record = new Record(start,"POST", end - start,"201");
          this.records.add(record);
          return true;
        }
      } catch (ApiException e) {
        times++;
        e.printStackTrace();
      }
    }
    return false;
  }
}
