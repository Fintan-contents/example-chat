import { RestClient, Logger } from 'framework';

const restClient = new RestClient();

function setCsrfTokenHeaderName(csrfTokenHeaderName: string): void {
  restClient.csrfTokenHeaderName = csrfTokenHeaderName;
}

function setCsrfTokenValue(csrfTokenValue: string): void {
  restClient.csrfTokenValue = csrfTokenValue;
}

const getCsrfToken = async () => {
  const response = await restClient.get('/api/csrf_token');
  return await response.json();
};

const registerAccount = async (userName: string, mailAddress: string, password: string) => {
  Logger.debug('call service of registerAccount', mailAddress, password);

  const response = await restClient.post('/api/signup', { userName, mailAddress, password });
  Logger.debug(response);
  if(response.status === 409) {
    return 'conflict';
  }
  return response;
};

const verifyAccount = async (userToken: string) => {
  Logger.debug('call service of verifyAccount', userToken);

  const response = await restClient.post('/api/signup/verify', { userToken });
  Logger.debug(response);
  if(response.status === 409) {
    return 'conflict';
  }
  return response;
};

const login = async (mailAddress: string, password: string) => {
  Logger.debug('call service of login', mailAddress, password);

  const response = await restClient.post('/api/login', { mailAddress, password });
  Logger.debug(response);
  if (response.ok) {
    const json = await response.json();
    return json.status;
  }
  if (response.status === 400 || response.status === 401) {
    // 400は検証エラーなので、フロントでチェックしていれば基本的にはありえないステータスだが、念の為カバーしておく
    return 'Unauthorized';
  }
};

const postMessage = (channelId: number, text: string) => {
  Logger.debug('call service of postMessage', text);

  restClient.post(`/api/channels/${channelId}/messages`, { text })
    .then(response => Logger.debug(response));
};

/**
 * ファイルのアップロードには、Fetch API(https://developer.mozilla.org/ja/docs/Web/API/Fetch_API) と
 * FormData(https://developer.mozilla.org/ja/docs/Web/API/FormData)を使用する。
 *
 * Fetch APIをラップした共通部品（RestClient）のpostメソッドに FormDataのインスタンスを渡すことでマルチパートで送信できる。
 */
const postFile = (channelId: number, file: File) => {
  Logger.debug('call service of postFile', file.name);

  const formData = new FormData();
  formData.append('image', file);

  restClient.post(`/api/channels/${channelId}/files`, formData)
    .then(response => Logger.debug(response));
};

const readMessage = (channelId: number, messageId: number) => {
  Logger.debug('call service of readMessage', channelId, messageId);

  restClient.put(`/api/read/${channelId}`, { messageId })
    .then(response => Logger.debug(response));
};

const findMessage = async (channelId: number, nextMessageId: number | null) => {
  Logger.debug('call service of findMessage', channelId, nextMessageId);

  const path = `/api/channels/${channelId}/messages`;
  const query = `?nextMessageId=${nextMessageId}`;
  const url = path + (nextMessageId ? query : '');

  const response = await restClient.get(url);
  Logger.debug(response);
  return response.json();
};
const findChannel = async (channelId: number) => {
  Logger.debug('call service of findChannel');

  const response = await restClient.get(`/api/channels/${channelId}`);
  Logger.debug(response);
  return response.json();
};
const findAllChannel = async () => {
  Logger.debug('call service of fetchAllChannel');

  const response = await restClient.get('/api/channels');
  Logger.debug(response);
  return response.json();
};
const findAllMembers = async (channelId: number) => {
  Logger.debug('call service of findAllMembers');

  const response = await restClient.get(`/api/channels/${channelId}/invitable_accounts`);
  Logger.debug(response);
  return response.json();
};
const findMembers = async (channelId: number) => {
  Logger.debug('call service of findMembers', channelId);

  const response = await restClient.get(`/api/channels/${channelId}/members`);
  Logger.debug(response);
  return response.json();
};
const postMembers = async (channelId: number, accountId: number) => {
  Logger.debug(`call service of postMembers. channelId: ${channelId}, accountId: ${accountId}`);

  const response = await restClient.post(`/api/channels/${channelId}/members`, { accountId });
  Logger.debug(response);
  return response;
};
const deleteMembers = async (channelId: number) => {
  Logger.debug(`call service of deleteMembers. channelId: ${channelId}`);
  const response = await restClient.delete(`/api/channels/${channelId}/members/me`);
  Logger.debug(response);
  if(response.status === 403) {
    return 'Forbidden';
  }
  return response;
};
const postChannel = async (channelName: string) => {
  Logger.debug('call service of postChannel', channelName);

  const response = await restClient.post('/api/channels', { channelName });
  Logger.debug(response);
  if(response.status === 409) {
    return 'conflict';
  }
  return response;
};

const putChannel = async (channelId: number, channelName: string) => {
  Logger.debug('call service of putChannel', channelName);

  const response = await restClient.put(`/api/channels/${channelId}/settings/name`, { channelName });
  Logger.debug(response);
  if(response.status === 409) {
    return 'conflict';
  }
  return response;
};

const deleteChannel = async (channelId: number) => {
  Logger.debug('call service of deleteChannel', channelId);

  const response = await restClient.delete(`/api/channels/${channelId}`);
  Logger.debug(response);
  if(response.status === 403) {
    return 'Forbidden';
  }
  return response;
};

