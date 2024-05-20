package org.example;


public class BroadcastsTime implements Comparable<BroadcastsTime> {
    public final byte hours;
    public final byte minutes;

    public BroadcastsTime(String time) {
        String[] parts = time.split(":");
        this.hours = Byte.parseByte(parts[0]);
        this.minutes = Byte.parseByte(parts[1]);
    }

    public byte hour() {
        return this.hours;
    }

    public byte minutes() {
        return this.minutes;
    }

    public boolean after(BroadcastsTime t) {
        return compareTo(t) > 0;
    }

    public boolean before(BroadcastsTime t) {
        return compareTo(t) < 0;
    }

    public boolean between(BroadcastsTime t1, BroadcastsTime t2) {
        return after(t1) && before(t2);
    }

    @Override
    public int compareTo(BroadcastsTime time) {
        if (this.hours != time.hours) {
            return this.hours - time.hours;
        }
        return this.minutes - time.minutes;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hours, minutes);
    }
}
