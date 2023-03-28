package Model;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.MatchesApi;
import io.swagger.client.api.StatsApi;
import io.swagger.client.model.MatchStats;
import io.swagger.client.model.Matches;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class GetProcessor implements Runnable{
  private String urlBase;
  private CountDownLatch countDownLatch;
  private List<Record> getRecords;
  private AtomicInteger successReq;
  private AtomicInteger failReq;
  private AtomicInteger getSuccessReq;
  private AtomicInteger getFailReq;
  private  int totalReq ;
  private static final int startSwiperId = 1;
  private static final int endSwiperId = 5000;
  public GetProcessor(String urlBase, CountDownLatch countDownLatch, List<Record> getRecords,
      AtomicInteger successReq,
      AtomicInteger failReq, int totalReq, AtomicInteger getSuccessReq,AtomicInteger getFailReq ) {
    this.urlBase = urlBase;
    this.countDownLatch = countDownLatch;
    this.getRecords = getRecords;
    this.successReq = successReq;
    this.failReq = failReq;
    this.totalReq = totalReq;
    this.getSuccessReq = getSuccessReq;
    this.getFailReq = getFailReq;

  }

  @Override
  public void run() {
    ApiClient apiClient = new ApiClient();
    MatchesApi matchesApi = new MatchesApi(apiClient);
    StatsApi statsApi = new StatsApi(apiClient);
    matchesApi.getApiClient().setBasePath(this.urlBase);
    int countOfSuccessEach = 1;
    int countOfFailEach = 1;

    while (!(successReq.get() + failReq.get() == totalReq)) {

      int swiperID = ThreadLocalRandom.current().nextInt(startSwiperId, endSwiperId + 1);
      String swiperid = Integer.toString(swiperID);
      Integer curTurn = ThreadLocalRandom.current().nextInt(0,2);
      boolean value = true;
      if(curTurn == 0){
        value = doMatch(matchesApi, swiperid);}
      else {
        value = doStats(statsApi, swiperid);
        }
      if (value){
        getSuccessReq.getAndAdd(countOfSuccessEach);

      } else {
        getFailReq.getAndAdd(countOfFailEach);      }
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }


  }
  private boolean doMatch(MatchesApi matchesApi, String swiperid) {
    int times = 0;
    while(times < 5) {
      try {
        long start = System.currentTimeMillis();
        ApiResponse<Matches> res = matchesApi.matchesWithHttpInfo(swiperid);
        if(res.getStatusCode() == 200) {
          long end = System.currentTimeMillis();
          Record record = new Record(start,"GET", end - start,"200");
          this.getRecords.add(record);
          return true;
        }
      } catch (ApiException e) {
        times++;
        e.printStackTrace();
      }
    }
    return false;
  }
  private boolean doStats(StatsApi statsApi, String swiperid) {
    int times = 0;
    while(times < 5) {
      try {
        long start = System.currentTimeMillis();
        ApiResponse<MatchStats> res = statsApi.matchStatsWithHttpInfo(swiperid);
        if(res.getStatusCode() == 200) {
          long end = System.currentTimeMillis();
          Record record = new Record(start,"GET", end - start,"200");
          this.getRecords.add(record);
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
