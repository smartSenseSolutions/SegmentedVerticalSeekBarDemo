package com.ss.svsdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.ss.svsdemo.R
import com.ss.svs.SegmentedVerticalSeekBar
import com.ss.svsdemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setPyramidValue()
        setTouchToggle()
        setClockwiseToggle()
        setListeners()
    }

    private fun setListeners(){
        activityMainBinding.btnIsTriangleViewEnable.setOnClickListener {
            activityMainBinding.svsLevelView.pyramidViewEnable = !activityMainBinding.svsLevelView.pyramidViewEnable
            setPyramidValue()
            setClockwiseToggle()
        }
        activityMainBinding.btnIsAllRadius.setOnClickListener {
            activityMainBinding.svsLevelView.isAllRadius = !activityMainBinding.svsLevelView.isAllRadius
        }
        activityMainBinding.btnTouchToggle.setOnClickListener {
            activityMainBinding.svsLevelView.touchDisabled = !activityMainBinding.svsLevelView.touchDisabled
            setTouchToggle()
        }
        activityMainBinding.btnClockWise.setOnClickListener {
            activityMainBinding.svsLevelView.clockwise = !activityMainBinding.svsLevelView.clockwise
            setClockwiseToggle()
        }

        activityMainBinding.btnMaxValue5.setOnClickListener {
            activityMainBinding.svsLevelView.max = 5
        }

        activityMainBinding.btnMaxValue10.setOnClickListener {
            activityMainBinding.svsLevelView.max = 10
        }

        activityMainBinding.btnMaxValue20.setOnClickListener {
            activityMainBinding.svsLevelView.max = 20
        }

        activityMainBinding.svsLevelView.setOnBoxedPointsChangeListener(object :
            SegmentedVerticalSeekBar.OnValuesChangeListener {
            override fun onProgressChanged(segmentedPointsSeekBar: SegmentedVerticalSeekBar, progress: Int) {
                activityMainBinding.tvCurrentValue.text = progress.toString()
            }

            override fun onStartTrackingTouch(segmentedPointsSeekBar: SegmentedVerticalSeekBar) {
                Log.e("MainAct","onStartTrackingTouch: "+segmentedPointsSeekBar.value)
            }

            override fun onStopTrackingTouch(segmentedPointsSeekBar: SegmentedVerticalSeekBar) {
                Log.e("MainAct","onStopTrackingTouch: "+segmentedPointsSeekBar.value)
                activityMainBinding.svsLevelView.value = segmentedPointsSeekBar.value
            }

            override fun onStopTouch(segmentedPointsSeekBar: SegmentedVerticalSeekBar) {
                Log.e("MainAct","onStopTouch: "+segmentedPointsSeekBar.value)
                activityMainBinding.svsLevelView.value = segmentedPointsSeekBar.value
            }

        })
    }

    private fun setPyramidValue(){
        if(activityMainBinding.svsLevelView.pyramidViewEnable){
            activityMainBinding.btnIsTriangleViewEnable.text = getString(R.string.stack_view)
        }else{
            activityMainBinding.btnIsTriangleViewEnable.text = getString(R.string.pyramid_view)
        }
    }

    private fun setTouchToggle(){
        if(activityMainBinding.svsLevelView.touchDisabled){
            activityMainBinding.btnTouchToggle.text = getString(R.string.enable_touch)
        }else{
            activityMainBinding.btnTouchToggle.text = getString(R.string.disable_touch)
        }
    }

    private fun setClockwiseToggle(){
        if(activityMainBinding.svsLevelView.clockwise){
            activityMainBinding.btnClockWise.text = getString(R.string.anti_clockwise)
        }else{
            activityMainBinding.btnClockWise.text = getString(R.string.clockwise)
        }
    }

    fun setBorder(v: View) {
        val newValue = v.getTag().toString().toFloat()
        activityMainBinding.svsLevelView.cornerRadius = newValue
        Toast.makeText(this@MainActivity, "New corner radius is $newValue", Toast.LENGTH_SHORT).show()
    }

    fun setMax(v: View) {
        val newValue: Int = Integer.valueOf(v.getTag().toString())
        activityMainBinding.svsLevelView.max = newValue
        Toast.makeText(this@MainActivity, "New max value is $newValue", Toast.LENGTH_SHORT).show()
    }
}