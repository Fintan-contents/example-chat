import FieldConstraint from './FieldConstraint';

export default class StringFieldConstraint extends FieldConstraint<string> {

  required(message: string): StringFieldConstraint {
    return this.define((value) => {
      if (value === null || value === '') {
        return message;
      }
    });
  }

  minLength(length: number, message: string): StringFieldConstraint {
    return this.define((value) => {
      if (value !== null && value !== '' && [...value].length < length) {
        return message;
      }
    });
  }

  maxLength(length: number, message: string): StringFieldConstraint {
    return this.define((value) => {
      if (value !== null && value !== '' && [...value].length > length) {
        return message;
      }
    });
  }

  email(message: string): StringFieldConstraint {
    return this.define((value) => {
      // HTML Validationでの形式チェックと同等にチェック
      // https://html.spec.whatwg.org/multipage/input.html#valid-e-mail-address
      const regExp = /^.+@.+$/;
      if (value !== null && value !== '' && !regExp.test(value)) {
        return message;
      }
    });
  }
}

export const stringField = () => new StringFieldConstraint();