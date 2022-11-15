package com.a301.theknight.domain.game.api;

import com.a301.theknight.domain.common.service.SendMessageService;
import com.a301.theknight.domain.game.factory.GameScreenDataServiceFactory;
import com.a301.theknight.domain.game.template.GameDataService;
import com.a301.theknight.domain.game.util.GameConvertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import javax.validation.constraints.Min;

@Controller
@RequiredArgsConstructor
public class GameScreenDataApi {
    private final GameScreenDataServiceFactory dataTemplateFactory;
    private final GameConvertUtil gameConvertUtil;
    private final SendMessageService messageService;

    @MessageMapping(value = "/games/{gameId}/screen-data")
    public void makeAndSendScreenData(@Min(1) @DestinationVariable long gameId) {
        boolean isFullCount = gameConvertUtil.requestCounting(gameId);
        if (!isFullCount) {
            return;
        }

        GameDataService dataTemplate = dataTemplateFactory.getGameDataTemplate(gameId);
        dataTemplate.makeAndSendData(gameId, messageService);

        messageService.proceedCall(gameId, 500);
    }

}
