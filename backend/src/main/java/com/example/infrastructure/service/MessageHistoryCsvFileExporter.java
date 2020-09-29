package com.example.infrastructure.service;

import com.example.domain.model.message.MessageHistoryFile;
import com.example.infrastructure.persistence.entity.MessageHistoryDto;
import com.example.domain.model.channel.ChannelId;
import com.example.application.service.message.MessageHistoryFileExporter;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import nablarch.common.dao.DeferredEntityList;
import nablarch.common.dao.UniversalDao;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

@SystemRepositoryComponent
public class MessageHistoryCsvFileExporter implements MessageHistoryFileExporter {

    @Override
    public MessageHistoryFile export(ChannelId channelId) {
        // メッセージ履歴が増えるとメモリが不足してしまう可能性があるため、データベースの検索結果を分割して取得して処理を行い、
        // 一時ファイルに書き出してからその一時ファイルをダウンロードさせることとする。
        try (DeferredEntityList<MessageHistoryDto> entityList = (DeferredEntityList<MessageHistoryDto>) UniversalDao
                .defer()
                .findAllBySqlFile(MessageHistoryDto.class, "ALL_MESSAGE", Map.of("channelId", channelId.value()))) {

            Path csvFilePath = Files.createTempFile(null, ".csv");
            try (BufferedWriter csvFileWriter = Files.newBufferedWriter(csvFilePath, StandardOpenOption.WRITE)) {

                CsvMapper mapper = new CsvMapper();
                CsvSchema schema = mapper.schemaFor(MessageHistoryDto.class).withHeader();
                mapper.writer(schema).writeValues(csvFileWriter).writeAll(entityList);
            }

            return new MessageHistoryFile(csvFilePath);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
