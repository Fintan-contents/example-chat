SELECT_BY_ACCOUNT_IDS =
select
    *
FROM
    chat_bot
WHERE
    account_id IN (:accountIds[])

SELECT_BY_ACCOUNT_ID =
select
    *
FROM
    chat_bot
WHERE
    account_id = :accountId