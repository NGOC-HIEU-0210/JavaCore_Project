package vn.devpro.management.model;

import java.time.LocalDate;

public class Reader extends Person {
    private LocalDate dateOfBirth;
    private Gender gender;
    private boolean student;

    public Reader(String familyName, String givenName, LocalDate dateOfBirth, Gender gender, boolean student) {
        super(familyName, givenName);
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.student = student;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isStudent() {
        return student;
    }

    public void setStudent(boolean student) {
        this.student = student;
    }
}
