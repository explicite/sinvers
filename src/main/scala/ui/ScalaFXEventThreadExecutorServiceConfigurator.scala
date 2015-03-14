package ui

import java.util.concurrent.{ ExecutorService, ThreadFactory }

import akka.dispatch.{ DispatcherPrerequisites, ExecutorServiceConfigurator, ExecutorServiceFactory }
import com.typesafe.config.Config

class ScalaFXEventThreadExecutorServiceConfigurator(config: Config, prerequisites: DispatcherPrerequisites) extends ExecutorServiceConfigurator(config, prerequisites) {
  private val executorServiceFactory = new ExecutorServiceFactory {
    def createExecutorService: ExecutorService = ScalaFXExecutorService
  }

  def createExecutorServiceFactory(id: String, threadFactory: ThreadFactory): ExecutorServiceFactory = executorServiceFactory
}
