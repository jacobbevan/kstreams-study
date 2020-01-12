package model;

import java.time.LocalTime;
import java.util.List;

public class SnapshotDef {

    private String cut;
    private List<String> inScope;
    private LocalTime cutTime;

    public LocalTime getCutTime() {
        return cutTime;
    }

    public void setCutTime(LocalTime cutTime) {
        this.cutTime = cutTime;
    }

    public String getCut() {
        return cut;
    }

    public void setCut(String cut) {
        this.cut = cut;
    }

    public List<String> getInScope() {
        return inScope;
    }

    public void setInScope(List<String> inScope) {
        this.inScope = inScope;
    }

    public SnapshotDef(String cut, LocalTime cutTime, List<String> inScope) {
        this.cut = cut;
        this.inScope = inScope;
        this.cutTime = cutTime;
    }

    @Override
    public String toString() {
        return "SnapshotDef{" +
                "cut='" + cut + '\'' +
                ", inScope=" + inScope +
                '}';
    }
}
