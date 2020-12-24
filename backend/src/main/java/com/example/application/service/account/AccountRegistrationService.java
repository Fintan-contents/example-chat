package com.example.application.service.account;

import com.example.application.*;
import com.example.application.service.channel.ChannelMemberRegistrationService;
import com.example.domain.model.notification.Mailer;
import com.example.domain.model.notification.SignupVerificationMail;
import com.example.domain.repository.AccountPasswordRepository;
import com.example.domain.repository.AccountRepository;
import com.example.domain.repository.TemporaryAccountRepository;
import com.example.domain.repository.TwoFactorAuthenticationSettingRepository;
import com.example.application.service.channel.ChannelRegistrationService;
import com.example.domain.model.account.*;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class AccountRegistrationService {

    private final TemporaryAccountRepository temporaryAccountRepository;

    private final AccountRepository accountRepository;

    private final AccountPasswordRepository passwordRepository;

    private final TwoFactorAuthenticationSettingRepository twoFactorAuthenticationSettingRepository;

    private final Mailer mailer;

    private final TemporaryUserTokenProvider userTokenProvider;

    private final AccountIdProvider idProvider;

    private final ChannelRegistrationService channelRegistrationService;

    private final ChannelMemberRegistrationService channelMemberRegistrationService;

    private final PasswordHashingProcessor passwordHashingProcessor;

    private final ApplicationConfig applicationConfig;

    public AccountRegistrationService(TemporaryAccountRepository temporaryAccountRepository,
                                      AccountRepository accountRepository, AccountPasswordRepository passwordRepository,
                                      TwoFactorAuthenticationSettingRepository twoFactorAuthenticationSettingRepository,
                                      Mailer mailer, TemporaryUserTokenProvider userTokenProvider,
                                      AccountIdProvider idProvider, ChannelRegistrationService channelRegistrationService,
                                      ChannelMemberRegistrationService channelMemberRegistrationService,
                                      PasswordHashingProcessor passwordHashingProcessor,
                                      ApplicationConfig applicationConfig) {
        this.temporaryAccountRepository = temporaryAccountRepository;
        this.accountRepository = accountRepository;
        this.passwordRepository = passwordRepository;
        this.twoFactorAuthenticationSettingRepository = twoFactorAuthenticationSettingRepository;
        this.mailer = mailer;
        this.userTokenProvider = userTokenProvider;
        this.idProvider = idProvider;
        this.channelRegistrationService = channelRegistrationService;
        this.channelMemberRegistrationService = channelMemberRegistrationService;
        this.passwordHashingProcessor = passwordHashingProcessor;
        this.applicationConfig = applicationConfig;
    }

    public void registerTemporaryAccount(UserName userName, MailAddress mailAddress, RawPassword rawPassword) {
        if (accountRepository.existsBy(userName)) {
            throw new UserNameConflictException();
        }
        if (accountRepository.existsBy(mailAddress)) {
            // エラーにすると登録済みメールアドレスの探索が可能になるため、メールを送信せずに正常終了にする
            return;
        }
        TemporaryUserToken userToken = userTokenProvider.generate();
        HashedPassword password = passwordHashingProcessor.hash(rawPassword);
        TemporaryAccount temporaryAccount = new TemporaryAccount(userToken, userName, mailAddress, password);

        temporaryAccountRepository.add(temporaryAccount);

        SignupVerificationMail mail = new SignupVerificationMail(mailAddress, userToken, applicationConfig.applicationExternalUrl());
        mail.send(mailer);
    }

    public void verifyAccount(TemporaryUserToken userToken) {
        TemporaryAccount temporaryAccount = temporaryAccountRepository.findBy(userToken);
        if (temporaryAccount == null) {
            throw new InvalidTokenException();
        }
        if (accountRepository.existsBy(temporaryAccount.userName())) {
            throw new UserNameConflictException();
        }

        AccountId id = idProvider.generate();
        Account account = new Account(id, temporaryAccount.userName(), temporaryAccount.mailAddress());

        accountRepository.add(account);

        AccountPassword accountPassword = new AccountPassword(id, temporaryAccount.password());
        passwordRepository.add(accountPassword);

        TwoFactorAuthenticationSetting setting = TwoFactorAuthenticationSetting.createDefault(account.accountId());
        twoFactorAuthenticationSettingRepository.add(setting);

        temporaryAccountRepository.remove(userToken);

        channelRegistrationService.registerChatBot(id);
    }

    public void deleteAccount(AccountId accountId, RawPassword rawPassword) {
        AccountPassword accountPassword = passwordRepository.findById(accountId);
        if (!passwordHashingProcessor.matches(accountPassword.password(), rawPassword)) {
            throw new AuthenticationException();
        }

        channelRegistrationService.deleteChatBot(accountId);
        channelRegistrationService.deleteOwnersChannel(accountId);
        channelMemberRegistrationService.deleteMembersChannel(accountId);
        twoFactorAuthenticationSettingRepository.remove(accountId);
        passwordRepository.remove(accountId);
        accountRepository.remove(accountId);
    }

    public void changePassword(AccountId accountId, RawPassword password, RawPassword newPassword) {
        AccountPassword accountPassword = passwordRepository.findById(accountId);
        if (!passwordHashingProcessor.matches(accountPassword.password(), password)) {
            throw new AuthenticationException();
        }

        AccountPassword changedAccountPassword = accountPassword.change(passwordHashingProcessor.hash(newPassword));

        passwordRepository.update(changedAccountPassword);
    }
}
