package com.a301.theknight.domain.common.service;

import com.a301.theknight.domain.game.dto.convert.GameStatusResponse;
import com.a301.theknight.domain.game.dto.convert.LimitTimeDto;
import com.a301.theknight.domain.game.entity.GameStatus;
import com.a301.theknight.domain.game.util.GameConvertUtil;
import com.a301.theknight.domain.limit.factory.TimeLimitServiceFactory;
import com.a301.theknight.domain.limit.template.TimeLimitServiceTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SendMessageService {
    private static final String SEND_PREFIX = "/sub/games/";
    private static final String CHAT_INFIX = "/chat-";

    private final SimpMessagingTemplate template;
    private final GameConvertUtil gameConvertUtil;
    private final TimeLimitServiceFactory timeLimitServiceFactory;

    public void sendData(long gameId, String postfix, Object payload) {
        template.convertAndSend(makeDestinationUrl(gameId, postfix), payload);
    }

    public void sendChatData(long gameId, String chattingSet, Object payload) {
        template.convertAndSend(makeChatDestinationUrl(gameId, chattingSet), payload);
    }

    public void convertCall(long gameId) {
        GameStatusResponse response = gameConvertUtil.convertScreen(gameId);
        sendData(gameId, "/convert", response);
    }

    public void convertCall(long gameId, long delayMillis) {
        try {
            Thread.sleep(delayMillis);
            convertCall(gameId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void forceConvertCall(long gameId) {
        GameStatusResponse response = gameConvertUtil.forceConvertScreen(gameId);
        sendData(gameId, "/convert", response);
    }

    public void proceedCall(long gameId, long delayMillis) {
        try {
            GameStatus gameStatus = gameConvertUtil.getGameStatus(gameId);
            Thread.sleep(delayMillis);

            sendData(gameId, "/proceed", LimitTimeDto.toDto(gameStatus));
            TimeLimitServiceTemplate timeLimitService = timeLimitServiceFactory.getTimeLimitService(gameStatus);
            timeLimitService.executeTimeLimit(gameId, this);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String makeDestinationUrl(long gameId, String postfix) {
        return SEND_PREFIX + gameId + postfix;
    }

    private String makeChatDestinationUrl(long gameId, String chattingSet) {
        return SEND_PREFIX + gameId + CHAT_INFIX + chattingSet.toLowerCase();
    }

}
