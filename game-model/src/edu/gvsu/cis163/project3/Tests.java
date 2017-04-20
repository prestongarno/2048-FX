package edu.gvsu.cis163.project3;

import org.junit.Test;

import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * Created by preston on 4/19/17.
 */
public class Tests {
	@Test
	public void testPlaceValue() {
		NumberGame ng;
		SlideDirection[] values = SlideDirection.values();
		for (int i = 0; i < 50000000; i++) {
			int rows = current().nextInt(2,20);
			int col = current().nextInt(2,20);
			ng = new NumberGame(rows, col);
			for (int j = 0; j < 40000000; j++) {
				if(ng.getStatus() == GameStatus.USER_LOST) break;
				ng.slide(values[current().nextInt(4)]);
				ng.placeRandomValue();
				ng.placeRandomValue();
			}
		}
	}
}
