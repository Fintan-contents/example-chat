SELECT_BY_MAIL_ADDRESS =
select
  *
FROM
  account
WHERE
  mail_address = :mailAddress

SELECT_INVITE_ACCOUNT =
SELECT
  *
FROM
  account
WHERE
  account_id not in (:memberIds[])

SELECT_BY_ACCOUNT_IDS =
SELECT
  *
FROM
  account
WHERE
  account_id in (:accountIds[])

SELECT_FOR_EXISTS =
select
  *
FROM
  account
WHERE
  user_name = :userName