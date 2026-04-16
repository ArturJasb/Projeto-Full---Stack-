package com.unasp.comandadigital.config;

/**
 * Validação de CNPJ pelo algoritmo oficial da Receita Federal.
 */
public class CnpjValidator {

    private CnpjValidator() {}

    public static boolean isValid(String cnpj) {
        if (cnpj == null) return false;

        // Remove formatação
        String cleaned = cnpj.replaceAll("[^0-9]", "");

        if (cleaned.length() != 14) return false;

        // Rejeita CNPJs com todos os dígitos iguais
        if (cleaned.chars().distinct().count() == 1) return false;

        // Calcula primeiro dígito verificador
        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += Character.getNumericValue(cleaned.charAt(i)) * weights1[i];
        }
        int remainder = sum % 11;
        int d1 = remainder < 2 ? 0 : 11 - remainder;

        if (d1 != Character.getNumericValue(cleaned.charAt(12))) return false;

        // Calcula segundo dígito verificador
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += Character.getNumericValue(cleaned.charAt(i)) * weights2[i];
        }
        remainder = sum % 11;
        int d2 = remainder < 2 ? 0 : 11 - remainder;

        return d2 == Character.getNumericValue(cleaned.charAt(13));
    }

    public static String format(String cnpj) {
        String c = cnpj.replaceAll("[^0-9]", "");
        if (c.length() != 14) return cnpj;
        return c.substring(0, 2) + "." + c.substring(2, 5) + "." +
               c.substring(5, 8) + "/" + c.substring(8, 12) + "-" + c.substring(12);
    }
}
