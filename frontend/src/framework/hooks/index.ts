import {useContext, useEffect, useState, useCallback} from 'react';
import SystemMessagesContext from 'framework/context/SystemMessagesContext';
import {Logger} from 'framework/logging';

/**
 * フォームについては、Reactの制御されたコンポーネント(https://ja.reactjs.org/docs/forms.html#controlled-components)となるように実装する。
 * コンポーネントは関数コンポーネントとして実装する方針であるため、ステートフックであるuseState（https://ja.reactjs.org/docs/hooks-state.html）を使用して、入力値を保持する。
 * 入力コンポーネントの実装では、input要素に渡す属性や関数等、同様の実装をすることが多くなる。
 * そのため、ステートフックとステート更新をラッピングした独自フックとしてuseInput（その他、select要素に対応したuseSelect等）を作成し、各入力コンポーネントの実装コストを下げる。
 */

/**
 * input要素のステートフックとステート更新をラッピングした独自フック。
 * 使用例は以下の通り。
 *
 * const Xxx: React.FC = () => {
 *  // 入力値のステートと、入力要素用の属性を取得する
 *  const [text, textAttributes] = useInput('');
 *  return (
 *    // スプレッド構文で展開して属性を一括設定する
 *    <input type='text' {...textAttributes}/>
 *  );
 *
 * @param initialState 初期値
 * @return [input要素のステート, input要素の属性, ステート更新の関数]
 */
export const useInput = (initialState: string = '') : [string, React.InputHTMLAttributes<HTMLInputElement>, React.Dispatch<React.SetStateAction<string>>] => {
  const [value, setValue] = useState<string>(initialState);

  const onChange = (event: React.FormEvent<HTMLInputElement>) => {
    setValue(event.currentTarget.value);
  };

  return [
    value,
    {
      value,
      onChange
    },
    setValue
  ];
};

/**
 * input[type=checkbox]要素のステートフックとステート更新をラッピングした独自フック。
 * 単一のチェックボックスの場合に使用する。
 *
 * @param value チェックボックスのvalue属性
 * @param initialChecked 初期状態でチェックをつけるかどうか {true/false}
 * @return [チェックしている値, チェックボックス要素の属性]
 */
export const useCheckbox = (value: string, initialChecked: boolean = false) : [string, React.InputHTMLAttributes<HTMLInputElement>] => {
  const [checked, setChecked] = useState<boolean>(initialChecked);
  const [checkedValue, setCheckedValue] = useState<string>(initialChecked ? value : '');

  const onChange = (event: React.FormEvent<HTMLInputElement>) => {
    setChecked(event.currentTarget.checked);
    if (event.currentTarget.checked) {
      setCheckedValue(value);
    } else {
      setCheckedValue('');
    }
  };

  return [
    checkedValue,
    {
      value,
      checked,
      onChange
    }
  ];
};

/**
 * input[type=checkbox]要素のステートフックとステート更新をラッピングした独自フック。
 * 複数のチェックボックスの場合に使用する。
 * 使用例は以下の通り。
 *
 * const [choices, checkedValues, attributes] = useCheckboxes(['a', 'b', 'c'], ['a']);
 *  〜〜（中略）〜〜
 * {choices.map((choice, index) => (
 *   <label key={index}>
 *     <input type="checkbox" {...attributes(choice)}/>
 *     <span>{choice}</span>
 *   </label>
 * ))}
 *
 * @param choices チェックボックスの選択肢
 * @param initialChecked 初期状態でチェックをつける選択肢
 * @return [チェックボックスの選択肢, チェックしている値, チェックボックスの属性を返す関数（選択肢の値が引数)]
 */
