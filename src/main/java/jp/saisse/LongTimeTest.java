package jp.saisse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
public class LongTimeTest {

	@Test
	public void test1() {
		wait(1);
	}
	
	@Test
	public void test2() {
		wait(2);
	}
	
	@Test
	public void test3() {
		throw new RuntimeException("error");
	}

	@Test
	public void test4() {
		wait(4);
	}
	
	private void wait(int times) {
		for(int i = 0; i < times; i++) {
			System.out.println(i);
			sleep();
		}
		
	}
	
	private void sleep() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// nothing todo.
		}
	}
}
