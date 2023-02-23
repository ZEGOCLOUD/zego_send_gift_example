// Dart imports:
import 'dart:async';
import 'dart:convert';
import 'dart:math';

// Flutter imports:
import 'package:flutter/material.dart';
import 'package:svgaplayer_flutter/parser.dart';
import 'package:svgaplayer_flutter/player.dart';
import 'package:http/http.dart' as http;
import 'package:svgaplayer_flutter/proto/svga.pb.dart';

// Package imports:
import 'package:zego_uikit_prebuilt_live_audio_room/zego_uikit_prebuilt_live_audio_room.dart';

/// Note that the userID needs to be globally unique,
final String localUserID = Random().nextInt(10000).toString();

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(title: 'Flutter Demo', home: HomePage());
  }
}

class HomePage extends StatelessWidget {
  HomePage({Key? key}) : super(key: key);

  /// Users who use the same liveID can join the same live audio room.
  final roomIDTextCtrl =
      TextEditingController(text: Random().nextInt(10000).toString());

  @override
  Widget build(BuildContext context) {
    var buttonStyle = ElevatedButton.styleFrom(
      fixedSize: const Size(120, 60),
    );

    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 10),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('User ID:$localUserID'),
            const Text('Please test with two or more devices'),
            TextFormField(
              controller: roomIDTextCtrl,
              decoration: const InputDecoration(labelText: "join a live by id"),
            ),
            const SizedBox(height: 20),
            // click me to navigate to LivePage
            ElevatedButton(
              style: buttonStyle,
              child: const Text('Start a live'),
              onPressed: () => jumpToLivePage(
                context,
                roomID: roomIDTextCtrl.text,
                isHost: true,
              ),
            ),
            const SizedBox(height: 20),
            // click me to navigate to LivePage
            ElevatedButton(
              style: buttonStyle,
              child: const Text('Watch a live'),
              onPressed: () => jumpToLivePage(
                context,
                roomID: roomIDTextCtrl.text,
                isHost: false,
              ),
            ),
          ],
        ),
      ),
    );
  }

  jumpToLivePage(BuildContext context,
      {required String roomID, required bool isHost}) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => LivePage(
          roomID: roomID,
          isHost: isHost,
        ),
      ),
    );
  }
}

class LivePage extends StatefulWidget {
  final String roomID;
  final bool isHost;

  const LivePage({
    Key? key,
    required this.roomID,
    this.isHost = false,
  }) : super(key: key);

  @override
  State<LivePage> createState() => _LivePageState();
}

class _LivePageState extends State<LivePage> {
  final List<StreamSubscription<dynamic>?> subscriptions = [];
  var animationVisibility = ValueNotifier<bool>(true);

