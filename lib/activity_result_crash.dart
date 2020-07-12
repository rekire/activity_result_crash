import 'dart:async';

import 'package:flutter/services.dart';

class ActivityResultCrash {
  static const MethodChannel _channel =
      const MethodChannel('activity_result_crash');

  static Future letItCrash() async {
    await _channel.invokeMethod('letItCrash');
  }
}
