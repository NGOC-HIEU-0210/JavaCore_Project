package vn.devpro.management.config;

import vn.devpro.management.model.Author;
import vn.devpro.management.model.Book;
import vn.devpro.management.model.BorrowTicket;
import vn.devpro.management.model.Category;
import vn.devpro.management.model.Reader;
import vn.devpro.management.repository.InMemoryRepository;
import vn.devpro.management.repository.Repository;
import vn.devpro.management.service.AuthorService;
import vn.devpro.management.service.BookService;
import vn.devpro.management.service.BorrowService;
import vn.devpro.management.service.CategoryService;
import vn.devpro.management.service.ReaderService;
import vn.devpro.management.service.StatisticsService;


public class AppContext {
    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final BookService bookService;
    private final ReaderService readerService;
    private final BorrowService borrowService;
    private final StatisticsService statisticsService;

    public AppContext() {
        Repository<Category> categoryRepo = new InMemoryRepository<>("Category-");
        Repository<Author> authorRepo = new InMemoryRepository<>("Author-");
        Repository<Book> bookRepo = new InMemoryRepository<>("Book-");
        Repository<Reader> readerRepo = new InMemoryRepository<>("Reader-");
        Repository<BorrowTicket> ticketRepo = new InMemoryRepository<>("Ticket-");

        categoryService = new CategoryService(categoryRepo, bookRepo);
        authorService = new AuthorService(authorRepo, bookRepo);
        bookService = new BookService(bookRepo, ticketRepo, authorService, categoryService);
        readerService = new ReaderService(readerRepo, ticketRepo);
        borrowService = new BorrowService(ticketRepo, readerService, bookService);
        statisticsService = new StatisticsService(ticketRepo, bookRepo, readerRepo);
    }

    public CategoryService categoryService() {
        return categoryService;
    }

    public AuthorService authorService() {
        return authorService;
    }

    public BookService bookService() {
        return bookService;
    }

    public ReaderService readerService() {
        return readerService;
    }

    public BorrowService borrowService() {
        return borrowService;
    }

    public StatisticsService statisticsService() {
        return statisticsService;
    }
}
