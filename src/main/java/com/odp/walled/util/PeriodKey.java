package com.odp.walled.util;

import java.util.Objects;

public class PeriodKey {
    private final int number;
    private final int year;

    public PeriodKey(int number, int year) {
        this.number = number;
        this.year = year;
    }

    public int getNumber() {
        return number;
    }

    public int getYear() {
        return year;
    }

    public String toWeekLabel() {
        return "Week " + number + " - " + year;
    }

    public String toQuarterLabel() {
        return "Q" + number + " - " + year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof PeriodKey))
            return false;
        PeriodKey that = (PeriodKey) o;
        return number == that.number && year == that.year;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, year);
    }
}
