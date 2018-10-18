package ui.anwesome.com.linkedtricornerstepview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.tricornerstepview.TriCornerStepView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TriCornerStepView.create(this)
    }
}
