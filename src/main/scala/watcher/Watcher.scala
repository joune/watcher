package watcher

import akka.actor.{Actor, Props, ActorSystem}
import scala.util.Try

import java.nio.file._
import java.nio.file.StandardWatchEventKinds._
import concurrent.future
import concurrent.ExecutionContext.Implicits.global
import collection.JavaConversions._


object Watcher {
  def start(path: Path)(listener: ((String,Path)) => Unit) {
    val watchService = FileSystems.getDefault.newWatchService
    path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)
    
    var loop = true
    
    future {
      while(loop) {
        println("take...")
        for {
          key <- Try(watchService.take) 
          ev <- key.pollEvents
        } {
          println(ev.kind.name)
          listener((ev.kind.name, ev.context.asInstanceOf[Path]))
          if (!key.reset) loop = false
        }
      }
    }
  }
}

class WatcherActor extends Actor {
  import Watcher._
  def receive = {
    case path: String =>
      val to = sender
      start(Paths.get(path)) { evt =>
        println(s"$evt => $to")
        to ! evt
      }
  }
}

object WatcherMain extends App {
  import Watcher._
  start(Paths.get(args(0)))(println)
  System.in.read
}
