const { withSentryConfig } = require('@sentry/nextjs');

/** @type {import('next').NextConfig} */
const nextConfig = {
  transpilePackages: [
    '@adobe/react-spectrum',
    '@react-spectrum/actionbar',
    '@react-spectrum/actiongroup',
    '@react-spectrum/avatar',
    '@react-spectrum/badge',
    '@react-spectrum/breadcrumbs',
    '@react-spectrum/button',
    '@react-spectrum/buttongroup',
    '@react-spectrum/calendar',
    '@react-spectrum/checkbox',
    '@react-spectrum/combobox',
    '@react-spectrum/contextualhelp',
    '@react-spectrum/datepicker',
    '@react-spectrum/dialog',
    '@react-spectrum/divider',
    '@react-spectrum/dnd',
    '@react-spectrum/form',
    '@react-spectrum/icon',
    '@react-spectrum/illustratedmessage',
    '@react-spectrum/inlinealert',
    '@react-spectrum/image',
    '@react-spectrum/label',
    '@react-spectrum/labeledvalue',
    '@react-spectrum/layout',
    '@react-spectrum/link',
    '@react-spectrum/list',
    '@react-spectrum/listbox',
    '@react-spectrum/menu',
    '@react-spectrum/meter',
    '@react-spectrum/numberfield',
    '@react-spectrum/overlays',
    '@react-spectrum/picker',
    '@react-spectrum/progress',
    '@react-spectrum/provider',
    '@react-spectrum/radio',
    '@react-spectrum/slider',
    '@react-spectrum/searchfield',
    '@react-spectrum/statuslight',
    '@react-spectrum/switch',
    '@react-spectrum/table',
    '@react-spectrum/tabs',
    '@react-spectrum/tag',
    '@react-spectrum/text',
    '@react-spectrum/toast',
    '@react-spectrum/textfield',
    '@react-spectrum/theme-dark',
    '@react-spectrum/theme-default',
    '@react-spectrum/theme-light',
    '@react-spectrum/tooltip',
    '@react-spectrum/view',
    '@react-spectrum/well',
    '@spectrum-icons/illustrations',
    '@spectrum-icons/ui',
    '@spectrum-icons/workflow',
  ],
};

module.exports = withSentryConfig(
  nextConfig,
  {
    // For all available options, see:
    // https://github.com/getsentry/sentry-webpack-plugin#options

    // Suppresses source map uploading logs during build
    silent: true,
    org: 'bytethebarrier',
    project: 'javascript-nextjs',
  },
  {
    // For all available options, see:
    // https://docs.sentry.io/platforms/javascript/guides/nextjs/manual-setup/

    // Upload a larger set of source maps for prettier stack traces (increases build time)
    widenClientFileUpload: true,

    // Transpiles SDK to be compatible with IE11 (increases bundle size)
    transpileClientSDK: true,

    // Routes browser requests to Sentry through a Next.js rewrite to circumvent ad-blockers (increases server load)
    tunnelRoute: '/monitoring',

    // Hides source maps from generated client bundles
    hideSourceMaps: true,

    // Automatically tree-shake Sentry logger statements to reduce bundle size
    disableLogger: true,
  }
);
