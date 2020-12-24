package com.example.application.service.account;

import com.example.application.ApplicationConfig;
import com.example.application.InvalidTokenException;
import com.example.domain.model.notification.Mailer;
import com.example.domain.model.notification.PasswordResetMail;
import com.example.domain.repository.AccountPasswordRepository;
import com.example.domain.repository.AccountRepository;
import com.example.domain.repository.PasswordResettableAccountRepository;
import com.example.domain.model.account.*;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class PasswordResetService {

    private final TemporaryUserTokenProvider userTokenProvider;

    private final Mailer mailer;

    private final AccountRepository accountRepository;

    private final AccountPasswordRepository passwordRepository;

    private final PasswordResettableAccountRepository resettableAccountRepository;

    private final PasswordHashingProcessor passwordHashingProcessor;

    private final ApplicationConfig applicationConfig;

    public PasswordResetService(TemporaryUserTokenProvider userTokenProvider,
                                Mailer mailer, AccountRepository accountRepository,
                                AccountPasswordRepository passwordRepository,
                                PasswordResettableAccountRepository resettableAccountRepository,
                                PasswordHashingProcessor passwordHashingProcessor, ApplicationConfig applicationConfig) {
        this.userTokenProvider = userTokenProvider;
        this.mailer = mailer;
        this.accountRepository = accountRepository;
        this.passwordRepository = passwordRepository;
        this.resettableAccountRepository = resettableAccountRepository;
        this.passwordHashingProcessor = passwordHashingProcessor;
        this.applicationConfig = applicationConfig;
    }

    public void issueToken(MailAddress mailAddress) {
        Account account = accountRepository.findBy(mailAddress);
        if (account == null) {
            // 有効なアカウントの発見に繋げないため、実際に登録されていなくてもエラーにはしない
            return;
        }
        TemporaryUserToken userToken = userTokenProvider.generate();
        PasswordResettableAccount resettableAccount = new PasswordResettableAccount(userToken, account);
        resettableAccountRepository.add(resettableAccount);

        PasswordResetMail mail = new PasswordResetMail(mailAddress, resettableAccount.userToken(), applicationConfig.applicationExternalUrl());
        mail.send(mailer);
    }

    public void verifyToken(TemporaryUserToken userToken) {
        PasswordResettableAccount resettableAccount = resettableAccountRepository.findBy(userToken);
        if (resettableAccount == null) {
            throw new InvalidTokenException();
        }
    }

    public void resetPassword(TemporaryUserToken userToken, RawPassword newPassword) {
        PasswordResettableAccount resettableAccount = resettableAccountRepository.findBy(userToken);
        if (resettableAccount == null) {
            throw new InvalidTokenException();
        }

        AccountPassword accountPassword = passwordRepository.findById(resettableAccount.account().accountId());
        AccountPassword changedAccountPassword = accountPassword.change(passwordHashingProcessor.hash(newPassword));
        passwordRepository.update(changedAccountPassword);

        resettableAccountRepository.remove(userToken);
    }
}
