package vn.devpro.management.controller;

import vn.devpro.management.model.Gender;
import vn.devpro.management.model.Reader;
import vn.devpro.management.service.ReaderService;
import vn.devpro.management.service.ReaderService.ReaderUpdate;
import vn.devpro.management.view.ConsoleInput;
import vn.devpro.management.view.ConsolePrinter;
import vn.devpro.management.view.Menu;

public class ReaderController {
    private static final String GENDER_PROMPT = "Gioi tinh (1. Nam, 2. Nu, 3. Khac): ";

    private final ReaderService service;
    private final ConsoleInput in;
    private final ConsolePrinter out;

    public ReaderController(ReaderService service, ConsoleInput in, ConsolePrinter out) {
        this.service = service;
        this.in = in;
        this.out = out;
    }

    public void showMenu() {
        new Menu("QUAN LY DANH SACH DOC GIA", in, out)
                .add("Xem danh sach doc gia", this::list)
                .add("Them doc gia moi", this::create)
                .add("Sua thong tin doc gia", this::update)
                .add("Xoa doc gia", this::delete)
                .add("Xem danh sach sap xep theo ten", this::listSorted)
                .run();
    }

    private void list() {
        out.title("DANH SACH DOC GIA");
        out.readers(service.findAll());
    }

    private void listSorted() {
        out.title("DANH SACH DOC GIA (SAP XEP THEO TEN)");
        out.readers(service.findAllSortedByName());
    }

    private void create() {
        out.title("THEM MOI DOC GIA");
        String familyName = in.readRequired("Nhap ho (vd Nguyen): ");
        String givenName = in.readRequired("Nhap ten dem va ten (vd Van An): ");
        var dateOfBirth = in.readDate("Nhap ngay sinh (dd/MM/yyyy): ");
        Gender gender = readGender(true);
        boolean student = in.readYesNo("Co phai sinh vien khong? (c/k): ");

        Reader created = service.create(familyName, givenName, dateOfBirth, gender, student);
        out.success("Them moi doc gia thanh cong: " + created.getCode());
    }

    private void update() {
        out.title("SUA THONG TIN DOC GIA");
        Reader current = in.readValid("Nhap ma doc gia: ", service::getByCode);
        out.message("(Enter de giu nguyen gia tri hien tai)");
        String familyName = in.readOptional("Ho moi (hien tai: " + current.getFamilyName() + "): ");
        String givenName = in.readOptional("Ten moi (hien tai: " + current.getGivenName() + "): ");
        var dateOfBirth = in.readOptionalDate("Ngay sinh moi dd/MM/yyyy: ");
        Gender gender = readGender(false);
        Boolean student = in.readOptionalYesNo("Sinh vien? c/k (hien tai: " + (current.isStudent() ? "c" : "k") + "): ");

        service.update(current.getCode(), new ReaderUpdate(familyName, givenName, dateOfBirth, gender, student));
        out.success("Sua thong tin doc gia thanh cong!");
    }

    private void delete() {
        out.title("XOA DOC GIA");
        String code = in.readRequired("Nhap ma doc gia: ");
        service.delete(code);
        out.success("Xoa doc gia thanh cong!");
    }

    private Gender readGender(boolean required) {
        while (true) {
            Integer choice = required ? Integer.valueOf(in.readInt(GENDER_PROMPT)) : in.readOptionalInt(GENDER_PROMPT);
            if (choice == null) return null;
            if (choice >= 1 && choice <= Gender.values().length) return Gender.values()[choice - 1];
            out.error("Vui long chon 1, 2 hoac 3");
        }
    }
}
