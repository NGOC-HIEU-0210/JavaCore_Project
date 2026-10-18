package vn.devpro.management.view;

import java.util.ArrayList;
import java.util.List;

import vn.devpro.management.exception.BusinessException;

public class Menu {
    private record Item(String label, Runnable action) {
    }

    private final String title;
    private final ConsoleInput in;
    private final ConsolePrinter out;
    private final List<Item> items = new ArrayList<>();
    private String exitLabel = "Quay lai";

    public Menu(String title, ConsoleInput in, ConsolePrinter out) {
        this.title = title;
        this.in = in;
        this.out = out;
    }

    public Menu add(String label, Runnable action) {
        items.add(new Item(label, action));
        return this;
    }

    public Menu exitLabel(String label) {
        this.exitLabel = label;
        return this;
    }

    public void run() {
        while (true) {
            out.menu(title, items.stream().map(Item::label).toList(), exitLabel);
            int choice = in.readInt("Lua chon cua ban: ");
            if (choice == 0) return;
            if (choice < 0 || choice > items.size()) {
                out.error("Lua chon khong hop le!");
                continue;
            }
            try {
                items.get(choice - 1).action().run();
            } catch (BusinessException e) {
                out.error(e.getMessage());
            }
        }
    }
}
