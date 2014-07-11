// SCOUT GUI
// (c) Copyright 2013-2014, BSI Business Systems Integration AG

scout.DesktopViewButtonBar = function(desktop, $parent, viewButtons) {
  this.$div;
  this.desktop = desktop;
  this.session = desktop.session;

  //  create container
  var $desktopView = $parent.appendDiv('DesktopViews');
  this.$div = $desktopView;

  //  add view-item, all before #viewAdd
  for (var i = 0; i < viewButtons.length; i++) {
    var button = viewButtons[i];
    button.render($desktopView);
  }

  //  create logo and plus sign
  $desktopView.appendDiv('ViewAdd').on('click', '', addOwnView);
  $desktopView.appendDiv('ViewLogo').delay(1000).animateAVCSD('width', 40, null, null, 1000);

  var that = this;
  function addOwnView() {
    var name = $desktopView.children('.selected').text().split('(');
    var c = name.length > 1 ? parseInt(name[1], 0) + 1 : 2;
    //TODO widget id?
    var dummyModel = {
      'id': 'ownView' + c,
      'text': name[0] + ' (' + c + ')'
    }, buttonOwn;
    buttonOwn = new scout.DesktopViewButtonOwn($desktopView);
    buttonOwn.init(dummyModel, that.session);
    buttonOwn.render();
  }

};
