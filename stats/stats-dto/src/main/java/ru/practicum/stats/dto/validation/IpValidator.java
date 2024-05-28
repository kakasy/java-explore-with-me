package ru.practicum.stats.dto.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpValidator implements ConstraintValidator<IpAddressValidation, String> {

    private static final String IPV4 = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";

    private static final Pattern pattern = Pattern.compile(IPV4);


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
