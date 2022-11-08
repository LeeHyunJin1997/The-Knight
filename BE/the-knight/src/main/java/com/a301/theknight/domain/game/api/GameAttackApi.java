package com.a301.theknight.domain.game.api;

import com.a301.theknight.domain.auth.annotation.LoginMemberId;
import com.a301.theknight.domain.game.dto.attack.request.GameAttackPassRequest;
import com.a301.theknight.domain.game.dto.attack.request.GameAttackRequest;
import com.a301.theknight.domain.game.dto.attack.response.AttackResponse;
import com.a301.theknight.domain.game.service.GameAttackService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RequiredArgsConstructor
@Controller
public class GameAttackApi {
    private static final String SEND_PREFIX = "/sub/games/";
    private static final String SERVER_PREFIX = "/pub/games/";
    private final SimpMessagingTemplate template;

    private final GameAttackService gameAttackService;

    @MessageMapping(value = "/games/{gameId}/attack")
    public void attack(@Min(1) @DestinationVariable long gameId, @Valid GameAttackRequest gameAttackRequest,
                       @LoginMemberId long memberId) {
        gameAttackService.attack(gameId, memberId, gameAttackRequest);

        template.convertAndSend(makeConvertUri(gameId));
    }

    @MessageMapping(value="/games/{gameId}/attack-info")
    public void attackInfo(@Min(1) @DestinationVariable long gameId) throws InterruptedException {
        AttackResponse response = gameAttackService.getAttackInfo(gameId);
        template.convertAndSend(makeDestinationUri(gameId, "/attack-info"), response);

        Thread.sleep(500);
        template.convertAndSend(makeDestinationUri(gameId, "/proceed"));
    }

    @MessageMapping(value="/games/{gameId}/attack-pass")
    public void attackPass(@Min(1) @DestinationVariable long gameId, @Valid GameAttackPassRequest gameAttackPassRequest,
                           @LoginMemberId long memberId){
        gameAttackService.isAttackPass(gameId, gameAttackPassRequest, memberId);
        template.convertAndSend(makeConvertUri(gameId));
    }

    private String makeDestinationUri(long gameId, String postfix) {
        return SEND_PREFIX + gameId + postfix;
    }

    private String makeConvertUri(long gameId) {
        return SERVER_PREFIX + gameId + "/convert";
    }
}
