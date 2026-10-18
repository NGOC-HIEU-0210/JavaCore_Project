package vn.devpro.management;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import vn.devpro.management.config.AppContext;
import vn.devpro.management.config.DataSeeder;
import vn.devpro.management.exception.BusinessException;
import vn.devpro.management.model.Author;
import vn.devpro.management.model.Book;
import vn.devpro.management.model.BorrowTicket;
import vn.devpro.management.model.Gender;
import vn.devpro.management.service.BookService.BookUpdate;
import vn.devpro.management.service.StatisticsService.LoanLine;

/** Kiem thu tang service va repository, khong can thu vien ngoai. Chay: xem README. */
public class ServiceTests {
    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        run("Du lieu mau nhat quan ton kho", ServiceTests::seedIsConsistent);
        run("Tao phieu tru ton kho", ServiceTests::createTicketReducesStock);
        run("Toi da 3 sach moi phieu", ServiceTests::maxThreeBooksPerTicket);
        run("Khong muon trung sach trong 1 phieu", ServiceTests::noDuplicateBookInTicket);
        run("Gioi han tong so sach: sinh vien 8, nguoi khac 5", ServiceTests::readerLimit);
        run("Het sach thi khong cho muon", ServiceTests::outOfStock);
        run("Xoa sach khoi phieu dung theo MA (loi cu: xoa theo chi so)", ServiceTests::removeBookByValue);
        run("Khong xoa duoc sach cuoi cung trong phieu", ServiceTests::cannotEmptyTicket);
        run("Thay sach trong phieu cap nhat ton kho 2 dau", ServiceTests::replaceBook);
        run("Tra phieu hoan ton kho va khoa phieu", ServiceTests::returnTicket);
        run("Xoa phieu chua tra hoan ton kho", ServiceTests::deleteActiveTicketRestoresStock);
        run("Khong xoa tac gia/danh muc/sach/doc gia dang duoc dung", ServiceTests::referentialIntegrity);
        run("Xoa duoc tac gia khong co sach", ServiceTests::deleteUnusedAuthor);
        run("Sua sach: khong dat so luong < so cuon dang muon", ServiceTests::cannotShrinkBelowBorrowed);
        run("Sua sach: chi ghi khi moi truong hop le", ServiceTests::updateIsAtomic);
        run("Sap xep theo TEN (khong theo ho)", ServiceTests::sortByGivenName);
        run("Kiem tra du lieu doc gia", ServiceTests::readerValidation);
        run("Thong ke", ServiceTests::statistics);

