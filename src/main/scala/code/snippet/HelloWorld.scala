package code 
package snippet

import net.liftweb.http.SHtml

import scala.xml.{NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import java.util.Date
import code.lib._
import Helpers._
import net.liftweb.http.js.{JsCmd, JsCmds, Jx, JE}
import net.liftweb.http.js.JE._
import JsCmds._

class HelloWorld {
  lazy val date: Box[Date] = DependencyFactory.inject[Date] // inject the date

  // replace the contents of the element with id "time" with the date
  def howdy = "#time *" #> date.map(_.toString) &
    "#loadscore [onClick]" #> SHtml.onEvent(_ => loadScore) &
    "#savescore [onClick]" #> SHtml.onEvent(_ => saveScore) &
    "#resetscore [onClick]" #> SHtml.onEvent(_ => resetScore)

  def loadScore():JsCmd = {
    println("loadScore")
    State.loadScore()
    Noop
  }

  def resetScore():JsCmd = {
    println("resetScore")
    State.resetScore()
    Noop
  }

  def saveScore():JsCmd = {
    println("saveScore")
    State.saveScore()
    Noop
  }

  /*
   lazy val date: Date = DependencyFactory.time.vend // create the date via factory

   def howdy = "#time *" #> date.toString
   */
}

