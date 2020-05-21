import 'package:flutter/material.dart';
import 'package:flutterarcore/flutterarcore.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _arModelurl =
      "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf";

  @override
  void initState() {
    super.initState();
    _getCameraPermission();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Ar'),
        ),
        bottomNavigationBar: Material(
          child: GestureDetector(
            onTap: () async{
              bool support = await FlutterArcore.checkArSupport();
              print("support $support");
              FlutterArcore.showArView(params: {"url": _arModelurl});
            },
            child: Container(
              height: 48,
              color: Colors.orange,
              child: Center(
                child: Text(
                  "OPEN AR CAMERA",
                  style: TextStyle(fontWeight: FontWeight.w600),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  _getCameraPermission() async {
    var result = await Permission.camera.request();
    if (result.isGranted) {
      return;
    } else {
      _getCameraPermission();
    }
  }
}
