import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key? key, required this.title}) : super(key: key);
  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;
  static const platform = MethodChannel('samples.flutter.dev/battery');
  dynamic values;

  void _incrementCounter() {
    phoneRequest();
    setState(() {
      _counter++;
    });
  }

  Future<void> _getBatteryLevel() async {
    // List<String> values = [];
    try {
      // final int result = await platform.invokeMethod('getBatteryLevel');
      final dynamic result = await platform.invokeMethod('getBatteryLevel');
      setState(() {
        values = result;
      });
    } on PlatformException catch (e) {
      throw e.message.toString();
    }
    await Fluttertoast.showToast(
        msg: values.length.toString(),
        toastLength: Toast.LENGTH_SHORT,
        gravity: ToastGravity.CENTER,
        backgroundColor: Colors.grey,
        textColor: Colors.white,
        fontSize: 12.0);
  }

  Future<void> phoneRequest() async {
    final PermissionStatus permissionStatus = await Permission.phone.request();
    if (permissionStatus.isGranted) {
      await _getBatteryLevel();
    } else if (permissionStatus.isPermanentlyDenied) {
      await openAppSettings();
    }
  }

  @override
  void initState() {
    phoneRequest();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            if (values.length != null)
              Expanded(
                  child: ListView.builder(
                      itemCount: values.length,
                      itemBuilder: (BuildContext context, int index) {
                        return GestureDetector(
                          onTap: () async {
                            final PermissionStatus permissionStatus =
                                await Permission.sms.request();
                            if (permissionStatus.isGranted) {
                              try {
                                final dynamic result =
                                    await platform.invokeMethod('SMS', {
                                  'selectedSimSlot': index,
                                });
                                values = result;
                              } on PlatformException catch (e) {
                                throw e.message.toString();
                              }
                              await Fluttertoast.showToast(
                                  msg: values.length.toString(),
                                  toastLength: Toast.LENGTH_SHORT,
                                  gravity: ToastGravity.CENTER,
                                  backgroundColor: Colors.grey,
                                  textColor: Colors.white,
                                  fontSize: 12.0);
                            } else if (permissionStatus.isPermanentlyDenied) {
                              await openAppSettings();
                            }
                          },
                          child: ListTile(
                              title: Text('${values[index].toString()}')),
                        );
                      }))
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: Icon(Icons.add),
      ),
    );
  }
}
