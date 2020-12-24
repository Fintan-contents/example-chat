package com.example.application.service.authentication;

import com.example.domain.model.account.*;
import com.example.domain.model.authentication.AuthenticationCode;
import com.example.domain.model.authentication.AuthenticationStatus;
import com.example.domain.repository.AccountPasswordRepository;
import com.example.presentation.restapi.LoginContext;
import com.example.domain.repository.AccountRepository;
import com.example.application.AuthenticationException;
import com.example.application.service.account.PasswordHashingProcessor;
import com.example.domain.repository.TwoFactorAuthenticationSettingRepository;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.common.web.session.SessionUtil;
import nablarch.fw.ExecutionContext;

@SystemRepositoryComponent
public class AuthenticationService {

    private static final String WAITING_2FA_ACTIVATION = "WAITING_2FA_ACTIVATION";

    private final AccountRepository accountRepository;

    private final AccountPasswordRepository passwordRepository;

    private final TwoFactorAuthenticationSettingRepository twoFactorAuthenticationSettingRepository;

    private final AuthenticationCodeGenerator authenticationCodeGenerator;

    private final PasswordHashingProcessor passwordHashingProcessor;

    public AuthenticationService(AccountRepository accountRepository, AccountPasswordRepository passwordRepository,
            TwoFactorAuthenticationSettingRepository twoFactorAuthenticationSettingRepository,
            PasswordHashingProcessor passwordHashingProcessor) {
        this.accountRepository = accountRepository;
        this.passwordRepository = passwordRepository;
        this.twoFactorAuthenticationSettingRepository = twoFactorAuthenticationSettingRepository;
        this.passwordHashingProcessor = passwordHashingProcessor;
        this.authenticationCodeGenerator = new AuthenticationCodeGenerator();
    }

    public AuthenticationStatus authenticate(LoginContext context, MailAddress mailAddress, RawPassword password) {
        Account account = accountRepository.findBy(mailAddress);
        if (account == null) {
            throw new AuthenticationException();
        }
        AccountPassword accountPassword = passwordRepository.findById(account.accountId());
        if (!passwordHashingProcessor.matches(accountPassword.password(), password)) {
            throw new AuthenticationException();
        }

        TwoFactorAuthenticationSetting twoFactorAuthenticationSetting = twoFactorAuthenticationSettingRepository
                .findById(account.accountId());
        if (twoFactorAuthenticationSetting.isEnabled()) {
            context.waiting2fa(account.accountId());
            return AuthenticationStatus.WAITING_2FA;
        }

        context.login(account.accountId());
        return AuthenticationStatus.COMPLETE;
    }

    public void verifyAuthenticationCode(LoginContext loginContext, AuthenticationCode code) {
        AccountId accountId = loginContext.getAccountIdWaiting2fa();
        if (accountId == null) {
            throw new CredentialNotFoundException();
        }
        TwoFactorAuthenticationSetting setting = twoFactorAuthenticationSettingRepository.findById(accountId);

        AuthenticationCode generatedCode = authenticationCodeGenerator.generate(setting.secretKey());
        if (!generatedCode.isMatch(code)) {
            throw new AuthenticationException();
        }

        loginContext.login(accountId);
    }

    public TwoFactorAuthenticationSettingStatus get2FASettingStatus(AccountId accountId) {
        TwoFactorAuthenticationSetting setting = twoFactorAuthenticationSettingRepository.findById(accountId);

        return setting.twoFactorAuthenticationSettingStatus();
    }

    public SecretString generate2FASecretString(ExecutionContext context) {
        SecretKey secretKey = SecretKey.generate();

        SessionUtil.put(context, WAITING_2FA_ACTIVATION, secretKey.value());

        // QRコードに変換することを考慮してBase32でエンコードする
        return SecretString.fromSecretKey(secretKey);
    }

    public void enable2FA(ExecutionContext context, AccountId accountId, AuthenticationCode code) {
        byte[] secretKeyValue = SessionUtil.orNull(context, WAITING_2FA_ACTIVATION);
        if (secretKeyValue == null) {
            // セッションが切れて取得できなかった場合も同様のエラーとして扱っておく
            throw new CredentialNotFoundException();
        }
        SecretKey secretKey = new SecretKey(secretKeyValue);

        AuthenticationCode generatedCode = authenticationCodeGenerator.generate(secretKey);
        if (!code.isMatch(generatedCode)) {
            throw new AuthenticationException();
        }

        TwoFactorAuthenticationSetting currentSetting = twoFactorAuthenticationSettingRepository.findById(accountId);

        TwoFactorAuthenticationSetting newSetting = currentSetting.enable(secretKey);

        twoFactorAuthenticationSettingRepository.add(newSetting);
        SessionUtil.delete(context, WAITING_2FA_ACTIVATION);
    }

    public void disable2FA(AccountId accountId) {
        TwoFactorAuthenticationSetting currentSetting = twoFactorAuthenticationSettingRepository.findById(accountId);

        TwoFactorAuthenticationSetting newSetting = currentSetting.disable();

        twoFactorAuthenticationSettingRepository.add(newSetting);
    }
}