  static const yourAppID = Your App ID;
  static const yourAppSign = 'Your App Sign';
  static const yourServerSecret = 'Your Server Secret';

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
      subscriptions
        ..add(ZegoUIKit()
            .getSignalingPlugin()
            .getInRoomTextMessageReceivedEventStream()
            .listen(onInRoomTextMessageReceived))
        ..add(ZegoUIKit()
            .getInRoomCommandReceivedStream()
            .listen(onInRoomCommandReceived));
    });
  }

  @override
  void dispose() {
    super.dispose();
    for (var subscription in subscriptions) {
      subscription?.cancel();
    }
  }

  // if you use reliable message channel, you need subscription this method.
  void onInRoomTextMessageReceived(
      ZegoSignalingPluginInRoomTextMessageReceivedEvent event) {
    final messages = event.messages;
    debugPrint("onInRoomTextMessageStream:$messages");
    // You can display different animations according to gift-type
    var message = messages.first;
    if (message.senderUserID != localUserID) {
      GiftWidget.show(context, "assets/sports-car.svga");
    }
  }

  // if you use unreliable message channel, you need subscription this method.
  void onInRoomCommandReceived(ZegoInRoomCommandReceivedData commandData) {
    debugPrint(
        "onInRoomCommandReceived, fromUser:${commandData.fromUser}, command:${commandData.command}");
    // You can display different animations according to gift-type
    if (commandData.fromUser.id != localUserID) {
      GiftWidget.show(context, "assets/sports-car.svga");
    }
  }

  Future<void> _sendGift() async {
    late http.Response response;
    try {
      response = await http.post(
        //http://localhost:3000
        //https://zego-example-server-nextjs.vercel.app
        Uri.parse(
            'https://zego-example-server-nextjs.vercel.app/api/send_gift'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'app_id': yourAppID,
          'server_secret': yourServerSecret,
          'room_id': widget.roomID,
          'user_id': localUserID,
          'user_name': 'user_$localUserID',
          'gift_type': 1001,
          'gift_count': 1,
          'timestamp': DateTime.now().millisecondsSinceEpoch,
        }),
      );

      if (response.statusCode == 200) {
        // When the gift giver calls the gift interface successfully,
        // the gift animation can start to be displayed
        GiftWidget.show(context, "assets/sports-car.svga");
      }
    } on Exception catch (error) {
      debugPrint("[ERROR], store fcm token exception, ${error.toString()}");
    }
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Stack(
        children: [
          ZegoUIKitPrebuiltLiveAudioRoom(
            appID: yourAppID,
            appSign: yourAppSign,
            userID: localUserID,
            userName: 'user_$localUserID',
            roomID: widget.roomID,
            config: (widget.isHost
                ? ZegoUIKitPrebuiltLiveAudioRoomConfig.host()
                : ZegoUIKitPrebuiltLiveAudioRoomConfig.audience())
              ..takeSeatIndexWhenJoining = widget.isHost ? 0 : -1
              ..bottomMenuBarConfig =
                  ZegoBottomMenuBarConfig(maxCount: 5, audienceExtendButtons: [
                ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    fixedSize: const Size(40, 40),
                    shape: const CircleBorder(),
                  ),
                  onPressed: () {
                    _sendGift();
                  },
                  child: const Icon(Icons.blender),
                )
              ]),
          ),
        ],
      ),
    );
  }
}

class GiftWidget extends StatefulWidget {
  static OverlayEntry? currentGiftEntries;
  static List<String> giftEntryPathCache = [];

  static void show(BuildContext context, String giftPath) {
    if (null != currentGiftEntries) {
      debugPrint("has gift displaying, cache $giftPath");

      giftEntryPathCache.add(giftPath);

      return;
    }

    currentGiftEntries = OverlayEntry(builder: (context) {
      return GiftWidget(
        giftPath: giftPath,
        onRemove: () {
          if (currentGiftEntries?.mounted ?? false) {
            currentGiftEntries?.remove();
          }
          currentGiftEntries = null;

          if (giftEntryPathCache.isNotEmpty) {
            var nextGiftPath = giftEntryPathCache.first;
            giftEntryPathCache.removeAt(0);

            debugPrint("has gift cache, play $nextGiftPath");

            GiftWidget.show(context, nextGiftPath);
          }
        },
      );
    });

    Overlay.of(context, rootOverlay: false).insert(currentGiftEntries!);
  }

  static bool clear() {
    if (currentGiftEntries?.mounted ?? false) {
      currentGiftEntries?.remove();
    }

    return true;
  }

  const GiftWidget({Key? key, required this.onRemove, required this.giftPath})
      : super(key: key);

  final VoidCallback onRemove;
  final String giftPath;

  @override
  State<GiftWidget> createState() => GiftWidgetState();
}

class GiftWidgetState extends State<GiftWidget>
    with SingleTickerProviderStateMixin {
  SVGAAnimationController? animationController;
  late Future<MovieEntity> movieEntity;

  @override
  void initState() {
    super.initState();
    movieEntity = SVGAParser.shared.decodeFromAssets(widget.giftPath);
  }

  @override
  void dispose() {
    animationController?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<MovieEntity>(
      future: movieEntity,
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          animationController ??= (SVGAAnimationController(vsync: this)
            ..videoItem = snapshot.data as MovieEntity
            ..forward().whenComplete(() {
              widget.onRemove();
            }));
          return SVGAImage(animationController!);
        } else if (snapshot.hasError) {
          return Text('${snapshot.error}');
        } else {
          return const CircularProgressIndicator();
        }
      },
    );
  }
}
