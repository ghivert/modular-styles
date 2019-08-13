# Modular Styles

Implements CSS Modules for any SPA framework, especially in other languages than
JavaScript!

Right now, the simplest solution to use CSS Modules consists in instanciating
Webpack, and running everything through it. Doings things like
`import styles from 'stylesheet.module.css'`. But what if you’re not running
Webpack? What if you’re using another language, not supported by Webpack? Or a
language which don’t have access to `require`? Well, unless your build tool
includes CSS Modules itself, you’re stuck with classic CSS. Or maybe with SASS.
But we can do better now! PostCSS is there, it’s time to do better things!

That’s where Modular Styles come on. The idea behind Module Style is to give
access to PostCSS and all of the future features of CSS, right now for every
languages and every frameworks.

# How does it works?

Modular Styles use the power of PostCSS and Gulp to provides an easy way to
compile your CSS into usable CSS for every browser right now.

- First, all CSS is gathered though Gulp.
- Everything is processed by PostCSS to activate the future features of CSS.
- Optionally, the files processed can be dumped somewhere in your project.
- In parallel, all files get an interface in JSON, with the corresponding CSS
Modules names from the stylesheets, converted into your favorite language
interface.
- Then all files get concatenated into one.
- This file is finally processed by `cssnano` in order to remove code duplication.
- Finally, just includes the resulting file into your HTML template, and enjoy
using all the features of CSSNext!

At the end, you end up with two parts: a `styles.css` stylesheet, containing all
the converted CSS, and a bunch of interfaces into multiple files, corresponding
to each stylesheet, like `navbar.cljs`, `main.cljs`, etc.

# What does Modular Styles supports?

For now, the package has been written for ClojureScript, in use with shadow-cljs.
It fully supports ClojureScript and CSS Modules. It is thought to also handle
all PostCSS plugins according to the project dependencies.

# How does it works?

Modular Styles exists in two flavors: the CLI, and the API, in order to
integrate easily with all flows, whether they are NPM scripts or more advanced
processes.

## CLI

```bash
modular-styles [command] <options>
```

`[command]` should either be `compile` or `watch`. The first runs the program
once, compiles everything and shutdown. When using `watch`, the program
constantly watch for every source file change and rebuild everything each time
to ensure you’re always up to date.

Some options are required, some not. You got a lot of things to configure what
you need.

- `--files <filesPath>` should points to your CSS files. You can also just
indicates your `src` path if you don’t have a specific stylesheets folder.
- `--dest <destPath>` should points to where you want to put your interfaces
once generated.
- `--source <sourcePath>` should points to the correct source path of your
interfaces. In ClojureScript, every interface gets compiled with a
`(ns package.name.path)` interface. The sourcePath allows to find the base path.
Some smarter things could be done for those languages.
- `--extension <extension>` should be your stylesheets extension. Defaults to css.
- `--tempCSS <tempCSS>` should points to the path for stylesheets without
minification dumping.
- `--bundleName <bundleName>` is the name for the resulting CSS bundle. Defaults to `styles.css`.
- `--bundlePath <bundleCSSPath>` is the path for the resulting CSS bundle. Defaults to `public`.

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
  filesPath: 'src/styles',,
  destPath: 'src/project_name/styles'
  extensions: 'css',
  bundlerName: 'styles.css',
  bundlePath: 'public',
  tempCSS: 'public/css',
}

// Use once.
modularStyles.compile(options)

// Or watch all files.
modularStyles.watch(options)
```

The options are the same than below.

# PostCSS configurations

You can use any plugin you want for PostCSS. Just add them to your `package.json`,
creates a `.postcssrc.json`, a `.postcssrc.js` or a `postcss.config.js`, and
add your plugins and options directly into this file. Like this:

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

# How the class names extraction works?

PostCSS allows, after the compilation of your CSS, to do what you want with the
JSON interfaces it provides. Right now, it extracts all the information and
dump them into the correct ClojureScript file. It is really easy to write
another function to convert the JSON into another language.

# Contributing?

All contribution is welcome! Please, PR or open an issue!
