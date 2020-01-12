package model;

import java.util.List;

public class SnapshotConfig {

    private List<SnapshotDef> snapshotDefs;

    public SnapshotConfig(List<SnapshotDef> snapshotDefs) {
        this.snapshotDefs = snapshotDefs;
    }

    public List<SnapshotDef> getSnapshotDefs() {
        return snapshotDefs;
    }

    public void setSnapshotDefs(List<SnapshotDef> snapshotDefs) {
        this.snapshotDefs = snapshotDefs;
    }

    @Override
    public String toString() {
        return "SnapshotConfig{" +
                "snapshotDefs=" + snapshotDefs +
                '}';
    }
}
