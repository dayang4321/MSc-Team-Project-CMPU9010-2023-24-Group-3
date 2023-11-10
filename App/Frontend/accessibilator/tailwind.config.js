/** @type {import('tailwindcss').Config} */

import { stone } from 'tailwindcss/colors';

export const content = ['./src/**/*.{js,ts,jsx,tsx,mdx}'];
export const theme = {
  extend: {
    colors: {
      primary: stone,
    },
    fontFamily: {
      sans: ['var(--font-lexend)'],
    },
  },
};

export const daisyui = {
  themes: false,
  darkTheme: 'light',
  styled: false,
  prefix: 'daisy-',
};
export const plugins = [
  require('@tailwindcss/forms'),
  require('@headlessui/tailwindcss'),
  require('daisyui'),
];
