package com.symbel.orienteeringquiz.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Validate Email with regular expression
     *
     * @param email the email to validate
     * @return true for Valid Email and false for Invalid Email
     */
    public static boolean validate(String email) {
        if (email != null && !email.isEmpty()) {
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }
        return false;
    }

    /**
     * Checks for Null String object
     *
     * @param txt the text to validate
     * @return true for null and false for not null String object
     */
    public static boolean isNotNull(String txt) {
        return txt != null && txt.trim().isEmpty();
    }
}