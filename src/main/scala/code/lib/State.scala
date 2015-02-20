package code.lib

import net.liftweb.actor.LiftActor
import net.liftweb.http.ListenerManager

/**
 * Created by andreas on 2/11/15.
 */
object State {
  val nominees = Map[String, NomineeState](
    "1" -> new NomineeState,
    "2" -> new NomineeState,
    "3" -> new NomineeState,
    "4" -> new NomineeState,
    "5" -> new NomineeState,
    "6" -> new NomineeState
  )

  val pics = Map[String, String](
    "1" -> "nominee.minami.normal.jpg",
    "2" -> "nominee.nagata.normal.jpg",
    "6" -> "nominee.takeuchi.normal.jpg",
    "5" -> "nominee.tada.normal.jpg",
    "3" -> "nominee.seki.normal.jpg",
    "4" -> "nominee.sonoda.normal.jpg"
  )

  def addVote(v:Vote) = nominees.get(v.nominee).foreach(_ ! v)

}

class NomineeState extends LiftActor with ListenerManager {
  private var lastVote: Vote = null
  private var score = Score(0,0,0,0)

  private def addVote(v: Vote):Unit = {
    lastVote = v
    score = score.add(v)
  }

  def createUpdate = VoteWithScore(lastVote,score)

  override def lowPriority = {

    case v: Vote =>
      addVote(v)
      updateListeners()

  }

}

case class Vote(nominee: String, mf: String, x: BigDecimal, y: BigDecimal, bk: String)

case class Score(mb: Int, mk: Int, fb: Int, fk: Int) {
  def add(v: Vote): Score = v match {
    case Vote(_, "m", _, _, "b") => this.copy(mb = this.mb+1)
    case Vote(_, "m", _, _, "k") => this.copy(mk = this.mk+1)
    case Vote(_, "f", _, _, "b") => this.copy(fb = this.fb+1)
    case Vote(_, "f", _, _, "k") => this.copy(fk = this.fk+1)
  }
}

case class VoteWithScore(nominee: String, mf: String, x: BigDecimal, y: BigDecimal, bk: String, mb: Int, mk: Int, fb: Int, fk: Int)

object VoteWithScore {
  def apply(v: Vote, s: Score):VoteWithScore = VoteWithScore(v.nominee, v.mf, v.x, v.y, v.bk, s.mb, s.mk, s.fb, s.fk)
}

