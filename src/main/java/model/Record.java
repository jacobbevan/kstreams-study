package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Record {

    private String key;
    private Double value;
    private LocalDateTime eventTime;

    public Record(String key, Double value, LocalDateTime eventTime) {
        this.key = key;
        this.value = value;
        this.eventTime = eventTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Objects.equals(key, record.key) &&
                Objects.equals(value, record.value) &&
                Objects.equals(eventTime, record.eventTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value, eventTime);
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Record{" +
                "key='" + key + '\'' +
                ", value=" + value +
                ", eventTime=" + eventTime +
                '}';
    }
}