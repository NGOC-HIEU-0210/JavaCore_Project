package vn.devpro.management.controller;

import vn.devpro.management.exception.BusinessException;
import vn.devpro.management.model.BorrowTicket;
import vn.devpro.management.service.BookService;
import vn.devpro.management.service.BorrowService;
import vn.devpro.management.service.ReaderService;
import vn.devpro.management.view.ConsoleInput;
import vn.devpro.management.view.ConsolePrinter;
import vn.devpro.management.view.Menu;

public class BorrowController {
    private final BorrowService service;
    private final ReaderService readerService;
    private final BookService bookService;
    private final ConsoleInput in;
    private final ConsolePrinter out;

    public BorrowController(BorrowService service, ReaderService readerService, BookService bookService,
                            ConsoleInput in, ConsolePrinter out) {
        this.service = service;
        this.readerService = readerService;
        this.bookService = bookService;
        this.in = in;
        this.out = out;
    }

    public void showMenu() {
        new Menu("QUAN LY PHIEU MUON SACH", in, out)
                .add("Tao phieu muon moi", this::create)
                .add("Xem danh sach phieu muon", this::list)
                .add("Xem chi tiet phieu muon", this::view)
                .add("Them sach vao phieu", this::addBook)
                .add("Sua (thay the) sach trong phieu", this::replaceBook)
                .add("Xoa sach khoi phieu", this::removeBook)
                .add("Tra sach (tra ca phieu)", this::returnTicket)
                .add("Xoa phieu muon", this::delete)
                .run();
    }

    private void create() {
        out.title("TAO PHIEU MUON MOI");
        String readerCode = in.readValid("Nhap ma doc gia: ", c -> readerService.getByCode(c).getCode());
        String bookCode = in.readValid("Nhap ma sach muon: ", c -> bookService.getByCode(c).getCode());

        BorrowTicket ticket = service.createTicket(readerCode, bookCode);
        out.success("Da tao phieu " + ticket.getCode() + " va them sach dau tien.");

        while (ticket.getBooks().size() < BorrowService.MAX_BOOKS_PER_TICKET) {
            String code = in.readOptional("Nhap ma sach muon them (Enter de ket thuc): ");
            if (code == null) break;
            try {
                ticket = service.addBook(ticket.getCode(), code);
                out.success("Da them sach vao phieu!");
            } catch (BusinessException e) {
                out.error(e.getMessage());
            }
        }
        if (ticket.getBooks().size() >= BorrowService.MAX_BOOKS_PER_TICKET) {
            out.message("Phieu da dat toi da " + BorrowService.MAX_BOOKS_PER_TICKET + " sach.");
        }
        out.success("Tao phieu muon thanh cong!");
        out.receipt(ticket);
    }

    private void list() {
        out.title("DANH SACH PHIEU MUON");
        out.tickets(service.findAll());
    }

    private void view() {
        BorrowTicket ticket = in.readValid("Nhap ma phieu muon: ", service::getByCode);
        out.receipt(ticket);
    }

    private void addBook() {
        String ticketCode = in.readValid("Nhap ma phieu muon: ", c -> service.getByCode(c).getCode());
        String bookCode = in.readRequired("Nhap ma sach can them: ");
        service.addBook(ticketCode, bookCode);
        out.success("Da them sach vao phieu!");
    }

    private void replaceBook() {
        String ticketCode = in.readValid("Nhap ma phieu muon: ", c -> service.getByCode(c).getCode());
        String oldCode = in.readRequired("Nhap ma sach dang co trong phieu can thay: ");
        String newCode = in.readRequired("Nhap ma sach moi thay the: ");
        service.replaceBook(ticketCode, oldCode, newCode);
        out.success("Da thay sach trong phieu!");
    }

    private void removeBook() {
        String ticketCode = in.readValid("Nhap ma phieu muon: ", c -> service.getByCode(c).getCode());
        String bookCode = in.readRequired("Nhap ma sach can xoa khoi phieu: ");
        service.removeBook(ticketCode, bookCode);
        out.success("Da xoa sach khoi phieu!");
    }

    private void returnTicket() {
        String ticketCode = in.readValid("Nhap ma phieu muon can tra: ", c -> service.getByCode(c).getCode());
        service.returnTicket(ticketCode);
        out.success("Da ghi nhan tra sach, ton kho da duoc cap nhat!");
    }

    private void delete() {
        String ticketCode = in.readRequired("Nhap ma phieu muon can xoa: ");
        service.deleteTicket(ticketCode);
        out.success("Da xoa phieu muon!");
    }
}
