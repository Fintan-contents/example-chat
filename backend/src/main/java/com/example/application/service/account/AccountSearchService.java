package com.example.application.service.account;

import com.example.domain.repository.AccountRepository;
import com.example.application.service.channel.InvitableAccountQuery;
import com.example.domain.model.account.Account;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.account.Accounts;
import com.example.domain.model.channel.ChannelId;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class AccountSearchService {

    private final AccountRepository accountRepository;

    private final InvitableAccountQuery invitableAccountQuery;

    public AccountSearchService(AccountRepository accountRepository, InvitableAccountQuery invitableAccountQuery) {
        this.accountRepository = accountRepository;
        this.invitableAccountQuery = invitableAccountQuery;
    }

    public Account findById(AccountId accountId) {
        return accountRepository.findById(accountId);
    }

    public Accounts findBy(AccountId... accountIds) {
        return accountRepository.findBy(accountIds);
    }

    public Accounts findInvitableAccounts(ChannelId channelId) {
        return invitableAccountQuery.findBy(channelId);
    }
}
