// /** @type {import('tailwindcss').Config} */
// export default {
//   content: [],
//   theme: {
//     extend: {},
//   },
//   plugins: [],
// };
// tailwind.config.js
import { nextui } from '@nextui-org/theme';

/** @type {import('tailwindcss').Config} */
export default {
  content: [
    // single component styles
    './node_modules/@nextui-org/theme/dist/components/button.js',
    // or you can use a glob pattern (multiple component styles)
    './node_modules/@nextui-org/theme/dist/components/(button|snippet|code|input).js',
  ],
  theme: {
    extend: {},
  },
  darkMode: 'class',
  plugins: [nextui()],
};
