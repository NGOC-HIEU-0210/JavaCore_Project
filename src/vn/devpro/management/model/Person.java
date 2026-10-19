package vn.devpro.management.model;

import java.util.Comparator;


public abstract class Person extends BaseEntity {


    public static final Comparator<Person> BY_NAME = (p1, p2) -> {
        int result = p1.getShortName().compareToIgnoreCase(p2.getShortName());

        if (result != 0) {
            return result;
        }
        return p1.getFullName().compareToIgnoreCase(p2.getFullName());
    };

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
