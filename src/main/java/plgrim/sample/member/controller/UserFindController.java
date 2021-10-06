package plgrim.sample.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import plgrim.sample.member.application.UserFindService;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.validation.EmailValidation;

import javax.validation.ConstraintViolationException;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/API")
public class UserFindController {
    private final UserFindService userFindService;

    /**
     * 유저 조회 - email
     * @PathVariable 사용하기 - 수정 완료
     */
    // RequestParam은 여러개 받을 수 있고 PathVariable은 한번에 1개만 받을 수 있다.
    @GetMapping("/user")
    public ResponseEntity<UserDTO> findUserByEmail(@EmailValidation @RequestParam("email") String email) {
        UserDTO user = userFindService.findUserByEmail(email);
        return ResponseEntity.ok(user);
    }
}
