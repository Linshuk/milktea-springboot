package com.hongkl.shop.handler;

import cn.hutool.core.util.CharsetUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hongkl.shop.exception.YamiShopBindException;
import com.hongkl.shop.response.ServerResponseEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;


@Component
public class HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(HttpHandler.class);

    @Autowired
    private ObjectMapper objectMapper;

    public <T> void printServerResponseToWeb(ServerResponseEntity<T> serverResponseEntity) {
        if (serverResponseEntity == null) {
            logger.info("print obj is null");
            return;
        }

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (requestAttributes == null) {
            logger.error("requestAttributes is null, can not print to web");
            return;
        }
        HttpServletResponse response = requestAttributes.getResponse();
        if (response == null) {
            logger.error("httpServletResponse is null, can not print to web");
            return;
        }
        logger.error("response error: " + serverResponseEntity.getMsg());
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
            printWriter.write(objectMapper.writeValueAsString(serverResponseEntity));
        }
        catch (IOException e) {
            throw new YamiShopBindException("io 异常", e);
        }
    }

    public <T> void printServerResponseToWeb(YamiShopBindException yamiShopBindException) {
        if (yamiShopBindException == null) {
            logger.info("print obj is null");
            return;
        }

        if (Objects.nonNull(yamiShopBindException.getServerResponseEntity())) {
            printServerResponseToWeb(yamiShopBindException.getServerResponseEntity());
            return;
        }

        ServerResponseEntity<T> serverResponseEntity = new ServerResponseEntity<>();
        serverResponseEntity.setCode(yamiShopBindException.getCode());
        serverResponseEntity.setMsg(yamiShopBindException.getMessage());
        printServerResponseToWeb(serverResponseEntity);
    }
}
