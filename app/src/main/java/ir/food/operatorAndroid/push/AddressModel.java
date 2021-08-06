package ir.food.operatorAndroid.push;

public class AddressModel {
  private String address;
  private long failTime;
  private long upTime;

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public long getFailTime() {
    return failTime;
  }

  public void setFailTime(long failTime) {
    this.failTime = failTime;
  }

  public long getUpTime() {
    return upTime;
  }

  public void setUpTime(long upTime) {
    this.upTime = upTime;
  }
}
