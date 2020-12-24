package com.example.system.nablarch.interceptor;

import com.example.application.service.authentication.AuthorizationException;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import com.example.infrastructure.persistence.entity.ChannelEntity;
import com.example.infrastructure.persistence.entity.ChannelMemberEntity;
import com.example.infrastructure.persistence.entity.ChannelOwnerEntity;
import com.example.presentation.restapi.RestApiException;
import nablarch.common.dao.NoDataException;
import nablarch.common.dao.UniversalDao;
import nablarch.core.ThreadContext;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Interceptor;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;

import java.lang.annotation.*;
import java.util.Map;

/**
 * Nablarchのインターセプター機能を使った、権限をチェックするアノテーション。
 *
 * 権限を制御したいActionクラスのメソッドに対して @CheckPermission のアノテーションを付与して使用する。
 * 引数には権限に応じた値を指定する。
 *
 * |種類|権限|
 * |---|---|
 * |CHANNEL_MEMBER|チャンネルのメンバー|
 * |CHANNEL_OWNER|チャンネルのオーナー|
 * |CHANNEL_MEMBER_NOT_OWNER|チャンネルのオーナー以外のメンバー|
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Interceptor(CheckPermission.Impl.class)
@Documented
public @interface CheckPermission {

    Permission value();

    class Impl extends nablarch.fw.Interceptor.Impl<HttpRequest, Object, CheckPermission> {

        @Override
        public Object handle(HttpRequest request, ExecutionContext context) {
            CheckPermission annotation = getInterceptor();

            AccountId accountId = new AccountId(Long.parseLong(ThreadContext.getUserId()));
            ChannelId channelId = new ChannelId(Long.valueOf(request.getParam("channelId")[0]));
            try {
                UniversalDao.findById(ChannelEntity.class, channelId.value());
            } catch (NoDataException e) {
                // チャンネルが存在しない場合
                throw new RestApiException(HttpResponse.Status.NOT_FOUND, "channel.notfound");
            }

            switch (annotation.value()) {
            case CHANNEL_MEMBER: {
                if (!isMember(accountId, channelId))
                    throw new AuthorizationException("not member of channel.");
                break;
            }
            case CHANNEL_OWNER: {
                if (!isOwner(accountId, channelId))
                    throw new AuthorizationException("not owner of channel.");
                break;
            }
            case CHANNEL_MEMBER_NOT_OWNER: {
                if (!isMember(accountId, channelId))
                    throw new AuthorizationException("not member of channel.");
                if (isOwner(accountId, channelId))
                    throw new AuthorizationException("owner of channel.");
                break;
            }
            }
            return this.getOriginalHandler().handle(request, context);
        }

        private boolean isMember(AccountId accountId, ChannelId channelId) {
            Map<String, Long> condition = Map.of("accountId", accountId.value(), "channelId", channelId.value());
            return UniversalDao.exists(ChannelMemberEntity.class, "SELECT_FOR_EXISTS", condition);
        }

        private boolean isOwner(AccountId accountId, ChannelId channelId) {
            Map<String, Long> condition = Map.of("accountId", accountId.value(), "channelId", channelId.value());
            return UniversalDao.exists(ChannelOwnerEntity.class, "SELECT_FOR_EXISTS", condition);
        }
    }

    enum Permission {
        CHANNEL_MEMBER, CHANNEL_OWNER, CHANNEL_MEMBER_NOT_OWNER
    }
}
