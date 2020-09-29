package com.example.application.service.message;

import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.message.ImageData;
import com.example.domain.model.message.ImageFile;
import com.example.domain.model.message.ImageKey;
import com.example.domain.model.message.MessageHistoryData;
import com.example.domain.model.message.MessageHistoryFile;
import com.example.domain.model.message.MessageHistoryKey;

public interface PublishableFileStorage {

    ImageKey save(ChannelId channelId, ImageFile imageFile);

    MessageHistoryKey save(ChannelId channelId, MessageHistoryFile messageHistoryFile);

    ImageData findImageData(ChannelId channelId, ImageKey imageKey);

    MessageHistoryData findMessageHistoryData(ChannelId channelId, MessageHistoryKey messageHistoryKey);
}
