package com.example.system.nablarch.di;

import com.example.application.event.*;
import nablarch.core.repository.SystemRepository;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

public class EventListenerRegister implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        EventPublisher publisher = SystemRepository.get(EventPublisher.class.getName());

        List<? extends EventListener<?>> listeners = List.of(
                SystemRepository.get(ChatBotCreateMessageNotifier.class.getName()),
                SystemRepository.get(ChannelJoinMessageNotifier.class.getName()),
                SystemRepository.get(ChannelInviteMessageNotifier.class.getName()),
                SystemRepository.get(ChannelInviteDataNotifier.class.getName()),
                SystemRepository.get(ChannelDeleteMessageNotifier.class.getName()),
                SystemRepository.get(ChannelDeleteDataNotifier.class.getName()),
                SystemRepository.get(ChannelNameChangeMessageNotifier.class.getName()),
                SystemRepository.get(ChannelNameChangeDataNotifier.class.getName()));

        for (EventListener<?> listener : listeners) {
            publisher.addEventListener(listener);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
