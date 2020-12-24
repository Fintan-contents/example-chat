import {RestClient} from './RestClient';

describe('rest client', () => {

  const mockFetch = jest.spyOn(window, 'fetch');

  beforeEach(() => {
  });

  afterEach(() => {
    mockFetch.mockReset();
  });

  test('レスポンスのステータスコードが200ならリトライしない', () => {
    mockFetch.mockImplementation(() => Promise.resolve({status: 200} as Response));
    const sut = new RestClient();

    const promise = sut.get('api call');

    return promise.then(response => {
      expect(response.status).toBe(200);
      expect(mockFetch.mock.calls.length).toBe(1);
    });
  });

  test('レスポンスのステータスコードが500ならリトライする', () => {
    mockFetch.mockImplementationOnce(() => Promise.resolve({status: 500} as Response));
    mockFetch.mockImplementation(() => Promise.resolve({status: 200} as Response));
    const sut = new RestClient();

    const promise = sut.get('api call');

    return promise.then(response => {
      expect(response.status).toBe(200);
      expect(mockFetch.mock.calls.length).toBe(2);
    });
  });

  test('レスポンスのステータスコードが503ならリトライする', () => {
    mockFetch.mockImplementationOnce(() => Promise.resolve({status: 503} as Response));
    mockFetch.mockImplementation(() => Promise.resolve({status: 200} as Response));
    const sut = new RestClient();

    const promise = sut.get('api call');

    return promise.then(response => {
      expect(response.status).toBe(200);
      expect(mockFetch.mock.calls.length).toBe(2);
    });
  });

  test('レスポンスのステータスコードが501ならリトライしない', () => {
    mockFetch.mockImplementationOnce(() => Promise.resolve({status: 501} as Response));
    const sut = new RestClient();

    const promise = sut.get('api call');

    return promise.catch(response => {
      expect(response.status).toBe(501);
    });
  });

  test('リトライが5回を超えたらエラーレスポンスをそのまま返す', async () => {
    mockFetch.mockImplementation(() => Promise.resolve({status: 500} as Response));
    const sut = new RestClient();

    const promise = sut.get('api call');

    return promise.catch(response => {
      expect(response.status).toBe(500);
      expect(mockFetch.mock.calls.length).toBe(6);
    });
  });

});