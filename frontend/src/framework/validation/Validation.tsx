import React, { useState } from 'react';
import StringFieldConstraint from './field/StringFieldConstraint';
import StringArrayFieldConstraint from './field/StringArrayFieldConstraint';
import NumberFieldConstraint from './field/NumberFieldConstraint';

/**
 * 単項目バリデーションの実装では、必須入力のバリデーション等、同様の実装をすることが多くなる。
 * そのため、制約定義やバリデーション実行、エラー情報のステート操作等をラッピングした独自フックとして useValidation を作成し、単項目バリデーションの実装コストを下げる。
 * 以下のような一般的な制約については、容易に実装できるようにあらかじめ関数を組み込んでおき、個別にバリデーション処理を実装しなくてもよいようにしておく。
 *   - 必須入力
 *   - 文字数
 *   - メールアドレス形式
 * また、一般的な制約以外にも独自の制約を定義できるようにしておくことで、使用場面を限定せずに利用できるようにする。
 *
 * 相関バリデーションについては、一般化が難しく、ほぼ独自の制約を定義することになるため、独自フックではなく、個別にバリデーション処理を実装する。
 * それぞれの項目が正常であった場合に相関バリデーションを行うのがよいため、単項目バリデーションでエラーが発生しなかった場合にコールバックされる関数内にて実装する。
 */
// stringは検証できるが他の型を考慮していないので型制約をつける
type AvailableFiledType = { [key: string]: string | number | string[] };

// IDEで補完が効くように、Genericsで検証対象フィールドを指定させてそれに基づいた型制約をつける
type FieldConstraintSchema<T> = Record<keyof T, Constraint<any>>;
type ValidationError<T> =  Record<keyof T, string>;
type SetValidationError<T> = (name: keyof T, message: string) => void;
type ConstraintValidatorList<T> = { [K in keyof T]: ConstraintValidator<T[K]> }

// 検証するクラスから制約を扱うときのインタフェース
interface Constraint<T> {
  validate(value: T): string | null;
}

// 検証実行用のカスタムフック。検証器と検証結果をもたせる。
// 実行タイミングを制御できるように、一括実行と個別実行をできるようにする。
//
// ReactHookForm、FORMIK、HTML5 ConstraintValidation、を参考にしている
// https://react-hook-form.com/jp/api#useForm
// https://jaredpalmer.com/formik/docs/guides/validation
// https://developer.mozilla.org/ja/docs/Web/Guide/HTML/HTML5/Constraint_validatio
const useValidation = <T extends AvailableFiledType>(schema: FieldConstraintSchema<T>) => {
  const [error, setError] = useState<ValidationError<T>>({} as ValidationError<T>);

  const setValidationError: SetValidationError<T> = (name, message): void => {
    const newError = {} as ValidationError<T>;
    newError[name] = message;
    setError((error) => {
      return {...error, ...newError};
    });
  };
  const resetError = () => {
    setError((error) => {
      return {} as ValidationError<T>;
    });
  };
  const validator = new ConstraintValidators<T>(schema, setValidationError);

  const handleSubmit = (value: T,
    callback: (event: React.FormEvent<HTMLFormElement>) => void,
    onError: (event: React.FormEvent<HTMLFormElement>) => void = () => {}) => {

    return (event: React.FormEvent<HTMLFormElement>) => {
      event.preventDefault();

      resetError();
      const isError = validator.validateAll(value);
      if (isError) {
        onError(event);
        return;
      }
      callback(event);
    };
  };

  return {
    error,
    resetError,
    validator,
    handleSubmit
  };
};

// 全ての制約をまとめて一括検証できるクラス
class ConstraintValidators<T extends AvailableFiledType> {

  constraint: ConstraintValidatorList<T>;

  constructor(schema: FieldConstraintSchema<T>, setValidationError: SetValidationError<T>) {
    const init = {} as ConstraintValidatorList<T>;
    for (const key of Object.keys(schema)) {
      const schemaItem = schema[key];
      init[key as keyof T] = new ConstraintValidator<any>(key, schemaItem, setValidationError);
    }
    this.constraint = init;
  }

  validateAll(value: T): boolean {
    let isError = false;
    for (const key of Object.keys(this.constraint)) {
      const error = this.constraint[key as keyof T].validate(value[key as keyof T]);
      if (error) {
        isError = true;
      }
    }
    return isError;
  }
}

// 制約を検証するクラス
class ConstraintValidator<T> {

  name: keyof T;

  constraint: Constraint<T>;

  setValidationError: SetValidationError<T>;

  constructor(name: keyof T, constraint: Constraint<T>, setValidationError: SetValidationError<T>) {
    this.name = name;
    this.constraint = constraint;
    this.setValidationError = setValidationError;
  }

  validate<V>(value: V): boolean {
    let message: string | null = null;
    if (this.constraint instanceof NumberFieldConstraint) {
      const numValue = this.constraint.isNumber(value) ? Number(value) : null;
      message = this.constraint.validate(numValue);
    } else if (this.constraint instanceof StringFieldConstraint) {
      // TypeGuardでコンパイルエラーになるため、instanceofで判定を行う
      message = this.constraint.validate(String(value));
    } else if (this.constraint instanceof StringArrayFieldConstraint) {
      const arrayValue = Array.isArray(value) ? value : null;
      message = this.constraint.validate(arrayValue);
    }

    if (message !== null) {
      this.setValidationError(this.name, message);
      return true;
    }
    return false;
  }
}

export { useValidation };
