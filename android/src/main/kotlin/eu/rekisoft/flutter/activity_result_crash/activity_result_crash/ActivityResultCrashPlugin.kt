package eu.rekisoft.flutter.activity_result_crash.activity_result_crash

import android.content.Intent
import android.preference.PreferenceManager
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar

/** ActivityResultCrashPlugin */
public class ActivityResultCrashPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private var binding: ActivityPluginBinding? = null
  private var asyncResult: Result? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "activity_result_crash")
    channel.setMethodCallHandler(this);
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "activity_result_crash")
      channel.setMethodCallHandler(ActivityResultCrashPlugin())
    }
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "letItCrash") {
      println("letItCrash() invoked")
      if(binding == null) {
        // activity was not yet bounded
        asyncResult = result
      } else {
        letItCrash(binding!!, result)
      }
    } else {
      result.notImplemented()
    }
  }

  private fun letItCrash(binding: ActivityPluginBinding, result: Result) {
    println("Adding ActivityResultListeners")
    binding.addActivityResultListener(object: PluginRegistry.ActivityResultListener {
      override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        println("Let it crash by removing the listener")
        binding.removeActivityResultListener(this)
        // assume that you waited for the result
        result.success(42)
        return true
      }
    })
    binding.addActivityResultListener { _, _, _ ->
      println("Listener 2 called, only executed when the bug is fixed")
      false
    }
    println("Starting second activity...")
    binding.activity.startActivityForResult(Intent(binding.activity, SecondActivity::class.java), 1)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onDetachedFromActivity() {
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    this.binding = binding
    // check if there is already a cached call...
    asyncResult?.let { result ->
      try {
        letItCrash(binding, result)
      } finally {
        // reset the cache
        asyncResult = null
      }
    }
  }

  override fun onDetachedFromActivityForConfigChanges() {
  }
}
