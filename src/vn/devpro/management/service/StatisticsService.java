package vn.devpro.management.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import vn.devpro.management.model.Book;
import vn.devpro.management.model.BorrowTicket;
import vn.devpro.management.model.Reader;
import vn.devpro.management.repository.Repository;


public class StatisticsService {

    public record LoanLine(String ticketCode, String bookName, String authorName,
                           String readerName, LocalDate dueDate, boolean overdue) {
    }

    public record ReaderLoans(Reader reader, List<Book> books) {
    }

    private final Repository<BorrowTicket> tickets;
    private final Repository<Book> books;
    private final Repository<Reader> readers;

    public StatisticsService(Repository<BorrowTicket> tickets, Repository<Book> books, Repository<Reader> readers) {
        this.tickets = tickets;
        this.books = books;
        this.readers = readers;
    }


    public Map<String, Long> borrowedCountByCategory() {
        return allBorrowedBooks().collect(Collectors.groupingBy(
                b -> b.getCategory().getName(), TreeMap::new, Collectors.counting()));
    }


    public Map<String, Long> borrowedCountByAuthor() {
        return allBorrowedBooks().collect(Collectors.groupingBy(
                b -> b.getAuthor().getFullName(), TreeMap::new, Collectors.counting()));
    }


    public List<LoanLine> currentLoans() {
        LocalDate today = LocalDate.now();
        List<LoanLine> lines = new ArrayList<>();
        for (BorrowTicket ticket : tickets.findAll()) {
            if (ticket.isReturned()) continue;
            for (Book book : ticket.getBooks()) {
                lines.add(new LoanLine(ticket.getCode(), book.getName(), book.getAuthor().getFullName(),
                        ticket.getReader().getFullName(), ticket.getDueDate(), ticket.isOverdue(today)));
            }
        }
        return lines;
    }


    public List<Book> availableBooks() {
        return books.findAll().stream().filter(b -> b.getAvailable() > 0).toList();
    }


    public List<ReaderLoans> loansByReader() {
        List<ReaderLoans> result = new ArrayList<>();
        for (Reader reader : readers.findAll()) {
            List<Book> borrowed = tickets.findAll().stream()
                    .filter(t -> t.getReader().getId() == reader.getId())
                    .flatMap(t -> t.getBooks().stream())
                    .toList();
            result.add(new ReaderLoans(reader, borrowed));
        }
        return result;
    }

    private Stream<Book> allBorrowedBooks() {
        return tickets.findAll().stream().flatMap(t -> t.getBooks().stream());
    }
}
