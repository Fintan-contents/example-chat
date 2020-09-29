// 検証するクラスから制約を扱うときのインタフェース
interface Constraint<T> {
  validate(value: T): string | null;
}

type ValueConstraint<T> = (value: T | null) => string | undefined;

export default abstract class FieldConstraint<T> implements Constraint<T> {

  constraints: Set<ValueConstraint<T>> = new Set();

  define(constraint: ValueConstraint<T>): this {
    this.constraints.add(constraint);
    return this;
  }

  validate(value: T | null): string | null {
    for (const constraint of this.constraints) {
      const message = constraint(value);
      if (message) {
        return message;
      }
    }
    return null;
  }
}