        System.out.println("\nKet qua: " + passed + " dat, " + failed + " loi");
        if (failed > 0) System.exit(1);
    }

    // ------------------------------------------------------------------

    private static AppContext fresh() {
        AppContext ctx = new AppContext();
        DataSeeder.seed(ctx);
        return ctx;
    }

    private static void seedIsConsistent() {
        AppContext c = fresh();
        // Book-101: tong 10, phieu 101 dang giu 1 (phieu 102 da tra) -> con 9
        check(c.bookService().getByCode("Book-101").getAvailable() == 9, "Book-101 con 9");
        check(c.bookService().getByCode("Book-102").getAvailable() == 4, "Book-102 con 4");
        check(c.bookService().getByCode("Book-103").getAvailable() == 7, "Book-103 con 7");
        check(c.bookService().getByCode("Book-104").getAvailable() == 12, "Book-104 con 12");
        check(c.borrowService().getByCode("Ticket-102").isReturned(), "Phieu 102 da tra");
        check(!c.borrowService().getByCode("Ticket-101").isReturned(), "Phieu 101 dang muon");
    }

    private static void createTicketReducesStock() {
        AppContext c = fresh();
        BorrowTicket t = c.borrowService().createTicket("Reader-103", "Book-104");
        check(t.getCode().equals("Ticket-103"), "ma phieu moi la Ticket-103, thuc te " + t.getCode());
        check(t.getDueDate().equals(t.getBorrowDate().plusDays(5)), "han tra +5 ngay");
        check(c.bookService().getByCode("Book-104").getAvailable() == 11, "ton kho giam 1");
    }

    private static void maxThreeBooksPerTicket() {
        AppContext c = fresh();
        c.borrowService().createTicket("Reader-103", "Book-101");
        c.borrowService().addBook("Ticket-103", "Book-102");
        c.borrowService().addBook("Ticket-103", "Book-103");
        expectError(() -> c.borrowService().addBook("Ticket-103", "Book-104"), "toi da 3");
    }

    private static void noDuplicateBookInTicket() {
        AppContext c = fresh();
        c.borrowService().createTicket("Reader-103", "Book-101");
        expectError(() -> c.borrowService().addBook("Ticket-103", "Book-101"), "1 cuon");
    }

    private static void readerLimit() {
        AppContext c = fresh();
        // Reader-102 la nguoi thuong (gioi han 5). Phieu 102 da tra nen dang giu 0.
        c.borrowService().createTicket("Reader-102", "Book-101");
        c.borrowService().addBook("Ticket-103", "Book-102");
        c.borrowService().addBook("Ticket-103", "Book-103");           // 3 sach
        c.borrowService().createTicket("Reader-102", "Book-104");       // 4
        c.borrowService().addBook("Ticket-104", "Book-101");            // 5
        expectError(() -> c.borrowService().addBook("Ticket-104", "Book-102"), "gioi han 5");
        expectError(() -> c.borrowService().createTicket("Reader-102", "Book-103"), "gioi han 5");
        // Sinh vien Reader-101 dang giu 3 -> con duoc muon them toi 8
        check(c.borrowService().countActiveBooks(c.readerService().getByCode("Reader-101")) == 3, "dang giu 3");
        check(c.borrowService().maxActiveBooks(c.readerService().getByCode("Reader-101")) == 8, "sinh vien 8");
    }

    private static void outOfStock() {
        AppContext c = fresh();
        Book b = c.bookService().create("Author-101", "Category-101", "Sach hiem", 100, 1);
        c.borrowService().createTicket("Reader-103", b.getCode());
        expectError(() -> c.borrowService().createTicket("Reader-104", b.getCode()), "het");
    }

    private static void removeBookByValue() {
        AppContext c = fresh();
        // Phieu 101 gom Book-101, Book-102, Book-103. Xoa Book-102 (id 2) phai giu lai 101 va 103.
        c.borrowService().removeBook("Ticket-101", "Book-102");
        List<String> codes = c.borrowService().getByCode("Ticket-101").getBooks().stream().map(Book::getCode).toList();
        check(codes.equals(List.of("Book-101", "Book-103")), "con lai " + codes);
        check(c.bookService().getByCode("Book-102").getAvailable() == 5, "Book-102 duoc hoan 1");
    }

    private static void cannotEmptyTicket() {
        AppContext c = fresh();
        c.borrowService().createTicket("Reader-103", "Book-101");
        expectError(() -> c.borrowService().removeBook("Ticket-103", "Book-101"), "con 1 sach");
    }

    private static void replaceBook() {
        AppContext c = fresh();
        c.borrowService().replaceBook("Ticket-101", "Book-103", "Book-104");
        check(c.bookService().getByCode("Book-103").getAvailable() == 8, "sach cu duoc hoan");
        check(c.bookService().getByCode("Book-104").getAvailable() == 11, "sach moi bi tru");
        expectError(() -> c.borrowService().replaceBook("Ticket-101", "Book-101", "Book-102"), "da co trong phieu");
        expectError(() -> c.borrowService().replaceBook("Ticket-101", "Book-103", "Book-104"), "khong co trong phieu");
    }

    private static void returnTicket() {
        AppContext c = fresh();
        c.borrowService().returnTicket("Ticket-101");
        check(c.bookService().getByCode("Book-101").getAvailable() == 10, "Book-101 day lai");
        check(c.bookService().getByCode("Book-102").getAvailable() == 5, "Book-102 day lai");
        check(c.bookService().getByCode("Book-103").getAvailable() == 8, "Book-103 day lai");
        expectError(() -> c.borrowService().returnTicket("Ticket-101"), "da tra");
        expectError(() -> c.borrowService().addBook("Ticket-101", "Book-104"), "da tra");
    }

    private static void deleteActiveTicketRestoresStock() {
        AppContext c = fresh();
        c.borrowService().deleteTicket("Ticket-101");
        check(c.bookService().getByCode("Book-101").getAvailable() == 10, "hoan Book-101");
        check(c.borrowService().findAll().size() == 1, "con 1 phieu");
        // xoa phieu da tra khong hoan them lan nua
        c.borrowService().deleteTicket("Ticket-102");
        check(c.bookService().getByCode("Book-101").getAvailable() == 10, "khong hoan lan 2");
    }

    private static void referentialIntegrity() {
        AppContext c = fresh();
        expectError(() -> c.authorService().delete("Author-101"), "dang co sach");
        expectError(() -> c.categoryService().delete("Category-102"), "dang co sach");
        expectError(() -> c.bookService().delete("Book-101"), "phieu muon");
        expectError(() -> c.readerService().delete("Reader-101"), "phieu muon");
    }

    private static void deleteUnusedAuthor() {
        AppContext c = fresh();
        Author a = c.authorService().create("Vo", "Van Z");
        c.authorService().delete(a.getCode());
        check(c.authorService().findAll().size() == 4, "ve 4 tac gia");
        // Reader-103/104 chua co phieu -> xoa duoc
        c.readerService().delete("Reader-104");
        check(c.readerService().findAll().size() == 3, "ve 3 doc gia");
    }

    private static void cannotShrinkBelowBorrowed() {
        AppContext c = fresh();
        // Book-102: tong 5, dang cho muon 1 (phieu 101)
        expectError(() -> c.bookService().update("Book-102", new BookUpdate(null, null, null, null, 0)), "dang cho muon");
        c.bookService().update("Book-102", new BookUpdate(null, null, null, null, 7));
        Book b = c.bookService().getByCode("Book-102");
        check(b.getQuantity() == 7 && b.getAvailable() == 6, "tong 7, con 6");
    }

    private static void updateIsAtomic() {
        AppContext c = fresh();
        expectError(() -> c.bookService().update("Book-101",
                new BookUpdate("Author-102", null, "Ten moi", null, 0)), "dang cho muon");
        Book b = c.bookService().getByCode("Book-101");
        check(b.getName().equals("Bao Cong ky an"), "ten khong bi doi");
        check(b.getAuthor().getCode().equals("Author-101"), "tac gia khong bi doi");
    }

    private static void sortByGivenName() {
        AppContext c = fresh();
        // Ten that: Hieu, Nam, Hoa, Minh -> Hieu, Hoa, Minh, Nam
        List<String> names = c.authorService().findAllSortedByName().stream().map(Author::getShortName).toList();
        check(names.equals(List.of("Hieu", "Hoa", "Minh", "Nam")), "thu tu " + names);
        // Danh sach goc khong bi doi thu tu
        check(c.authorService().findAll().get(0).getCode().equals("Author-101"), "list goc giu nguyen");
    }

    private static void readerValidation() {
        AppContext c = fresh();
        expectError(() -> c.readerService().create(" ", "A", LocalDate.of(2000, 1, 1), Gender.MALE, true), "Ho");
        expectError(() -> c.readerService().create("A", "B", LocalDate.now().plusDays(1), Gender.MALE, true), "tuong lai");
        expectError(() -> c.readerService().create("A", "B", null, Gender.MALE, true), "Ngay sinh");
        expectError(() -> c.bookService().create("Author-101", "Category-101", "X", -5, 1), "So trang");
        expectError(() -> c.bookService().create("Author-999", "Category-101", "X", 5, 1), "khong ton tai");
    }

    private static void statistics() {
        AppContext c = fresh();
        Map<String, Long> byCategory = c.statisticsService().borrowedCountByCategory();
        // 5 luot muon: B101 x2 (Trinh tham), B102 x2 (Khoa hoc), B103 x1 (Giao trinh)
        check(byCategory.get("Trinh tham") == 2 && byCategory.get("Khoa hoc") == 2
                && byCategory.get("Giao trinh") == 1, "theo danh muc " + byCategory);
        check(c.statisticsService().borrowedCountByAuthor().size() == 3, "3 tac gia co luot muon");

        List<LoanLine> loans = c.statisticsService().currentLoans();
        check(loans.size() == 3 && loans.stream().allMatch(LoanLine::overdue), "3 sach dang muon, deu qua han");
        check(c.statisticsService().availableBooks().size() == 4, "4 dau sach con ke");
        check(c.statisticsService().loansByReader().size() == 4, "4 doc gia");
    }

    // ------------------------------------------------------------------

    private static void run(String name, Runnable test) {
        try {
            test.run();
            passed++;
            System.out.println("  [OK]   " + name);
        } catch (Throwable e) {
            failed++;
            System.out.println("  [LOI]  " + name + "\n         -> " + e);
        }
    }

    private static void check(boolean condition, String what) {
        if (!condition) throw new AssertionError(what);
    }

    private static void expectError(Runnable action, String messagePart) {
        try {
            action.run();
        } catch (BusinessException e) {
            if (!e.getMessage().contains(messagePart)) {
                throw new AssertionError("Thong bao '" + e.getMessage() + "' khong chua '" + messagePart + "'");
            }
            return;
        }
        throw new AssertionError("Phai nem BusinessException (" + messagePart + ")");
    }
}
