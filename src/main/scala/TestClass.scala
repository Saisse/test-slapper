
package jp.saisse.test.slapper

import org.junit.Test
import org.junit.Ignore

class TestClass(testClass: Class[_]) {
  
  lazy val _testMethods = testClass.getMethods().filter(
      m => m.getAnnotation(classOf[Test]) != null).map {
        m => TestMethod(this, m.getName(), m.getAnnotation(classOf[Ignore]) != null)
      }

  def testMethods = _testMethods

  def name = testClass.getName()
  
}

case class TestMethod(testClass: TestClass, name: String, ignored: Boolean) {
}