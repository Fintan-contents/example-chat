SELECT_BY_ACCOUNTID =
SELECT
    channel.channel_id AS channel_id,
    channel.channel_name AS channel_name,
    channel.type AS type,
    message.latest_message_id AS latest_message_id,
    read_message.last_read_message_id AS last_read_message_id
FROM
    channel
    INNER JOIN channel_member
        ON channel.channel_id = channel_member.channel_id
    INNER JOIN channel_owner
        ON channel.channel_id = channel_owner.channel_id
    INNER JOIN (
        SELECT channel_id, max(message_id) AS latest_message_id
        FROM message
        GROUP BY channel_id
    ) message
        ON channel.channel_id = message.channel_id
    INNER JOIN read_message
        ON channel.channel_id = read_message.channel_id
        AND channel_member.account_id = read_message.account_id
WHERE
    channel_member.account_id = :accountId
ORDER BY
    channel.channel_name
