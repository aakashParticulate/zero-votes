var gulp = require('gulp');
var concat = require('gulp-concat');
var header = require('gulp-header');
var less = require('gulp-less');
var minifyCss = require('gulp-minify-css');
var recess = require('gulp-recess');
var rename = require('gulp-rename');
var rimraf = require('gulp-rimraf');
var uglify = require('gulp-uglify');
var util = require('gulp-util');

var pkg = require('./package.json');
var currentYear = util.date(new Date(), 'yyyy');


var paths = {
    scripts: [
        './_resources/vendors/jquery/dist/jquery.js',
        './_resources/vendors/bootstrap/js/transition.js',
        './_resources/vendors/bootstrap/js/collapse.js',
        './_resources/scripts/main.js'
    ],
    styles: [
        './_resources/styles/zvotes.less'
    ],
    statics: [
        './_resources/statics/**/*'
    ],
    static_favicon: [
        './_resources/statics/favicon.ico'
    ],
    static_fonts: [
        './_resources/vendors/font-awesome/fonts/*'
    ],
    build: './ZVotes-web/src/main/resources/'
};

var bannerScripts = [
    '/*!',
    ' * <%= pkg.name %> v<%= pkg.version %> | Copyright (c) <%= currentYear %> <%= pkg.author %> | <%= pkg.homepage %>',
    ' * jQuery JavaScript Library v2.1.1 | Copyright (c) 2005, 2014 jQuery Foundation, Inc. and other contributors | MIT license | http://jquery.com',
    ' * Bootstrap v3.2.0 | Copyright (c) 2011-2014 Twitter, Inc. | MIT license | http://getbootstrap.com',
    ' */',
    ''
].join('\n');

var bannerStyles = [
    '/*!',
    ' * <%= pkg.name %> v<%= pkg.version %> | Copyright (c) 2012-<%= currentYear %> <%= pkg.author %> | <%= pkg.homepage %>',
    ' * normalize.css v3.0.1 | Copyright (c) Nicolas Gallagher and Jonathan Neal | MIT License | http://git.io/normalize',
    ' * Font Awesome v4.1.0 | by @davegandy | Font: SIL OFL 1.1, CSS: MIT License | http://fontawesome.io',
    ' * Bootstrap v3.2.0 | Copyright (c) 2011-2014 Twitter, Inc. | MIT license | http://getbootstrap.com',
    ' */',
    ''
].join('\n');


gulp.task('scripts', function() {
    return gulp.src(paths.scripts)
        .pipe(concat('zvotes.min.js'))
        .pipe(uglify())
        .pipe(header(bannerScripts, {pkg: pkg, currentYear: currentYear}))
        .pipe(gulp.dest(paths.build + 'js/'));
});

gulp.task('styles', function() {
    return gulp.src(paths.styles)
//        .pipe(recess())
        .pipe(less())
        .pipe(minifyCss({keepSpecialComments: 0}))
        .pipe(header(bannerStyles, {pkg: pkg, currentYear: currentYear}))
        .pipe(rename({suffix: '.min'}))
        .pipe(gulp.dest(paths.build + 'css/'));
});

gulp.task('staticsGeneral', function() {
    return gulp.src(paths.statics)
        .pipe(gulp.dest(paths.build))
});

gulp.task('staticsFavicon', function() {
    return gulp.src(paths.static_favicon)
        .pipe(gulp.dest('.'))
});

gulp.task('staticsFonts', function() {
    return gulp.src(paths.static_fonts)
        .pipe(gulp.dest(paths.build + 'fonts/'))
});

gulp.task('statics', ['staticsGeneral', 'staticsFavicon', 'staticsFonts']);

gulp.task('clean', function() {
    return gulp.src(paths.build, {read: false})
        .pipe(rimraf({force: true}));
});

gulp.task('watch', function() {
    gulp.watch(paths.scripts, ['scripts']);
    gulp.watch(paths.styles, ['styles']);
});

gulp.task('default', ['scripts', 'styles', 'statics']);
