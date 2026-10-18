package vn.devpro.management.controller;

import vn.devpro.management.model.Category;
import vn.devpro.management.service.CategoryService;
import vn.devpro.management.view.ConsoleInput;
import vn.devpro.management.view.ConsolePrinter;
import vn.devpro.management.view.Menu;

public class CategoryController {
    private final CategoryService service;
    private final ConsoleInput in;
    private final ConsolePrinter out;

    public CategoryController(CategoryService service, ConsoleInput in, ConsolePrinter out) {
        this.service = service;
        this.in = in;
        this.out = out;
    }

    public void showMenu() {
        new Menu("QUAN LY DANH MUC", in, out)
                .add("Xem danh sach danh muc", this::list)
                .add("Them danh muc moi", this::create)
                .add("Sua thong tin danh muc", this::update)
                .add("Xoa danh muc", this::delete)
                .add("Xem danh sach sap xep theo ten", this::listSorted)
                .run();
    }

    private void list() {
        out.title("DANH SACH DANH MUC");
        out.categories(service.findAll());
    }

    private void listSorted() {
        out.title("DANH SACH DANH MUC (SAP XEP THEO TEN)");
        out.categories(service.findAllSortedByName());
    }

    private void create() {
        out.title("THEM MOI DANH MUC");
        String name = in.readRequired("Nhap ten danh muc: ");
        Category created = service.create(name);
        out.success("Them moi danh muc thanh cong: " + created.getCode());
    }

    private void update() {
        out.title("SUA THONG TIN DANH MUC");
        Category current = in.readValid("Nhap ma danh muc: ", service::getByCode);
        String name = in.readOptional("Ten moi (Enter de giu \"" + current.getName() + "\"): ");
        service.update(current.getCode(), name);
        out.success("Sua danh muc thanh cong!");
    }

    private void delete() {
        out.title("XOA DANH MUC");
        String code = in.readRequired("Nhap ma danh muc: ");
        service.delete(code);
        out.success("Xoa danh muc thanh cong!");
    }
}
