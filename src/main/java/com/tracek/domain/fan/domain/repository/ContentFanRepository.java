package com.tracek.domain.fan.domain.repository;

import com.tracek.domain.fan.domain.model.ContentFan;
import com.tracek.domain.fan.domain.model.ContentFanId;
import java.util.List;
import java.util.Optional;

public interface ContentFanRepository {

    boolean existsById(ContentFanId id);

    void saveAndFlush(ContentFan contentFan);

    Optional<ContentFan> findById(ContentFanId fanId);

    void delete(ContentFanId fanId);

    List<ContentFan> findAllByUserId(Long userId);

    Integer countContentsFansByUserId(Long userId);

    Integer countContentsFansByContentId(Long contentId);
}
