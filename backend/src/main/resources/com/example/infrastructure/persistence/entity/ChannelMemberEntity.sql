SELECT_BY_CHANNEL_ID = 
select 
  *
FROM
  channel_member
WHERE
  channel_id = :channelId

SELECT_FOR_EXISTS =
select
  *
FROM
  channel_member
WHERE
  channel_id = :channelId
  AND account_id = :accountId