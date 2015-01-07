package us.wedeliver.commons.util;

public enum DistanceUnit {
  MILES(1609.3),
  METERS(1),
  KILOMETERS(1000);

  private double meters;

  private DistanceUnit(double meters) {
    this.meters = meters;
  }

  public double convert(double value, DistanceUnit unit) {
    return value * unit.meters / meters;
  }

}
