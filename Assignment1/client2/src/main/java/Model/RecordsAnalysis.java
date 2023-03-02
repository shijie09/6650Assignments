package Model;

import java.util.Collections;
import java.util.List;

public class RecordsAnalysis {
  private List<Record> records;

  public RecordsAnalysis(List<Record> records) {
    this.records = records;
    Collections.sort(this.records);
  }

  public double getMeanLatency() {
    long sum = 0;
    for (Record record : this.records) {
      sum += record.getLatency();
    }

    return (double) sum / (double)(this.records.size());
  }

  public double getMedianLatency() {
    if (this.records.size() % 2 == 1) {
      return records.get(records.size() / 2).getLatency();
    } else {
      return (records.get(records.size() / 2 - 1).getLatency() + records.get(records.size() / 2).getLatency()) / 2;
    }
  }

  public long get99PercentileLatency() {
    return records.get((int)Math.floor(records.size()*0.99)).getLatency();
  }

  public long getMinLatency() {
    return records.get(0).getLatency();
  }

  public long getMaxLatency() {
    return records.get(records.size() - 1).getLatency();
  }
}