export const useCheckboxes = (choices: string[], initialChecked: string[] = []) : [string[], string[], (value: string) => React.InputHTMLAttributes<HTMLInputElement>] => {
  const [checkedValues, setCheckedValues] = useState<string[]>(initialChecked.filter(v => choices.includes(v)));
  initialChecked.forEach(value => {
    if(!choices.includes(value)){
      Logger.debug('checkbox initialChecked(' + value + ') is not includes choices.');
    }
  });

  const onChange = (event: React.FormEvent<HTMLInputElement>) => {
    const currentTarget = event.currentTarget;
    if (currentTarget.checked) {
      if (!checkedValues.includes(currentTarget.value)) {
        setCheckedValues([...checkedValues, currentTarget.value]);
      }
    } else {
      setCheckedValues(checkedValues.filter(v => v !== currentTarget.value));
    }
  };
  const attributes = (value: string) => {
    const checked = checkedValues.includes(value);
    return {value, onChange, checked};
  };

  return [
    choices,
    checkedValues,
    attributes,
  ];
};

/**
 * input[type=radio]要素のステートフックとステート更新をラッピングした独自フック。
 * 使用例は以下の通り。
 *
 * const [choices, checkedValue, attributes] = useRadio(['a', 'b'], 'a');
 *  〜〜（中略）〜〜
 * {choices.map((choice, index) => (
 *   <label key={index}>
 *     <input type="radio" {...attributes(choice)}/>
 *     <span>{choice}</span>
 *   </label>
 * ))}
 *
 * @param choices ラジオボタンの選択肢の値
 * @param initialChecked 初期状態でチェックをつける値
 * @return [ラジオボタンの選択肢, チェックしている値, ラジオボタンの属性を返す関数（選択肢の値が引数）]
 */
export const useRadio = (choices: string[], initialChecked: string = '') : [string[], string, (value: string) => React.InputHTMLAttributes<HTMLInputElement> ] => {
  const [checkedValue, setCheckedValue] = useState<string>(choices.includes(initialChecked) ? initialChecked : '');
  if(initialChecked && !choices.includes(initialChecked)){
    Logger.debug('radio initialChecked(' + initialChecked + ') is not includes choices.');
  }

  const onChange = (event: React.FormEvent<HTMLInputElement>) => {
    setCheckedValue(event.currentTarget.value);
  };
  // ランダムなname属性を生成する
  const [name] = useState(() => 'radio_' + new Date().getTime().toString(16) + Math.floor(10000 * Math.random()).toString(16));
  const attributes = (value: string) => {
    const checked = value === checkedValue;
    return {name, value, onChange, checked};
  };

  return [
    choices,
    checkedValue,
    attributes,
  ];
};

/**
 * textarea要素のステートフックとステート更新をラッピングした独自フック。
 *
 * @param initialState 初期値
 * @return [textareaのステート, textareaの属性, ステート更新の関数]
 */
export const useTextarea = (initialState: string = '') : [string, React.TextareaHTMLAttributes<HTMLTextAreaElement>, React.Dispatch<React.SetStateAction<string>>] => {
  const [value, setValue] = useState<string>(initialState);

  const onChange = (event: React.FormEvent<HTMLTextAreaElement>) => {
    setValue(event.currentTarget.value);
  };

  return [
    value,
    {
      value,
      onChange
    },
    setValue
  ];
};

/**
 * select要素のステートフックとステート更新をラッピングした独自フック。
 *
 * @param initialState 初期値
 * @return [selectのステート, selectの属性]
 */
export const useSelect = (initialState: string = '') : [string, React.SelectHTMLAttributes<HTMLSelectElement> ] => {
  const [value, setValue] = useState<string>(initialState);

  const onChange = (event: React.FormEvent<HTMLSelectElement>) => {
    setValue(event.currentTarget.value);
  };

  return [
    value,
    {
      value,
      onChange
    }
  ];
};

/**
 * select(multiple)要素のステートフックとステート更新をラッピングした独自フック。
 *
 * @param initialState 初期値
 * @return [select(multiple)のステート, select(multiple)の属性]
 */
export const useSelectMultiple = (initialState: string[] = []) : [string[], React.SelectHTMLAttributes<HTMLSelectElement> ] => {
  const [value, setValue] = useState<string[]>(initialState);

  const onChange = (event: React.FormEvent<HTMLSelectElement>) => {
    const options = event.currentTarget.options;

    const selectedValues = [];
    for (let i = 0; i < options.length; i++) {
      if (options[i].selected) {
        selectedValues.push(options[i].value);
      }
    }
    setValue(selectedValues);
  };

  return [
    value,
    {
      value,
      onChange,
      'multiple': true
    }
  ];
};

