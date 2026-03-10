# CSS/SCSS ツール: PostCSS・ビルド

**このドキュメントについて**: CSS/SCSS コーディング規約 - ツール: PostCSS・ビルド

---

```javascript
/* ✅ Good: 本番対応 PostCSS 設定 */

const isProduction = process.env.NODE_ENV === 'production';

module.exports = {
  plugins: [
    // Modern CSS features
    require('postcss-preset-env')({
      stage: 2,
      features: {
        'custom-properties': false, // Keep CSS variables as-is
        'nesting-rules': true,
        'custom-media-queries': true,
        'media-query-ranges': true,
        'logical-properties-and-values': true,
        'color-functional-notation': true,
        'lab-function': true,
        'oklab-function': true,
        'color-mix': true,
        'trigonometric-functions': true,
        'exponential-functions': true,
      },
      browsers: [
        '> 1%',
        'last 2 versions',
        'Firefox ESR',
        'not dead',
        'not IE 11',
      ],
      autoprefixer: {
        flexbox: 'no-2009',
        grid: 'autoplace',
      },
    }),
    
    // Import processing
    require('postcss-import')({
      plugins: [
        require('stylelint')({
          fix: true,
        }),
      ],
    }),
    
    // CSS nesting (for non-Sass projects)
    require('postcss-nesting'),
    
    // Custom properties enhancements
    require('postcss-custom-properties')({
      preserve: true,
      exportTo: './src/styles/design-tokens.json',
    }),
    
    // Media query optimization
    require('postcss-sort-media-queries')({
      sort: 'desktop-first',
    }),
    
    // CSS optimization for production
    ...(isProduction
      ? [
          // Critical CSS extraction
          require('@fullhuman/postcss-purgecss')({
            content: [
              './src/**/*.html',
              './src/**/*.vue',
              './src/**/*.js',
              './src/**/*.jsx',
              './src/**/*.ts',
              './src/**/*.tsx',
            ],
            defaultExtractor: content => {
              // Extract class names including those with special characters
              const broadMatches = content.match(/[^<>"'`\\s]*[^<>"'`\\s:]/g) || [];
              const innerMatches = content.match(/[^<>"'`\\s.()]*[^<>"'`\\s.():]/g) || [];
              return broadMatches.concat(innerMatches);
            },
            safelist: {
              standard: [
                /^(html|body|#root|#__next)/,
                /^focus-visible$/,
                /^data-/,
                /^aria-/,
                /^sr-only$/,
              ],
              deep: [
                /modal/,
                /dropdown/,
                /tooltip/,
                /loading/,
                /error/,
                /success/,
                /warning/,
                /info/,
              ],
              greedy: [
                /^swiper/,
                /^aos/,
                /^hljs/,
              ],
            },
            // Skip purging for component libraries
            skippedContentGlobs: ['node_modules/**'],
          }),
          
          // CSS minification
          require('cssnano')({
            preset: [
              'advanced',
              {
                cssDeclarationSorter: false, // Handled by stylelint
                discardComments: {
                  removeAll: true,
                },
                reduceIdents: {
                  keyframes: false, // Preserve animation names
                },
                zindex: false, // Don't optimize z-index values
                colormin: {
                  // Preserve CSS custom property references
                  ignore: ['var()'],
                },
              },
            ],
          }),
        ]
      : []),
      
    // Development helpers
    ...(!isProduction
      ? [
          // CSS debugging
          require('postcss-reporter')({
            clearReportedMessages: true,
            throwError: true,
          }),
        ]
      : []),
  ],
};
```

#### **Tailwind CSS Integration**
```javascript
/* ✅ Good: Tailwind CSS + PostCSS 統合設定 */

// tailwind.config.js
const colors = require('tailwindcss/colors');
const plugin = require('tailwindcss/plugin');

