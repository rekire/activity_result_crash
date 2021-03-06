#import "ActivityResultCrashPlugin.h"
#if __has_include(<activity_result_crash/activity_result_crash-Swift.h>)
#import <activity_result_crash/activity_result_crash-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "activity_result_crash-Swift.h"
#endif

@implementation ActivityResultCrashPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftActivityResultCrashPlugin registerWithRegistrar:registrar];
}
@end
