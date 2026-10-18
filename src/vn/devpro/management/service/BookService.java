package vn.devpro.management.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import vn.devpro.management.exception.BusinessException;
import vn.devpro.management.model.Author;
import vn.devpro.management.model.Book;
import vn.devpro.management.model.BorrowTicket;
import vn.devpro.management.model.Category;
import vn.devpro.management.repository.Repository;

public class BookService {

    public record BookUpdate(String authorCode, String categoryCode, String name,
                             Integer numberOfPages, Integer quantity) {
    }

    private final Repository<Book> books;
    private final Repository<BorrowTicket> tickets;
    private final AuthorService authorService;
    private final CategoryService categoryService;

    public BookService(Repository<Book> books, Repository<BorrowTicket> tickets,
                       AuthorService authorService, CategoryService categoryService) {
        this.books = books;
        this.tickets = tickets;
        this.authorService = authorService;
        this.categoryService = categoryService;
    }

    //  Truy van

    public List<Book> findAll() {
        return books.findAll();
    }

    public List<Book> findAllSortedByName() {
        List<Book> list = new ArrayList<>(books.findAll());
        list.sort(Comparator.comparing(Book::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    public Book getByCode(String code) {
        return books.findByCode(code)
                .orElseThrow(() -> new BusinessException("Sach khong ton tai: " + code));
    }

    public List<Book> findByCategory(String categoryCode) {
        Category category = categoryService.getByCode(categoryCode);
        return books.findAll().stream().filter(b -> b.getCategory().getId() == category.getId()).toList();
    }

    public List<Book> findByAuthor(String authorCode) {
        Author author = authorService.getByCode(authorCode);
        return books.findAll().stream().filter(b -> b.getAuthor().getId() == author.getId()).toList();
    }

    public List<Book> searchByName(String keyword) {
        String key = Validation.requireText(keyword, "Tu khoa").toLowerCase(Locale.ROOT);
        return books.findAll().stream().filter(b -> b.getName().toLowerCase(Locale.ROOT).contains(key)).toList();
    }

    //  Thay doi

    public Book create(String authorCode, String categoryCode, String name, int numberOfPages, int quantity) {
        Author author = authorService.getByCode(authorCode);
        Category category = categoryService.getByCode(categoryCode);
        Book book = new Book(
                Validation.requireText(name, "Ten sach"),
                author,
                category,
                Validation.requirePositive(numberOfPages, "So trang"),
                Validation.requireNonNegative(quantity, "So luong"));
        return books.save(book);
    }

    public Book update(String code, BookUpdate change) {
        Book book = getByCode(code);


        Author author = change.authorCode() == null ? null : authorService.getByCode(change.authorCode());
        Category category = change.categoryCode() == null ? null : categoryService.getByCode(change.categoryCode());
        if (change.numberOfPages() != null) {
            Validation.requirePositive(change.numberOfPages(), "So trang");
        }
        if (change.quantity() != null) {
            Validation.requireNonNegative(change.quantity(), "So luong");
            if (change.quantity() < book.getBorrowedCount()) {
                throw new BusinessException("So luong moi (" + change.quantity()
                        + ") nho hon so cuon dang cho muon (" + book.getBorrowedCount() + ")");
            }
        }

        if (change.name() != null && !change.name().isBlank()) book.setName(change.name().trim());
        if (author != null) book.setAuthor(author);
        if (category != null) book.setCategory(category);
        if (change.numberOfPages() != null) book.setNumberOfPages(change.numberOfPages());
        if (change.quantity() != null) book.changeQuantity(change.quantity());
        return books.save(book);
    }

    public void delete(String code) {
        Book book = getByCode(code);
        boolean referenced = tickets.findAll().stream().anyMatch(t -> t.contains(book));
        if (referenced) {
            throw new BusinessException("Khong the xoa: sach da xuat hien trong phieu muon");
        }
        books.delete(book);
    }

    // Ton kho (duoc BorrowService goi, moi noi chi thay doi ton kho o day)

    public void lendCopy(Book book) {
        if (book.getAvailable() <= 0) {
            throw new BusinessException("Sach da het: " + book.getName());
        }
        book.takeCopy();
        books.save(book);
    }

    public void receiveCopy(Book book) {
        book.returnCopy();
        books.save(book);
    }
}
