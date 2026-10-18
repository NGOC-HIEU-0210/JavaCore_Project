package vn.devpro.management.model;

public enum Gender {
    MALE("Nam"),
    FEMALE("Nu"),
    OTHER("Khac");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
