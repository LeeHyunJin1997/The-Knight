package com.a301.theknight.domain.player.service;

import com.a301.theknight.domain.game.entity.Game;
import com.a301.theknight.domain.game.entity.GameStatus;
import com.a301.theknight.domain.game.repository.GameRepository;
import com.a301.theknight.domain.member.entity.Member;
import com.a301.theknight.domain.member.repository.MemberRepository;
import com.a301.theknight.domain.player.dto.*;
import com.a301.theknight.domain.player.dto.request.PlayerReadyRequest;
import com.a301.theknight.domain.player.dto.request.PlayerTeamRequest;
import com.a301.theknight.domain.player.dto.response.PlayerEntryResponse;
import com.a301.theknight.domain.player.dto.response.PlayerExitResponse;
import com.a301.theknight.domain.player.dto.response.PlayerReadyResponse;
import com.a301.theknight.domain.player.dto.response.PlayerTeamResponse;
import com.a301.theknight.domain.player.entity.Player;
import com.a301.theknight.domain.player.entity.Team;
import com.a301.theknight.domain.player.repository.PlayerRepository;
import com.a301.theknight.global.error.errorcode.GameErrorCode;
import com.a301.theknight.global.error.errorcode.GameWaitingErrorCode;
import com.a301.theknight.global.error.errorcode.MemberErrorCode;
import com.a301.theknight.global.error.errorcode.PlayerErrorCode;
import com.a301.theknight.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PlayerService {

    private final MemberRepository memberRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    @Transactional
    public PlayerEntryResponse entry(long gameId, long memberId){
        Game entryGame = getGame(gameId);
        if(!isWaiting(entryGame)){
            throw new CustomException(GameWaitingErrorCode.GAME_IS_NOT_READY_STATUS);
        }
        if(!isEnterPossible(entryGame)){
            throw new CustomException(GameWaitingErrorCode.CAN_NOT_ACCOMMODATE);
        }
        Member entryMember = getMember(memberId);
        Player entryPlayer = Player.builder().member(entryMember).game(entryGame).build();
        playerRepository.save(entryPlayer);

        return PlayerEntryResponse.builder().playerId(entryPlayer.getMember().getId())
                .nickname(entryMember.getNickname())
                .image(entryMember.getImage())
                .build();
    }

    @Transactional
    public PlayerExitResponse exit(long gameId, long memberId){
        Game findGame = getGame(gameId);

        if(!isWaiting(findGame)){
            throw new CustomException(GameWaitingErrorCode.GAME_IS_NOT_READY_STATUS);
        }
        Member findMember = getMember(memberId);
        Player exitPlayer = getPlayer(findGame, findMember);
        exitPlayer.exitGame();

        PlayerExitResponse exitPlayerId = new PlayerExitResponse(exitPlayer.getMember().getId());
        playerRepository.delete(exitPlayer);

        return exitPlayerId;
    }

    @Transactional
    public PlayerTeamResponse team(long gameId, long memberId, PlayerTeamRequest playerTeamMessage){
        Game findGame = getGame(gameId);
        Member findMember = getMember(memberId);

        Player findPlayer = getPlayer(findGame, findMember);
        //TODO 유효성 검사 컨트롤러에서 처리하기, A,B 이외의 값이 들어온 경우
        findPlayer.selectTeam(Team.valueOf(playerTeamMessage.getTeam()));

        return PlayerTeamResponse.builder()
                .playerId(findPlayer.getMember().getId())
                .team(findPlayer.getTeam().name())
                .build();
    }

    @Transactional
    public ReadyResponseDto ready(long gameId, long memberId, PlayerReadyRequest playerReadyMessage){
        Game findGame = getGame(gameId);
        Member findMember = getMember(memberId);

        Player readyPlayer = getPlayer(findGame, findMember);

        readyPlayer.ready(playerReadyMessage.isReadyStatus());

        ReadyResponseDto readyResponseDto = new ReadyResponseDto();

        if(isOwner(findGame, readyPlayer)){
            if(!isEqualPlayerNum(findGame)) throw new CustomException(GameWaitingErrorCode.NUMBER_OF_PLAYERS_ON_BOTH_TEAM_IS_DIFFERENT);
            if(!isAllReady(findGame)) throw new CustomException(GameWaitingErrorCode.NOT_All_USERS_ARE_READY);
            if(!findGame.isCanStart()) throw new CustomException((GameWaitingErrorCode.NOT_MET_ALL_THE_CONDITIONS_YET));

            findGame.changeStatus(GameStatus.PLAYING);
            readyResponseDto.setSetGame(findGame.setGameMessage());
        }
        readyResponseDto.setPlayerReadyList(startAllPlayers(findGame));

        return readyResponseDto;
    }

    private Member getMember(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_IS_NOT_EXIST));
    }

    private Game getGame(long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(GameErrorCode.GAME_IS_NOT_EXIST));
    }

    private Player getPlayer(Game game, Member member){
        return playerRepository.findByGameAndMember(game, member)
                .orElseThrow(() -> new CustomException(PlayerErrorCode.PLAYER_IS_NOT_EXIST));
    }

    private boolean isWaiting(Game game){
        return game.getStatus() == GameStatus.WAITING;
    }
    private boolean isEnterPossible(Game game){
        return game.getCapacity() > game.getPlayers().size();
    }
    private boolean isOwner(Game game, Player player){
        return game.getPlayers().get(0).equals(player);
    }
    private boolean isEqualPlayerNum(Game game){
        AtomicInteger teamA = new AtomicInteger();
        AtomicInteger teamB = new AtomicInteger();
        game.getPlayers().stream().map(player -> player.getTeam().name().equals("A") ? teamA.getAndIncrement() : teamB.getAndIncrement())
                .collect(Collectors.toList());
        return teamA.intValue() == teamB.intValue();
    }

    private boolean isAllReady(Game game){
        boolean canStart = game.getPlayers().stream().filter(Player::isReady).count() == game.getCapacity();
        if(canStart) {
            game.completeReady();
            return true;
        } else{
            return false;
        }
    }

    private List<PlayerReadyResponse> startAllPlayers(Game game){
        return game.getPlayers()
                .stream()
                .map(player ->
                        PlayerReadyResponse.builder()
                                .playerId(player.getMember().getId())
                                .readyStatus(player.isReady())
                                .build()).
                collect(Collectors.toList());
    }
}