import {
  MdFormatAlignCenter,
  MdFormatAlignLeft,
  MdFormatAlignRight,
} from 'react-icons/md';

export const FONT_STYLE_OPTIONS: Array<{
  id: NonNullable<DocModifyParams['fontType']>;
  name: string;
}> = [
  { id: 'arial', name: 'Arial' },
  { id: 'comicSans', name: 'Comic Sans' },
  { id: 'openDyslexic', name: 'Open Dyslexic' },
  { id: 'helvetica', name: 'Helvetica' },
  { id: 'lexend', name: 'Lexend' },
  { id: 'openSans', name: 'Open Sans' },
];

export const ALIGNMENT_OPTIONS: Array<{
  name: string;
  content: JSX.Element;
}> = [
  {
    name: 'LEFT',
    content: <MdFormatAlignLeft className='h-6 w-6' />,
  },
  {
    name: 'CENTRE',
    content: <MdFormatAlignCenter className='h-6 w-6' />,
  },
  {
    name: 'RIGHT',
    content: <MdFormatAlignRight className='h-6 w-6' />,
  },
];

export const THEME_MAP = {
  black_on_white: {
    bgColor: 'FFFFFF',
    textColor: '000000',
  },
  white_on_black: {
    bgColor: '0D0D0D',
    textColor: 'FAFAFA',
  },
  dark_on_cream: {
    bgColor: 'FFFEF7',
    textColor: '212121',
  },
  dark_on_pink: {
    bgColor: 'FFEBF6',
    textColor: '212121',
  },
  dark_on_green: {
    bgColor: 'D9FFE6',
    textColor: '212121',
  },
  dark_on_purple: {
    bgColor: 'E6D9FA',
    textColor: '212121',
  },
  dark_on_blue: {
    bgColor: 'A8FFFB',
    textColor: '212121',
  },
};
