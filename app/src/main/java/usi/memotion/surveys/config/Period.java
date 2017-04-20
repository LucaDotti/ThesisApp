package usi.memotion.surveys.config;

/**
 * Created by usi on 16/02/17.
 */
public enum Period {
    WEEKEND("weekend", Days.FRIDAY, Days.SATURDAY, Days.SUNDAY),
    WEEK("week", Days.MONDAY, Days.TUESDAY, Days.WEDNESDAY, Days.THURSDAY),
    MONDAY("monday", Days.MONDAY),
    TUESDAY("tuesday", Days.TUESDAY),
    WEDNESDAY("wednesday", Days.WEDNESDAY),
    THURSDAY("thrusday", Days.THURSDAY),
    FRIDAY("friday", Days.FRIDAY),
    SATURDAY("saturday", Days.SATURDAY),
    SUNDAY("sunday", Days.SUNDAY);

    public Days[] days;
    public String name;

    Period(String name, Days... days) {
        this.days = days;
        this.name = name;
    }

    public static Period getPeriod(String name) {
        if(name == null) {
            return null;
        }

        for(Period period: Period.values()) {
            if(name.equals(period.name)) {
                return period;
            }
        }

        throw new IllegalArgumentException("Period not found");
    }

    public String getPeriodName() {
        return name;
    }



}
