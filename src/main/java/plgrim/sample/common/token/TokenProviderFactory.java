package plgrim.sample.common.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import plgrim.sample.common.enums.Sns;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class TokenProviderFactory {
    private final Map<Sns, TokenProvider> tokenProviderMap = new HashMap<>();

    public TokenProvider findTokenProvider(Sns sns) {
        return tokenProviderMap.get(sns);
    }

    @Autowired
    public TokenProviderFactory(Set<TokenProvider> tokenProviderSet) {
        tokenProviderSet.forEach(tokenProvider -> tokenProviderMap.put(tokenProvider.getThisTargetSns(), tokenProvider));
    }
}
