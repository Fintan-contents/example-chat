# フロントエンド

## ディレクトリ設計

機能ごとにグループ化し、その下でファイルタイプ別にグループ化します。

本アプリケーションにおいて機能別のディレクトリは`chat`と`framework`があります。

- `chat` ... チャットに関する部品を格納
- `framework` ... Fetch APIのラッパーやバリデーション、ロギングといった機能を横断して共通的に使用できる部品を格納

ファイルタイプ別のディレクトリは主に次の6つがあります。

- `context` ... コンテキスト(コンポーネントをまたいで使用する値)を格納。コンテキストについては [https://ja.reactjs.org/docs/context.html](https://ja.reactjs.org/docs/context.html) を参照してください
- `backend` ... REST APIやWebSocketなどのバックエンドと通信する処理を格納
- `components` ... Reactのコンポーネント。このディレクトリの下は更に細分化されます。詳細はコンポーネント設計のセクションを参照してください
- `hooks` ... 独自フックを格納。フックについては [https://ja.reactjs.org/docs/hooks-intro.html](https://ja.reactjs.org/docs/hooks-intro.html) を参照してください
- `logging` ... 開発時に使用するロガーを格納
- `validation` ... バリデーションの部品を格納

ディレクトリ設計は [https://ja.reactjs.org/docs/faq-structure.html](https://ja.reactjs.org/docs/faq-structure.html) も参考にするとよいでしょう。

## コンポーネント設計

フロントエンドの文脈におけるコンポーネントとは、Reactを利用して作られる画面全体あるいは画面を構成する部品のことを指します。

ディレクトリ設計のセクションでコンポーネントは`components`ディレクトリに格納すると述べました。
`components`ディレクトリの下は次のようにディレクトリを分けます。

|分類|概要|例|
|---|---|---|
|`basics`|これ以上は分割できない最小のコンポーネントで、単体では意味をなさない。他の`basics`を組み合わせてはいけない|ボタン、テキスト入力|
|`parts`|ある程度まとまった機能を持ったコンポーネントで、(主に)`basics`を組み合わせて作成する。`parts`を組み合わせてもよい|メッセージ入力フォーム|
|`pages`|1つの画面を表すコンポーネントで、`basic`や`parts`を組み合わせて作成する。他の`pages`を組み合わせてはいけない|ログイン画面、チャット画面|

なお、React Routerによるルーティングをまとめたコンポーネントやコンテキストを提供するためのコンポーネントのように画面(UI)の構築に直接関わらないものは`basics`、`parts`、`pages`のいずれにも該当しないと捉え、`components`ディレクトリ直下に格納します。

## CSSの適用

スタイルを適用するセレクタは影響範囲をコンポーネントに限定するため、`コンポーネント名 + "_"`をprefixとして用いたクラスセレクタを使用します。

ただしルートコンポーネントである`App.tsx`に対応する`App.css`では、アプリケーション全体の調整をするためクラスセレクタ以外のセレクタを用いてスタイルを設定してもよいです。

CSSファイルはコンポーネント単位で作成し、コンポーネントと同じディレクトリへ配置します。

