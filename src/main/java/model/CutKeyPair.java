package model;

public class CutKeyPair {

    private String cut;
    private String key;

    public CutKeyPair(String cut, String key) {
        this.cut = cut;
        this.key = key;
    }

    public String getCut() {
        return cut;
    }

    public void setCut(String cut) {
        this.cut = cut;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "CutKeyPair{" +
                "cut='" + cut + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