module.exports = {
  content: [
    './src/**/*.{html,js,ts,jsx,tsx,vue,svelte}',
    './components/**/*.{js,ts,jsx,tsx,vue,svelte}',
    './pages/**/*.{js,ts,jsx,tsx,vue,svelte}',
    './app/**/*.{js,ts,jsx,tsx,vue,svelte}',
  ],
  
  theme: {
    extend: {
      // Design system integration
      colors: {
        primary: {
          50: 'rgb(239 246 255)',
          100: 'rgb(219 234 254)',
          200: 'rgb(191 219 254)',
          300: 'rgb(147 197 253)',
          400: 'rgb(96 165 250)',
          500: 'rgb(59 130 246)',
          600: 'rgb(37 99 235)',
          700: 'rgb(29 78 216)',
          800: 'rgb(30 64 175)',
          900: 'rgb(30 58 138)',
          950: 'rgb(23 37 84)',
        },
        gray: colors.slate,
        success: colors.emerald,
        warning: colors.amber,
        error: colors.red,
      },
      
      fontFamily: {
        sans: [
          'Inter',
          'system-ui',
          '-apple-system',
          'BlinkMacSystemFont',
          '"Segoe UI"',
          'Roboto',
          '"Helvetica Neue"',
          'Arial',
          '"Noto Sans"',
          'sans-serif',
          '"Apple Color Emoji"',
          '"Segoe UI Emoji"',
          '"Segoe UI Symbol"',
          '"Noto Color Emoji"',
        ],
        mono: [
          '"SF Mono"',
          'Monaco',
          'Inconsolata',
          '"Roboto Mono"',
          '"Source Code Pro"',
          'Consolas',
          '"Liberation Mono"',
          '"Menlo"',
          'monospace',
        ],
      },
      
      spacing: {
        '18': '4.5rem',
        '88': '22rem',
        '128': '32rem',
      },
      
      animation: {
        'fade-in': 'fadeIn 0.3s ease-in-out',
        'slide-in': 'slideIn 0.3s ease-out',
        'bounce-slow': 'bounce 2s infinite',
        'pulse-slow': 'pulse 3s infinite',
      },
      
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideIn: {
          '0%': { transform: 'translateY(-10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
      },
      
      // Container queries support
      screens: {
        'xs': '475px',
        '3xl': '1920px',
      },
      
      // Custom utilities
      typography: (theme) => ({
        DEFAULT: {
          css: {
            color: theme('colors.gray.700'),
            lineHeight: theme('lineHeight.relaxed'),
            fontSize: theme('fontSize.base'),
            fontFamily: theme('fontFamily.sans').join(', '),
          },
        },
      }),
    },
  },
  
  plugins: [
    // Typography plugin
    require('@tailwindcss/typography'),
    
    // Forms plugin
    require('@tailwindcss/forms')({
      strategy: 'class',
    }),
    
    // Container queries
    require('@tailwindcss/container-queries'),
    
    // Aspect ratio
    require('@tailwindcss/aspect-ratio'),
    
    // Custom component plugin
    plugin(function({ addComponents, addUtilities, theme }) {
      // Add component classes
      addComponents({
        '.btn': {
          padding: `${theme('spacing.2')} ${theme('spacing.4')}`,
          borderRadius: theme('borderRadius.md'),
          fontWeight: theme('fontWeight.medium'),
          display: 'inline-flex',
          alignItems: 'center',
          justifyContent: 'center',
          transition: 'all 0.2s ease',
          cursor: 'pointer',
          border: 'none',
          textDecoration: 'none',
          
          '&:focus-visible': {
            outline: `2px solid ${theme('colors.blue.500')}`,
            outlineOffset: '2px',
          },
          
          '&:disabled': {
            opacity: '0.6',
            cursor: 'not-allowed',
          },
        },
        
        '.btn-primary': {
          backgroundColor: theme('colors.primary.600'),
          color: theme('colors.white'),
          
          '&:hover:not(:disabled)': {
            backgroundColor: theme('colors.primary.700'),
          },
        },
        
        '.btn-secondary': {
          backgroundColor: theme('colors.gray.100'),
          color: theme('colors.gray.900'),
          
          '&:hover:not(:disabled)': {
            backgroundColor: theme('colors.gray.200'),
          },
        },
      });
      
      // Add utility classes
      addUtilities({
        '.text-balance': {
          textWrap: 'balance',
        },
        '.text-pretty': {
          textWrap: 'pretty',
        },
        '.scrollbar-none': {
          scrollbarWidth: 'none',
          '&::-webkit-scrollbar': {
            display: 'none',
          },
        },
      });
    }),
    
    // Accessibility plugin
    plugin(function({ addBase, theme }) {
      addBase({
        ':focus-visible': {
          outline: `2px solid ${theme('colors.blue.500')}`,
          outlineOffset: '2px',
          borderRadius: theme('borderRadius.sm'),
        },
        
        // Screen reader only
        '.sr-only': {
          position: 'absolute',
          width: '1px',
          height: '1px',
          padding: '0',
          margin: '-1px',
          overflow: 'hidden',
          clip: 'rect(0, 0, 0, 0)',
          whiteSpace: 'nowrap',
          border: '0',
        },
        
        // Respect user preferences
        '@media (prefers-reduced-motion: reduce)': {
          '*': {
            animationDuration: '0.01ms !important',
            animationIterationCount: '1 !important',
            transitionDuration: '0.01ms !important',
          },
        },
      });
    }),
  ],
  
  // Dark mode configuration
  darkMode: 'class',
  
  // Prefix for avoiding conflicts
  prefix: '',
  
  // Important strategy
  important: false,
  
  // Separator for variants
  separator: ':',
  
  // Core plugins to disable
  corePlugins: {
    preflight: true,
    container: false, // Using custom container
  },
};
```

### 10.3 ビルドツール統合

#### **Vite 設定例**
```javascript
/* ✅ Good: Vite + CSS 最適化設定 */

import { defineConfig } from 'vite';
import { resolve } from 'path';

export default defineConfig({
  css: {
    // PostCSS configuration
    postcss: './postcss.config.js',
    
    // CSS modules configuration
    modules: {
      localsConvention: 'camelCaseOnly',
      generateScopedName: '[name]__[local]___[hash:base64:5]',
    },
    
    // Preprocessor options
    preprocessorOptions: {
      scss: {
        // Make design tokens available globally
        additionalData: `
          @use "/src/styles/design-tokens" as *;
          @use "/src/styles/mixins" as *;
        `,
        
        // Modern Sass API
        api: 'modern-compiler',
        
        // Include paths
        includePaths: ['node_modules', 'src/styles'],
        
        // Silence deprecation warnings
        silenceDeprecations: ['legacy-js-api'],
      },
    },
    
    // Development CSS source maps
    devSourcemap: true,
  },
  
  build: {
    // CSS optimization
    css: {
      // Enable CSS code splitting
      codeSplit: true,
      
      // Inline CSS threshold (4KB)
      inlineLimit: 4096,
    },
    
    rollupOptions: {
      output: {
        // Separate CSS chunks by entry
        assetFileNames: (assetInfo) => {
          if (assetInfo.name.endsWith('.css')) {
            return 'css/[name]-[hash][extname]';
          }
          return 'assets/[name]-[hash][extname]';
        },
      },
    },
    
    // Enable CSS minification
    cssMinify: 'esbuild',
    
    // Source maps for production debugging
    sourcemap: process.env.NODE_ENV === 'development',
  },
  
  // Development server configuration
  server: {
    // HMR for CSS
    hmr: {
      overlay: true,
    },
  },
  
  // Plugin configuration
  plugins: [
    // CSS analysis plugin
    {
      name: 'css-analyzer',
      buildStart() {
        console.log('🎨 CSS build started');
      },
      generateBundle(options, bundle) {
        let totalCSSSize = 0;
        Object.keys(bundle).forEach(fileName => {
          if (fileName.endsWith('.css')) {
            const cssAsset = bundle[fileName];
            totalCSSSize += cssAsset.source.length;
            console.log(`📄 ${fileName}: ${(cssAsset.source.length / 1024).toFixed(2)}KB`);
          }
        });
        console.log(`📦 Total CSS size: ${(totalCSSSize / 1024).toFixed(2)}KB`);
      },
    },
  ],
  
  resolve: {
    alias: {
      '@styles': resolve(__dirname, 'src/styles'),
      '@components': resolve(__dirname, 'src/components'),
    },
  },
});
```

#### **Webpack 設定例**
```javascript
/* ✅ Good: Webpack + CSS 最適化設定 */

const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const PurgeCSSPlugin = require('purgecss-webpack-plugin');
const glob = require('glob');

const isProduction = process.env.NODE_ENV === 'production';

module.exports = {
  module: {
    rules: [
      // CSS/SCSS processing
      {
        test: /\.(css|scss)$/,
        use: [
          // Extract CSS in production
          isProduction ? MiniCssExtractPlugin.loader : 'style-loader',
          
          // CSS loader with modules support
          {
            loader: 'css-loader',
            options: {
              modules: {
                auto: /\.module\.(css|scss)$/,
                localIdentName: isProduction
                  ? '[hash:base64:8]'
                  : '[name]__[local]__[hash:base64:5]',
              },
              sourceMap: !isProduction,
              importLoaders: 2,
            },
          },
          
          // PostCSS processing
          {
            loader: 'postcss-loader',
            options: {
              sourceMap: !isProduction,
              postcssOptions: {
                config: path.resolve(__dirname, 'postcss.config.js'),
              },
            },
          },
          
          // Sass processing
          {
            loader: 'sass-loader',
            options: {
              sourceMap: !isProduction,
              implementation: require('sass'),
              additionalData: `
                @use "@/styles/design-tokens" as *;
                @use "@/styles/mixins" as *;
              `,
              sassOptions: {
                includePaths: [
                  path.resolve(__dirname, 'src/styles'),
                  path.resolve(__dirname, 'node_modules'),
                ],
                outputStyle: 'expanded',
              },
            },
          },
        ],
      },
    ],
  },
  
  plugins: [
    // Extract CSS to separate files
    ...(isProduction ? [
      new MiniCssExtractPlugin({
        filename: 'css/[name].[contenthash:8].css',
        chunkFilename: 'css/[name].[contenthash:8].chunk.css',
        ignoreOrder: false,
      }),
      
      // Remove unused CSS
      new PurgeCSSPlugin({
        paths: glob.sync(`${path.join(__dirname, 'src')}/**/*`, {
          nodir: true,
          ignore: ['**/node_modules/**'],
        }),
        
        // Safelist important classes
        safelist: {
          standard: [
            /^(html|body|#root|#__next)/,
            /^focus-visible$/,
            /^data-/,
            /^aria-/,
          ],
          deep: [/modal/, /dropdown/, /tooltip/],
          greedy: [/^swiper/, /^aos/],
        },
        
        // Custom extractors
        defaultExtractor: content => {
          const broadMatches = content.match(/[^<>"'`\\s]*[^<>"'`\\s:]/g) || [];
          const innerMatches = content.match(/[^<>"'`\\s.()]*[^<>"'`\\s.():]/g) || [];
          return broadMatches.concat(innerMatches);
        },
      }),
    ] : []),
  ],
  
  optimization: {
    minimizer: [
      // CSS minification
      ...(isProduction ? [
        new CssMinimizerPlugin({
          minimizerOptions: {
            preset: [
              'advanced',
              {
                discardComments: { removeAll: true },
                cssDeclarationSorter: false,
                reduceIdents: { keyframes: false },
                zindex: false,
              },
            ],
          },
        }),
      ] : []),
    ],
  },
  
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
      '@styles': path.resolve(__dirname, 'src/styles'),
    },
  },
  
  // Development configuration
  devServer: {
    hot: true,
    liveReload: true,
  },
};
```

