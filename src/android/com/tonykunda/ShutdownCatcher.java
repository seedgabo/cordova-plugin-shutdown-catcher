package com.tonykunda;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Date;

public class ShutdownCatcher extends CordovaPlugin {
  private static final String TAG = "ShutdownCatcher";
  BroadcastReceiver receiver;
  private CallbackContext jsCallback = null;

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    this.receiver = null;
  }

  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if(action.equals("catchShutdown")) {
      BroadcastReceiver receiver = null;

      IntentFilter intentFilter = new IntentFilter();
      this.jsCallback = callbackContext;

      // ACTION_SHUTDOWN
      //intentFilter.addAction(Intent.ACTION_SCREEN_ON);
      intentFilter.addAction(Intent.ACTION_SHUTDOWN);
      if (this.receiver == null) {
        this.receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
              sendUpdate(false);
            }
        };
        webView.getContext().registerReceiver(this.receiver, intentFilter);
      }

      // Don't return any result now, since status results will be sent when events come in from broadcast receiver
      PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);
      return true;
    } else if (action.equals("stop")) {
      removeListener();
      this.sendUpdate(true); // release status callback in JS side
      this.jsCallback = null;
      callbackContext.success();
      return true;
    }
    return true;
  }

  private void removeListener() {
      if (this.receiver != null) {
          try {
              webView.getContext().unregisterReceiver(this.receiver);
              this.receiver = null;
          } catch (Exception e) {
              Log.d(TAG, "Error unregistering receiver: " + e.getMessage(), e);
          }
      }
  }

  /**
   * Stop battery receiver.
   */
  public void onDestroy() {
      removeListener();
  }

  /**
   * Stop battery receiver.
   */
  public void onReset() {
      removeListener();
  }

  private void sendUpdate(boolean release) {
    JSONObject obj = new JSONObject();
    try {
        if (!release) {
          obj.put("shutdown", true);
        } else {
          obj.put("release", true);
        }
    } catch (JSONException e) {
        Log.d(TAG, e.getMessage(), e);
    }
    PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
    result.setKeepCallback(!release);
    this.jsCallback.sendPluginResult(result);
  }

}
