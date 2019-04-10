const path = require('path');
const webpack = require('webpack');

const CleanWebpackPlugin = require('clean-webpack-plugin');
const CopyPlugin = require('copy-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const OptimizeCssAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const WebpackShellPlugin = require('webpack-shell-plugin');

module.exports = (env, args) => {
  var devMode = args.mode !== 'production';
  var cssFilename = devMode ? '[name].css' : '[name]-[contenthash].min.css';

  return {
    target: 'web',

    /* ------------------------------------------------------
     * + Entry                                              +
     * ------------------------------------------------------ */
    entry: {
      // TODO [awe] toolstack: per Default erzeugt Webpack für jeden entry ein .js file
      // Dieses müssen wir nachträglich mit Post-Processing o.ä. löschen (oder einfach ignorieren).
      // Grundsätzlich sollten wir im Team diskutieren ob, wir die LESS Definitionen code-nah haben
      // wollen, d.h. jede JS Komponente macht ein require auf seine LESS-Datei. Dieser Ansatz erlaubt
      // Unterstützung durch die IDE (Warning, wenn eine CSS Klasse im LESS File nicht existiert), hat
      // aber Probleme wenn wir mehrere Themes haben wollen. Wir könnten zwar im index.js/module.js
      // alle Themes mit einem require auflisten, das hat aber den Nachteil, dass dann per Default das
      // CSS von allen Themes im gleichen File landet und man diese erst mit SplitChunks wieder separieren
      // müsste, was ähnlich hässlich ist wie Post-Processing und Löschen.
      //
      // Evtl. könnte man auch beides machen, d.h. sowohl require in den Komponenten als auch entry
      // points für alle Themes definieren.
      // Evtl. hilft das ManifestPlugin beim aufräumen, check:
      // https://webpack.js.org/guides/output-management#cleaning-up-the-dist-folder
      scout: './src/module.js',
      'theme-default': './src/theme-default.less',
      'theme-dark': './src/theme-dark.less'
    },
    /* ------------------------------------------------------
     * + Output                                             +
     * ------------------------------------------------------ */
    output: {
      filename: '[name].js',
      path: __dirname + '/dist'
    },
    /* ------------------------------------------------------
     * + Optimization                                       +
     * ------------------------------------------------------ */
    optimization: {
      // # Minify CSS
      minimizer: [
        // Used to minify CSS assets (by default, run when mode is 'production')
        // see: https://github.com/NMFR/optimize-css-assets-webpack-plugin
        new OptimizeCssAssetsPlugin({
          assetNameRegExp: /\.min\.css$/g
        })
      ]
    },
    /* ------------------------------------------------------
     * + Modules                                            +
     * ------------------------------------------------------ */
    module: {
      // LESS
      rules: [{
        test: /\.less$/,
        use: [{
          // Extracts CSS into separate files. It creates a CSS file per JS file which contains CSS.
          // It supports On-Demand-Loading of CSS and SourceMaps.
          // see: https://webpack.js.org/plugins/mini-css-extract-plugin/
          //
          // TODO [awe] toolstack: discuss with MVI - unnecessary .js files
          // Note: this creates some useless *.js files, like dark-theme.js
          // This seems to be an issue in webpack, workaround is to remove the files later
          // see: https://github.com/webpack-contrib/mini-css-extract-plugin/issues/151
          loader: MiniCssExtractPlugin.loader
        }, {
          // Interprets @import and url() like import/require() and will resolve them.
          // see: https://webpack.js.org/loaders/css-loader/
          loader: 'css-loader', options: {
            sourceMap: true
          }
        }, {
          // Compiles Less to CSS.
          // see: https://webpack.js.org/loaders/less-loader/
          loader: 'less-loader', options: {
            sourceMap: true
          }
        }]
      }]
    },
    /* ------------------------------------------------------
     * + Devtool                                            +
     * ------------------------------------------------------ */
    // This option controls if and how source maps are generated.
    // see: https://webpack.js.org/configuration/devtool/
    devtool: 'inline-source-map',
    /* ------------------------------------------------------
     * + Plugins                                            +
     * ------------------------------------------------------ */
    plugins: [
      // see: https://webpack.js.org/plugins/mini-css-extract-plugin/
      new MiniCssExtractPlugin({
        filename: cssFilename
      }),
      // see: https://webpack.js.org/guides/output-management/#cleaning-up-the-dist-folder
      new CleanWebpackPlugin(),
      // see: https://www.npmjs.com/package/webpack-shell-plugin
      new WebpackShellPlugin({
        onBuildEnd:['node post-build.js']
      }),
      // https://www.npmjs.com/package/copy-webpack-plugin
      new CopyPlugin([
        { from: 'res', to: '.' }
      ]),
      // Shows progress information in the console
      new webpack.ProgressPlugin()
    ]
  };
}
