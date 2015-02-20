package bootstrap.liftweb

import code.lib.VoteRest
import code.snippet.ParamInfo
import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._

import code.model._
import net.liftmodules.FoBo

import scala.language.postfixOps

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends Loggable {
  def boot {

    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor =
        new StandardDBVendor(
          Props.get("db.driver") openOr "org.h2.Driver",
          Props.get("db.url") openOr "jdbc:h2:~/lift_proto.db;AUTO_SERVER=TRUE",
          Props.get("db.user"),
          Props.get("db.password")
        )

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(util.DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User, Stats)

    // where to search snippet
    LiftRules.addToPackages("code")


    def sitemapMutators = User.sitemapMutator
    //The SiteMap is built in the Site object bellow 
    LiftRules.setSiteMapFunc(() => sitemapMutators(Site.sitemap))

    LiftRules.dispatch.append(VoteRest)

    //Init the FoBo - Front-End Toolkit module, 
    //see http://liftweb.net/lift_modules for more info
    FoBo.InitParam.JQuery = FoBo.JQuery1111
    FoBo.InitParam.ToolKit = FoBo.Bootstrap320
    FoBo.InitParam.ToolKit = FoBo.AngularJS1219
    FoBo.init()

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    LiftRules.noticesAutoFadeOut.default.set((notices: NoticeType.Value) => {
      notices match {
        case NoticeType.Notice => Full((8 seconds, 4 seconds))
        case _ => Empty
      }
    }
    )

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
  }

  object Site {

    import scala.xml._

    val divider1 = Menu("divider1") / "divider1"
    val ddLabel1 = Menu.i("UserDDLabel") / "ddlabel1"
    val home = Menu.i("Home") / "index"


    //val nominee = Menu.i("Nominee") / "nominee"
    val nominee = Menu.param[ParamInfo]("Nominee", "Nominee",
      s => Full(ParamInfo(s)),
      pi => pi.theParam) / "nominee"

    val nominees = Menu.i("Nominees") / "nominees"
    val results = Menu.i("Results") / "results"
    val voters = Menu.i("Voters") / "voters"
    val top = Menu.i("Top") / "top"
    val userMenu = User.AddUserMenusHere
    def nominee(no:String) = Menu(Loc("Nominee"+no, Link(List("nominee"+no), true, "/nominee/"+no), S.loc("Nominee"+no, scala.xml.Text("Nominee "+no)), LocGroup("lg2", "topRight")))


    def sitemap = SiteMap(
      home >> LocGroup("lg1"),
      top >> LocGroup("lg1"),
      nominee("1"),
      nominee("2"),
      nominee("3"),
      nominee("4"),
      nominee("5"),
      nominee("6"),
      nominee >> Hidden,
      nominees >> LocGroup("lg1"),
      results >> LocGroup("lg1"),
      voters >> LocGroup("lg1")
    )
  }
}

