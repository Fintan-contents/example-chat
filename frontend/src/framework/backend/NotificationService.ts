import { Logger } from 'framework';

class Notifier {

  private listeners: ((type: string, payload: any) => void)[] = [];

  subscribe(type: string, listener: (payload: any) => void): () => void {
    const wrapper = (t: string, p: string) => {
      if (t === type) {
        listener(p);
      }     
    };
    this.listeners.push(wrapper);
    return () => {
      const index = this.listeners.indexOf(wrapper);
      if (index > -1) {
        this.listeners.splice(index, 1);
      }
    };
  }

  notify(type: string, payload: any): void {
    this.listeners.forEach(listener => {
      listener(type, payload);
    });
  }
}

class NotificationService {

  private webSocket?: WebSocket;
  private active: boolean = false;
  private pinger?: number;
  private reconnector?: number;
  private notifier: Notifier = new Notifier();

  private heartbeatInSeconds: number = 10;
  private reconnectDelayInSeconds: number = 5;
  private decode: (text: string) => any = JSON.parse;
  private encode: (value: any) => string = JSON.stringify;

  connect(websocketUri: string): void {
    Logger.debug('Connect WebSocket');
    this.active = true;
    this.init(websocketUri);
  }

  private init(websocketUri: string): void {
    const ws = new WebSocket(websocketUri);
    ws.onopen = (event: Event) => {
      this.pinger = this.schedulePing();
    };
    ws.onmessage = (event: MessageEvent) => {
      const { type, payload }: { type: string; payload: any; } = this.decode(event.data);
      this.notifier.notify(type, payload);
    };
    ws.onclose = (event: CloseEvent) => {
      Logger.debug('Handle close WebSocket', event);
      // pingをクリアするためにdestroyを呼び出している
      this.destroy();
      // ネットワークの問題などで切断された場合、active = trueのままなので再接続をスケジュールする
      if (this.active) {
        this.reconnector = this.scheduleReconnect(websocketUri);
      }
    };
    this.webSocket = ws;
  }

  private schedulePing(): number {
    return window.setInterval(() => {
      if (this.webSocket) {
        this.webSocket.send(this.encode({
          type: 'ping'
        }));
      }
    }, this.heartbeatInSeconds * 1000);
  }

  private scheduleReconnect(websocketUri: string): number {
    return window.setTimeout(() => {
      Logger.debug('Reconnect WebSocket');
      this.reconnector = undefined;
      this.init(websocketUri);
    }, this.reconnectDelayInSeconds * 1000);
  }

  disconnect(): void {
    Logger.debug('Disconnect WebSocket');
    this.active = false;
    this.destroy();
  }

  destroy(): void {
    if (this.pinger !== undefined) {
      window.clearInterval(this.pinger);
      this.pinger = undefined;
    }
    if (this.reconnector !== undefined) {
      window.clearTimeout(this.reconnector);
      this.reconnector = undefined;
    }
    if (this.webSocket) {
      const ws = this.webSocket;
      this.webSocket = undefined;
      // ここでクローズするとWebSocket.oncloseが呼び出されてdestroyも再度呼び出されるが、
      // 2度目のdestroyではいずれのifもtrueにならず何も処理されない
      ws.close();
    }
  }

  subscribe(type: string, listener: (payload: string) => void): () => void {
    Logger.debug('Subscribe ', type);
    const unsubscribe = this.notifier.subscribe(type, listener);
    return () => {
      Logger.debug('Unsubscribe ', type);
      unsubscribe();
    };
  }
}

const notificationServiceInstance = new NotificationService();
export default notificationServiceInstance;
