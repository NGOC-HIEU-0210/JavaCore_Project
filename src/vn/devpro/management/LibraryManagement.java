package vn.devpro.management;

import java.util.NoSuchElementException;

import vn.devpro.management.config.AppContext;
import vn.devpro.management.config.DataSeeder;
import vn.devpro.management.controller.AuthorController;
import vn.devpro.management.controller.BookController;
import vn.devpro.management.controller.BorrowController;
import vn.devpro.management.controller.CategoryController;
import vn.devpro.management.controller.MainController;
import vn.devpro.management.controller.ReaderController;
import vn.devpro.management.controller.StatisticsController;
import vn.devpro.management.view.ConsoleInput;
import vn.devpro.management.view.ConsolePrinter;


public class LibraryManagement {

    public static void main(String[] args) {
        AppContext ctx = new AppContext();
        DataSeeder.seed(ctx);

        ConsoleInput in = new ConsoleInput(System.in, System.out);
        ConsolePrinter out = new ConsolePrinter(System.out);

        MainController app = new MainController(
                new CategoryController(ctx.categoryService(), in, out),
                new AuthorController(ctx.authorService(), in, out),
                new BookController(ctx.bookService(), ctx.authorService(), ctx.categoryService(), in, out),
                new ReaderController(ctx.readerService(), in, out),
                new BorrowController(ctx.borrowService(), ctx.readerService(), ctx.bookService(), in, out),
                new StatisticsController(ctx.statisticsService(), in, out),
                in, out);

        try {
            app.run();
        } catch (NoSuchElementException endOfInput) {
            out.message("\nKhong con du lieu nhap, thoat chuong trinh.");
        }
    }
}
