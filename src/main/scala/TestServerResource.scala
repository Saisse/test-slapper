package jp.saisse.test.slapper

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

import scala.Array.canBuildFrom
import scala.xml.NodeBuffer

import org.restlet.data.MediaType
import org.restlet.data.Protocol
import org.restlet.resource.Get
import org.restlet.resource.ServerResource
import org.restlet.routing.Router
import org.restlet.{Application => RestletApplicaion}
import org.restlet.Context
import org.restlet.Request
import org.restlet.Response
import org.restlet.Restlet
import org.restlet.Server
import scala.concurrent.ops._

class TestApplication(server: TestServerResource) extends RestletApplicaion {
  
  override def createInboundRoot(): Restlet = {
    val router = new Router(getContext())
    val testRestlet = new TestRestlet(router.getContext())
    router.attach("http://localhost:8182/end", new TerminateRestlet(router.getContext(), server))
    router.attach("http://localhost:8182/tests", new TestsRestlet(router.getContext()))
    router.attach("http://localhost:8182/test/{test}", testRestlet)
    router.attach("http://localhost:8182/test/{test}/{method}", testRestlet)
    return router
  }
}

class TestServerResource extends ServerResource {
  val server = new Server(Protocol.HTTP, 8182)
  
  server.setNext(new TestApplication(this))
  server.start()
  
  def start() = server.start()
  def stop() = server.stop()
}

class TerminateRestlet(context: Context, server: TestServerResource) extends Restlet(context) {
  override def handle(request: Request, response: Response): Unit = {
    response.setEntity("start shutdown server.", MediaType.TEXT_PLAIN)
    future{ server.stop()}
  }
}


class TestRestlet(context: Context) extends Restlet(context) {
  override def handle(request: Request, response: Response): Unit = {
    val test = request.getAttributes().get("test").toString()
    var method = Option(request.getAttributes().get("method")).getOrElse("").toString()
    val byteArrayStream = new ByteArrayOutputStream()
    val printStream = new PrintStream(byteArrayStream)
    val invoker = new TestInvoker(printStream)
    val c = Class.forName(test)

    method match {
      case "" => invoker.run(c)
      case _ => invoker.run(c, method)
    }
    val result = new String(byteArrayStream.toByteArray())
    
    response.setEntity(result, MediaType.TEXT_PLAIN)
  }
}


class TestsRestlet(context: Context) extends Restlet(context) {
  val testSourceDirectory = "./src/main/java"

  override def handle(request: Request, response: Response): Unit = {

    val tests = collectTestClasses
    
    val html = 
        <html>
         <body>
           <a href="./end">shutdown</a><hr/>
           <ul>
         {
           listTests(tests) map {
             t => {
               t match {
                 case t: TestClass => <li><a href={"./test/%s".format(t.name)}>{t.name}</a></li>
                 case m: TestMethod => {
                   <li><a href={"./test/%s/%s".format(m.testClass.name, m.name)}>{m.testClass.name}.{m.name}</a></li>
                 }
               }
             }
           }
         }
           </ul>
         </body>
        </html>
    
    response.setEntity(html.toString(), MediaType.TEXT_HTML)
  }
  
  def listTests(tests: List[TestClass]): List[Any] = {
    var list = List[Any]()
    tests.foreach(
      t => {
        list = t :: list
        t.testMethods.foreach(m => list = m :: list)
      }
    )
    return list.reverse
  }

  def collectTestClasses(): List[TestClass] = {
    collect(testSourceDirectory, "").reverse
  }
  
  def collect(base: String, packagePath: String): List[TestClass] = {
    val dir = new File(joinPath(base, packagePath))
    
    var list = List[TestClass]()
    for(f <- dir.listFiles()) {
      f match {
        case f if f.isDirectory() => list = collect(base, joinPath(packagePath, f.getName())) ::: list
        case f if f.isFile() => {
          val c = toClass(packagePath, f.getName())
          if(c.getSimpleName().endsWith("Test")) {
            list = new TestClass(c) :: list
          }
        }
        case _ => {throw new RuntimeException("ディレクトリでもファイルでもない。")}
      }
    }
    list
  }
  
  private def joinPath(base: String, child: String): String = {
    if(child.isEmpty()) {
      return base
    } else if(base.isEmpty()) {
      return child
    } else {
      return "%s/%s".format(base, child)
    }
  }
  
  private def toClass(packagePath: String, fileName: String): Class[_] = {
    val fullClassName = toFullClassName(packagePath, fileName)
    return Class.forName(fullClassName)
  }
  def toFullClassName(packagePath: String, fileName: String): String = {
    return packagePath.replaceAll("\\/", ".") + "." + fileName.replaceAll("\\.java", "")
  }
}

object TestServerResource extends App {
  new TestServerResource()
}
