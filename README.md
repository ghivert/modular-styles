# Modular Styles

Implements CSS Modules for any SPA framework, especially in other languages than
JavaScript!

Right now, the simplest solution to use CSS Modules consists of instantiating
Webpack, and running everything through it. Doing things like
`import styles from 'stylesheet.module.css'`. But what if you’re not running
Webpack? What if you’re using another language that is not supported by Webpack? Or a
language which don’t have access to `require`? Well, unless your build tool
includes CSS Modules itself, you’re stuck with classic CSS. Or maybe with SASS.
But we can do better now! PostCSS is here, it’s time to do better things!

The idea behind Modular Styles is to give
access to PostCSS and all of the future features of CSS, right now, for all
languages and frameworks.

# How does it work?

Modular Styles uses the power of PostCSS and Gulp to provide an easy way to
compile your CSS into usable CSS for any browser.

- First, all CSS is gathered though Gulp.
- Everything is processed by PostCSS to activate the future features of CSS.
- Optionally, the files processed can be dumped somewhere in your project.
- In parallel, all files get an interface in JSON, with the corresponding CSS
Modules names from the stylesheets, converted into your favorite language
interface.
- All files get concatenated into one.
- This file is finally processed by `cssnano` in order to remove code duplication.
- Finally, just include the resulting file into your HTML template, and enjoy
using all the features of CSSNext!

At the end, you end up with two parts: a `styles.css` stylesheet, containing all
the converted CSS, and a bunch of interfaces into multiple files, corresponding
to each stylesheet, such as `navbar.cljs`, `main.cljs`, etc.

# What does Modular Styles support?

For now, the package has been written for ClojureScript, in use with shadow-cljs.
It fully supports ClojureScript and CSS Modules. It is thought to also handle
all PostCSS plugins according to the project dependencies.

# How does it work?

Modular Styles exists in two flavors: the CLI, and the API, in order to
integrate easily with all flows, whether they are NPM scripts or more advanced
processes.

## Installation

```bash
npm install --save-dev modular-styles
# For Yarn users
yarn add --dev modular-styles
```

## Additional Set-up

To start using modular styles, you need to prepare several things.

### Install PostCSS plugins and configure it:
```bash
npm install --save-dev <list of PostCSS plugins>
# For Yarn users
yarn add --dev <list of PostCSS plugins>
```

Example:
```bash
npm install --save-dev postcss-import postcss-preset-env
# For Yarn users
yarn add --dev postcss-import postcss-preset-env
```

For configuration look at the [dedicated section](#postcss-configurations).

### Prepare NPM scripts

```json
{
  "scripts": {
    "watch-styles": "<your styles watching script>",
    "build-styles": "<your styles building script>"
  }
}
```

To write scripts either use [CLI](#cli) ([example](https://github.com/ghivert/re-frame-template/blob/master/package.json)) or [JavaScript](#api) ([example](https://github.com/guillaumeboudon/wishlist)).

## Usage

Before the development, open a dedicated terminal window and run:
```bash
npm run watch-styles
# For Yarn users
yarn watch-styles
```

This command will bring to foreground watching process rebuilding your CSS on every file save.

## CLI

```bash
modular-styles [command] <options>
```

`[command]` should either be `compile` or `watch`. The `compile` command runs the program
once, compiles everything and shutdown. When using `watch`, the program
constantly watches for every source file change and rebuilds everything each time
to ensure you’re always up to date.

Some options are required, some are not. Many configuration options are available to suit your needs.

- `--files <filesPath>` should point to your CSS files. You can also just
indicate your `src` path if you don’t have a specific stylesheets folder.
- `--dest <destPath>` should point to where you want to put your interfaces
once generated.
- `--source <sourcePath>` should point to the correct source path of your
interfaces. In ClojureScript, every interface gets compiled with a
`(ns package.name.path)` interface. The sourcePath allows to find the base path.
Some smarter things could be done for those languages.
- `--extension <extension>` should be your stylesheets extension. Defaults to css.
- `--tempCSS <tempCSS>` should point to the path for stylesheets without
minification dumping.
- `--bundleName <bundleName>` is the name for the resulting CSS bundle. Defaults to `styles.css`.
- `--bundlePath <bundleCSSPath>` is the path for the resulting CSS bundle. Defaults to `public`.
- `--lang <language>` is the language in which you want your CSS modules to be converted. Defaults to `cljs`. Supports `elm` as well.

You probably will end up with something like:

```bash
modular-styles watch --source src --dest src/project_name/styles --files src/styles
```

This means all files into `src/styles` will be converted into one
`public/styles.css` file.

# API

```javascript
const modularStyles = require('modular-styles')

const options = {
  sourcePath: 'src',
  filesPath: 'src/styles',
  destPath: 'src/project_name/styles',
  extension: 'css',
  bundleName: 'styles.css',
  bundleCSSPath: 'public',
  tempCSS: 'public/css',
  language: 'cljs',
}

// Use once.
modularStyles.compile(options)

// Or watch all files.
modularStyles.watch(options)
```

The options are the same as below.

# PostCSS configurations

You can use any plugin you want for PostCSS. Just add them to your `package.json`,
create a `.postcssrc.json`, a `.postcssrc.js` or a `postcss.config.js`, and
add your plugins and options directly into this file.

Like so:

```javascript
// .postcss.config.js
module.exports = {
  plugins: {
    'postcss-import': {},
    'postcss-preset-env': {
      stage: 1,
    },
  },
}
```

This way, all the plugins and options are transferred to PostCSS.

# How does the class name extraction work?

After the compilation of your CSS, PostCSS allows you to do what you want with the
JSON interfaces it provides. By default, it extracts all the information and
dumps them into the correct ClojureScript file. It is really easy to write
another function to convert the JSON into another language.

# Contributing?

All contributions are welcome! Please submit a PR or open an issue!
