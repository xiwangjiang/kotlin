/*
 * Copyright 2010-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.plugin.caches.resolve

import org.jetbrains.jet.context.GlobalContextImpl
import org.jetbrains.jet.storage.LockBasedStorageManager
import org.jetbrains.jet.storage.ExceptionTracker

fun GlobalContextImpl.withCompositeExceptionTrackerUnderSameLock(): GlobalContextImpl {
    val newExceptionTracker = CompositeExceptionTracker(this.exceptionTracker)
    val newStorageManager = LockBasedStorageManager.createDelegatingWithSameLock(this.storageManager, newExceptionTracker)
    return GlobalContextImpl(newStorageManager, newExceptionTracker)
}

private class CompositeExceptionTracker(val delegate: ExceptionTracker): ExceptionTracker() {
    override fun getModificationCount(): Long {
        return super<ExceptionTracker>.getModificationCount() + delegate.getModificationCount()
    }
}