package com.tracek.domain.location.domain.model;

import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.global.exception.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Address {

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "district", length = 50)
    private String district;

    @Column(name = "address", length = 200)
    private String address;

    public static Address of(String city, String district, String address) {
        validateAddress(address);
        return new Address(city, district, address);
    }

    private static void validateAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new CustomException(LocationErrorCode.INVALID_ADDRESS);
        }
    }
}
