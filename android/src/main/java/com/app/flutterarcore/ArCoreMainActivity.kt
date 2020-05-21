package com.app.flutterarcore

import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.animation.ModelAnimator
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode


class ArCoreMainActivity : AppCompatActivity() {
    private val pointer = PointerDrawable()
    private var isTracking = false
    private var isHitting = false
    lateinit var arFragment: ArFragment
    lateinit var add3dModelfab: FloatingActionButton
    var isAddFabEnabled = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_ACTION_BAR)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        add3dModelfab = findViewById<FloatingActionButton>(R.id.fab)
        val url = intent.getStringExtra("arUrl")

        add3dModelfab.setOnClickListener {
            addObject(Uri.parse(url))
        }
        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment
        arFragment.arSceneView.scene.addOnUpdateListener { frameTime ->
            arFragment.onUpdate(frameTime)
            onUpdate()
        }
        addObject(Uri.parse(url))
    }

    private fun addObject(uri: Uri) {
        val frame = arFragment.arSceneView.arFrame
        val point = getScreenCenter()

        if (frame != null) {
            val hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        ModelRenderable.builder()
                                .setSource(this,
                                        RenderableSource.builder()
                                                .setSource(this,
                                                        uri, RenderableSource.SourceType.GLTF2)
                                                .setRecenterMode(RenderableSource.RecenterMode.CENTER)
                                                .build())
                                .setRegistryId(uri)
                                .build()
                                .handle { renderable: ModelRenderable?, throwable: Throwable? ->
                                    if (throwable != null) {
                                        onException(throwable)
                                    } else {
                                        addNodeToScene(hit.createAnchor(), renderable!!)
                                    }
                                    return@handle null
                                }
                    }
                }
            }
        }
    }

    private fun onUpdate() {
        val trackingChanged: Boolean = updateTracking()

        val contentView = findViewById<View>(android.R.id.content)
        if (trackingChanged) {
            if (isTracking) {
                contentView.overlay.add(pointer)
            } else {
                contentView.overlay.remove(pointer)
            }
            contentView.invalidate()
        }
        if (isTracking) {
            val hitTestChanged: Boolean = updateHitTest()
            if (hitTestChanged) {
                pointer.enabled = isHitting
                isAddFabEnabled = isHitting
                contentView.invalidate()
            }
        }
    }

    private fun showFab() {
        add3dModelfab.show()
    }

    private fun hideFab() {
        add3dModelfab.hide()
    }

    private fun updateTracking(): Boolean {
        val frame: Frame? = arFragment.arSceneView.arFrame
        val wasTracking = isTracking
        isTracking = frame != null &&
                frame.camera.trackingState === TrackingState.TRACKING
        return isTracking != wasTracking
    }

    private fun updateHitTest(): Boolean {
        val frame = arFragment.arSceneView.arFrame
        val pt: Point = getScreenCenter()
        val hits: List<HitResult>
        val wasHitting = isHitting
        isHitting = false
        if (frame != null) {
            hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane &&
                        trackable.isPoseInPolygon(hit.hitPose)) {
                    isHitting = true
                    break
                }
            }
        }
        return wasHitting != isHitting
    }


    fun addNodeToScene(createAnchor: Anchor, renderable: ModelRenderable) {
        val anchorNode = AnchorNode(createAnchor)
        val transformableNode = TransformableNode(arFragment.transformationSystem)
        transformableNode.renderable = renderable
        transformableNode.setParent(anchorNode)
        arFragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
        startAnimation(transformableNode, renderable)

    }

    private fun getScreenCenter(): Point {
        val mWidth = this.resources.displayMetrics.widthPixels
        val mHeight = this.resources.displayMetrics.heightPixels
//        val vw = findViewById<View>(android.R.id.content)
        return Point(mWidth / 2, mHeight / 2)
    }

    fun onException(throwable: Throwable) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(throwable.message)
                .setTitle("error!")
        val dialog = builder.create()
        dialog.show()
        return
    }

    fun startAnimation(node: TransformableNode?, renderable: ModelRenderable?) {
        if (renderable == null || renderable.animationDataCount == 0) {
            return
        }
        for (i in 0 until renderable.animationDataCount) {
            val animationData = renderable.getAnimationData(i)
        }
        val animator = ModelAnimator(renderable.getAnimationData(0), renderable)
        animator.start()

        node?.setOnTapListener { hitTestResult, motionEvent ->
            togglePauseAndResume(animator)
        }
    }

    fun togglePauseAndResume(animator: ModelAnimator) {
        if (animator.isPaused) {
            animator.resume()
        } else if (animator.isStarted) {
            animator.pause()
        } else {
            animator.start()
        }
    }
}
