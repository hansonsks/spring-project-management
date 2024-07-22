package com.example.Todo_list.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Service class for managing passwords.
 */
@Service
@RequiredArgsConstructor
public class PasswordService extends BCryptPasswordEncoder {

    public boolean matches(String rawPassword, String encodedPassword) {
        return super.matches(rawPassword, encodedPassword);
    }

    /**
     * Generates a random password with the given length
     * @param length the length of the password
     * @return the generated password
     */
    public String generateEncodedPassword(int length) {
        return super.encode(generatePassword(length));
    }

    /**
     * Encodes the given password
     * @param rawPassword the password to encode
     * @return the encoded password
     */
    public String encodePassword(String rawPassword) {
        return super.encode(rawPassword);
    }

    /**
     * Generates a random password with the given length
     * @param length the length of the password
     * @return the generated password
     */
    public String generatePassword(int length) {
        if (length < 8) {
            length = 8;
        }

        final char[] lowercase = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        final char[] uppercase = "ABCDEFGJKLMNPRSTUVWXYZ".toCharArray();
        final char[] numbers = "0123456789".toCharArray();
        final char[] symbols = "^$?!@#%&".toCharArray();
        final char[] allAllowed = "abcdefghijklmnopqrstuvwxyzABCDEFGJKLMNPRSTUVWXYZ0123456789^$?!@#%&".toCharArray();

        Random random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length-4; i++) {
            password.append(allAllowed[random.nextInt(allAllowed.length)]);
        }

        password.insert(random.nextInt(password.length()), lowercase[random.nextInt(lowercase.length)]);
        password.insert(random.nextInt(password.length()), uppercase[random.nextInt(uppercase.length)]);
        password.insert(random.nextInt(password.length()), numbers[random.nextInt(numbers.length)]);
        password.insert(random.nextInt(password.length()), symbols[random.nextInt(symbols.length)]);

        return password.toString();
    }
}
