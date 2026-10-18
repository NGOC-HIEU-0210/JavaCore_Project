package vn.devpro.management.view;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import vn.devpro.management.model.Author;
import vn.devpro.management.model.Book;
import vn.devpro.management.model.BorrowTicket;
import vn.devpro.management.model.Category;
import vn.devpro.management.model.Reader;
import vn.devpro.management.service.StatisticsService.LoanLine;
import vn.devpro.management.service.StatisticsService.ReaderLoans;


public class ConsolePrinter {
    private static final String ORGANIZATION = "TRUNG TAM TT THU VIEN";
    private static final String SCHOOL = "TRUONG DH DEVPRO";
    private static final String CITY = "Ha Noi";
    private static final String LIBRARIAN = "Nguyen Tran Mai Huong";

    private final PrintStream out;

    public ConsolePrinter(PrintStream out) {
        this.out = out;
    }

    //Thong bao chung

    public void title(String text) {
        out.println("\n\t\t" + text);
    }

    public void message(String text) {
        out.println(text);
    }

    public void success(String text) {
        out.println("\t" + text);
    }

    public void error(String text) {
        out.println("\t! " + text);
    }

    public void menu(String title, List<String> labels, String exitLabel) {
        out.println("\n\t\t" + title);
        out.println("Chon mot chuc nang:");
        for (int i = 0; i < labels.size(); i++) {
            out.println("\t" + (i + 1) + ". " + labels.get(i));
        }
        out.println("\t0. " + exitLabel);
    }

    //  Bang du lieu

    public void authors(List<Author> list) {
        out.printf("%-12s %-15s %-20s%n", "Ma", "Ho", "Ten");
        for (Author a : list) {
            out.printf("%-12s %-15s %-20s%n", a.getCode(), a.getFamilyName(), a.getGivenName());
        }
        footer(list.size());
    }

    public void categories(List<Category> list) {
        out.printf("%-13s %-25s%n", "Ma", "Ten danh muc");
        for (Category c : list) {
            out.printf("%-13s %-25s%n", c.getCode(), c.getName());
        }
        footer(list.size());
    }

    public void books(List<Book> list) {
        out.printf("%-10s %-28s %-22s %-14s %6s %5s %5s%n",
                "Ma", "Ten sach", "Tac gia", "Danh muc", "Trang", "Tong", "Con");
        for (Book b : list) {
            out.printf("%-10s %-28s %-22s %-14s %6d %5d %5d%n",
                    b.getCode(), cut(b.getName(), 28), cut(b.getAuthor().getFullName(), 22),
                    cut(b.getCategory().getName(), 14), b.getNumberOfPages(), b.getQuantity(), b.getAvailable());
        }
        footer(list.size());
    }

    public void readers(List<Reader> list) {
        out.printf("%-11s %-24s %-11s %-9s %-10s%n", "Ma", "Ho va ten", "Ngay sinh", "Gioi tinh", "Sinh vien");
        for (Reader r : list) {
            out.printf("%-11s %-24s %-11s %-9s %-10s%n",
                    r.getCode(), cut(r.getFullName(), 24), r.getDateOfBirth().format(Formats.DATE),
                    r.getGender().getLabel(), r.isStudent() ? "Co" : "Khong");
        }
        footer(list.size());
    }

    public void tickets(List<BorrowTicket> list) {
        LocalDate today = LocalDate.now();
        out.printf("%-11s %-11s %-24s %-11s %-11s %-6s %-10s%n",
                "Ma phieu", "Ma DG", "Doc gia", "Ngay muon", "Han tra", "So sach", "Trang thai");
        for (BorrowTicket t : list) {
            out.printf("%-11s %-11s %-24s %-11s %-11s %-6d %-10s%n",
                    t.getCode(), t.getReader().getCode(), cut(t.getReader().getFullName(), 24),
                    t.getBorrowDate().format(Formats.DATE), t.getDueDate().format(Formats.DATE),
                    t.getBooks().size(), status(t, today));
        }
        footer(list.size());
    }


    public void receipt(BorrowTicket t) {
        Reader reader = t.getReader();
        out.println();
        out.println(ORGANIZATION);
        out.println(SCHOOL);
        out.println("PHIEU MUON SACH\n");
        out.println("Ma phieu: " + t.getCode());
        out.println("Ma doc gia: " + reader.getCode());
        out.println("Ho va ten doc gia: " + reader.getFullName());
        out.println("Ngay muon: " + t.getBorrowDate().format(Formats.DATE));
        out.println("Trang thai: " + status(t, LocalDate.now())
                + (t.isReturned() ? " (ngay " + t.getReturnDate().format(Formats.DATE) + ")" : ""));
        out.println("\nDanh sach cac sach cho muon:");
        out.printf("%-4s %-28s %-22s %-14s %-12s%n", "STT", "Ten sach", "Ten tac gia", "Loai sach", "Ngay hen tra");
        int stt = 1;
        for (Book b : t.getBooks()) {
            out.printf("%-4d %-28s %-22s %-14s %-12s%n", stt++, cut(b.getName(), 28),
                    cut(b.getAuthor().getFullName(), 22), cut(b.getCategory().getName(), 14),
                    t.getDueDate().format(Formats.DATE));
        }
        out.println("\n" + CITY + ", ngay " + t.getBorrowDate().format(Formats.DATE));
        out.println("Thu thu");
        out.println(LIBRARIAN);
    }

    // Thong ke

    public void counts(String heading, Map<String, Long> counts) {
        out.println("\n" + heading);
        if (counts.isEmpty()) out.println("(chua co du lieu)");
        counts.forEach((name, n) -> out.println("- " + name + ": " + n + " cuon"));
    }

    public void currentLoans(List<LoanLine> lines) {
        out.println("\nCac cuon sach dang duoc muon:");
        if (lines.isEmpty()) {
            out.println("(khong co)");
            return;
        }
        out.printf("%-11s %-28s %-22s %-22s %-11s%n", "Phieu", "Ten sach", "Tac gia", "Nguoi muon", "Han tra");
        for (LoanLine l : lines) {
            out.printf("%-11s %-28s %-22s %-22s %-11s%s%n", l.ticketCode(), cut(l.bookName(), 28),
                    cut(l.authorName(), 22), cut(l.readerName(), 22), l.dueDate().format(Formats.DATE),
                    l.overdue() ? "  QUA HAN" : "");
        }
    }

    public void loansByReader(List<ReaderLoans> list) {
        out.println("\nSach da muon theo tung doc gia:");
        for (ReaderLoans rl : list) {
            out.println("\nDoc gia: " + rl.reader().getFullName() + " (" + rl.reader().getCode() + ")");
            if (rl.books().isEmpty()) out.println("  (chua muon sach nao)");
            for (Book b : rl.books()) out.println("- " + b.getName());
        }
    }

    // Noi bo
    private void footer(int count) {
        if (count == 0) out.println("(khong co du lieu)");
        else out.println("Tong: " + count);
    }

    private static String status(BorrowTicket t, LocalDate today) {
        if (t.isReturned()) return "DA TRA";
        return t.isOverdue(today) ? "QUA HAN" : "DANG MUON";
    }

    private static String cut(String s, int width) {
        return s.length() <= width ? s : s.substring(0, width - 1) + "~";
    }
}
