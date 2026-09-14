package com.tracek.domain.location.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.tracek.domain.location.application.service.LocationArchiveCommandService;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.security.authentication.AuthenticationPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocationArchiveControllerTest {

    @Mock private LocationArchiveCommandService locationArchiveCommandService;

    private LocationArchiveController controller;

    @BeforeEach
    void setUp() {
        controller = new LocationArchiveController(locationArchiveCommandService);
    }

    @Test
    @DisplayName("아카이브 등록 요청을 서비스에 위임하고 성공 응답을 반환한다")
    void archive_success() {
        AuthenticationPrincipal principal = new AuthenticationPrincipal(1L, "user", "USER");

        ApiResponse<Void> response = controller.archive(1L, principal);

        assertThat(response.getIsSuccess()).isTrue();
        verify(locationArchiveCommandService).archive(1L, 1L);
    }

    @Test
    @DisplayName("아카이브 취소 요청을 서비스에 위임하고 성공 응답을 반환한다")
    void deleteArchive_success() {
        AuthenticationPrincipal principal = new AuthenticationPrincipal(1L, "user", "USER");

        ApiResponse<Void> response = controller.deleteArchive(1L, principal);

        assertThat(response.getIsSuccess()).isTrue();
        verify(locationArchiveCommandService).deleteArchive(1L, 1L);
    }
}
