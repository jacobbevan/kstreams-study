package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class RateFile {

    private String cut;
    private LocalDate date;
    private LocalDateTime uploadTime;
    private List<Record> rates;

    public RateFile(String cut, LocalDate date, LocalDateTime uploadTime, List<Record> rates) {
        this.cut = cut;
        this.date = date;
        this.uploadTime = uploadTime;
        this.rates = rates;
    }

    public String getCut() {
        return cut;
    }

    public void setCut(String cut) {
        this.cut = cut;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public List<Record> getRates() {
        return rates;
    }

    public void setRates(List<Record> rates) {
        this.rates = rates;
    }

    @Override
    public String toString() {
        return "RateFile{" +
                "cut='" + cut + '\'' +
                ", date=" + date +
                ", uploadTime=" + uploadTime +
                ", rates=" + rates +
                '}';
    }
}