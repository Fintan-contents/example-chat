package com.example.application.service.message;

import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.message.MessageHistoryFile;

public interface MessageHistoryFileExporter {

    MessageHistoryFile export(ChannelId channelId);
}
