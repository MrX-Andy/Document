### 有状态的widget  
```

void main() => runApp(new MaterialApp(title: "Alex", home: new Counter()));

class Counter extends StatefulWidget {
  @override
  _CounterState createState() => new _CounterState();
}

class _CounterState extends State<Counter> {
  int _counter = 0;

  void _increment() {
    setState(() {
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    return new Row(
      children: <Widget>[
        new RaisedButton(onPressed: _increment,
          child: new Text("increment"),
        ),
        new Text('Count: $_counter')
      ],
    );
  }
}
```