package watcher

import akka.testkit.TestKit
import akka.actor.{Actor, Props, ActorSystem}
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.ShouldMatchers
import akka.testkit.ImplicitSender
import akka.testkit.TestProbe
import scala.concurrent.duration._
import concurrent.{Future, Promise, Await}
import concurrent.ExecutionContext.Implicits.global
import java.nio.file._

class WatcherSpec extends TestKit(ActorSystem("WatcherSpec"))
  with FunSuite
  with BeforeAndAfterAll
  with ShouldMatchers
  with ImplicitSender
{

  override def afterAll(): Unit = system.shutdown

  test("outside akka") {
    import Watcher._
    val dir = Files.createTempDirectory(Paths.get("target"), "test1-")

    var events = List[(String,Path)]()
    val c = Promise[String]()
    val d = Promise[String]()
    start(dir) { evt =>
      events = evt :: events
      if (evt._1 == "ENTRY_CREATE") c success evt._1
      if (evt._1 == "ENTRY_DELETE") d success evt._1
    }
    val f = Files.createTempFile(dir, "file1-", "")
    Files.write(f, "new".getBytes)
    var expected = List(("ENTRY_CREATE",f.getFileName))
    expected = List(("ENTRY_MODIFY",f.getFileName)) ::: expected
    Thread.sleep(100)
    assert(Await.result(c.future, 1 seconds) == "ENTRY_CREATE")
    assert(expected === events)
    Files.write(f, "modif".getBytes)
    expected = List(("ENTRY_MODIFY",f.getFileName)) ::: expected
    Thread.sleep(100)
    assert(expected === events)
    Files.delete(f)
    expected = List(("ENTRY_DELETE",f.getFileName)) ::: expected
    Thread.sleep(100)
    assert(Await.result(d.future, 1 seconds) == "ENTRY_DELETE")
    assert(expected === events)
  }

    test("in akka") {
      val dir = Files.createTempDirectory(Paths.get("target"), "test2-")
      system.actorOf(Props(new WatcherActor)) ! dir.toString
      expectNoMsg
      val f = Files.createTempFile(dir, "file2-", "")
      expectMsg(("ENTRY_CREATE",f.getFileName))
      Files.write(f, "new".getBytes)
      expectMsg(("ENTRY_MODIFY",f.getFileName))
      Files.write(f, "modif".getBytes)
      expectMsg(("ENTRY_MODIFY",f.getFileName))
      Files.delete(f)
      expectMsg(("ENTRY_DELETE",f.getFileName))
    }


}
