import FieldConstraint from './FieldConstraint';

export default class StringArrayFieldConstraint extends FieldConstraint<string[]>{

  required(message: string): StringArrayFieldConstraint {
    return this.define((value) => {
      if (value === null || value.length === 0) {
        return message;
      }
    });
  }
}

export const stringArrayField = () => new StringArrayFieldConstraint();