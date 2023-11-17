import React from 'react';
import { useValidation } from './Validation';
import { stringField } from './field/StringFieldConstraint';
import { stringArrayField } from './field/StringArrayFieldConstraint';
import { numberField } from './field/NumberFieldConstraint';
import '@testing-library/jest-dom/extend-expect';
import { render, fireEvent, screen } from '@testing-library/react';

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
        <li data-testid='str'>{error.str ? error.str : 'OK'}</li>
        <li data-testid='num'>{error.num ? error.num : 'OK'}</li>
        <li data-testid='email'>{error.email ? error.email : 'OK'}</li>
        <li data-testid='array'>{error.array ? error.array : 'OK'}</li>
        <li data-testid='custom'>{error.custom ? error.custom : 'OK'}</li>
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
    render(
      <Validation {...value} />
    );

    fireEvent.click(screen.getByTestId('validate'));

    const str = screen.getByTestId('str');
    expect(str).toHaveTextContent('OK');

    const num = screen.getByTestId('num');
    expect(num).toHaveTextContent('OK');

    const email = screen.getByTestId('email');
    expect(email).toHaveTextContent('OK');

    const array = screen.getByTestId('array');
    expect(array).toHaveTextContent('OK');

    const custom = screen.getByTestId('custom');
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
    render(
      <Validation {...value} />
    );

    fireEvent.click(screen.getByTestId('validate'));

    const str = screen.getByTestId('str');
    expect(str).toHaveTextContent('missing');

    const num = screen.getByTestId('num');
    expect(num).toHaveTextContent('missing');

    const email = screen.getByTestId('email');
    expect(email).toHaveTextContent('OK');

    const array = screen.getByTestId('array');
    expect(array).toHaveTextContent('missing');

    const custom = screen.getByTestId('custom');
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
    render(
      <Validation {...value} />
    );

    fireEvent.click(screen.getByTestId('validate'));

    const str = screen.getByTestId('str');
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
    render(
      <Validation {...value} />
    );

    fireEvent.click(screen.getByTestId('validate'));

    const str = screen.getByTestId('str');
    expect(str).toHaveTextContent('long');
  });

  test('サロゲートペア文字を1文字でカウントできる', () => {

    const value: Field = {
      str: '12345𩸽',
      num: '100',
      email: '',
      array: ['test'],
      custom: '',
    };
    render(
      <Validation {...value} />
    );

    fireEvent.click(screen.getByTestId('validate'));

    const str = screen.getByTestId('str');
    expect(str).toHaveTextContent('OK');
  });

  test('絵文字を1文字でカウントできる', () => {

    const value: Field = {
      str: '12345😭',
      num: '100',
      email: '',
      array: ['test'],
      custom: '',
    };
    render(
      <Validation {...value} />
    );

    fireEvent.click(screen.getByTestId('validate'));

    const str = screen.getByTestId('str');
    expect(str).toHaveTextContent('OK');
  });

  test('結合文字は1文字としてカウントしない', () => {

    const value: Field = {
      str: '12345ǖ',
      num: '100',
      email: '',
      array: ['test'],
      custom: '',
    };
    render(
      <Validation {...value} />
    );

    fireEvent.click(screen.getByTestId('validate'));

    const str = screen.getByTestId('str');
    expect(str).toHaveTextContent('long');
  });

  test('ZWJの結合文字は1文字としてカウントしない', () => {

    const value: Field = {
      str: '12345👨‍👩‍👧‍👦',
      num: '100',
      email: '',
      array: ['test'],
      custom: '',
    };
    render(
      <Validation {...value} />
    );

    fireEvent.click(screen.getByTestId('validate'));

    const str = screen.getByTestId('str');
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
    render(
      <Validation {...value} />
    );

    fireEvent.click(screen.getByTestId('validate'));

    const email = screen.getByTestId('email');
    expect(email).toHaveTextContent('invalid format');
  });
});
