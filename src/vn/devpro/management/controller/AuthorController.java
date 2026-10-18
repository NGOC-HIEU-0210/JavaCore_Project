package vn.devpro.management.controller;

import vn.devpro.management.model.Author;
import vn.devpro.management.service.AuthorService;
import vn.devpro.management.view.ConsoleInput;
import vn.devpro.management.view.ConsolePrinter;
import vn.devpro.management.view.Menu;

public class AuthorController {
    private final AuthorService service;
    private final ConsoleInput in;
    private final ConsolePrinter out;

    public AuthorController(AuthorService service, ConsoleInput in, ConsolePrinter out) {
        this.service = service;
        this.in = in;
        this.out = out;
    }

    public void showMenu() {
        new Menu("QUAN LY TAC GIA", in, out)
                .add("Xem danh sach tac gia", this::list)
                .add("Them tac gia moi", this::create)
                .add("Sua thong tin tac gia", this::update)
                .add("Xoa tac gia", this::delete)
                .add("Xem danh sach sap xep theo ten", this::listSorted)
                .run();
    }

    private void list() {
        out.title("DANH SACH TAC GIA");
        out.authors(service.findAll());
    }

    private void listSorted() {
        out.title("DANH SACH TAC GIA (SAP XEP THEO TEN)");
        out.authors(service.findAllSortedByName());
    }

    private void create() {
        out.title("THEM MOI TAC GIA");
        String familyName = in.readRequired("Nhap ho (vd Nguyen): ");
        String givenName = in.readRequired("Nhap ten dem va ten (vd Van An): ");
        Author created = service.create(familyName, givenName);
        out.success("Them moi tac gia thanh cong: " + created.getCode());
    }

    private void update() {
        out.title("SUA THONG TIN TAC GIA");
        Author current = in.readValid("Nhap ma tac gia: ", service::getByCode);
        String familyName = in.readOptional("Ho moi (Enter de giu \"" + current.getFamilyName() + "\"): ");
        String givenName = in.readOptional("Ten moi (Enter de giu \"" + current.getGivenName() + "\"): ");
        service.update(current.getCode(), familyName, givenName);
        out.success("Sua thong tin tac gia thanh cong!");
    }

    private void delete() {
        out.title("XOA TAC GIA");
        String code = in.readRequired("Nhap ma tac gia: ");
        service.delete(code);
        out.success("Xoa tac gia thanh cong!");
    }
}
