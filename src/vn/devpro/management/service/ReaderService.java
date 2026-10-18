package vn.devpro.management.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import vn.devpro.management.exception.BusinessException;
import vn.devpro.management.model.BorrowTicket;
import vn.devpro.management.model.Gender;
import vn.devpro.management.model.Person;
import vn.devpro.management.model.Reader;
import vn.devpro.management.repository.Repository;

public class ReaderService {


    public record ReaderUpdate(String familyName, String givenName, LocalDate dateOfBirth,
                               Gender gender, Boolean student) {}

    private final Repository<Reader> readers;
    private final Repository<BorrowTicket> tickets;

    public ReaderService(Repository<Reader> readers, Repository<BorrowTicket> tickets) {
        this.readers = readers;
        this.tickets = tickets;
    }

    public List<Reader> findAll() {
        return readers.findAll();
    }

    public List<Reader> findAllSortedByName() {
        List<Reader> list = new ArrayList<>(readers.findAll());
        list.sort(Person.BY_NAME);
        return list;
    }

    public Reader getByCode(String code) {
        return readers.findByCode(code)
                .orElseThrow(() -> new BusinessException("Doc gia khong ton tai: " + code));
    }

    public Reader create(String familyName, String givenName, LocalDate dateOfBirth,
                         Gender gender, boolean student) {
        return readers.save(new Reader(
                Validation.requireText(familyName, "Ho"),
                Validation.requireText(givenName, "Ten"),
                requireValidBirthDate(dateOfBirth),
                requireGender(gender),
                student));
    }

    public Reader update(String code, ReaderUpdate change) {
        Reader reader = getByCode(code);
        if (change.dateOfBirth() != null) requireValidBirthDate(change.dateOfBirth());

        reader.setFamilyName(Validation.keepIfBlank(change.familyName(), reader.getFamilyName()));
        reader.setGivenName(Validation.keepIfBlank(change.givenName(), reader.getGivenName()));
        if (change.dateOfBirth() != null) reader.setDateOfBirth(change.dateOfBirth());
        if (change.gender() != null) reader.setGender(change.gender());
        if (change.student() != null) reader.setStudent(change.student());
        return readers.save(reader);
    }

    public void delete(String code) {
        Reader reader = getByCode(code);
        boolean hasTickets = tickets.findAll().stream().anyMatch(t -> t.getReader().getId() == reader.getId());
        if (hasTickets) {
            throw new BusinessException("Khong the xoa: doc gia da co phieu muon");
        }
        readers.delete(reader);
    }

    private static LocalDate requireValidBirthDate(LocalDate date) {
        if (date == null) throw new BusinessException("Ngay sinh khong duoc bo trong");
        if (date.isAfter(LocalDate.now())) throw new BusinessException("Ngay sinh khong duoc o tuong lai");
        return date;
    }

    private static Gender requireGender(Gender gender) {
        if (gender == null) throw new BusinessException("Gioi tinh khong duoc bo trong");
        return gender;
    }
}
