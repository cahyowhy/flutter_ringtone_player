package io.inway.ringtone.player_example;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        BinaryMessenger binaryMessenger = flutterEngine.getDartExecutor().getBinaryMessenger();
        MethodChannel channelAndroid = new MethodChannel(binaryMessenger, "ANDROID_CHANNEL");
        channelAndroid.setMethodCallHandler((call, result) -> {
            if (call.method.equals("getResource")) {
                getRawID(call, result);
                return;
            }
            result.notImplemented();
        });
    }

    private void getRawID(MethodCall call, MethodChannel.Result result) {
        if (!call.hasArgument("filePath")) {
            result.error("Exception", "file empty", null);
            return;
        }

        try {
            String filePath = call.argument("filePath");
            Field field = R.raw.class.getDeclaredField(filePath);
            result.success(field.getInt(field));
        } catch (Exception e) {
            result.error("Exception", e.getMessage(), null);
            return;
        }
    }
}
