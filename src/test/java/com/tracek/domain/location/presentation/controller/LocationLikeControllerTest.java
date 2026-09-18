package com.tracek.domain.location.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.tracek.domain.location.application.service.LocationLikeCommandService;
import com.tracek.global.security.authentication.AuthenticationPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocationLikeControllerTest {

    @Mock LocationLikeCommandService locationLikeCommandService;

    @Test
    void likeAndUnlikeDelegateAuthenticatedUser() {
        var controller = new LocationLikeController(locationLikeCommandService);
        var principal = new AuthenticationPrincipal(7L, "user", "ROLE_USER");

        assertThat(controller.like(3L, principal)).isNotNull();
        assertThat(controller.unlike(3L, principal)).isNotNull();

        verify(locationLikeCommandService).like(7L, 3L);
        verify(locationLikeCommandService).unlike(7L, 3L);
    }
}
