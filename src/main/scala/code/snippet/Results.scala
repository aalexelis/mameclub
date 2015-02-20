package code.snippet

import code.lib.{NomineeState, State}
import net.liftweb.http.S
import net.liftweb.util._
import Helpers._

/**
 * Created by andreas on 2/20/15.
 */
object Results {

  def img_src(id: String):String = "/assets/images/"+State.pics.getOrElse(id,"face.png")

  def kisslist = State.nominees.toList.sortWith(_._2.getKisses > _._2.getKisses)
  def beanlist = State.nominees.toList.sortWith(_._2.getBeans > _._2.getBeans)

  def render =
    "#kisses *" #> kisslist.map(n => {
    ".id *" #> n._1 &
    "img [src]" #> img_src(n._1) &
    ".totkisses" #> n._2.getKisses &
    ".score" #> n._2.getScore.toString
    }) &
    "#beans *" #> beanlist.map(n => {
    ".id *" #> n._1 &
      "img [src]" #> img_src(n._1) &
      ".totbeans" #> n._2.getBeans &
      ".score" #> n._2.getScore.toString
    })

}
