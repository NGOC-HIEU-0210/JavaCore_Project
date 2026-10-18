package vn.devpro.management.controller;

import vn.devpro.management.service.StatisticsService;
import vn.devpro.management.view.ConsoleInput;
import vn.devpro.management.view.ConsolePrinter;
import vn.devpro.management.view.Menu;

public class StatisticsController {
    private final StatisticsService service;
    private final ConsoleInput in;
    private final ConsolePrinter out;

    public StatisticsController(StatisticsService service, ConsoleInput in, ConsolePrinter out) {
        this.service = service;
        this.in = in;
        this.out = out;
    }

    public void showMenu() {
        new Menu("THONG KE", in, out)
                .add("Thong ke luot muon theo danh muc", () ->
                        out.counts("Thong ke luot muon sach theo danh muc:", service.borrowedCountByCategory()))
                .add("Thong ke luot muon theo tac gia", () ->
                        out.counts("Thong ke luot muon sach theo tac gia:", service.borrowedCountByAuthor()))
                .add("Thong ke cac sach dang duoc muon", () ->
                        out.currentLoans(service.currentLoans()))
                .add("Thong ke cac sach con lai co the muon", () -> {
                    out.title("SACH CON CO THE MUON");
                    out.books(service.availableBooks());
                })
                .add("Thong ke sach da muon theo tung doc gia", () ->
                        out.loansByReader(service.loansByReader()))
                .run();
    }
}
