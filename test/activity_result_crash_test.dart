import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:activity_result_crash/activity_result_crash.dart';

void main() {
  const MethodChannel channel = MethodChannel('activity_result_crash');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await ActivityResultCrash.platformVersion, '42');
  });
}
