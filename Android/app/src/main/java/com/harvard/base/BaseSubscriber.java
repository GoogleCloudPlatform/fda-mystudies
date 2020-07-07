/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.base;

import com.harvard.FdaEventBus;

public abstract class BaseSubscriber implements BaseEventBusRegistry.EventBusSubscriber {

  private FdaEventBus eventBus;
  private static final String EXCEPTION_MESSAGE =
      "PluginController.register() was not called. Is the controller registered in the EventBusRegistry?";

  @Override
  public final Object register(FdaEventBus eventBus) {
    eventBus = eventBus;
    eventBus.register(this);
    return this;
  }

  public final void unregister(FdaEventBus eventBus) {
    eventBus.unregister(this);
    eventBus = null;
  }

  protected void post(Object event) {
    if (eventBus == null) {
      throw new NullPointerException(EXCEPTION_MESSAGE);
    }
    eventBus.post(event);
  }

  protected void postSticky(Object event) {
    if (eventBus == null) {
      throw new NullPointerException(EXCEPTION_MESSAGE);
    }
    eventBus.postSticky(event);
  }

  protected <T> T removeStickyEvent(Class<T> eventType) {
    if (eventBus == null) {
      throw new NullPointerException(EXCEPTION_MESSAGE);
    }
    return eventBus.removeStickyEvent(eventType);
  }

  protected boolean removeStickyEvent(Object event) {
    if (eventBus == null) {
      throw new NullPointerException(EXCEPTION_MESSAGE);
    }
    return eventBus.removeStickyEvent(event);
  }

  protected <T> T getStickyEvent(Class<T> eventType) {
    if (eventBus == null) {
      throw new NullPointerException(EXCEPTION_MESSAGE);
    }
    return eventBus.getStickyEvent(eventType);
  }
}
