// ---- Popup ----

scout.Popup = function(session) {
  scout.Popup.parent.call(this);
  this.$container;
  this.$body;
  this.session = session;
  this._mouseDownHandler;
  this.keyStrokeAdapter = this._createKeyStrokeAdapter();
};
scout.inherits(scout.Popup, scout.Widget);

/**
 * The popup is always appended to the HTML document body.
 * That way we never have z-index issues with the rendered popups.
 */
scout.Popup.prototype.render = function($parent) {
  scout.Popup.parent.prototype.render.call(this, $parent);
  setTimeout(function() {
    this.$container.installFocusContext('auto', this.session.uiSessionId);
  }.bind(this), 0);
  this._attachCloseHandler();
};

scout.Popup.prototype._render = function() {
  var $docBody = this.session.$entryPoint;
  this.$body = $.makeDiv('popup-body');
  this.$container = $.makeDiv('popup')
    .append(this.$body)
    .appendTo($docBody);
};

scout.Popup.prototype.closePopup = function() {
  this.remove();
};

/**
 * click outside container, scroll down closes popup
 */
scout.Popup.prototype._attachCloseHandler = function() {
  this._mouseDownHandler = this._onMouseDown.bind(this);
  $(document).on('mousedown', this._mouseDownHandler);

  if (this.$origin) {
    scout.scrollbars.attachScrollHandlers(this.$origin, this.remove.bind(this));
  }
};

scout.Popup.prototype._detachCloseHandler = function() {
  if (this.$origin) {
    scout.scrollbars.detachScrollHandlers(this.$origin);
  }
  if (this._mouseDownHandler) {
    $(document).off('mousedown', this._mouseDownHandler);
    this._mouseDownHandler = null;
  }
};

scout.Popup.prototype._onMouseDown = function(event) {
  var $target = $(event.target);
  // close the popup only if the click happened outside of the popup
  if (this.$container.has($target).length === 0) {
    this._onMouseDownOutside(event);
  }
};

scout.Popup.prototype._onMouseDownOutside = function(event) {
  this.closePopup();
};

scout.Popup.prototype.appendToBody = function($element) {
  this.$body.append($element);
};

scout.Popup.prototype.addClassToBody = function(clazz) {
  this.$body.addClass(clazz);
};

scout.Popup.prototype.remove = function() {
  this.$container.uninstallFocusContext(this.session.uiSessionId);
  scout.Popup.parent.prototype.remove.call(this);
  // remove all clean-up handlers
  this._detachCloseHandler();
};

scout.Popup.prototype.setLocation = function(location) {
  this.$container
    .css('left', location.x)
    .css('top', location.y);
};

scout.Popup.prototype._createKeyStrokeAdapter = function() {
  return new scout.PopupKeyStrokeAdapter(this);
};