/**
 * エラーメッセージに、画面のラベルを表示するSystemMessageコンポーネントを使用してもよいが、
 * エラーの種類によっては1つの場所に異なるエラーメッセージを表示できるようにしたい場合がある。
 * その場合は、useSystemMessagesフックを使用する。
 *
 * useSystemMessagesフックは、メッセージIDを引数にし、メッセージを戻り値としたファンクションを返す。
 * メッセージに文字列を埋め込む場合は、ファンクションの第二引数（可変長引数）に埋め込む文字列を指定する。
 * 指定した順番に文字列が埋め込まれる。
 *
 * messages.properties例：
 *   errors.email=メールアドレスの形式が正しくありません。
 *   errors.required={0}を入力してください。
 *   errors.length.min={0}は{1}文字以上の値を入力してください。
 *
 * コード例：
 *   const systemMessages = useSystemMessages();
 *   const emailError = systemMessages('errors.email'); // メールアドレスの形式が正しくありません。
 *   const requiredError = systemMessages('errors.required', 'パスワード'); // パスワードを入力してください。
 *   const lengthError = systemMessages('errors.length.min', 'パスワード', '8'); // パスワードは8文字以上の値を入力してください。
 */
export function useSystemMessages(): (key: string, ...options: string[]) => string {
  const [systemMessages] = useContext(SystemMessagesContext);

  return useCallback((key: string, ...options: string[]): string => {
    if (!options || options.length === 0){
      return systemMessages[key];
    } else {
      const formatMessage = options.reduce((message, option, index) => {
        return message && message.replace('{' + index + '}', option);
      }, systemMessages[key]);
      return formatMessage;
    }
  }, [systemMessages]);
}

/**
 * document.titleを設定するフック。
 * 各画面で呼び出すことで、タイトルを変更することができる。
 */
export function usePageTitle(title?: string): void {
  useEffect(() => {
    if (title) {
      const previousTitle = document.title;
      document.title = 'チャットExample | ' + title;
      return () => {
        document.title = previousTitle;
      };
    }
  }, [title]);
}

/**
 * ファイルのダウンロードは次の手順にしたがって行う。
 *
 *   1. レスポンスボディをBlobオブジェクトへ変換する
 *   2. URL.createObjectURL(blob)でURLを生成する
 *   3. a要素を動的に生成しhref属性に生成したURL、download属性にファイル名を設定する
 *   4. JavaScriptでa要素のclick()を実行する
 *   5. 生成したa要素とURLを破棄する（メモリリークの回避）
 *
 * 各手順の実装例を以下に示す。
 *   1. Fetch API(https://developer.mozilla.org/ja/docs/Web/API/Fetch_API)でファイルデータを取得し、
 *      ResponseのblobメソッドでBlobオブジェクトを得る。
 *      実装イメージ：
 *        const downloadMessageHistory = async (channelId: string, fileKey: string) => {
 *          const response = await restClient.get(`/api/channels/${channelId}/history/archive/${fileKey}`);
 *          return await response.blob();
 *        };
 *
 *   2~5. URL.createObjectURL(blob)でURLを生成して、動的に生成した a要素と組み合わせてダウンロードを行うが、
 *        これらの処理はボイラープレートなプログラムになるため useDownloaderフックにまとめた。
 *        開発者は useDownloader() から得られる関数にblobとファイル名を渡せばよい。
 *        実装イメージ：
 *          const download = useDownloader();
 *          const blob = await BackendService.downloadMessageHistory(channelId, fileKey);
 *          const filename = `${channel.name}-history-archive.csv`;
 *          download(blob, filename);
 */
export function useDownloader(): (blob: Blob, filename: string) => void {
  const download = (blob: Blob, filename: string) => {
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = filename;
    document.body.appendChild(anchor);
    anchor.click();
    URL.revokeObjectURL(url);
    document.body.removeChild(anchor);
  };
  return download;
}

