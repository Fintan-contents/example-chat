import { Logger } from 'framework';

const baseUrl: string = process.env.REACT_APP_BACKEND_BASE_URL!.toString();

type Serializer = (body: any) => string;

type ErrorHandler = (error: Error) => Promise<Response>;

const defaultSerializer: Serializer = body => JSON.stringify(body);

const defaultErrorHandler: ErrorHandler = error => {
  console.error(error);
  return Promise.reject(error);
};

/**
 * Fetch APIへ設定するパラメーターは以下の通り。
 * CORSを使用した通信を行うため mode には cors を設定する。
 * また、Cookieを送信するため credentials には include を設定する。
 * いずれも個々の処理で設定するのではなく、共通部品で設定する。
 * 参考情報: WindowOrWorkerGlobalScope.fetch()（https://developer.mozilla.org/ja/docs/Web/API/WindowOrWorkerGlobalScope/fetch）
 */
const defaultInit: RequestInit = {
  headers: {
    'content-type': 'application/json',
    'Accept': 'application/json',
  },
  redirect: 'manual',
  mode: 'cors',
  credentials: 'include',
};

function defaultCalculateInterval(executionCount: number): number {
  // 切り捨て指数型バックオフでリトライのインターバルを算出する
  // https://cloud.google.com/storage/docs/exponential-backoff?hl=ja
  const maxBackoff = 32000; //32秒
  return Math.min(Math.floor((Math.pow(2, executionCount) + Math.random()) * 1000), maxBackoff);
}

const updatingMethod = ['POST', 'PUT', 'DELETE', 'PATCH'];

/**
 * REST APIとの通信には[Fetch API](https://developer.mozilla.org/ja/docs/Web/API/Fetch_API)を用いる。
 * 一部のリクエストヘッダー(Content-Type、Accept、CSRFトークン)やリクエストボディのシリアライズ方式(JavaScriptのオブジェクトを
 * JSONの文字列へシリアライズする)など、共通化できる要素があるためFetch APIをそのまま使用するのでなくラップした共通部品を用意する。
 *
 * // GETリクエストのコード例
 * restClient.get('/api/channels').then(response => ...);
 *
 * // POSTリクエストのコード例
 * restClient.post('/api/login', { mailAddress, password }).then(response => ...);
 *
 * // PUTリクエストのコード例
 * restClient.put('/api/settings/password', { password, newPassword }).then(response => ...);
 *
 * // DELETEリクエストのコード例
 * restClient.delete(`/api/channels/${channelId}`).then(response => ...);
 *
 * いずれの操作も内部ではFetch APIを使用しておりPromise#Response(https://developer.mozilla.org/ja/docs/Web/API/Response)が返却される。
 */
export class RestClient {

  csrfTokenHeaderName: string = '';
  csrfTokenValue: string = '';

  constructor(
    private init = defaultInit,
    private serializer = defaultSerializer,
    private errorHandler = defaultErrorHandler,
    private maxRetries = 5,
    private calculateInterval = defaultCalculateInterval) {
  }

  private doFetch(input: string, method: string, body?: any): Promise<Response> {
    const init: RequestInit = { ...this.init, method };
    init.headers = { ...init.headers };
    if (body) {
      if (body instanceof FormData) {
        init.body = body;
        delete (init.headers as any)['content-type'];
      } else {
        init.body = this.serializer(body);
      }
    } else {
      delete (init.headers as any)['content-type'];
    }
    if (updatingMethod.includes(method) && this.csrfTokenHeaderName && this.csrfTokenValue) {
      init.headers = { ...init.headers, [this.csrfTokenHeaderName]: this.csrfTokenValue };
    }
    return this.fetchWithRetry(baseUrl + input, init, 0)
      .then(response => {
        if (500 <= response.status && response.status <= 599) {
          return Promise.reject(response);
        }
        return response;
      })
      .catch(error => {
        return this.errorHandler(error);
      });
  }

  private async fetchWithRetry(input: string, init: RequestInit, executionCount: number): Promise<Response> {
    const response = await fetch(input, init);
    if ([500, 503].indexOf(response.status) > -1 && executionCount < this.maxRetries) {
      const retryInterval = this.calculateInterval(executionCount);
      Logger.debug(`Retry: executionCount = ${executionCount}, interval = ${retryInterval}`);
      return new Promise(resolve => {
        setTimeout(() => {
          const response = this.fetchWithRetry(input, init, executionCount + 1);
          resolve(response);
        }, retryInterval);
      });
    }
    return response;
  }

  get(input: string): Promise<Response> {
    return this.doFetch(input, 'GET');
  }

  post(input: string, body?: any): Promise<Response> {
    return this.doFetch(input, 'POST', body);
  }

  put(input: string, body: any): Promise<Response> {
    return this.doFetch(input, 'PUT', body);
  }

  delete(input: string, body?: any): Promise<Response> {
    return this.doFetch(input, 'DELETE', body);
  }
}

