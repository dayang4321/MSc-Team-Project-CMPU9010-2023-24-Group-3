# NEXT.JS Accessibility

## next.config.js Options

NEXT.JS can be configured through a next.config.js file in the root of your project directory (for example, by package.json).

>next.config.js

``` TypeScript
/** @type {import('next').NextConfig} */
const nextConfig = {
  /* config options here */
}

module.exports = nextConfig
```

`next.config.js` is a regular Node.js module, not a JSON file. It gets used by the NEXT.JS server and build phases, and it's not included in the browser build.

Reference: [NEXT.JS Accessibility Documentation](https://nextjs.org/docs/app/api-reference/next-config-js)
