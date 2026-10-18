package vn.devpro.management.controller;

import vn.devpro.management.model.Book;
import vn.devpro.management.service.AuthorService;
import vn.devpro.management.service.BookService;
import vn.devpro.management.service.BookService.BookUpdate;
import vn.devpro.management.service.CategoryService;
import vn.devpro.management.view.ConsoleInput;
import vn.devpro.management.view.ConsolePrinter;
import vn.devpro.management.view.Menu;

public class BookController {
    private final BookService service;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final ConsoleInput in;
    private final ConsolePrinter out;

    public BookController(BookService service, AuthorService authorService, CategoryService categoryService,
                          ConsoleInput in, ConsolePrinter out) {
        this.service = service;
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.in = in;
        this.out = out;
    }

    public void showMenu() {
        new Menu("QUAN LY SACH", in, out)
                .add("Xem danh sach sach", this::list)
                .add("Them sach moi", this::create)
                .add("Sua thong tin sach", this::update)
                .add("Xoa sach", this::delete)
                .add("Xem danh sach sap xep theo ten sach", this::listSorted)
                .add("Tim sach theo danh muc", this::searchByCategory)
                .add("Tim sach theo tac gia", this::searchByAuthor)
                .add("Tim sach theo ten", this::searchByName)
                .run();
    }

    private void list() {
        out.title("DANH SACH SACH");
        out.books(service.findAll());
    }

    private void listSorted() {
        out.title("DANH SACH SACH (SAP XEP THEO TEN SACH)");
        out.books(service.findAllSortedByName());
    }

    private void create() {
        out.title("THEM MOI SACH");
        out.authors(authorService.findAll());
        String authorCode = in.readValid("Nhap ma tac gia: ", c -> authorService.getByCode(c).getCode());
        out.categories(categoryService.findAll());
        String categoryCode = in.readValid("Nhap ma danh muc: ", c -> categoryService.getByCode(c).getCode());
        String name = in.readRequired("Nhap ten sach: ");
        int pages = in.readInt("Nhap so trang: ");
        int quantity = in.readInt("Nhap so luong: ");

        Book created = service.create(authorCode, categoryCode, name, pages, quantity);
        out.success("Them moi sach thanh cong: " + created.getCode());
    }

    private void update() {
        out.title("SUA THONG TIN SACH");
        Book current = in.readValid("Nhap ma sach: ", service::getByCode);
        out.message("(Enter de giu nguyen gia tri hien tai)");
        String name = in.readOptional("Ten moi (hien tai: " + current.getName() + "): ");
        Integer pages = in.readOptionalInt("So trang moi (hien tai: " + current.getNumberOfPages() + "): ");
        Integer quantity = in.readOptionalInt("Tong so luong moi (hien tai: " + current.getQuantity()
                + ", dang cho muon: " + current.getBorrowedCount() + "): ");
        String authorCode = in.readOptionalValid("Ma tac gia moi (hien tai: " + current.getAuthor().getCode() + "): ",
                c -> authorService.getByCode(c).getCode());
        String categoryCode = in.readOptionalValid("Ma danh muc moi (hien tai: " + current.getCategory().getCode() + "): ",
                c -> categoryService.getByCode(c).getCode());

        service.update(current.getCode(), new BookUpdate(authorCode, categoryCode, name, pages, quantity));
        out.success("Sua thong tin sach thanh cong!");
    }

    private void delete() {
        out.title("XOA SACH");
        String code = in.readRequired("Nhap ma sach: ");
        service.delete(code);
        out.success("Xoa sach thanh cong!");
    }

    private void searchByCategory() {
        String code = in.readRequired("Nhap ma danh muc: ");
        out.title("KET QUA TIM KIEM THEO DANH MUC");
        out.books(service.findByCategory(code));
    }

    private void searchByAuthor() {
        String code = in.readRequired("Nhap ma tac gia: ");
        out.title("KET QUA TIM KIEM THEO TAC GIA");
        out.books(service.findByAuthor(code));
    }

    private void searchByName() {
        String keyword = in.readRequired("Nhap ten sach (hoac mot phan): ");
        out.title("KET QUA TIM KIEM THEO TEN SACH");
        out.books(service.searchByName(keyword));
    }
}
