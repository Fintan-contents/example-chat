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
      const svg = codeWriter.write(input, width, height);
      if (ref.current.firstChild) {
        ref.current.replaceChild(svg, ref.current.firstChild);
      } else {
        ref.current.appendChild(svg);
      }
    }
  }, [input, height, width]);
  return (
    <div ref={ref}></div>
  );
};
