package plgrim.sample.member.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import plgrim.sample.common.enums.Sns;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class SnsStrategyFactory {
    private Map<Sns, SnsStrategy> strategyMap;

    @Autowired
    public SnsStrategyFactory(Set<SnsStrategy> strategySet) {
        createSnsStrategy(strategySet);
    }

    public SnsStrategy findSnsStrategy(Sns sns) {
        return strategyMap.get(sns);
    }

    private void createSnsStrategy(Set<SnsStrategy> strategySet) {
        strategyMap = new HashMap<>();
        strategySet.forEach(snsStrategy -> strategyMap.put(snsStrategy.getSns(), snsStrategy));
    }
}
