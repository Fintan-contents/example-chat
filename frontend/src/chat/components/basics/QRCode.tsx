import React, { useEffect, useRef } from 'react';
import { BrowserQRCodeSvgWriter } from '@zxing/library';

type QRCodeProps = {
  input: string;
  width: number;
  height: number;
};

export const QRCode: React.FC<QRCodeProps> = ({ input, width, height }) => {
  const ref = useRef<HTMLDivElement|null>(null);
  useEffect(() => {
    if (ref.current !== null && input !== '') {
      const codeWriter = new BrowserQRCodeSvgWriter();
      codeWriter.writeToDom(ref.current, input, width, height);
    }
    return () => {
      ref.current = null;
    };
  }, [input, height, width]);
  return (
    <div ref={ref}></div>
  );
};
