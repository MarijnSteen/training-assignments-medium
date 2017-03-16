
import static org.junit.Assert.*;

import org.junit.Test;

public class TestJavaFunctionality {
	
	@Test
	public void testDivide(){
		//Assemble
		int a = 12;
		int b = 4;
		
		//Act
		int c = a/b;
		
		//Assert
		assertEquals(c, 3);
	}
	
	@Test
	public void testStringReverse(){
		//Assemble
		StringBuilder test = new StringBuilder();
		test.append("Hello");
		
		//Act
		test.reverse();
		
		//Assert
		assertEquals(test.toString(), "olleH");
		
	}
	
}
