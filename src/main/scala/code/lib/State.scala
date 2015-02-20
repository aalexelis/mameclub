package code.lib

import code.model.Stats
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
    "3" -> "nominee.takeuchi.normal.jpg",
    "4" -> "nominee.tada.normal.jpg",
    "5" -> "nominee.seki.normal.jpg",
    "6" -> "nominee.sonoda.normal.jpg"
  )

  def addVote(v:Vote) = nominees.get(v.nominee).foreach(_ ! v)

  def saveScore() = {
    def saveNominee(id: String, score: Score) ={
      println("id: " + id + ",  score: " + score)
      Stats.create.nominee(id).dispName("nameHere")
        .maleBeans(score.mb).maleKisses(score.mk).
        femaleBeans(score.fb).femaleKisses(score.fk)
        .save()
    }
    nominees.foreach((s: (String,NomineeState)) => {saveNominee(s._1,s._2.getScore)})

  }

  def loadScore() = {
    val scores = Stats.findAll()
    scores.foreach(st => {
      println("id: " + st.nominee.get + ",  score: " + Score(st.maleBeans.get,st.maleKisses.get,st.femaleBeans.get,st.femaleKisses.get) )
      nominees.get(st.nominee.get).foreach(_.setScore(Score(st.maleBeans.get,st.maleKisses.get,st.femaleBeans.get,st.femaleKisses.get)))
    })
  }

  def resetScore() = {
    Stats.findAll().foreach(s => println("delete: " + Stats.delete_!(s)))
    nominees.foreach((s: (String, NomineeState)) => {
      s._2.setScore(Score(0, 0, 0, 0))
    })
    saveScore()
  }

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

  def getScore = Score(score.mb, score.mk, score.fb, score.fk)

  def setScore(s: Score) = { score = s}

  def getKisses = score.mk + score.fk

  def getBeans = score.mb + score.fb

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

