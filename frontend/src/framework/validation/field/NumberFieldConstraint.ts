import FieldConstraint from './FieldConstraint';

export default class NumberFieldConstraint extends FieldConstraint<number> {

  isNumber<T>(value: T): boolean {
    if (value === null || value === undefined) {
      return false;
    }
    const str = String(value);
    if (str === '') {
      return false;
    }
    const num = Number.parseInt(str);
    if (Number.isNaN(num)) {
      return false;
    }
    return true;
  }

  required(message: string): NumberFieldConstraint {
    return this.define((value) => {
      if (value === null) {
        return message;
      }
    });
  }
}

export const numberField = () => new NumberFieldConstraint();