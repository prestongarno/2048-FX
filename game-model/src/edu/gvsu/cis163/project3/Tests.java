package edu.gvsu.cis163.project3;

import org.junit.Test;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by preston on 4/19/17.
 */
public class Tests {
	@Test
	public void testPlaceValue() {
		NumberGame ng;
		SlideDirection[] values = SlideDirection.values();
		for (int i = 0; i < 1; i++) {
			int rows = 4;//current().nextInt(2,20);
			int col = 4;//current().nextInt(2,20);
			ng = new NumberGame(rows, col);
			for (int rand = 0; rand < 16; rand++) {
				ng.placeRandomValue();
				ng.saveState();
			}
			assertTrue(ng.getStatus() == GameStatus.USER_LOST);
			ng.setStatus(GameStatus.IN_PROGRESS);
			System.out.println("Game over @" + ng.prettyPrintBoard());
			for (int j = 0; j < 17; j++) {
				ng.undo();
				System.out.println("------------------");
				System.out.println("Previous state: " + ng.prettyPrintBoard());
			}
/*			for (int j = 0; j < 40000000; j++) {
				ng.slide(values[current().nextInt(4)]);
				try {
					ng.placeRandomValue();
					ng.placeRandomValue();
					ng.placeRandomValue();
				} catch (IllegalStateException x) {
					ng.setStatus(GameStatus.IN_PROGRESS);
					System.out.println("Game over @" + ng.prettyPrintBoard());
					ng.undo();
					System.out.println("Previous state: " + ng.prettyPrintBoard());
				}
			}*/
		}
	}

	@Test
	public void testImpossibleGame() throws Exception {
		for (int w = 0; w <100; w++) {

			NumberGame ng = new NumberGame(10,5);
			boolean offset = false;
			for (int r = 0; r < ng.rows(); r++) {
				for (int c = 0; c < ng.columns(); c++) {
					ng.setAt(r,c, offset ? 1 : 9);
					offset = ! offset;
				}
			}
			for (int i = 0; i < 5; i++) {
				ng.setAt(0,i,i%2==0?8:7);
			}
			ng.setAt(0,0,0);
			System.out.println(ng.getStatus());
			System.out.println(ng.prettyPrintBoard());
			ng.slide(SlideDirection.LEFT);
			System.out.println(ng.placeRandomValue());
			System.out.println(ng.prettyPrintBoard());

			assertFalse(ng.getStatus() == GameStatus.USER_LOST);
		}
	}
}
