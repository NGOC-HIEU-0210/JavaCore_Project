package vn.devpro.management.config;

import java.time.LocalDate;

import vn.devpro.management.model.Gender;


public final class DataSeeder {
    private DataSeeder() {
    }

    public static void seed(AppContext ctx) {
        var categories = ctx.categoryService();
        categories.create("Thoi trang");   // Category-101
        categories.create("Trinh tham");   // Category-102
        categories.create("Khoa hoc");     // Category-103
        categories.create("Giao trinh");   // Category-104

        var authors = ctx.authorService();
        authors.create("Nguyen", "Ngoc Hieu");  // Author-101
        authors.create("Tran", "Van Nam");      // Author-102
        authors.create("Le", "Thi Hoa");        // Author-103
        authors.create("Pham", "Quang Minh");   // Author-104

        var books = ctx.bookService();
        books.create("Author-101", "Category-102", "Bao Cong ky an", 250, 10);        // Book-101
        books.create("Author-102", "Category-103", "AI va con duong moi", 300, 5);    // Book-102
        books.create("Author-103", "Category-104", "Nguyen ly he dieu hanh", 280, 8); // Book-103
        books.create("Author-104", "Category-101", "Mau ao thinh hanh", 320, 12);     // Book-104

        var readers = ctx.readerService();
        readers.create("Nguyen", "Van An", LocalDate.of(2000, 5, 12), Gender.MALE, true);    // Reader-101
        readers.create("Tran", "Thi Binh", LocalDate.of(1999, 8, 20), Gender.FEMALE, false); // Reader-102
        readers.create("Le", "Van Cong", LocalDate.of(2001, 1, 15), Gender.MALE, true);      // Reader-103
        readers.create("Pham", "Thi Dat", LocalDate.of(1998, 11, 30), Gender.FEMALE, false); // Reader-104

        var borrow = ctx.borrowService();
        // Phieu 101: dang muon (da qua han)
        borrow.createTicket("Reader-101", "Book-101", LocalDate.of(2023, 5, 22));
        borrow.addBook("Ticket-101", "Book-102");
        borrow.addBook("Ticket-101", "Book-103");
        // Phieu 102: da tra
        borrow.createTicket("Reader-102", "Book-102", LocalDate.of(2023, 6, 10));
        borrow.addBook("Ticket-102", "Book-101");
        borrow.returnTicket("Ticket-102", LocalDate.of(2023, 6, 14));
    }
}
