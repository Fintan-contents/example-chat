SELECT_ALL =
select
    channel.channel_id,
    channel.channel_name,
    channel.type
FROM
    channel
INNER JOIN channel_member
    ON channel.channel_id = channel_member.channel_id
WHERE
    channel_member.account_id = :accountId
    AND channel.type = 'CHANNEL'
ORDER BY
    channel.channel_name

SELECT_OWNERS_CHANNEL_ID =
select
    channel.channel_id
FROM
    channel
INNER JOIN channel_owner
    ON channel.channel_id = channel_owner.channel_id
WHERE
    channel_owner.account_id = :accountId

SELECT_FOR_EXISTS_BY_ID =
select
  *
FROM
  channel
WHERE
  channel_id = :channelId

SELECT_FOR_EXISTS_BY_NAME =
select
  *
FROM
  channel
WHERE
  channel_name = :channelName