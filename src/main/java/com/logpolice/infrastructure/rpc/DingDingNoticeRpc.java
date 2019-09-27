package com.logpolice.infrastructure.rpc;

import com.alibaba.fastjson.JSONObject;
import com.logpolice.domain.entity.ExceptionNotice;
import com.logpolice.domain.repository.ExceptionNoticeRepository;
import com.logpolice.infrastructure.dto.DingDingCommand;
import com.logpolice.infrastructure.enums.NoticeSendEnum;
import com.logpolice.infrastructure.exception.DingDingTokeNotExistException;
import com.logpolice.infrastructure.properties.LogpoliceDingDingProperties;
import com.logpolice.infrastructure.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 钉钉推送逻辑层
 *
 * @author huang
 * @date 2019/8/28
 */
@Slf4j
public class DingDingNoticeRpc implements ExceptionNoticeRepository {

    private final LogpoliceDingDingProperties logpoliceDingDingProperties;

    public DingDingNoticeRpc(LogpoliceDingDingProperties logpoliceDingDingProperties) {
        this.logpoliceDingDingProperties = logpoliceDingDingProperties;
    }

    @Override
    public NoticeSendEnum getType() {
        return NoticeSendEnum.DING_DING;
    }

    @Override
    public void send(ExceptionNotice exceptionNotice) {
        String webHook = logpoliceDingDingProperties.getWebHook();
        if (StringUtils.isEmpty(webHook)) {
            throw new DingDingTokeNotExistException("DingDingNoticeSendServiceImpl.send error! webHook is null!");
        }
        DingDingCommand dingDingCommand = new DingDingCommand(exceptionNotice.getText(),
                logpoliceDingDingProperties.getMsgType(),
                logpoliceDingDingProperties.getAtMobiles(),
                logpoliceDingDingProperties.getIsAtAll());
        String body = JSONObject.toJSONString(dingDingCommand);
        String result = HttpUtils.post(webHook, body);
        log.info("noticeSendServiceImpl.send success, webHook:{}, command:{}, result:{}", webHook, body, result);
    }
}
