import Model.EventGenerator;
import Model.Event;
import Model.GetProcessor;
import Model.Processor;

import Model.Record;
import Model.RecordsAnalysis;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreads {
  private static String urlBase;
  private static String urlBase2;
  private static AtomicInteger successReq;
  private static AtomicInteger failReq;
  private static AtomicInteger getSuccessReq;
  private static AtomicInteger getFailReq;
  private static BlockingQueue<Event> events;
  private static final int numOfThread = 200;
  private static final int totalReq = 500000;
  private static final int processorNumberEach = 2500;
  private static String pathName;
  private static String pathName2;
  public static void main(String[] args) throws InterruptedException {
    urlBase = "http://localhost:8080/server3_war_exploded";
    urlBase2 = "http://34.222.65.140:8080/server2_war";
//    urlBase = "http://my-alb-1984034483.us-west-2.elb.amazonaws.com/server2_war";
//    urlBase = "http://34.209.47.67:8080/server3_war";
    successReq = new AtomicInteger(0);
    failReq = new AtomicInteger(0);
    getSuccessReq =new AtomicInteger(0);
    getFailReq = new AtomicInteger(0);
    events = new LinkedBlockingQueue<>();
    pathName = "/Users/xieshijie/Desktop/new6650/assignment3/client2/src/main/java/output/MultThreadData.csv";
    pathName2 = "/Users/xieshijie/Desktop/new6650/assignment3/client2/src/main/java/output/MultThreadDataGet.csv";
    List<Record> records = Collections.synchronizedList(new ArrayList<>());
    List<Record> getRecords = Collections.synchronizedList(new ArrayList<>());
    System.out.println("*********************************************************");
    System.out.println("Processing Begins");
    System.out.println("*********************************************************");

    CountDownLatch latch = new CountDownLatch(numOfThread);
    long start = System.currentTimeMillis();
    EventGenerator eventGenerator = new EventGenerator(events, totalReq);
    Thread generatorThread = new Thread(eventGenerator);
    generatorThread.start();

    for (int i = 0; i < numOfThread; i++) {
      Processor processor = new Processor(urlBase2,successReq, failReq, processorNumberEach, events, latch, records);
      Thread thread = new Thread(processor);
      thread.start();
    }
    GetProcessor getProcessor = new GetProcessor(urlBase, latch, getRecords,successReq, failReq,totalReq, getSuccessReq, getFailReq);
    Thread getThread = new Thread(getProcessor);
    getThread.start();

    latch.await();

    long end = System.currentTimeMillis();
    long wallTime = end - start;
    RecordsAnalysis recordsAnalysis = new RecordsAnalysis(records);
    long maxLatency = recordsAnalysis.getMaxLatency();
    long minLatency = recordsAnalysis.getMinLatency();
    long percentitle99Latency = recordsAnalysis.get99PercentileLatency();
    double medianTime = recordsAnalysis.getMedianLatency();
    double meanLatency = recordsAnalysis.getMeanLatency();


    System.out.println("*********************************************************");
    System.out.println("Processing Ends");
    System.out.println("*********************************************************");
    System.out.println("Number of successful requests :" + successReq.get());
    System.out.println("Number of failed requests :" + failReq.get());
    System.out.println("Mean Response Time :" + meanLatency);
    System.out.println("Median Response Time :" + medianTime);
    System.out.println("Min Response Time :" + minLatency);
    System.out.println("Max Response Time :" + maxLatency);
    System.out.println("Total wall time: " + wallTime);
    System.out.println("p99 (99th percentile) response time: " + percentitle99Latency);
    System.out.println( "Throughput: " + (int)((successReq.get() + failReq.get()) / (double)(wallTime / 1000)) + " requests/second");
    System.out.println("Number of threads: " + numOfThread);
    System.out.println("*********************************************************");
    System.out.println("Following are the statics of get requests");
    System.out.println("*********************************************************");
    getThread.interrupt();
    RecordsAnalysis getRecordsAnalysis = new RecordsAnalysis(getRecords);
    long getmaxLatency = getRecordsAnalysis.getMaxLatency();
    long getminLatency = getRecordsAnalysis.getMinLatency();
    long getpercentitle99Latency = getRecordsAnalysis.get99PercentileLatency();
    double getmedianTime = getRecordsAnalysis.getMedianLatency();
    double getmeanLatency = getRecordsAnalysis.getMeanLatency();
    System.out.println("Mean Response Time :" + getmeanLatency);
    System.out.println("Median Response Time :" + getmedianTime);
    System.out.println("Min Response Time :" + getminLatency);
    System.out.println("Max Response Time :" + getmaxLatency);
    System.out.println("p99 (99th percentile) response time: " + getpercentitle99Latency);
    System.out.println("Number of get successful requests :" + getSuccessReq.get());
    System.out.println("Number of get failed requests :" + getFailReq.get());
  }
}
