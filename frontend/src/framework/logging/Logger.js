/**
 * console.logを直接使用すると本番運用でも出力されてしまうためconsole.logをラッピングした関数を作成する。
 * それにより、開発ログは開発環境でのみ出力されるようにする。
 *
 * コード例：
 *   import { Logger } from 'framework';
 *   Logger.debug('Hello, log example!');
 */
if (process.env.NODE_ENV === 'production') {
  module.exports = require('./Logger.prod.ts');
} else {
  module.exports = require('./Logger.dev.ts');
}
