package com.app.flutterarcore

import android.net.Uri
import android.util.Log
import com.google.ar.core.Anchor
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable

import java.lang.ref.WeakReference

class ModelLoader internal constructor(private val owner: WeakReference<ArCoreMainActivity>) {

    private val TAG: String? = "ModelLoader"


    fun loadModelFromInternet(anchor: Anchor?, uri: Uri?) {
        if (owner.get() == null) {
            Log.d(TAG, "Activity is null.  Cannot load model.")
            return
        }
        ModelRenderable.builder()
                .setSource(owner.get(),
                        RenderableSource.builder()
                                .setSource(owner.get(),
                                        uri, RenderableSource.SourceType.GLTF2)
                                .setRecenterMode(RenderableSource.RecenterMode.CENTER)
                                .build())
                .setRegistryId(uri)
                .build()
                .handle { renderable: ModelRenderable?, throwable: Throwable? ->
                    val activity = owner.get()
                    if (activity == null) {
                        return@handle null
                    } else if (throwable != null) {
                        activity.onException(throwable)
                    } else {
                        activity.addNodeToScene(anchor!!, renderable!!)
                    }
                    return@handle null
                }
        return
    }

    companion object {
    }
}