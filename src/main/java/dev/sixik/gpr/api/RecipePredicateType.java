package dev.sixik.gpr.api;

public enum RecipePredicateType {
    NONE;

    public byte getId() {
        return (byte) this.ordinal();
    }
}
