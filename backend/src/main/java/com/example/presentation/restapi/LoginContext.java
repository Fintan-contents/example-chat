package com.example.presentation.restapi;

import com.example.domain.model.account.AccountId;
import nablarch.common.web.session.SessionUtil;
import nablarch.fw.ExecutionContext;

public class LoginContext {

    private final ExecutionContext executionContext;

    private static final String WAITING_2FA_SESSION_KEY = "waiting2fa";

    public LoginContext(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public void login(AccountId accountId) {
        SessionUtil.invalidate(executionContext);

        SessionUtil.delete(executionContext, WAITING_2FA_SESSION_KEY);

        // NablarchのThreadContextでユーザーIDを使用するための設定
        SessionUtil.put(executionContext, "user.id", accountId.value().toString());
    }

    public void waiting2fa(AccountId accountId) {
        SessionUtil.put(executionContext, WAITING_2FA_SESSION_KEY, accountId.value().toString());
    }

    public AccountId getAccountIdWaiting2fa() {
        String value = SessionUtil.orNull(executionContext, WAITING_2FA_SESSION_KEY);
        return new AccountId(Long.parseLong(value));
    }
}
