package com.logpolice.infrastructure.rpc;

import com.alibaba.fastjson.JSONObject;
import com.logpolice.domain.entity.ExceptionNotice;
import com.logpolice.domain.repository.ExceptionNoticeRepository;
import com.logpolice.infrastructure.dto.FeiShuCommand;
import com.logpolice.infrastructure.enums.NoticeSendEnum;
import com.logpolice.infrastructure.exception.DingDingTokeNotExistException;
import com.logpolice.infrastructure.properties.LogpoliceFeiShuProperties;
import com.logpolice.infrastructure.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 飞书推送逻辑层
 *
 * @author huang
 * @date 2019/8/28
 */
@Slf4j
public class FeiShuNoticeRpc implements ExceptionNoticeRepository {

    private final LogpoliceFeiShuProperties logpoliceFeiShuProperties;

    public FeiShuNoticeRpc(LogpoliceFeiShuProperties logpoliceFeiShuProperties) {
        this.logpoliceFeiShuProperties = logpoliceFeiShuProperties;
    }

    @Override
    public NoticeSendEnum getType() {
        return NoticeSendEnum.FEI_SHU;
    }

    @Override
    public void send(ExceptionNotice exceptionNotice) {
        String webHook = logpoliceFeiShuProperties.getFeiShuWebHook();
        if (StringUtils.isEmpty(webHook)) {
            throw new DingDingTokeNotExistException("FeiShuNoticeSendServiceImpl.send error! webHook is null!");
        }
        FeiShuCommand feiShuCommand = new FeiShuCommand(exceptionNotice.getProject(), exceptionNotice.getText());
        String body = JSONObject.toJSONString(feiShuCommand);
        String result = HttpUtils.post(webHook, body);
        log.info("FeiShuNoticeSendServiceImpl.send success, webHook:{}, command:{}, result:{}", webHook, body, result);
    }
}
