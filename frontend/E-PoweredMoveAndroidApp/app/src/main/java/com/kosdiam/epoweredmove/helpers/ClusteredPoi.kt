package com.kosdiam.epoweredmove.helpers

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ClusteredPoi(private val position: LatLng, private val title: String, private val snippet: String, private val isAvailable: Boolean?, private val poiId: String): ClusterItem {
    override fun getPosition(): LatLng {
        return this.position
    }

    override fun getTitle(): String {
        return this.title
    }

    override fun getSnippet(): String {
        return this.snippet
    }

    fun isAvailable(): Boolean? {
        return this.isAvailable
    }

    fun getPoiId(): String {
        return this.poiId
    }
}