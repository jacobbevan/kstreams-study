package model;

import java.util.ArrayList;
import java.util.List;

public class StringList {


    public StringList(){
        this.values = new ArrayList<>();
    }

    public StringList(List<String> values) {
        this.values = values;
    }

    private List<String> values;

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "StringList{" +
                "values=" + values +
                '}';
    }
}
