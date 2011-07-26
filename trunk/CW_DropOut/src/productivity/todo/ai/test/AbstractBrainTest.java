package productivity.todo.ai.test;

import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;

import org.junit.Test;

import productivity.todo.ai.AbstractBrain;
import productivity.todo.model.GameMap;
import productivity.todo.model.Player;

public class AbstractBrainTest extends AbstractBrain{

	@Test
	public void testScaleVector() {
		Point2D.Double vector = new Point2D.Double(1.5,1.5);
		Point2D.Double result = AbstractBrain.scaleVector(vector,2);
		assertTrue(result.x == 3 && result.y == 3);
	}

	@Test
	public void testAddVectors() {
		Point2D.Double v1 = new Point2D.Double(1,2);
		Point2D.Double v2 = new Point2D.Double(2,1);
		Point2D.Double result = AbstractBrain.addVectors(v1, v2);
		assertTrue(result.x == 3 && result.y == 3);
	}

	@Test
	public void testGetSlopeBetweenPoints() {
		Point2D.Double v1 = new Point2D.Double(0,0);
		Point2D.Double v2 = new Point2D.Double(1,1);
		double result = AbstractBrain.getSlopeBetweenPoints(v1, v2);
		assertTrue(result == 1);
	}

	@Test
	public void testGetAngleBetweenVectors() {
		Point2D.Double v1 = new Point2D.Double(0,1);
		Point2D.Double v2 = new Point2D.Double(1,0);
		double result = AbstractBrain.getAngleBetweenVectors(v1, v2);
		assertTrue(result == Math.PI/2);
	}

	@Test
	public void testLength() {
		Point2D.Double vector = new Point2D.Double(1.5,1.5);
		double result = AbstractBrain.length(vector);
		assertTrue(result == Math.hypot(1.5, 1.5));
	}

	@Test
	public void testGetDirectionToLoc() {
		Point2D.Double v1 = new Point2D.Double(1,1);
		Point2D.Double v2 = new Point2D.Double(1,3);
		Point2D.Double result = AbstractBrain.getDirectionToLoc(v1, v2);
		assertTrue(result.x == 1 && result.y == 2);
	}

	@Test
	public void testNormalize() {
		Point2D.Double vector = new Point2D.Double(3,0);
		Point2D.Double result = AbstractBrain.normalize(vector);
		assertTrue(result.x == 1 && result.y == 0);
	}

	@Test
	public void testGetVectorBetween() {
		Point2D.Double p1 = new Point2D.Double(2,2);
		Point2D.Double p2 = new Point2D.Double(4,4);
		Point2D.Double result = AbstractBrain.getVectorBetween(p1,p2);
		assertTrue(result.x == 2 && result.y == 2);
	}

	@Override
	public void makeMove(GameMap gm, Player p) {
		return;
	}

}
