package org.jglue.totorom;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Predicate;
import com.tinkerpop.gremlin.Tokens;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.Pipe;
import com.tinkerpop.pipes.transform.TransformPipe.Order;
import com.tinkerpop.pipes.util.structures.Pair;
import com.tinkerpop.pipes.util.structures.Table;
import com.tinkerpop.pipes.util.structures.Tree;

@SuppressWarnings("rawtypes")
abstract class TraversalBase<T, SE, SideEffectParam1, SideEffectParam2> implements
		Traversal<T, SE, SideEffectParam1, SideEffectParam2> {

	protected abstract FramedGraph graph();

	protected abstract GremlinPipeline pipeline();

	@Override
	public VertexTraversal V() {
		pipeline().V();
		return castToVertices();
	}

	@Override
	public EdgeTraversal E() {
		pipeline().E();
		return castToEdges();
	}

	@Override
	public VertexTraversal v(Object... ids) {
		return (VertexTraversal) graph().v(ids);
	}

	@Override
	public EdgeTraversal e(Object... ids) {
		return (EdgeTraversal) graph().e(ids);
	}

	@Override
	public long count() {
		return pipeline().count();
	}

	@Override
	public Traversal as(String name) {
		pipeline().as(name);
		return this;
	}

	@Override
	public void iterate() {
		pipeline().iterate();
	}

	public Traversal has(String key) {
		pipeline().has(key);
		return this;
	}

	public Traversal has(String key, Object value) {
		pipeline().has(key, value);
		return this;
	}

	public Traversal has(String key, Tokens.T compareToken, Object value) {
		if (value.getClass().isArray()) {
			value = Arrays.asList((Object[]) value);
		}
		pipeline().has(key, compareToken, value);
		return this;
	}

	public Traversal has(String key, Predicate predicate, Object value) {
		pipeline().has(key, predicate, value);
		return this;
	}

	public Traversal hasNot(String key) {
		pipeline().hasNot(key);
		return this;
	}

	public Traversal hasNot(String key, Object value) {
		pipeline().hasNot(key, value);
		return this;
	}

	public Traversal interval(String key, Comparable startValue, Comparable endValue) {
		pipeline().interval(key, startValue, endValue);
		return this;
	}

	@Override
	public Traversal identity() {
		pipeline()._();
		return this;
	}

	public Traversal except(Collection collection) {
		pipeline().except(collection);
		return this;
	}

	@Override
	public Traversal map(String... keys) {
		pipeline().map(keys);
		return asTraversal();
	}

	@Override
	public Traversal property(String key) {
		pipeline().property(key);
		return asTraversal();
	}

	@Override
	public Traversal copySplit(Pipe... pipes) {
		pipeline().copySplit(pipes);
		return asTraversal();
	}

	@Override
	public Traversal exhaustMerge() {
		pipeline().exhaustMerge();
		return asTraversal();
	}

	@Override
	public Traversal fairMerge() {
		pipeline().fairMerge();
		return asTraversal();
	}

	@Override
	public Traversal ifThenElse(TraversalFunction ifFunction, TraversalFunction thenFunction, TraversalFunction elseFunction,
			Class clazz) {
		pipeline().ifThenElse(ifFunction, thenFunction, elseFunction);
		return asTraversal();
	}

	@Override
	public Traversal loop(String namedStep, TraversalFunction whileFunction, Class clazz) {
		pipeline().loop(namedStep, whileFunction);
		return asTraversal();
	}

	@Override
	public Traversal loop(String namedStep, TraversalFunction whileFunction, TraversalFunction emitFunction, Class clazz) {
		pipeline().loop(namedStep, whileFunction, emitFunction);
		return asTraversal();
	}

	@Override
	public Traversal back(String namedStep) {
		pipeline().back(namedStep);
		return asTraversal();
	}

	@Override
	public Traversal back(String namedStep, Class clazz) {
		pipeline().back(namedStep);
		return asTraversal();
	}

	@Override
	public Traversal dedup() {
		pipeline().dedup();
		return this;
	}

	@Override
	public Traversal dedup(TraversalFunction dedupFunction) {
		pipeline().dedup(dedupFunction);
		return this;
	}

	@Override
	public Traversal except(String... namedSteps) {
		pipeline().except(namedSteps);
		return this;
	}

	@Override
	public Traversal filter(TraversalFunction filterFunction) {
		pipeline().filter(filterFunction);
		return this;
	}

	@Override
	public Traversal random(Double bias) {
		pipeline().random(bias);
		return this;
	}

	@Override
	public Traversal range(int low, int high) {
		pipeline().range(low, high);
		return this;
	}

	@Override
	public Traversal retain(Collection collection) {
		pipeline().retain(collection);
		return this;
	}

	@Override
	public Traversal retain(String... namedSteps) {
		pipeline().retain(namedSteps);
		return this;
	}

	@Override
	public Traversal simplePath() {
		pipeline().simplePath();
		return this;
	}

	@Override
	public Traversal aggregate() {
		pipeline().aggregate();
		return this;
	}

	@Override
	public Traversal aggregate(Collection aggregate) {
		pipeline().aggregate(aggregate);
		return this;
	}

	@Override
	public Traversal aggregate(Collection aggregate, TraversalFunction aggregateFunction) {
		pipeline().aggregate(aggregate, aggregateFunction);
		return this;
	}

	@Override
	public Traversal aggregate(TraversalFunction aggregateFunction) {
		pipeline().aggregate(aggregateFunction);
		return this;
	}

	@Override
	public Traversal optional(String namedStep) {
		pipeline().optional(namedStep);
		return this;
	}

	@Override
	public Traversal groupBy(Map map, TraversalFunction keyFunction, TraversalFunction valueFunction) {
		pipeline().groupBy(map, keyFunction, valueFunction);
		return this;
	}

	@Override
	public Traversal groupBy(TraversalFunction keyFunction, TraversalFunction valueFunction) {
		pipeline().groupBy(keyFunction, valueFunction);
		return this;
	}

	@Override
	public Traversal groupBy(Map reduceMap, TraversalFunction keyFunction, TraversalFunction valueFunction,
			TraversalFunction reduceFunction) {
		pipeline().groupBy(reduceMap, keyFunction, valueFunction, reduceFunction);
		return this;
	}

	@Override
	public Traversal groupBy(TraversalFunction keyFunction, TraversalFunction valueFunction, TraversalFunction reduceFunction) {
		pipeline().groupBy(keyFunction, valueFunction, reduceFunction);
		return this;
	}

	@Override
	public Traversal groupCount(Map map, TraversalFunction keyFunction, TraversalFunction valueFunction) {
		pipeline().groupCount(map, keyFunction, valueFunction);
		return this;
	}

	@Override
	public Traversal groupCount(TraversalFunction keyFunction, TraversalFunction valueFunction) {
		pipeline().groupCount(keyFunction, valueFunction);
		return this;
	}

	@Override
	public Traversal groupCount(Map map, TraversalFunction keyFunction) {
		pipeline().groupCount(map, keyFunction);
		return this;
	}

	@Override
	public Traversal groupCount(TraversalFunction keyFunction) {
		pipeline().groupCount(keyFunction);
		return this;
	}

	@Override
	public Traversal groupCount(Map map) {
		pipeline().groupCount(map);
		return this;
	}

	@Override
	public Traversal groupCount() {
		pipeline().groupCount();
		return this;
	}

	@Override
	public EdgeTraversal idEdge(Graph graph) {
		pipeline().idEdge(graph);
		return castToEdges();
	}

	@Override
	public Traversal id() {
		pipeline().id();
		return this;
	}

	@Override
	public VertexTraversal idVertex(Graph graph) {
		pipeline().idVertex(graph);
		return castToVertices();
	}

	@Override
	public Traversal sideEffect(final SideEffectFunction sideEffectFunction) {
		pipeline().sideEffect(new TraversalFunction() {

			@Override
			public Object compute(Object argument) {
				sideEffectFunction.execute(argument);
				return null;
			}

		});
		return this;
	}

	@Override
	public Traversal store(Collection storage) {
		pipeline().store(storage);
		return this;
	}

	@Override
	public Traversal store(Collection storage, TraversalFunction storageFunction) {
		pipeline().store(storage, storageFunction);
		return this;
	}

	@Override
	public Traversal store() {
		pipeline().store();
		return this;
	}

	@Override
	public Traversal store(TraversalFunction storageFunction) {
		pipeline().store(storageFunction);
		return this;
	}

	@Override
	public Traversal table(Table table, Collection stepNames, TraversalFunction... columnFunctions) {
		pipeline().table(table, stepNames, wrap(columnFunctions));
		return this;
	}

	@Override
	public Traversal table(Table table, TraversalFunction... columnFunctions) {
		pipeline().table(table, wrap(columnFunctions));
		return this;
	}

	@Override
	public Traversal table(TraversalFunction... columnFunctions) {
		pipeline().table(wrap(columnFunctions));
		return this;
	}

	@Override
	public Traversal table(Table table) {
		pipeline().table(table);
		return this;
	}

	@Override
	public Traversal table() {
		pipeline().table();
		return this;
	}

	@Override
	public Traversal tree(Tree tree, TraversalFunction... branchFunctions) {
		pipeline().tree(tree, wrap(branchFunctions));
		return this;
	}

	@Override
	public Traversal tree(TraversalFunction... branchFunctions) {
		pipeline().tree(wrap(branchFunctions));
		return this;
	}

	@Override
	public Traversal gather() {
		pipeline().gather();
		return asTraversal();
	}

	@Override
	public Traversal gather(TraversalFunction function) {
		pipeline().gather(function);
		return asTraversal();
	}

	@Override
	public Traversal memoize(String namedStep) {
		pipeline().memoize(namedStep);
		return this;
	}

	@Override
	public Traversal memoize(String namedStep, Map map) {
		pipeline().memoize(namedStep, map);
		return this;
	}

	@Override
	public Traversal order() {
		pipeline().order();
		return this;
	}

	@Override
	public Traversal order(Order order) {
		pipeline().order(order);
		return this;
	}

	@Override
	public Traversal order(Tokens.T order) {
		pipeline().order(order);
		return this;
	}

	@Override
	public Traversal order(final Comparator compareFunction) {
		pipeline().order(new TraversalFunction<Pair<Object, Object>, Integer>() {

			@Override
			public Integer compute(Pair<Object, Object> argument) {
				return compareFunction.compare(argument.getA(), argument.getB());
			}
		});
		return this;
	}

	@Override
	public Traversal path(TraversalFunction... pathFunctions) {
		pipeline().path(wrap(pathFunctions));
		return asTraversal();
	}

	@Override
	public Traversal scatter() {
		pipeline().scatter();
		return asTraversal();
	}

	@Override
	public Traversal select(Collection stepNames, TraversalFunction... columnFunctions) {
		pipeline().select(stepNames, wrap(columnFunctions));
		return asTraversal();
	}

	@Override
	public Traversal select(TraversalFunction... columnFunctions) {
		pipeline().select(wrap(columnFunctions));
		return asTraversal();
	}

	@Override
	public Traversal select() {
		pipeline().select();
		return asTraversal();
	}

	@Override
	public Traversal shuffle() {
		pipeline().shuffle();
		return asTraversal();
	}

	@Override
	public Traversal cap() {
		pipeline().cap();
		return asTraversal();
	}

	@Override
	public Traversal orderMap(Order order) {
		pipeline().orderMap(order);
		return this;
	}

	@Override
	public Traversal orderMap(Tokens.T order) {
		pipeline().orderMap(order);
		return this;
	}

	@Override
	public Traversal orderMap(final Comparator compareFunction) {
		final Comparator wrapped = new FramingComparator(compareFunction, graph());
		pipeline().orderMap(new TraversalFunction<Pair<Object, Object>, Integer>() {

			@Override
			public Integer compute(Pair<Object, Object> argument) {
				return wrapped.compare(argument.getA(), argument.getB());
			}
		});
		return this;
	}

	@Override
	public Traversal transform(TraversalFunction function) {
		pipeline().transform(function);
		return asTraversal();
	}

	@Override
	public Traversal start(Object object) {
		pipeline().start(object);
		return this;
	}

	@Override
	public List next(int number) {
		return pipeline().next(number);
	}

	@Override
	public List toList() {
		return pipeline().toList();
	}

	@Override
	public Collection fill(Collection collection) {
		return pipeline().fill(collection);
	}

	@Override
	public Traversal enablePath() {
		pipeline().enablePath();
		return this;
	}

	@Override
	public Traversal optimize(boolean optimize) {
		pipeline().optimize(optimize);
		return this;
	}

	@Override
	public void remove() {
		pipeline().remove();
	}

	@Override
	public T next() {

		return (T) pipeline().next();
	}

	@Override
	public EdgeTraversal start(FramedEdge object) {
		pipeline().start(object);
		return castToEdges();
	}

	@Override
	public VertexTraversal start(FramedVertex object) {
		pipeline().start(object);
		return castToVertices();
	}

	@Override
	public Traversal property(String key, Class type) {
		return (Traversal) property(key);
	}

	protected abstract Traversal asTraversal();

	@Override
	public boolean hasNext() {
		return pipeline().hasNext();
	}

	@Override
	public Iterator<T> iterator() {

		return pipeline().iterator();
	}

	protected HashSet unwrap(Collection collection) {
		HashSet unwrapped = new HashSet(Collections2.transform(collection, new Function<Object, Object>() {

			@Override
			public Object apply(Object o) {
				if (o instanceof FramedVertex) {
					return ((FramedVertex) o).element();
				}
				if (o instanceof FramedEdge) {
					return ((FramedEdge) o).element();
				}
				return o;
			}
		}));
		return unwrapped;
	}

	private TraversalFunction[] wrap(TraversalFunction... branchFunctions) {
		Collection<TraversalFunction> wrapped = Collections2.transform(Arrays.asList(branchFunctions),
				new Function<TraversalFunction, TraversalFunction>() {

					@Override
					public TraversalFunction apply(TraversalFunction input) {
						return new FramingTraversalFunction(input, graph());
					}

				});
		TraversalFunction[] wrappedArray = wrapped.toArray(new TraversalFunction[wrapped.size()]);
		return wrappedArray;
	}

	@Override
	public Traversal gatherScatter() {
		pipeline().gather().scatter();
		return this;
	}

	@Override
	public Traversal cast(Class<T> clazz) {

		return this;
	}
}