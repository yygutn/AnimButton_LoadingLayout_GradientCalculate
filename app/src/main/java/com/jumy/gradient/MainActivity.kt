package com.jumy.gradient

import android.app.Service
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var current: Long = 0
    private lateinit var sm: SensorManager
    private lateinit var sensor: Sensor
    private lateinit var sl: SenListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        current = 0
        sm = getSystemService(Service.SENSOR_SERVICE) as SensorManager
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sl = SenListener(show)
        sm.registerListener(sl, sensor, SensorManager.SENSOR_DELAY_FASTEST)


        mAnimButton.setCount(0).setMaxCount(10)

//        mAnimButton.clickWatcher {
//            onAddSuccess { }
//            onAddFailed { count, failType -> }
//            onDelSuccess { }
//            onDelFailed { count, failType -> }
//        }
    }
}
