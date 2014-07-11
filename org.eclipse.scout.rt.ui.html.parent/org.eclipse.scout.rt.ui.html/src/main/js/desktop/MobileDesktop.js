// SCOUT GUI
// (c) Copyright 2013-2014, BSI Business Systems Integration AG

scout.MobileDesktop = function() {
  scout.MobileDesktop.parent.call(this);
  this.areas = {};
  this._addAdapterProperties(['breadCrumbNavigation']);
};
scout.inherits(scout.MobileDesktop, scout.BaseDesktop);

/**
 * @override
 */
scout.MobileDesktop.prototype._render = function($parent) {
  var  marginTop=0;
  this.$parent = $parent;

  //FIXME CGU in menu auslagern, bzw. wichtigste rechts oben darstellen
  //  if (this.toolButtons) {
  //    tools = new scout.MobileDesktopToolButtons(this.toolButtons, this.session);
  //    tools.render($parent);
  //    marginTop = tools.$div.outerHeight();
  //  }

  this.layout = new scout.BorderLayout(marginTop, 0, 'desktop-area');
  if (this.breadCrumbNavigation) {
    this.breadCrumbNavigation.render($parent);
  }

  scout.MobileDesktop.parent.prototype._render.call(this, $parent);
};

/**
 * @override
 */
scout.MobileDesktop.prototype._resolveViewContainer = function(form) {
  return this.areas[form.displayViewId];
};

/**
 * @override
 */
scout.MobileDesktop.prototype._addForm = function(form) {
  var layoutDirty = false;
  if (form.displayHint == 'view') {
    var position = form.displayViewId;
    if (position !== 'C' && position !== 'E') {
      position = 'C';
    }
    var $area = this.areas[position];
    if (!$area) {
      if (position === 'C') {
        $area = this._createMainArea();
      } else {
        $area = this.$parent.appendDiv();
      }
      $area.data('forms', []);
      this.layout.register($area, position);
      this.areas[position] = $area;
      layoutDirty = true;
    }
    $area.data('forms').push(form);
  }

  if (layoutDirty) {
    this.layout.layout();
  }
  scout.MobileDesktop.parent.prototype._addForm.call(this, form);
};

scout.MobileDesktop.prototype._createMainArea = function() {
  var $area = this.$parent.appendDiv();
  if (this.breadCrumbs) {
    this.breadCrumbs.attach($area);
  }
  return $area;
};

/**
 * @override
 */
scout.MobileDesktop.prototype._removeForm = function(form) {
  scout.MobileDesktop.parent.prototype._removeForm.call(this, form);

  if (form.displayHint == 'view') {
    var position = form.displayViewId;
    if (position !== 'C' && position !== 'E') {
      position = 'C';
    }
    var $area = this.areas[position];
    var forms = $area.data('forms');
    scout.arrays.remove(forms, form);

    if (forms.length === 0) {
      $area.remove();
      this.areas[position] = null;
      this.layout.unregister($area);
      this.layout.layout();
    } else {
      $area.data('forms', forms);
    }
  }
};
