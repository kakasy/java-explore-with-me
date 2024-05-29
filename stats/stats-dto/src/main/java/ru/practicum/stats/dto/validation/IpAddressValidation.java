package ru.practicum.stats.dto.validation;

import java.lang.annotation.*;
import javax.validation.Payload;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IpAddressValidation {

    String message() default "ip указан неверно";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
