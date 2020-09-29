SELECT_READ_MESSAGE =
select
  *
FROM
  read_message
WHERE
  channel_id = :channelId
  AND account_id = :accountId

SELECT_BY_CHANNEL_ID =
select
    *
FROM
    read_message
WHERE
    channel_id = :channelId
