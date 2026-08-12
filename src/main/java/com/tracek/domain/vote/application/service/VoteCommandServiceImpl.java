package com.tracek.domain.vote.application.service;

import com.tracek.domain.location.application.dto.LocationContentArtistResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.vote.application.dto.command.VoteCreateCommand;
import com.tracek.domain.vote.application.dto.result.VoteCreateResult;
import com.tracek.domain.vote.domain.exception.VoteErrorCode;
import com.tracek.domain.vote.domain.model.Vote;
import com.tracek.domain.vote.domain.model.VoteTarget;
import com.tracek.domain.vote.domain.repository.VoteRepository;
import com.tracek.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteCommandServiceImpl implements VoteCommandService {

    private final VoteRepository voteRepository;
    private final LocationQueryService locationQueryService;

    @Transactional
    @Override
    public VoteCreateResult createVote(VoteCreateCommand command) {
        // 해당 관광지에 이미 투표 했는 지 확인
        if (voteRepository.hasAlreadyVotedLocation(command.userId(), command.locationId())) {
            throw new CustomException(VoteErrorCode.ALREADY_VOTED);
        }
        // 해당 관광지-아티스트-콘텐츠 테이블 결과 가져오기
        LocationContentArtistResult locationContentArtistResult =
                locationQueryService.getMappingById(command.locationContentArtistId());
        // 투표 하기
        Vote vote =
                Vote.createVote(
                        command.userId(),
                        VoteTarget.of(
                                locationContentArtistResult.getLocationId(),
                                command.locationContentArtistId(),
                                locationContentArtistResult.getArtistId(),
                                locationContentArtistResult.getContentId(),
                                command.voteTargetNameSnapShot()));
        Vote saveVote = voteRepository.save(vote);
        return VoteCreateResult.from(saveVote);
    }
}
