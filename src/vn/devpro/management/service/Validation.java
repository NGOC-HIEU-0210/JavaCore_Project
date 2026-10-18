package vn.devpro.management.service;

import vn.devpro.management.exception.BusinessException;


final class Validation {
    private Validation() {}

    static String requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(label + " khong duoc bo trong");
        }
        return value.trim();
    }


    static String keepIfBlank(String newValue, String current) {
        return (newValue == null || newValue.isBlank()) ? current : newValue.trim();
    }

    static int requirePositive(int value, String label) {
        if (value <= 0) throw new BusinessException(label + " phai lon hon 0");
        return value;
    }

    static int requireNonNegative(int value, String label) {
        if (value < 0) throw new BusinessException(label + " khong duoc am");
        return value;
    }
}
