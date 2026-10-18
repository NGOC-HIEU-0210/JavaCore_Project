package vn.devpro.management.controller;

import vn.devpro.management.view.ConsoleInput;
import vn.devpro.management.view.ConsolePrinter;
import vn.devpro.management.view.Menu;


public class MainController {
    private final CategoryController categories;
    private final AuthorController authors;
    private final BookController books;
    private final ReaderController readers;
    private final BorrowController borrows;
    private final StatisticsController statistics;
    private final ConsoleInput in;
    private final ConsolePrinter out;

    public MainController(CategoryController categories, AuthorController authors, BookController books,
                          ReaderController readers, BorrowController borrows, StatisticsController statistics,
                          ConsoleInput in, ConsolePrinter out) {
        this.categories = categories;
        this.authors = authors;
        this.books = books;
        this.readers = readers;
        this.borrows = borrows;
        this.statistics = statistics;
        this.in = in;
        this.out = out;
    }

    public void run() {
        new Menu("CHUONG TRINH QUAN LY THU VIEN", in, out)
                .add("Cap nhat thong tin he thong", this::systemMenu)
                .add("Quan ly giao dich muon sach", borrows::showMenu)
                .add("Thong ke", statistics::showMenu)
                .exitLabel("Thoat")
                .run();
        out.message("Da thoat chuong trinh.");
    }

    private void systemMenu() {
        new Menu("CAP NHAT THONG TIN HE THONG", in, out)
                .add("Quan ly danh muc", categories::showMenu)
                .add("Quan ly tac gia", authors::showMenu)
                .add("Quan ly sach", books::showMenu)
                .add("Quan ly doc gia", readers::showMenu)
                .run();
    }
}
