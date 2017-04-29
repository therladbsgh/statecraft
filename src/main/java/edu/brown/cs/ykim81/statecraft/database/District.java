package edu.brown.cs.ykim81.statecraft.database;

/**
 * Created by therl on 4/24/2017.
 */
public enum District {

  CITYCENTER ("City Center"),
  STATE ("State"),
  PRIMARY ("Agricultural"),
  INDUSTRIAL ("Industrial"),
  MILITARY ("Military"),
  UNIVERSITY ("University"),
  RESIDENTIAL ("Residential"),
  NULL ("Null");

  private String s;

  District(String s) {
    this.s = s;
  }

  @Override
  public String toString() {
    return s;
  }

  public static District fromString(String s) {
    switch (s.toUpperCase()) {
      case "CITY CENTER":
        return District.CITYCENTER;
      case "STATE":
        return District.STATE;
      case "AGRICULTURAL":
        return District.PRIMARY;
      case "INDUSTRIAL":
        return District.INDUSTRIAL;
      case "MILITARY":
        return District.MILITARY;
      case "UNIVERSITY":
        return District.UNIVERSITY;
      case "RESIDENTIAL":
        return District.RESIDENTIAL;
      case "NULL":
        return District.NULL;
      default:
        throw new IllegalArgumentException();
    }
  }

}
