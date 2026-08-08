package com.tracek.domain.auth.application.provider;

import com.tracek.domain.auth.application.client.OAuthClient;
import com.tracek.domain.auth.domain.enums.OAuthProvider;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OAuthClientProvider {

    private final Map<OAuthProvider, OAuthClient> clients;

    public OAuthClientProvider(List<OAuthClient> clients) {
        this.clients =
                clients.stream()
                        .collect(Collectors.toMap(OAuthClient::support, Function.identity()));
    }

    public OAuthClient getClient(OAuthProvider provider) {
        return clients.get(provider);
    }
}
