package com.tracek.domain.user.domain.model;

import com.tracek.domain.user.domain.exception.UserErrorCode;
import com.tracek.global.exception.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Email {

    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);

    @Column(nullable = false, unique = true)
    private String email;

    public static Email of(String value) {
        if (!isValid(value)) {
            throw new CustomException(UserErrorCode.INVALID_EMAIL_FORMAT);
        }
        Email email = new Email();
        email.email = value;
        return email;
    }

    private static boolean isValid(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = PATTERN.matcher(email);
        return matcher.matches();
    }
}
