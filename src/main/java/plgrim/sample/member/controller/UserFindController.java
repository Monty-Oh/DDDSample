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
     * 유저 조회 - useNo
     * */
    @GetMapping("/user")
    public ResponseEntity<UserDTO> findUserByUsrNo(@RequestParam("usrNo") Long usrNo) {
        UserDTO user = userFindService.findUserByUsrNo(usrNo);
        return ResponseEntity.ok(user);
    }
}
