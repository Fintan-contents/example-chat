ALL_MESSAGE =
SELECT
    channel.channel_name,
    account.user_name,
    message.text,
    message.send_date_time
FROM
    message
    INNER JOIN channel
        ON message.channel_id = channel.channel_id
    INNER JOIN account
        ON message.account_id = account.account_id
WHERE
    message.channel_id = :channelId
ORDER BY
    message_id