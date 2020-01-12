package model;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class KeyAtDiscreteTime {

    private String key;
    private LocalDate date;
    private String cut;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyAtDiscreteTime that = (KeyAtDiscreteTime) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(date, that.date) &&
                Objects.equals(cut, that.cut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, date, cut);
    }

    public KeyAtDiscreteTime(String key, LocalDate date, String cut) {
        this.key = key;
        this.date = date;
        this.cut = cut;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCut() {
        return cut;
    }

    public void setCut(String cut) {
        this.cut = cut;
    }

    @Override
    public String toString() {
        return "KeyAtDiscreteTime{" +
                "key='" + key + '\'' +
                ", date=" + date +
                ", cut='" + cut + '\'' +
                '}';
    }
}
