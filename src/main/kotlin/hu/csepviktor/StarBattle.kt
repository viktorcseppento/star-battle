package hu.csepviktor

import hu.csepviktor.view.Styles
import hu.csepviktor.view.MainView
import javafx.application.Application
import javafx.stage.Stage
import tornadofx.*

class StarBattle : App(MainView::class, Styles::class) {

    override fun start(stage: Stage) {
        stage.isResizable = false
        stage.width = 800.0
        stage.height = 600.0
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    Application.launch(StarBattle::class.java, *args)
}
