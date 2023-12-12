import {
  MdFormatAlignCenter,
  MdFormatAlignLeft,
  MdFormatAlignRight,
} from 'react-icons/md';

/**
 * FONT_STYLE_OPTIONS: Array of objects representing the different font style options.
 * Each option has an 'id' used for referencing the style and a 'name' that is the display name of the font.
 * ==============================================================================
 * `{
 *    id (DocModifyParams): Use the font style value from the constant - DocModifyParams,
 *    name (String): Define the text for font style value,
 * }`
 */
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

/**
 * ALIGNMENT_OPTIONS: An Array of objects for options to define text alignment.
 * Each option has a 'name' representing the type of alignment and
 * 'content', which is a JSX Element of the corresponding icon.
 * ==============================================================================
 * `{
 *    name (String): Options for text alignment ('LEFT', 'CENTRE', and 'RIGHT'),
 *    content (JSX.Element): Define the JSX Code for an HTML Element,
 * }`
 */
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

/**
 * THEME_MAP: This Object is used to represent the different theme options.
 * Each key-value pair represents a theme with 'bgColor' for background colour and
 * 'textColor' for the text colour. The values are color codes.
 * ==============================================================================
 * `{
 *    bgColor (String): background colour in hex code,
 *    textColor (String): Colour of the text in hex code,
 * }`
 */
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
