package vn.devpro.management.service;

import java.time.LocalDate;
import java.util.List;

import vn.devpro.management.exception.BusinessException;
import vn.devpro.management.model.Book;
import vn.devpro.management.model.BorrowTicket;
import vn.devpro.management.model.Reader;
import vn.devpro.management.repository.Repository;


public class BorrowService {
    public static final int MAX_BOOKS_PER_TICKET = 3;
    public static final int MAX_ACTIVE_BOOKS_STUDENT = 8;
    public static final int MAX_ACTIVE_BOOKS_OTHER = 5;
    public static final int LOAN_DAYS = 5;

    private final Repository<BorrowTicket> tickets;
    private final ReaderService readerService;
    private final BookService bookService;

    public BorrowService(Repository<BorrowTicket> tickets, ReaderService readerService, BookService bookService) {
        this.tickets = tickets;
        this.readerService = readerService;
        this.bookService = bookService;
    }

    //  Truy van

    public List<BorrowTicket> findAll() {
        return tickets.findAll();
    }

    public BorrowTicket getByCode(String code) {
        return tickets.findByCode(code)
                .orElseThrow(() -> new BusinessException("Khong tim thay phieu muon: " + code));
    }

    public int maxActiveBooks(Reader reader) {
        return reader.isStudent() ? MAX_ACTIVE_BOOKS_STUDENT : MAX_ACTIVE_BOOKS_OTHER;
    }


    public int countActiveBooks(Reader reader) {
        return tickets.findAll().stream()
                .filter(t -> !t.isReturned() && t.getReader().getId() == reader.getId())
                .mapToInt(t -> t.getBooks().size())
                .sum();
    }

    //  Nghiep vu

    public BorrowTicket createTicket(String readerCode, String bookCode) {
        return createTicket(readerCode, bookCode, LocalDate.now());
    }


    public BorrowTicket createTicket(String readerCode, String bookCode, LocalDate borrowDate) {
        Reader reader = readerService.getByCode(readerCode);
        Book book = bookService.getByCode(bookCode);

        ensureReaderBelowLimit(reader);
        ensureAvailable(book);

        BorrowTicket ticket = new BorrowTicket(reader, borrowDate, borrowDate.plusDays(LOAN_DAYS));
        ticket.addBook(book);
        bookService.lendCopy(book);
        return tickets.save(ticket);
    }

    public BorrowTicket addBook(String ticketCode, String bookCode) {
        BorrowTicket ticket = getActiveTicket(ticketCode);
        Book book = bookService.getByCode(bookCode);

        if (ticket.getBooks().size() >= MAX_BOOKS_PER_TICKET) {
            throw new BusinessException("Moi phieu chi duoc muon toi da " + MAX_BOOKS_PER_TICKET + " sach");
        }
        if (ticket.contains(book)) {
            throw new BusinessException("Moi sach chi duoc muon 1 cuon trong mot phieu");
        }
        ensureAvailable(book);
        ensureReaderBelowLimit(ticket.getReader());

        ticket.addBook(book);
        bookService.lendCopy(book);
        return tickets.save(ticket);
    }


    public BorrowTicket replaceBook(String ticketCode, String oldBookCode, String newBookCode) {
        BorrowTicket ticket = getActiveTicket(ticketCode);
        Book oldBook = bookService.getByCode(oldBookCode);
        Book newBook = bookService.getByCode(newBookCode);

        if (!ticket.contains(oldBook)) {
            throw new BusinessException("Sach " + oldBook.getCode() + " khong co trong phieu");
        }
        if (oldBook.getId() == newBook.getId()) {
            throw new BusinessException("Sach moi trung voi sach cu");
        }
        if (ticket.contains(newBook)) {
            throw new BusinessException("Sach moi da co trong phieu");
        }
        ensureAvailable(newBook);

        ticket.removeBook(oldBook);
        ticket.addBook(newBook);
        bookService.receiveCopy(oldBook);
        bookService.lendCopy(newBook);
        return tickets.save(ticket);
    }

    public BorrowTicket removeBook(String ticketCode, String bookCode) {
        BorrowTicket ticket = getActiveTicket(ticketCode);
        Book book = bookService.getByCode(bookCode);

        if (!ticket.contains(book)) {
            throw new BusinessException("Sach " + book.getCode() + " khong co trong phieu");
        }
        if (ticket.getBooks().size() == 1) {
            throw new BusinessException("Phieu chi con 1 sach; hay tra phieu hoac xoa phieu");
        }
        ticket.removeBook(book);
        bookService.receiveCopy(book);
        return tickets.save(ticket);
    }

    public BorrowTicket returnTicket(String ticketCode) {
        return returnTicket(ticketCode, LocalDate.now());
    }


    public BorrowTicket returnTicket(String ticketCode, LocalDate returnDate) {
        BorrowTicket ticket = getActiveTicket(ticketCode);
        if (returnDate.isBefore(ticket.getBorrowDate())) {
            throw new BusinessException("Ngay tra khong duoc truoc ngay muon");
        }
        ticket.getBooks().forEach(bookService::receiveCopy);
        ticket.markReturned(returnDate);
        return tickets.save(ticket);
    }

    public void deleteTicket(String ticketCode) {
        BorrowTicket ticket = getByCode(ticketCode);
        if (!ticket.isReturned()) {
            ticket.getBooks().forEach(bookService::receiveCopy);
        }
        tickets.delete(ticket);
    }

    // Kiem tra luat

    private BorrowTicket getActiveTicket(String ticketCode) {
        BorrowTicket ticket = getByCode(ticketCode);
        if (ticket.isReturned()) {
            throw new BusinessException("Phieu " + ticket.getCode() + " da tra, khong the thay doi");
        }
        return ticket;
    }

    private void ensureAvailable(Book book) {
        if (book.getAvailable() <= 0) {
            throw new BusinessException("Sach da het: " + book.getName());
        }
    }

    private void ensureReaderBelowLimit(Reader reader) {
        int limit = maxActiveBooks(reader);
        if (countActiveBooks(reader) >= limit) {
            throw new BusinessException("Doc gia da dat gioi han " + limit + " sach dang muon");
        }
    }
}
