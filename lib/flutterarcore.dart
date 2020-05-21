import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class FlutterArcore {
  static const MethodChannel _channel = const MethodChannel('flutterarcore');

  static Future showArView({@required Map<String, dynamic> params}) async {
    final String version = await _channel.invokeMethod('launchAr', params);
    return version;
  }

  static Future<bool> checkArSupport() async {
    final bool result = await _channel.invokeMethod('check_compatible');
    return result;
  }
}
