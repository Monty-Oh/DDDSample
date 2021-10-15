package plgrim.sample.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plgrim.sample.member.application.UserJoinService;
import plgrim.sample.member.controller.dto.mapper.UserCommandMapper;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserJoinDTO;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserJoinController {
    private final UserJoinService userJoinService;
    private final UserCommandMapper userCommandMapper;      // 커맨드 변환 맵퍼

    /**
     * 회원 가입
     */
    @PostMapping("/users")
    public ResponseEntity<UserDTO> join(@Valid @RequestBody UserJoinDTO userJoinDTO) {
        UserDTO userDTO = userJoinService.join(userCommandMapper.UserJoinMapper(userJoinDTO));
        return ResponseEntity.ok(userDTO);
    }
}
