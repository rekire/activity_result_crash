package eu.rekisoft.flutter.activity_result_crash.activity_result_crash

import android.content.Intent
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
  private lateinit var channel : MethodChannel
  private var binding: ActivityPluginBinding? = null
  private var asyncResult: Result? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "activity_result_crash")
    channel.setMethodCallHandler(this);
  }

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

  private class DummyListener(
    private val binding: ActivityPluginBinding,
    private val result: Result): PluginRegistry.ActivityResultListener {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
      // There must be a second listener which should be invoked afterwards to cause the crash.
      // Since the listeners are in a HashSet we cannot simply control the order so I used this hack
      return if (firstListener) {
        firstListener = false
        println("Let it crash by removing the listener")
        binding.removeActivityResultListener(this)
        // assume that you waited for the result
        result.success(42)
        true
      } else {
        // won't be called currently due the bug
        println("Second listener called successfully")
        false
      }
    }

    companion object {
      private var firstListener = true
    }
  }

  private fun letItCrash(binding: ActivityPluginBinding, result: Result) {
    println("Adding ActivityResultListeners")
    binding.addActivityResultListener(DummyListener(binding, result))
    binding.addActivityResultListener(DummyListener(binding, result))
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
