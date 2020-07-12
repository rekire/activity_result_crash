import 'package:flutter/material.dart';
import 'dart:async';

import 'package:activity_result_crash/activity_result_crash.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  @override
  void initState() {
    super.initState();
    letItCrash();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> letItCrash() async {
    await ActivityResultCrash.letItCrash();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Good, this bug was fixed'),
        ),
      ),
    );
  }
}
