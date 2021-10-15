package plgrim.sample.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import plgrim.sample.member.application.UserFindService;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.domain.model.aggregates.User;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping
// Controller 하나로 합치기
public class UserFindController {
    private final UserFindService userFindService;

    /**
     * 유저 조회 - usrNo
     */
    @GetMapping("/users/{usrNo}")
    public ResponseEntity<UserDTO> findUserByUsrNo(@PathVariable("usrNo") Long usrNo) {
        UserDTO user = userFindService.findUserByUsrNo(usrNo);
        return ResponseEntity.ok(user);
    }

    /**
     * 유저 목록 조회 page, size
     **/
    @GetMapping("/users")
    public ResponseEntity<List<User>> findUserList(@RequestParam("page") int page,
                                                   @RequestParam("size") int size) {
        List<User> users = userFindService.findUsers(page, size);
        return ResponseEntity.ok(users);
    }
}
