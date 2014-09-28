package io

object ExecuctionContext {
  import java.util.concurrent.Executors
  import scala.concurrent._

  implicit val context = new ExecutionContext {
    val threadPool = Executors.newFixedThreadPool(4)

    def execute(runnable: Runnable) {
      threadPool.submit(runnable)
    }

    def reportFailure(t: Throwable) { println(t)}
  }
}
