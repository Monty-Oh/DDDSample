package plgrim.sample.member.controller.validation;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.util.regex.Pattern;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Documented
@Constraint(validatedBy = PhoneNumberValidation.Validator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumberValidation {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<PhoneNumberValidation, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            // 전화번호가 비어있는지?
            if (!isNotBlank(value)) {
                addConstraintViolation(context, "전화번호를 입력해주세요.");
                return false;
            }

            // 전화번호 정규식
            Pattern pattern = Pattern.compile("/^\\d{3}-\\d{3,4}-\\d{4}$/;");
            if (!pattern.matcher(value).matches()) {
                addConstraintViolation(context, "전화번호 형식을 확인해주세요.");
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
