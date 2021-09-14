package plgrim.sample.member.controller.dto.mapper;

import org.springframework.stereotype.Component;
import plgrim.sample.member.controller.dto.user.UserJoinDTO;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.model.vo.UserBasic;

@Component
public class UserCommandMapper {

    public UserJoinCommand UserJoinMapper(UserJoinDTO userJoinDTO) {
        return UserJoinCommand.builder()
                .id(userJoinDTO.getId())
                .password(userJoinDTO.getPassword())
                .phoneNumber(userJoinDTO.getPhoneNumber())
                .userBasic(UserBasic.builder()
                        .address(userJoinDTO.getAddress())
                        .gender(userJoinDTO.getGender())
                        .birth(userJoinDTO.getBirth())
                        .snsType(userJoinDTO.getSnsType())
                        .build())
                .build();
    }
}
