package com.tracek.domain.location.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    @DisplayName("상세 주소가 있으면 Address를 생성한다")
    void of_valid() {
        Address address = Address.of("서울특별시", "종로구", "사직로 161");

        assertThat(address.getCity()).isEqualTo("서울특별시");
        assertThat(address.getDistrict()).isEqualTo("종로구");
        assertThat(address.getAddress()).isEqualTo("사직로 161");
    }

    @Test
    @DisplayName("상세 주소가 null이면 예외가 발생한다")
    void of_nullAddress() {
        assertThatThrownBy(() -> Address.of("서울특별시", "종로구", null))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.INVALID_ADDRESS);
    }

    @Test
    @DisplayName("상세 주소가 공백이면 예외가 발생한다")
    void of_blankAddress() {
        assertThatThrownBy(() -> Address.of("서울특별시", "종로구", "   "))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.INVALID_ADDRESS);
    }
}
