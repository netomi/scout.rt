/*
 * Copyright (c) 2010-2018 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.rt.client.ui.desktop.notification;

import java.util.function.Consumer;

import org.eclipse.scout.rt.client.ModelContextProxy;
import org.eclipse.scout.rt.client.ModelContextProxy.ModelContext;
import org.eclipse.scout.rt.client.ui.desktop.IDesktop;
import org.eclipse.scout.rt.client.ui.notification.Notification;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.classid.ClassId;
import org.eclipse.scout.rt.platform.status.IStatus;
import org.eclipse.scout.rt.platform.status.Status;
import org.eclipse.scout.rt.platform.util.ObjectUtility;

@ClassId("cd82392d-609d-44c2-ac41-87fca7a78646")
public class DesktopNotification extends Notification implements IDesktopNotification {

  private long m_duration;
  private final IDesktopNotificationUIFacade m_uiFacade = BEANS.get(ModelContextProxy.class).newProxy(new P_UIFacade(), ModelContext.copyCurrent());

  /**
   * Creates a closable, simple info notification with a text and the {@linkplain IDesktopNotification#DEFAULT_DURATION
   * default duration}.
   */
  public DesktopNotification(String text) {
    this(new Status(text, IStatus.INFO));
  }

  /**
   * Creates a closable, notification with a status and the {@linkplain IDesktopNotification#DEFAULT_DURATION default
   * duration}.
   */
  public DesktopNotification(IStatus status) {
    this(status, DEFAULT_DURATION, true);
  }

  /**
   * Creates a notification with the given attributes.
   *
   * @param status
   *          see {@link #getStatus()}
   * @param duration
   *          see {@link #getDuration()}
   * @param closable
   *          see {@link #isClosable()}
   */
  public DesktopNotification(IStatus status, long duration, boolean closable) {
    this(status, duration, closable, false, null);
  }

  /**
   * Creates a notification with the given attributes.
   *
   * @param status
   *          see {@link #getStatus()}
   * @param duration
   *          see {@link #getDuration()}
   * @param closable
   *          see {@link #isClosable()}
   * @param htmlEnabled
   *          see {@link #isHtmlEnabled()}
   * @param appLinkConsumer
   *          see {@link #getAppLinkConsumer()}
   */
  public DesktopNotification(IStatus status, long duration, boolean closable, boolean htmlEnabled, Consumer<String> appLinkConsumer) {
    super(status, closable, htmlEnabled, appLinkConsumer);
    m_duration = duration;
    NativeNotificationDefaults nativeNotificationDefaults = IDesktop.CURRENT.get().getNativeNotificationDefaults();
    if (nativeNotificationDefaults != null) {
      withNativeNotificationTitle(nativeNotificationDefaults.getTitle());
      withNativeNotificationIconId(nativeNotificationDefaults.getIconId());
      withNativeNotificationVisibility(ObjectUtility.nvl(nativeNotificationDefaults.getVisibility(), NATIVE_NOTIFICATION_VISIBILITY_NONE));
    }
  }

  @Override
  public DesktopNotification withDuration(long duration) {
    m_duration = duration;
    return this;
  }

  @Override
  public long getDuration() {
    return m_duration;
  }

  @Override
  public DesktopNotification withNativeOnly(boolean nativeOnly) {
    propertySupport.setPropertyBool(PROP_NATIVE_ONLY, nativeOnly);
    return this;
  }

  @Override
  public boolean isNativeOnly() {
    return propertySupport.getPropertyBool(PROP_NATIVE_ONLY);
  }

  @Override
  public DesktopNotification withNativeNotificationVisibility(String nativeNotificationVisibility) {
    propertySupport.setPropertyString(PROP_NATIVE_NOTIFICATION_VISIBILITY, nativeNotificationVisibility);
    return this;
  }

  @Override
  public String getNativeNotificationVisibility() {
    return propertySupport.getPropertyString(PROP_NATIVE_NOTIFICATION_VISIBILITY);
  }

  @Override
  public DesktopNotification withNativeNotificationTitle(String nativeNotificationTitle) {
    propertySupport.setPropertyString(PROP_NATIVE_NOTIFICATION_TITLE, nativeNotificationTitle);
    return this;
  }

  @Override
  public String getNativeNotificationTitle() {
    return propertySupport.getPropertyString(PROP_NATIVE_NOTIFICATION_TITLE);
  }

  @Override
  public DesktopNotification withNativeNotificationIconId(String nativeNotificationIconId) {
    propertySupport.setPropertyString(PROP_NATIVE_NOTIFICATION_ICON_ID, nativeNotificationIconId);
    return this;
  }

  @Override
  public String getNativeNotificationIconId() {
    return propertySupport.getPropertyString(PROP_NATIVE_NOTIFICATION_ICON_ID);
  }

  /**
   * Internal method used by the ui to update the state. Not intended to be used from model code.
   */
  private void setNativeNotificationShown(boolean shown) {
    propertySupport.setPropertyBool(PROP_NATIVE_NOTIFICATION_SHOWN, shown);
  }

  @Override
  public boolean isNativeNotificationShown() {
    return propertySupport.getPropertyBool(PROP_NATIVE_NOTIFICATION_SHOWN);
  }

  @Override
  public IDesktopNotificationUIFacade getUIFacade() {
    return m_uiFacade;
  }

  protected class P_UIFacade implements IDesktopNotificationUIFacade {

    @Override
    public void fireClosedFromUI() {
      IDesktop.CURRENT.get().getUIFacade().removedNotificationFromUI(DesktopNotification.this);
    }

    @Override
    public void setNativeNotificationShownFromUI(boolean shown) {
      setNativeNotificationShown(shown);
    }

    @Override
    public void fireAppLinkActionFromUI(String ref) {
      if (getAppLinkConsumer() != null) {
        getAppLinkConsumer().accept(ref);
      }
    }
  }
}
