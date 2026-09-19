package com.wayfinder.structure.materialization;

@FunctionalInterface
public interface MaterializationBlockView {
    boolean matches(MaterializationRecord record, MaterializedBlockCell cell);
}
