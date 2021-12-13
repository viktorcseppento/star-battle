package hu.csepviktor.view

import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import tornadofx.*

open class Styles : Stylesheet() {
    companion object {
        val mainTitle by cssid("mainTitle")
        val canvas by csselement("Canvas")
        val variableText by cssclass()
    }

    init {
        mainTitle {
            padding = box(40.px)
            fontSize = 50.px
            fontWeight = FontWeight.BOLD
        }

        label {
            fontSize = 16.px
            fontWeight = FontWeight.BOLD
        }

        variableText {
            fontSize = 18.px
            fontWeight = FontWeight.BOLD
        }

        tableColumn {
            alignment = Pos.CENTER
        }

        button {
            prefHeight = 50.px
            prefWidth = 120.px
        }
        canvas {
            prefHeight = 100.px
            prefWidth = 100.px
        }
    }
}
