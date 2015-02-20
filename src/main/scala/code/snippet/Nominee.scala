package code.snippet

import code.lib.State
import net.liftweb.http.S
import net.liftweb.util._
import Helpers._

/**
 * Created by andreas on 2/13/15.
 */
class Nominee(pi: ParamInfo) {

  def img_src:String = "/assets/images/"+State.pics.getOrElse(pi.theParam,"face.png")
  def pic = "img [src]" #> img_src
  def comet = <lift:comet type="NomineeComet" name={pi.theParam}/>
}

case class ParamInfo(theParam: String)
