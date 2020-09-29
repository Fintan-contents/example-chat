import React from 'react';
import { useValidation } from './Validation';
import { stringField } from './field/StringFieldConstraint';
import { stringArrayField } from './field/StringArrayFieldConstraint';
import { numberField } from './field/NumberFieldConstraint';
import '@testing-library/jest-dom/extend-expect';
import { render, fireEvent } from '@testing-library/react';

type Field = {
  str: string;
  num: string;
  email: string;
  array: string[];
  custom: string;
}
const Validation: React.FC<Field> = (props: Field) => {
  const { error, validator, resetError } = useValidation<Field>({
    str: stringField().required('missing').minLength(4, 'short').maxLength(6, 'long'),
    num: numberField().required('missing'),
    email: stringField().email('invalid format'),
    array: stringArrayField().required('missing'),
    custom: stringField().define((value) => {
      if (value === 'error') return 'error';
    })
  });
  const validate = () => {
    resetError();
    validator.validateAll({
      str: props.str,
      num: props.num,
      email: props.email,
      array: props.array,
      custom: props.custom
    });
  };
  return (
    <React.Fragment>
      <button data-testid='validate' onClick={validate}/>
      <ul>
        <li id='str'>{error.str ? error.str : 'OK'}</li>
        <li id='num'>{error.num ? error.num : 'OK'}</li>
        <li id='email'>{error.email ? error.email : 'OK'}</li>
        <li id='array'>{error.array ? error.array : 'OK'}</li>
        <li id='custom'>{error.custom ? error.custom : 'OK'}</li>
      </ul>
    </React.Fragment>
  );
};

describe('useValidation', () => {

  test('エラー無し', () => {

    const value: Field = {
      str: 'test',
      num: '100',
      email: 'test@example.com',
      array: ['test'],
      custom: 'no error',
    };
    const component = render(
      <Validation {...value} />
    );

    fireEvent.click(component.getByTestId('validate'));

    const str = component.container.querySelector('#str');
    expect(str).toHaveTextContent('OK');

    const num = component.container.querySelector('#num');
    expect(num).toHaveTextContent('OK');

    const email = component.container.querySelector('#email');
    expect(email).toHaveTextContent('OK');

    const array = component.container.querySelector('#array');
    expect(array).toHaveTextContent('OK');

    const custom = component.container.querySelector('#custom');
    expect(custom).toHaveTextContent('OK');

  });

  test('必須エラー', () => {

    const value: Field = {
      str: '',
      num: '',
      email: '',
      array: [],
      custom: '',
    };
    const component = render(
      <Validation {...value} />
    );

    fireEvent.click(component.getByTestId('validate'));

    const str = component.container.querySelector('#str');
    expect(str).toHaveTextContent('missing');

    const num = component.container.querySelector('#num');
    expect(num).toHaveTextContent('missing');

    const email = component.container.querySelector('#email');
    expect(email).toHaveTextContent('OK');

    const array = component.container.querySelector('#array');
    expect(array).toHaveTextContent('missing');

    const custom = component.container.querySelector('#custom');
    expect(custom).toHaveTextContent('OK');
  });

  test('最小文字列数エラー', () => {

    const value: Field = {
      str: '1',
      num: '100',
      email: '',
      array: ['test'],
      custom: '',
    };
    const component = render(
      <Validation {...value} />
    );

    fireEvent.click(component.getByTestId('validate'));

    const str = component.container.querySelector('#str');
    expect(str).toHaveTextContent('short');
  });

  test('最大文字列数エラー', () => {

    const value: Field = {
      str: '1234567',
      num: '100',
      email: '',
      array: ['test'],
      custom: '',
    };
    const component = render(
      <Validation {...value} />
    );

    fireEvent.click(component.getByTestId('validate'));

    const str = component.container.querySelector('#str');
    expect(str).toHaveTextContent('long');
  });

  test('メールアドレス形式エラー', () => {

    const value: Field = {
      str: 'test',
      num: '100',
      email: 'test',
      array: ['test'],
      custom: 'no error',
    };
    const component = render(
      <Validation {...value} />
    );

    fireEvent.click(component.getByTestId('validate'));

    const email = component.container.querySelector('#email');
    expect(email).toHaveTextContent('invalid format');
  });
});
