package it.unicam.hackhub.utils.facade;

import org.springframework.stereotype.Service;

@Service
public class DummyGoogleOAuthTokenStore implements GoogleOAuthTokenStore {

    @Override
    public String findAccessTokenByStaffProfileId(Long staffProfileId) {
        // Per ora restituiamo un token fittizio per non bloccare i test
        return "mock_google_token_12345";
    }
}