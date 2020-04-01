/*******************************************************************************
 * Copyright (c) 2010-2018 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.platform.events.management;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.ObjectName;

import org.eclipse.scout.rt.platform.events.ListenerListRegistry;
import org.eclipse.scout.rt.platform.events.ListenerListSnapshot;
import org.eclipse.scout.rt.platform.jmx.MBeanUtility;

public class ListenerListMonitor implements IListenerListMonitor {

  @PostConstruct
  protected void postConstruct() {
    MBeanUtility.register(jmxObjectName(), this);
  }

  @PreDestroy
  protected void preDestroy() {
    MBeanUtility.unregister(jmxObjectName());
  }

  protected ObjectName jmxObjectName() {
    return MBeanUtility.toJmxName("org.eclipse.scout.rt.platform", "Application", "EventListeners");
  }

  @Override
  public int getListenerListCount() {
    return ListenerListRegistry.globalInstance().getListenerListCount();
  }

  @Override
  public ListenerListInfo[] getListenerListInfos() {
    ListenerListSnapshot snapshot = ListenerListRegistry
        .globalInstance()
        .createSnapshot();
    //remap listener lists and merge all lists with same class name
    Map<String, Integer> listenerListCount = new TreeMap<>();
    Map<String, Map<String, List<String>>> listenerListTypes = new TreeMap<>();
    snapshot
        .getData()
        .forEach(
            (listenerList, types) -> {
              String className = listenerList.getClass().getName();
              listenerListCount.put(className, listenerListCount.getOrDefault(className, 0));
              Map<String, List<String>> mergedTypes = listenerListTypes.computeIfAbsent(className, className2 -> new TreeMap<>());
              types.forEach((type, listeners) -> {
                listeners.forEach(listener -> {
                  mergedTypes
                      .computeIfAbsent(type, type2 -> new ArrayList<>())
                      .add(listener.getClass().getName());
                });
              });
            });
    return listenerListTypes
        .entrySet()
        .stream()
        .map(e -> createListenerListInfo(e.getKey(), listenerListCount.get(e.getKey()), e.getValue()))
        .toArray(n -> new ListenerListInfo[n]);
  }

  protected ListenerListInfo createListenerListInfo(String listenerListClassName, int listenerListInstanceCount, Map<String, List<String>> listenerTypes) {
    return new ListenerListInfo(
        listenerListClassName,
        listenerListInstanceCount,
        listenerTypes
            .entrySet()
            .stream()
            .map(e -> createListenerType(e.getKey(), e.getValue()))
            .toArray(n -> new EventType[n]));
  }

  protected EventType createListenerType(String listenerType, List<String> listeners) {
    return new EventType(listenerType,
        listeners
            .stream()
            .collect(Collectors.groupingBy(name -> name))
            .entrySet()
            .stream()
            .map(e -> createListenerInfo(e.getKey(), e.getValue().size()))
            .toArray(n -> new ListenerInfo[n]));
  }

  protected ListenerInfo createListenerInfo(String listenerClassName, int listenerInstanceCount) {
    return new ListenerInfo(listenerClassName, listenerInstanceCount);
  }
}