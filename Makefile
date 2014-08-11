npm:
	npm install

bower:
	./node_modules/.bin/bower install

gulp:
	./node_modules/.bin/gulp clean
	./node_modules/.bin/gulp

install: npm bower gulp
