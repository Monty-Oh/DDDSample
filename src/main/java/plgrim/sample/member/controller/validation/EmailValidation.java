package plgrim.sample.member.controller.validation;

import plgrim.sample.common.enums.ErrorCode;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.util.regex.Pattern;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Documented
@Constraint(validatedBy = EmailValidation.Validator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailValidation {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<EmailValidation, String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {

            // 이메일이 비어있는지? (null 값인지?)
            if(!isNotBlank(value)) {
                addConstraintViolation(context, ErrorCode.VALIDATION_ERROR_ID_EMPTY.getDetail());
                return false;
            }

            // 이메일 정규식
            Pattern pattern = Pattern.compile("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$");
            if(!pattern.matcher(value).matches()) {
                addConstraintViolation(context, ErrorCode.VALIDATION_ERROR_ID.getDetail());
                return false;
            }

            return true;
        }

        // context에 메세지 설정
        private void addConstraintViolation(ConstraintValidatorContext context, String msg) {
            //기본 메시지 비활성화
            context.disableDefaultConstraintViolation();
            //새로운 메시지 추가
            context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
        }
    }
}
