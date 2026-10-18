package vn.devpro.management.view;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

final class Formats {
    private Formats() {
    }


    static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
}
