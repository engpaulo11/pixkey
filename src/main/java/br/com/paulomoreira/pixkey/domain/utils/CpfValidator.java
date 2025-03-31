package br.com.paulomoreira.pixkey.domain.utils;

public class CpfValidator {
    public static boolean isValid(String cpf) {
        if (cpf == null || cpf.length() != 11 || !cpf.matches("\\d+")) {
            return false;
        }
        int[] digits = cpf.chars().map(Character::getNumericValue).toArray();
        return validateDigit(digits, 10, 9) && validateDigit(digits, 11, 10);
    }

    private static boolean validateDigit(int[] digits, int weight, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += digits[i] * (weight - i);
        }
        int remainder = (sum * 10) % 11;
        int expectedDigit = (remainder == 10 || remainder == 11) ? 0 : remainder;
        return digits[length] == expectedDigit;
    }
}