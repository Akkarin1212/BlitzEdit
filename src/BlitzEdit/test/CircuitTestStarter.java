package BlitzEdit.test;

import java.util.ArrayList;

import BlitzEdit.test.CircuitCreateTest;

public class CircuitTestStarter {
	
	private static ArrayList<Runnable> tests = new ArrayList<Runnable>();
	
	public static void main(String[] args) {
		
		tests.add(new CircuitCreateTest());
		tests.add(new CircuitAddElementTest());
		tests.add(new CircuitSelectTest());
		
		System.out.println("Starting " + tests.size() + " tests.");
		//Arbeitet die tests sequentiell ab
		for (Runnable test : tests)
			test.run();
		
		System.out.println("All tests completed.");
		
	}

}
