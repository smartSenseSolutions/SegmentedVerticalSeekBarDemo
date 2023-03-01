package com.ss.svs.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.ss.svs.SegmentedVerticalSeekBar
import com.ss.svs.app.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setTouchToggle()
        setListeners()
    }

    private fun setListeners(){
        activityMainBinding.btnTouchToggle.setOnClickListener {
            activityMainBinding.svsLevelView.touchDisabled = !activityMainBinding.svsLevelView.touchDisabled
            setTouchToggle()
        }

        activityMainBinding.btnMaxValue5.setOnClickListener {
            activityMainBinding.svsLevelView.max = 5
        }

        activityMainBinding.btnMaxValue4.setOnClickListener {
            activityMainBinding.svsLevelView.max = 4
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

    private fun setTouchToggle(){
        if(activityMainBinding.svsLevelView.touchDisabled){
            activityMainBinding.btnTouchToggle.text = getString(R.string.enable_touch)
        }else{
            activityMainBinding.btnTouchToggle.text = getString(R.string.disable_touch)
        }
    }
}