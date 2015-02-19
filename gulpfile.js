var gulp = require('gulp');
var bs = require('browser-sync');

gulp.task('bs', function () {
	bs({
		server: {
			baseDir: './src/main/webapp/'
		}
	});
});

gulp.task('default', ['bs'],function () {
	gulp.watch('./**/*.{html,js,css}', function () {
		bs.reload();
	});
});
