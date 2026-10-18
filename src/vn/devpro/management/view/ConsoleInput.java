package vn.devpro.management.view;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Function;

import vn.devpro.management.exception.BusinessException;


public class ConsoleInput {
    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleInput(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
    }

    //  Chuoi

    public String readOptional(String prompt) {
        out.print(prompt);
        String line = scanner.nextLine().trim();
        return line.isEmpty() ? null : line;
    }

    public String readRequired(String prompt) {
        while (true) {
            String value = readOptional(prompt);
            if (value != null) return value;
            out.println("\t! Khong duoc de trong.");
        }
    }

    //  So

    public int readInt(String prompt) {
        while (true) {
            Integer value = readOptionalInt(prompt);
            if (value != null) return value;
            out.println("\t! Khong duoc de trong.");
        }
    }

    public Integer readOptionalInt(String prompt) {
        while (true) {
            String line = readOptional(prompt);
            if (line == null) return null;
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                out.println("\t! Vui long nhap mot so nguyen.");
            }
        }
    }

    //  Ngay, co/khong

    public LocalDate readDate(String prompt) {
        while (true) {
            LocalDate value = readOptionalDate(prompt);
            if (value != null) return value;
            out.println("\t! Khong duoc de trong.");
        }
    }

    public LocalDate readOptionalDate(String prompt) {
        while (true) {
            String line = readOptional(prompt);
            if (line == null) return null;
            try {
                return LocalDate.parse(line, Formats.DATE);
            } catch (DateTimeParseException e) {
                out.println("\t! Ngay khong hop le, dung dinh dang dd/MM/yyyy (vd 25/12/2000).");
            }
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            Boolean value = readOptionalYesNo(prompt);
            if (value != null) return value;
            out.println("\t! Khong duoc de trong.");
        }
    }

    public Boolean readOptionalYesNo(String prompt) {
        while (true) {
            String line = readOptional(prompt);
            if (line == null) return null;
            switch (line.toLowerCase(Locale.ROOT)) {
                case "c", "co", "y", "yes", "true" -> {
                    return true;
                }
                case "k", "khong", "n", "no", "false" -> {
                    return false;
                }
                default -> out.println("\t! Vui long nhap c (co) hoac k (khong).");
            }
        }
    }

    // Nhap kem kiem tra (vd tim theo ma)


    public <T> T readValid(String prompt, Function<String, T> lookup) {
        while (true) {
            String line = readRequired(prompt);
            try {
                return lookup.apply(line);
            } catch (BusinessException e) {
                out.println("\t! " + e.getMessage());
            }
        }
    }


    public <T> T readOptionalValid(String prompt, Function<String, T> lookup) {
        while (true) {
            String line = readOptional(prompt);
            if (line == null) return null;
            try {
                return lookup.apply(line);
            } catch (BusinessException e) {
                out.println("\t! " + e.getMessage());
            }
        }
    }
}
