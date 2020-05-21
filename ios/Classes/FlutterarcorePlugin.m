#import "FlutterarcorePlugin.h"
#if __has_include(<flutterarcore/flutterarcore-Swift.h>)
#import <flutterarcore/flutterarcore-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutterarcore-Swift.h"
#endif

@implementation FlutterarcorePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterarcorePlugin registerWithRegistrar:registrar];
}
@end
