package model;

import java.util.Objects;

public class Pair<T1, T2> {
    public T1 value1;
    public T2 value2;

    public Pair(T1 v1, T2 v2) {
        value1 = v1;
        value2 = v2;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "value1=" + value1 +
                ", value2=" + value2 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> tuple2 = (Pair<?, ?>) o;
        return Objects.equals(value1, tuple2.value1) &&
                Objects.equals(value2, tuple2.value2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value1, value2);
    }
}
