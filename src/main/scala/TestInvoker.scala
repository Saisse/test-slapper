
package jp.saisse.test.slapper

import java.io.PrintStream
import junit.textui.TestRunner
import junit.framework.TestSuite
import junit.framework.JUnit4TestAdapter
import java.lang.Class
import org.junit.runner.manipulation.Filter
import org.junit.runner.Description

class TestInvoker(stream: PrintStream) {
  def this() = this(System.out)

  val runner = new TestRunner(stream)
  
  def run(testType: Class[_]): Unit = {
    runner.doRun(new JUnit4TestAdapter(testType))
  }

  def run(testType: Class[_], methodName: String): Unit = {
    val t = new JUnit4TestAdapter(testType)
    t.filter(new NameFilter(methodName))
    runner.doRun(t)
  }
}

class NameFilter(name: String) extends Filter {
  def shouldRun(description: Description): Boolean = {
    return description.getMethodName() == name
  }
  def describe(): String = "method name filter."
}
