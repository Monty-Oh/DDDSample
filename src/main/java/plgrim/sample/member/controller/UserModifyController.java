package plgrim.sample.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import plgrim.sample.common.enums.SuccessCode;
import plgrim.sample.member.application.UserModifyService;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserModifyDTO;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserModifyController {
    private final UserModifyService userModifyService;

    /**
     * 유저 수정 usrNo 필수
     * */
    @PutMapping("/user")
    public ResponseEntity<UserDTO> modify(@Valid @RequestBody UserModifyDTO userModifyDTO) {
        UserDTO userDTO = userModifyService.modify(userModifyDTO);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * 유저 삭제 usrNo 필수
     * */
    @DeleteMapping("/user")
    public ResponseEntity<String> delete(@RequestParam("usrNo") Long usrNo) {
        userModifyService.delete(usrNo);
        return ResponseEntity.ok(SuccessCode.DELETE_SUCCESS.getDetail());
    }
}
