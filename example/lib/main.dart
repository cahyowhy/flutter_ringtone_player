import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_ringtone_player/flutter_ringtone_player.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  MethodChannel _channel = const MethodChannel('ANDROID_CHANNEL');

  @override
  void initState() {
    super.initState();
  }

  onPlayCustomURI() async {
    try {
      var resID = await _channel.invokeMethod('getResource', {
        "filePath": "alarm_clock",
      });
      await FlutterRingtonePlayer.stop();
      await FlutterRingtonePlayer.playURI(
        android: resID,
        ios: IosSounds.glass.value,
        volume: 1.0,
        streamType: 4,
        fromRes: true,
      );
    } on PlatformException {}
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Ringtone player'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              Padding(
                padding: EdgeInsets.all(8),
                child: RaisedButton(
                  child: const Text('playAlarm'),
                  onPressed: () {
                    FlutterRingtonePlayer.playAlarm();
                  },
                ),
              ),
              Padding(
                padding: EdgeInsets.all(8),
                child: RaisedButton(
                  child: const Text('playNotification'),
                  onPressed: () {
                    FlutterRingtonePlayer.playNotification();
                  },
                ),
              ),
              Padding(
                padding: EdgeInsets.all(8),
                child: RaisedButton(
                  child: const Text('playRingtone'),
                  onPressed: () {
                    FlutterRingtonePlayer.playRingtone();
                  },
                ),
              ),
              Padding(
                padding: EdgeInsets.all(8),
                child: RaisedButton(
                  child: const Text('play'),
                  onPressed: () async {
                    await FlutterRingtonePlayer.stop();
                    await FlutterRingtonePlayer.play(
                      android: AndroidSounds.notification,
                      ios: IosSounds.glass,
                      volume: 1.0,
                      streamType: 4,
                    );
                  },
                ),
              ),
              Padding(
                padding: EdgeInsets.all(8),
                child: RaisedButton(
                  child: const Text('play custom uri'),
                  onPressed: onPlayCustomURI,
                ),
              ),
              Padding(
                padding: EdgeInsets.all(8),
                child: RaisedButton(
                  child: const Text('stop'),
                  onPressed: () {
                    FlutterRingtonePlayer.stop();
                  },
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
