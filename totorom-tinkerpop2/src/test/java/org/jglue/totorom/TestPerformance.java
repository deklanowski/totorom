package org.jglue.totorom;

import org.junit.Test;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;

public class TestPerformance {
	private static final int ITERATIONS = 100000;
	private static final MetricRegistry metrics = new MetricRegistry();

	@Test
	public void runTests() {
		for(int count = 0; count < 10; count++) {
			testTraversalPerformance();
			testIndexPerformance();
			testFilterPerformance();
		}
	}
	
	
	public void testTraversalPerformance() {

		TinkerGraph t = TinkerGraphFactory.createTinkerGraph();

		FramedGraph f = new FramedGraph(t);

		Timer timer = metrics.timer("gremlin");
		Context time = timer.time();

		for (int count = 0; count < ITERATIONS; count++) {
			GremlinPipeline g = new GremlinPipeline(t);
			g.V().both().both().both().toList();
		}
		long nanoseconds = time.stop();

		System.out.println("Iterate over all GremlinPipeline1 " + nanoseconds/1000000);

		time = timer.time();

		for (int count = 0; count < ITERATIONS; count++) {
			GremlinPipeline g = new GremlinPipeline(t);
			g.V().both().both().both().toList();
		}
		nanoseconds = time.stop();

		System.out.println("Iterate over all GremlinPipeline2 " + nanoseconds/1000000);

		time = timer.time();
		for (int count = 0; count < ITERATIONS; count++) {
			f.V().both().both().both().toList();
		}
		nanoseconds = time.stop();
		System.out.println("Iterate over all Totorom1 " + nanoseconds/1000000);

		time = timer.time();
		for (int count = 0; count < ITERATIONS; count++) {
			f.V().both().both().both().toList();
		}
		nanoseconds = time.stop();
		System.out.println("Iterate over all Totorom2 " + nanoseconds/1000000);
	}

	public void testIndexPerformance() {

		TinkerGraph t = TinkerGraphFactory.createTinkerGraph();
		t.createKeyIndex("name", Vertex.class);

		FramedGraph f = new FramedGraph(t);

		Timer timer = metrics.timer("gremlin");
		Context time = timer.time();

		Vertex lastVertex = null;
		for (int count = 0; count < ITERATIONS; count++) {
			Vertex v = t.addVertex(null);
			v.setProperty("name", "name" + count);
			if (lastVertex != null) {
				v.addEdge("test", lastVertex);
			}
			lastVertex = v;

		}

		for (int count = 0; count < ITERATIONS; count++) {
			GremlinPipeline g = new GremlinPipeline(t);
			g.V("name", "name" + count).both().both().both().toList();
		}
		long nanoseconds = time.stop();

		System.out.println("Iterate with index lookup GemlinPipeline1 " + nanoseconds/1000000);

		time = timer.time();

		for (int count = 0; count < ITERATIONS; count++) {
			GremlinPipeline g = new GremlinPipeline(t);
			g.V("name", "name" + count).both().both().both().toList();
		}
		nanoseconds = time.stop();

		System.out.println("Iterate with index lookup GemlinPipeline2 " + nanoseconds/1000000);

		time = timer.time();
		for (int count = 0; count < ITERATIONS; count++) {
			f.V("name", "name" + count).both().both().both().toList();
		}
		nanoseconds = time.stop();
		System.out.println("Iterate with index lookup Totorom1 " + nanoseconds/1000000);

		time = timer.time();
		for (int count = 0; count < ITERATIONS; count++) {
			f.V("name", "name" + count).both().both().both().toList();
		}
		nanoseconds = time.stop();
		System.out.println("Iterate with index lookup Totorom2 " + nanoseconds/1000000);
	}
	
	
	public void testFilterPerformance() {

		TinkerGraph t = TinkerGraphFactory.createTinkerGraph();

		FramedGraph f = new FramedGraph(t);

		Timer timer = metrics.timer("gremlin");
		Context time = timer.time();

		for (int count = 0; count < ITERATIONS; count++) {
			GremlinPipeline g = new GremlinPipeline(t);
			g.V().both().both().both().filter(new PipeFunction<Vertex, Boolean>() {

				@Override
				public Boolean compute(Vertex argument) {

					return "java".equals(argument.getProperty("lang"));
				}
				
			}).toList();
		}
		long nanoseconds = time.stop();

		System.out.println("Iterate with filter GremlinPipeline1 " + nanoseconds/1000000);

		time = timer.time();

		for (int count = 0; count < ITERATIONS; count++) {
			GremlinPipeline g = new GremlinPipeline(t);
			g.V().both().both().both().filter(new PipeFunction<Vertex, Boolean>() {

				@Override
				public Boolean compute(Vertex argument) {

					return "java".equals(argument.getProperty("lang"));
				}
				
			}).toList();
		}
		nanoseconds = time.stop();

		System.out.println("Iterate with filter GremlinPipeline2 " + nanoseconds/1000000);

		time = timer.time();
		for (int count = 0; count < ITERATIONS; count++) {
			f.V().both().both().both().filter(new TraversalFunction<TVertex, Boolean>() {
				
				@Override
				public Boolean compute(TVertex argument) {
					return "java".equals(argument.getProperty("name"));
				}
			}).toList();
		}
		nanoseconds = time.stop();
		System.out.println("Iterate with filter Totorom1 " + nanoseconds/1000000);

		time = timer.time();
		for (int count = 0; count < ITERATIONS; count++) {
			f.V().both().both().both().filter(new TraversalFunction<TVertex, Boolean>() {
				
				@Override
				public Boolean compute(TVertex argument) {
					return "java".equals(argument.getProperty("name"));
				}
			}).toList();
		}
		nanoseconds = time.stop();
		System.out.println("Iterate with filter Totorom2 " + nanoseconds/1000000);
	}
}