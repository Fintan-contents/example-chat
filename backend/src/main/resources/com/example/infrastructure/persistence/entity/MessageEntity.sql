SELECT_BY_CHANNEL_ID =
SELECT
  *
FROM
  message
WHERE
  channel_id = :channelId
ORDER BY
  message_id

SELECT_BY_CHANNEL_ID_AND_ACCOUNT_ID =
SELECT
  *
FROM
  message
WHERE
  channel_id = :channelId
  AND account_id = :accountId
ORDER BY
  message_id

SELECT_BY_CHANNEL_ID_AND_MESSAGE_ID =
SELECT
  *
FROM
  message
WHERE
  channel_id = :channelId
  AND message_id <= :messageId
ORDER BY
  message_id DESC
LIMIT :limit

SELECT_LATEST_MESSAGE_ID =
SELECT
  max(message_id) AS message_id
FROM
  message
WHERE
  channel_id = :channelId
