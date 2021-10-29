package plgrim.sample.member.domain.service;

import org.springframework.stereotype.Component;
import plgrim.sample.common.enums.Sns;

@Component
public class SnsStrategyKakao implements SnsStrategy{


    @Override
    public Sns getSns() {
        return Sns.GOOGLE;
    }
}
