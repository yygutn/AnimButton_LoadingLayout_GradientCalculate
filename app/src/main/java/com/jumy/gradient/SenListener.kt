package com.jumy.gradient

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.widget.TextView
import java.util.*


/**
 * Created by Jumy on 17/1/5 16:54.
 * Copyright (c) 2016, yygutn@gmail.com All Rights Reserved.
 */
class SenListener : SensorEventListener {
    var sizeLimit: Int = 0 //缓冲区大小
    var queX: Queue<Double>
    var queY: Queue<Double>
    var queZ: Queue<Double> //各分量缓冲区
    var sumX: Double = 0.0
    var sumY: Double = 0.0
    var sumZ: Double = 0.0 //和
    var aveX: Double = 0.0
    var aveY: Double = 0.0
    var aveZ: Double = 0.0 //平均
    var g: Double = 0.0 //矢量和
    var gradient: Double = 0.0 //坡度
    val show: TextView

    constructor(show: TextView) {
        this.show = show
        sizeLimit = 100
        queX = LinkedList()
        queY = LinkedList()
        queZ = LinkedList()
        sumZ = 0.0
        sumY = sumZ
        sumX = sumY
    }

    fun setLimit(grade: Int) {
        sizeLimit = 100 * (grade + 1) //SeekBar选择来控制缓冲区大小，据此可以调节灵敏度
    }

    private fun grad2Deg(grad: Double): Double {
        return grad * 180 / Math.PI
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }
        val x: Double = event.values[0].toDouble()
        val y: Double = event.values[1].toDouble()
        val z: Double = event.values[2].toDouble()

        /*滑动平均算法核心部分*/
        sumX += x
        sumY += y
        sumZ += z
        queX.offer(x)
        queY.offer(y)
        queZ.offer(z)
        while (queX.size > sizeLimit) {
            sumX -= queX.poll()
            sumY -= queY.poll()
            sumZ -= queZ.poll()
        }
        aveX = sumX / queX.size
        aveY = sumY / queX.size
        aveZ = sumZ / queX.size
        g = Math.sqrt(aveX * aveX + aveY * aveY + aveZ * aveZ)
        gradient = Math.acos(Math.abs(aveZ) / g)

        val message = Math.round(grad2Deg(gradient)).toString() + "º" //弧度转为度
        show.text = message
    }

}