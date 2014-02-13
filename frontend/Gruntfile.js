'use strict';

module.exports = function (grunt) {

  require('load-grunt-tasks')(grunt);
  require('time-grunt')(grunt);

  grunt.initConfig({
    watch: {
      sass: {
        files: ['sass/*.scss'],
        tasks: ['sass-compile']
      }
    },
    karma: {
      unit: {
        configFile: 'karma.conf.js',
        autoWatch: false,
        singleRun: true
      },
      unit_ff: {
        configFile: 'karma.conf.js',
        autoWatch: false,
        singleRun: true,
        browsers: ['Firefox'],
        colors: false
      },
      unit_auto: {
        configFile: 'karma.conf.js'
      }
    },
    bowercopy: {
      libs: {
        options : {
          destPrefix : '../resources/public/js/bower_components'
        },
        files: {
          'angular.js': 'angular/angular.js',
          'lodash.js': 'lodash/dist/lodash.js'
        }
      },
      test_libs: {
        options : {
          destPrefix : 'test/bower_components'
        },
        files: {
          'angular-mocks.js': 'angular-mocks/angular-mocks.js',
        }
      }
    },
    sass: {
      dist : {
        files: {
          '../resources/public/compiled_css/main.css': 'sass/main.scss'
        }
      }
    }
  });

  grunt.registerTask('test', ['karma:unit']);

  grunt.registerTask('test_ff', ['karma:unit_ff']);

  grunt.registerTask('autotest', ['karma:unit_auto']);

  grunt.registerTask('bower', ['bowercopy:libs', 'bowercopy:test_libs']);

  grunt.registerTask('sass-compile', ['sass:dist']);

  grunt.registerTask('sass-watch', ['sass-compile', 'watch:sass']);
};
