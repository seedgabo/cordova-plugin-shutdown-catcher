
var exec = require('cordova/exec');

var PLUGIN_NAME = 'ShutdownCatcher';

var ShutdownCatcher = {
  catchShutdown: function(cb) {
    exec(cb, null, PLUGIN_NAME, 'catchShutdown', []);
  }
};

module.exports = ShutdownCatcher;
