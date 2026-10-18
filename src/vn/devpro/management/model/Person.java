package vn.devpro.management.model;

import java.util.Comparator;


public abstract class Person extends BaseEntity {


    public static final Comparator<Person> BY_NAME = Comparator
            .comparing(Person::getShortName, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(Person::getFullName, String.CASE_INSENSITIVE_ORDER);

    private String familyName;
    private String givenName;

    protected Person(String familyName, String givenName) {
        this.familyName = familyName;
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFullName() {
        return (familyName + " " + givenName).trim();
    }


    public String getShortName() {
        String g = givenName == null ? "" : givenName.trim();
        int i = g.lastIndexOf(' ');
        return i < 0 ? g : g.substring(i + 1);
    }
}
