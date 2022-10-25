package com.a301.theknight.domain.game.service;

import com.a301.theknight.domain.game.dto.GameInfoResponse;
import com.a301.theknight.domain.game.entity.Game;
import com.a301.theknight.domain.game.dto.GameCreateRequest;
import com.a301.theknight.domain.game.dto.GameListDto;
import com.a301.theknight.domain.game.dto.GameListResponse;
import com.a301.theknight.domain.game.repository.GameRepository;
import com.a301.theknight.domain.member.entity.Member;
import com.a301.theknight.domain.member.repository.MemberRepository;
import com.a301.theknight.domain.player.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GameService {
    private final GameRepository gameRepository;
    private final MemberRepository memberRepository;
    private final PlayerRepository playerRepository;

    //  테스트용
    private static long memberId = 1L;

    @Transactional(readOnly = true)
    public GameListResponse getGameList(@Nullable String keyword, Pageable pageable){
        GameListResponse gameListResponse = new GameListResponse();
        Page<Game> gamePage = null;
        if(keyword != null){
            gamePage = gameRepository.findByTitleIsContaining(keyword, pageable);
        }else{
            gamePage = gameRepository.findAll(pageable);
        }

        List<GameListDto> gameListDtos = gamePage.stream().map(game -> {
            return GameListDto.builder()
                    .gameId(game.getId())
                    .title(game.getTitle())
                    .status(game.getStatus().name())
                    .capacity(game.getCapacity())
                    .participant(game.getPlayers().size())
                    .build();
        }).collect(Collectors.toList());

        gameListResponse.setGames(gameListDtos);

        return gameListResponse;
    }

    @Transactional
    public long createGame(GameCreateRequest gameCreateRequest){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        Game newGame = gameCreateRequest.toEntity();
        gameRepository.save(newGame);
        return newGame.getId();
    }

    @Transactional(readOnly = true)
    public GameInfoResponse getGameInfo(long gameId){
        Game findGame = gameRepository.findById(gameId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 게임입니다."));

        return GameInfoResponse.builder()
                .gameId(gameId)
                .title(findGame.getTitle())
                .capacity(findGame.getCapacity())
                .participant(findGame.getPlayers().size())
                .sword(findGame.getSword())
                .twin(findGame.getTwin())
                .shield(findGame.getShield())
                .hand(findGame.getHand())
                .build();
    }
}
