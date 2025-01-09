package com.oussamameg.contentreveal

import androidx.compose.runtime.Composable


class ContentRevealScope {
    private var visibleContentSet = false
    private var hiddenContentSet = false

    var visibleContent: (@Composable () -> Unit)? = null
        private set
    var hiddenContent: (@Composable () -> Unit)? = null
        private set

    fun visibleContent(content: @Composable () -> Unit) {
        visibleContentSet = true
        visibleContent = content
    }

    fun hiddenContent(content: @Composable () -> Unit) {
        hiddenContentSet = true
        hiddenContent = content
    }

    internal fun validate() {
        require(visibleContentSet) { "Visible Content must be provided" }
        require(hiddenContentSet) { "Hidden Content must be provided" }
    }
}