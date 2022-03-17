package io.inway.ringtone.player;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterRingtonePlayerPlugin
 */
public class FlutterRingtonePlayerPlugin implements MethodCallHandler, FlutterPlugin {
    private Context context;
    private MethodChannel methodChannel;
    private RingtoneManager ringtoneManager;
    private Ringtone ringtone;

    /**
     * Plugin registration.
     */
    @SuppressWarnings("deprecation")
    public static void registerWith(Registrar registrar) {
        new FlutterRingtonePlayerPlugin().onAttachedToEngine(registrar.context(), registrar.messenger());
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        onAttachedToEngine(binding.getApplicationContext(), binding.getBinaryMessenger());
    }

    private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
        this.context = applicationContext;
        this.ringtoneManager = new RingtoneManager(context);
        this.ringtoneManager.setStopPreviousRingtone(true);

        methodChannel = new MethodChannel(messenger, "flutter_ringtone_player");
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        context = null;
        methodChannel.setMethodCallHandler(null);
        methodChannel = null;
    }

    public Uri resourceToUri(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID));
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call,@NonNull Result result) {
        try {
            Uri ringtoneUri = null;

            if (call.method.equals("play") && !call.hasArgument("android")) {
                result.notImplemented();
                return;
            }

            if (call.method.equals("stop")) {
                if (ringtone != null) {
                    ringtone.stop();
                }

                result.success(null);
                return;
            }
            
            boolean fromRes = false;
            if (call.method.equals("play")) {
                final int kind = call.argument("android");
                if (call.hasArgument("fromRes")) {
                    fromRes = call.argument("fromRes");
                }

                if (fromRes) {
                    ringtoneUri = resourceToUri(context, kind);
                } else {
                    switch (kind) {
                        case 1:
                            ringtoneUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                            break;
                        case 2:
                            ringtoneUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                            break;
                        case 3:
                            ringtoneUri = Settings.System.DEFAULT_RINGTONE_URI;
                            break;
                        default:
                            result.notImplemented();
                            return;
                    }
                }
            }

            if (ringtoneUri != null) {
                if (ringtone != null) {
                    ringtone.stop();
                }
                ringtone = ringtoneManager.getRingtone(context, ringtoneUri);

                if (call.hasArgument("volume")) {
                    final double volume = call.argument("volume");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ringtone.setVolume((float) volume);
                    }
                }

                if (call.hasArgument("looping")) {
                    final boolean looping = call.argument("looping");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ringtone.setLooping(looping);
                    }
                }

                if (call.hasArgument("streamType")) {
                    final int streamType = call.argument("streamType");
                    List<Integer> availableStream = Arrays.asList(
                            AudioManager.STREAM_ALARM,
                            AudioManager.STREAM_ACCESSIBILITY,
                            AudioManager.STREAM_ALARM,
                            AudioManager.STREAM_DTMF,
                            AudioManager.STREAM_MUSIC,
                            AudioManager.STREAM_NOTIFICATION,
                            AudioManager.STREAM_RING,
                            AudioManager.STREAM_SYSTEM,
                            AudioManager.STREAM_VOICE_CALL
                    );
                    boolean hasStream = availableStream.contains(streamType);
                    if (!hasStream) {
                        result.error("Exception", "Invalid streamType", null);
                        return;
                    }

                    ringtone.setStreamType(streamType);
                }


                ringtone.play();
                result.success(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.error("Exception", e.getMessage(), null);
        }
    }
}
