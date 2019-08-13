#! /usr/bin/env node

const program = require('commander')
const main = require('../src/main')

const convertOptions = options => ({
  sourcePath: options.source,
  filesPath: options.files,
  destPath: options.dest,
  extensions: options.extension,
  bundlerName: options.bundleName,
  bundlePath: options.bundlePath,
  tempCSS: options.tempCSS,
})

program.version('0.0.1')

program
  .command('compile')
  .option('--files <filesPath>', 'Source path for files.')
  .option('--dest <destPath>', 'Destination path for interfaces.')
  .option('--source <sourcePath>', 'Source path for project.')
  .option('--extension <extension>', 'Stylesheets extension. Defaults to css.')
  .option('--tempCSS <tempCSS>', 'Path for temporary stylesheets.')
  .option('--bundleName <bundleName>', 'Name for resulting CSS bundle. Defaults to styles.css.')
  .option('--bundlePath <bundleCSSPath>', 'Path for resulting CSS bundle. Defaults to public.')
  .action(options => {
    main.compile(convertOptions(options))
  })

program
  .command('watch')
  .option('--files <filesPath>', 'Source path for files.')
  .option('--dest <destPath>', 'Destination path for interfaces.')
  .option('--source <sourcePath>', 'Source path for project.')
  .option('--extension <extension>', 'Stylesheets extension. Defaults to css.')
  .option('--tempCSS <tempCSS>', 'Path for temporary stylesheets.')
  .option('--bundlName <bundleName>', 'Name for resulting CSS bundle. Defaults to styles.css.')
  .option('--bundlePath <bundleCSSPath>', 'Path for resulting CSS bundle. Defaults to public.')
  .action((options) => {
    main.watch(convertOptions(options))
  })

program.parse(process.argv)
