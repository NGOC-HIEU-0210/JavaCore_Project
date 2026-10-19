package vn.devpro.management.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BorrowTicket extends BaseEntity {
    private final Reader reader;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private final List<Book> books = new ArrayList<>();

    public BorrowTicket(Reader reader, LocalDate borrowDate, LocalDate dueDate) {
        this.reader = reader;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public Reader getReader() {
        return reader;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public boolean isOverdue(LocalDate today) {
        return !isReturned() && today.isAfter(dueDate);
    }

    public boolean contains(Book book) {
        return books.stream().anyMatch(b -> b.getId() == book.getId());
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public boolean removeBook(Book book) {
        for (Book x : books) {
            if (x.getId() == book.getId()) {
                books.remove(x);
                return true;
            }
        }
        return false;
    }

    public void markReturned(LocalDate date) {
        this.returnDate = date;
    }
}
