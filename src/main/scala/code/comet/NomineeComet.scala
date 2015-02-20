package code.comet

import code.lib.CustomSerializers.BigDecimalSerializer
import code.lib.{VoteWithScore, State, Score, Vote}
import net.liftweb.actor.LiftActor
import net.liftweb.common.Loggable
import net.liftweb.http.js.{JE, JsCmd}
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.{ListenerManager, CometListener, CometActor}
import net.liftweb.json._
import net.liftweb.util.ClearClearable
import net.liftweb.json.Extraction._


/**
 * Created by andreas on 2/11/15.
 */
class NomineeComet extends CometActor with CometListener with Loggable {

  def registerWith = State.nominees.get(name.getOrElse("ma")).getOrElse(State.nomineelist)

  override def lowPriority = {

    case vs:VoteWithScore =>
      sendVoteWithScore(vs)


  }

  /**
   * Clear any elements that have the clearable class.
   */
  def render = {
    ClearClearable
  }

  private[this] def sendVoteWithScore(vs: VoteWithScore) = {
      implicit val formats = DefaultFormats.lossless + BigDecimalSerializer
      val json: JValue = decompose(vs)
      val js = JE.JsRaw(""" $(document).trigger('new-ng-vote', %s)""".format( compact( net.liftweb.json.render( json ) ) ) )
      println("VoteWithScore: " + vs + ",  json: " + json + ",  js: "  + js)
      partialUpdate(js)
  }
}

