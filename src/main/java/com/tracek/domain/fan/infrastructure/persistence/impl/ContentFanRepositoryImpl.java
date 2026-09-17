package com.tracek.domain.fan.infrastructure.persistence.impl;

import com.tracek.domain.fan.domain.model.ContentFan;
import com.tracek.domain.fan.domain.model.ContentFanId;
import com.tracek.domain.fan.domain.repository.ContentFanRepository;
import com.tracek.domain.fan.infrastructure.persistence.jpa.ContentFanJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentFanRepositoryImpl implements ContentFanRepository {

    private final ContentFanJpaRepository contentFanJpaRepository;

    @Override
    public boolean existsById(ContentFanId id) {
        return contentFanJpaRepository.existsById(id);
    }

    @Override
    public void saveAndFlush(ContentFan contentFan) {
        contentFanJpaRepository.saveAndFlush(contentFan);
    }

    @Override
    public Optional<ContentFan> findById(ContentFanId fanId) {
        return contentFanJpaRepository.findById(fanId);
    }

    @Override
    public void delete(ContentFanId fanId) {
        contentFanJpaRepository.deleteById(fanId);
    }

    @Override
    public List<ContentFan> findAllByUserId(Long userId) {
        return contentFanJpaRepository.findAllByUserId(userId);
    }

    @Override
    public Integer countContentsFansByUserId(Long userId) {
        return contentFanJpaRepository.countContentFanByUserId(userId);
    }

    @Override
    public Integer countContentsFansByContentId(Long contentId) {
        return contentFanJpaRepository.countContentFanByContentId(contentId);
    }
}