const get2FASetting = async () => {
  Logger.debug('call service of get2FASetting');

  const response = await restClient.get('/api/settings/2fa');
  Logger.debug(response);
  if (response.ok) {
    const json = await response.json();
    Logger.debug(json);
    return json.status;
  }
};

const get2FASecretString = async () => {
  Logger.debug('call service of get2FASecretString');

  const response = await restClient.post('/api/settings/2fa/enable');
  Logger.debug(response);
  if (response.ok) {
    const json = await response.json();
    return json.secretString;
  }
};

const enable2FASetting = async (code: string) => {
  Logger.debug('call service of enable2FASetting', code);

  const response = await restClient.post('/api/settings/2fa/enable/verify', { code });
  Logger.debug(response);
  if (response.ok) {
    return;
  }
  if (response.status === 400 || response.status === 401 || response.status === 403) {
    return 'Unauthorized';
  }
};

const disable2FASetting = async () => {
  Logger.debug('call service of disable2FASetting');

  const response = await restClient.post('/api/settings/2fa/disable');
  Logger.debug(response);
  if (response.ok) {
    return;
  }
  if (response.status === 400 || response.status === 403) {
    return 'Unauthorized';
  }
};

const verify2FA = async (code: string) => {
  Logger.debug('call service of verify2FA', code);

  const response = await restClient.post('/api/2fa', { code });
  Logger.debug(response);
  if (response.ok) {
    return;
  }
  if (response.status === 400 || response.status === 401 || response.status === 403) {
    return 'Unauthorized';
  }
};

const deleteAccount = async (password: string) => {
  Logger.debug('call service of deleteAccount', password);

  const response = await restClient.post('/api/accounts/me/delete', { password });
  Logger.debug(response);
  if (response.ok) {
    return;
  }
  if (response.status === 400 || response.status === 401 || response.status === 403) {
    return 'Unauthorized';
  }
};

const changePassword = async (password: string, newPassword: string) => {
  Logger.debug('call service of changePassword', password, newPassword);

  const response = await restClient.put('/api/settings/password', { password, newPassword });
  Logger.debug(response);
  if (response.ok) {
    return;
  }
  if (response.status === 400 || response.status === 401 || response.status === 403) {
    return 'Unauthorized';
  }
};

const getSystemMessages = async () => {
  const response = await restClient.get('/api/systeminfo/messages');
  Logger.debug(response);
  if (response.ok) {
    return await response.json();
  }
};

const issuePasswordResetToken = async (mailAddress: string) => {
  Logger.debug('call service of sendPasswordResetLink', mailAddress);

  const response = await restClient.post('/api/reset_password', { mailAddress });
  Logger.debug(response);
  return response;
};

const isValidPasswordResetToken = async (token: string) => {
  Logger.debug('call service of isValidPasswordResetToken', token);

  const response = await restClient.post('/api/reset_password/verify', { token });
  Logger.debug(response);
  if (response.ok) {
    return true;
  }
  if (response.status === 404) {
    return false;
  }
  throw new Error('Unexpected response');
};

const resetPassword = async (token: string, newPassword: string) => {
  Logger.debug('call service of resetPassword', token, newPassword);

  const response = await restClient.post('/api/reset_password/new', { token, newPassword });
  Logger.debug(response);
  return response;
};

const exportMessageHistory = async (channelId: string) => {
  Logger.debug('call service of exportMessageHistory');

  const response = await restClient.post(`/api/channels/${channelId}/history/archive`);
  Logger.debug(response);
  return await response.json();
};

const downloadMessageHistory = async (channelId: string, fileKey: string) => {
  Logger.debug('call service of downloadMessageHistory');

  const response = await restClient.get(`/api/channels/${channelId}/history/archive/${fileKey}`);
  Logger.debug(response);
  return await response.blob();
};

const getUser = async () => {
  Logger.debug('call service of getUser');

  const response = await restClient.get('/api/accounts/me');
  Logger.debug(response);
  if (response.status === 403) {
    return Promise.reject();
  }
  return await response.json();
};

const logout = async () => {
  Logger.debug('call service of logout');

  const response = await restClient.post('/api/logout');
  Logger.debug(response);
  return response;
};

const getNotificationUrl = async () => {
  Logger.debug('call service of getNotificationUrl');

  const response = await restClient.post('/api/systeminfo/notification');
  Logger.debug(response);
  return await response.json();
};

export default {
  registerAccount,
  verifyAccount,
  login,
  postMessage,
  postFile,
  readMessage,
  findMessage,
  findChannel,
  findAllChannel,
  findAllMembers,
  findMembers,
  postMembers,
  deleteMembers,
  postChannel,
  putChannel,
  deleteChannel,
  get2FASetting,
  get2FASecretString,
  enable2FASetting,
  disable2FASetting,
  verify2FA,
  deleteAccount,
  changePassword,
  getSystemMessages,
  issuePasswordResetToken,
  isValidPasswordResetToken,
  resetPassword,
  exportMessageHistory,
  downloadMessageHistory,
  getUser,
  logout,
  getNotificationUrl,
  setCsrfTokenHeaderName,
  setCsrfTokenValue,
  getCsrfToken,
};
