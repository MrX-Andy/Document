### demo  
```

class TutorialHome extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        leading: new IconButton(
          icon: new Icon(Icons.menu),
          onPressed: null,
          tooltip: "menu",
        ),
        title: new Text("title"),
        actions: <Widget>[
          new IconButton(
              icon: new Icon(Icons.search), onPressed: null, tooltip: "search")
        ],
      ),
      body: new Center(
        child: new Text("hello world"),
      ),
      floatingActionButton: new FloatingActionButton(
          tooltip: "Add", child: new Icon(Icons.add), onPressed: null),
    );
  }
}
```