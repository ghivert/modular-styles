const path = require('path')
const fs = require('fs')
const gulp = require('gulp')
const postcss = require('gulp-postcss')
const concat = require('gulp-concat')
const debug = require('gulp-debug')
const plumber = require('gulp-plumber')
const gulpif = require('gulp-if')
const cssnano = require('cssnano')
const importCwd = require('import-cwd')

const cljsGetJSON = require('../dist/main')

const getFile = filePath => {
  try {
    return require(filePath)
  } catch (error) {
    return null
  }
}

const getConfig = () => {
  return (
    getFile(path.resolve('.postcssrc.js')) ||
    getFile(path.resolve('.postcss.config.js')) ||
    getFile(path.resolve('.postcssrc.json')) ||
    {}
  )
}

const usePlugins = plugins => {
  return Object.entries(plugins).map(([plugin, options]) => {
    return importCwd(plugin)(options)
  })
}

const postcssModules = ({ sourcePath, filesPath, destPath, language }) => {
  return require('postcss-modules')({
    getJSON: cljsGetJSON(sourcePath, filesPath, destPath, language),
  })
}

const computeOptions = options => {
  const config = getConfig()
  const plugs = usePlugins(config.plugins || {})
  return {
    ...config,
    plugins: [postcssModules(options), ...plugs],
  }
}

const allFilesPlugins = [cssnano()]

const css = ({
  sourcePath,
  destPath,
  filesPath,
  extension,
  tempCSS,
  bundleName,
  bundleCSSPath,
  language,
}) => {
  const { plugins, ...options } = computeOptions({
    sourcePath,
    filesPath,
    destPath,
    language,
  })
  return () => {
    return gulp
      .src(path.resolve(filesPath, `**/*.${extension || 'css'}`))
      .pipe(plumber())
      .pipe(debug({ title: 'Beginning compiling CSS Modules...' }))
      .pipe(postcss(plugins, options))
      .pipe(gulpif(!!tempCSS, gulp.dest(path.resolve(tempCSS || ''))))
      .pipe(concat(bundleName || 'styles.css'))
      .pipe(postcss(allFilesPlugins))
      .pipe(gulp.dest(path.resolve(bundleCSSPath || './public')))
      .pipe(debug({ title: 'CSS Modules compilation finished!' }))
  }
}

const styles = options => () => {
  gulp.watch(options.filesPath, css(options))
}

const watch = options => {
  gulp.task('watch-styles', gulp.series(css(options), styles(options)))
  gulp.task('watch-styles')()
}

const compile = options => {
  gulp.task('css', css(options))
  gulp.task('css')()
}

module.exports = {
  compile,
  watch,
}
