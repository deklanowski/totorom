/**
 * Copyright 2014-Infinity Bryn Cooke
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * This project is derived from code in the Tinkerpop project under the following licenses:
 *
 * Tinkerpop3
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Tinkerpop2
 * Copyright (c) 2009-Infinity, TinkerPop [http://tinkerpop.com]
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the TinkerPop nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL TINKERPOP BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jglue.totorom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jglue.totorom.internal.DivertPipe;
import org.jglue.totorom.internal.TotoromGremlinPipeline;
import org.jglue.totorom.internal.TraversalFunctionPipe;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Predicate;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.Tokens;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.branch.LoopPipe;
import com.tinkerpop.pipes.sideeffect.SideEffectPipe;
import com.tinkerpop.pipes.transform.TransformPipe.Order;
import com.tinkerpop.pipes.util.FluentUtility;
import com.tinkerpop.pipes.util.structures.Pair;
import com.tinkerpop.pipes.util.structures.Table;
import com.tinkerpop.pipes.util.structures.Tree;

@SuppressWarnings("rawtypes")
abstract class TraversalBase<T, Cap, SideEffect, Mark> implements Traversal<T, Cap, SideEffect, Mark> {

	protected abstract FramedGraph graph();

	protected abstract TotoromGremlinPipeline pipeline();

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
		if(value instanceof Enum) {
			value = value.toString();
		}
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
		if(value instanceof Enum) {
			value = value.toString();
		}
		pipeline().has(key, predicate, value);
		return this;
	}

	public Traversal hasNot(String key) {
		pipeline().hasNot(key);
		return this;
	}

	public Traversal hasNot(String key, Object value) {
		if(value instanceof Enum) {
			value = value.toString();
		}
		pipeline().hasNot(key, value);
		return this;
	}

	public Traversal interval(String key, Comparable startValue, Comparable endValue) {
		if(startValue instanceof Enum) {
			startValue = startValue.toString();
		}
		if(endValue instanceof Enum) {
			endValue = endValue.toString();
		}
		pipeline().interval(key, startValue, endValue);
		return this;
	}

	@Override
	public Traversal identity() {
		pipeline()._();
		return this;
	}

	public Traversal except(Iterable collection) {
		pipeline().except(unwrap(Lists.newArrayList(collection)));
		return this;
	}

	@Override
	public Traversal map(String... keys) {
		pipeline().map(keys);
		return castToTraversal();
	}

	// @Override
	// public Traversal back(String namedStep) {
	// pipeline().back(namedStep);
	// return asTraversal();
	// }
	//
	// @Override
	// public Traversal back(String namedStep, Class clazz) {
	// pipeline().back(namedStep);
	// return asTraversal();
	// }

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
		pipeline().filter(new FramingTraversalFunction(filterFunction, graph()));
		return this;
	}

	@Override
	public Traversal random(double bias) {
		pipeline().random(bias);
		return this;
	}

	@Override
	public Traversal range(int low, int high) {
		pipeline().range(low, high);
		return this;
	}

	@Override
	public Traversal retain(Iterable collection) {
		pipeline().retain(unwrap(Lists.newArrayList(collection)));
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
		return this.aggregate(new ArrayList());

	}

	@Override
	public Traversal aggregate(Collection aggregate) {
		pipeline().aggregate(aggregate, new FramingTraversalFunction<>(graph()));
		return this;
	}

	@Override
	public Traversal aggregate(Collection aggregate, TraversalFunction aggregateFunction) {
		pipeline().aggregate(aggregate, new FramingTraversalFunction<>(aggregateFunction, graph()));
		return this;
	}

	@Override
	public Traversal aggregate(TraversalFunction aggregateFunction) {
		pipeline().aggregate(new FramingTraversalFunction<>(aggregateFunction, graph()));
		return this;
	}

	// @Override
	// public Traversal optional(String namedStep) {
	// pipeline().optional(namedStep);
	// return this;
	// }

	@Override
	public Traversal groupBy(Map map, TraversalFunction keyFunction, TraversalFunction valueFunction) {
		pipeline().groupBy(map, new FramingTraversalFunction<>(keyFunction, graph()),
				new TraversalFunctionPipe(new FramingTraversalFunction<>(valueFunction, graph())));
		return this;
	}

	@Override
	public Traversal groupBy(TraversalFunction keyFunction, TraversalFunction valueFunction) {

		pipeline().groupBy(new FramingTraversalFunction<>(keyFunction, graph()),
				new TraversalFunctionPipe(new FramingTraversalFunction<>(valueFunction, graph())));
		return this;
	}

	@Override
	public Traversal groupBy(Map reduceMap, TraversalFunction keyFunction, TraversalFunction valueFunction,
			TraversalFunction reduceFunction) {
		pipeline().groupBy(reduceMap, new FramingTraversalFunction<>(keyFunction, graph()),
				new TraversalFunctionPipe(new FramingTraversalFunction<>(valueFunction, graph())), reduceFunction);
		return this;
	}

	@Override
	public Traversal groupBy(TraversalFunction keyFunction, TraversalFunction valueFunction, TraversalFunction reduceFunction) {
		pipeline().groupBy(new FramingTraversalFunction<>(keyFunction, graph()),
				new TraversalFunctionPipe(new FramingTraversalFunction<>(valueFunction, graph())), reduceFunction);
		return this;
	}

	@Override
	public Traversal groupCount(Map map, TraversalFunction keyFunction, TraversalFunction valueFunction) {
		pipeline().groupCount(map, new FramingTraversalFunction<>(keyFunction, graph()),
				new TraversalFunctionPipe(new FramingTraversalFunction<>(valueFunction, graph())));
		return this;
	}

	@Override
	public Traversal groupCount(TraversalFunction keyFunction, TraversalFunction valueFunction) {
		pipeline().groupCount(new FramingTraversalFunction<>(keyFunction, graph()),
				new FramingTraversalFunction<>(valueFunction, graph()));
		return this;
	}

	@Override
	public Traversal groupCount(Map map, TraversalFunction keyFunction) {
		pipeline().groupCount(new FramingMap<>(map, graph()), new FramingTraversalFunction<>(keyFunction, graph()));
		return this;
	}

	@Override
	public Traversal groupCount(TraversalFunction keyFunction) {
		pipeline().groupCount(new FramingTraversalFunction<>(keyFunction, graph()));
		return this;
	}

	@Override
	public Traversal groupCount(Map map) {
		pipeline().groupCount(new FramingMap<>(map, graph()));
		return this;
	}

	@Override
	public Traversal groupCount() {
		return this.groupCount(new HashMap());

	}

	@Override
	public EdgeTraversal idEdge(Graph graph) {
		pipeline().idEdge(graph);
		return castToEdges();
	}

	public Traversal id() {
		pipeline().id();
		return castToTraversal();
	}

	public Traversal id(Class c) {
		return id();
	}

	@Override
	public VertexTraversal idVertex(Graph graph) {
		pipeline().idVertex(graph);
		return castToVertices();
	}

	@Override
	public Traversal sideEffect(final SideEffectFunction sideEffectFunction) {
		final FramingSideEffectFunction function = new FramingSideEffectFunction<>(sideEffectFunction, graph());
		pipeline().sideEffect(new TraversalFunction() {

			@Override
			public Object compute(Object argument) {
				function.execute(argument);
				return null;
			}

		});
		return this;
	}

	@Override
	public Traversal store(Collection storage) {
		pipeline().store(storage, new FramingTraversalFunction<>(graph()));
		return this;
	}

	@Override
	public Traversal store(Collection storage, TraversalFunction storageFunction) {
		pipeline().store(storage, new FramingTraversalFunction<>(storageFunction, graph()));
		return this;
	}

	@Override
	public Traversal store() {
		return this.store(new ArrayList<>());
	}

	@Override
	public Traversal store(TraversalFunction storageFunction) {
		pipeline().store(new FramingTraversalFunction<>(storageFunction, graph()));
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
		pipeline().table(table, new FramingTraversalFunction<>(graph()));
		return this;
	}

	@Override
	public Traversal table() {
		pipeline().table(new FramingTraversalFunction<>(graph()));
		return this;
	}

	public Traversal tree(Tree tree) {
		pipeline().tree(tree, new FramingTraversalFunction<>(graph()));
		return this;
	}

	public Traversal tree() {
		pipeline().tree(new FramingTraversalFunction<>(graph()));
		return this;
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
		final FramingComparator framingComparator = new FramingComparator(compareFunction, graph());
		pipeline().order(new TraversalFunction<Pair<Object, Object>, Integer>() {

			@Override
			public Integer compute(Pair<Object, Object> argument) {
				return framingComparator.compare(argument.getA(), argument.getB());
			}
		});
		return this;
	}

	@Override
	public Traversal path(TraversalFunction... pathFunctions) {
		if (pathFunctions.length == 0) {
			pipeline().path(new FramingTraversalFunction<>(graph()));
		} else {
			pipeline().path(wrap(pathFunctions));
		}
		return castToTraversal();
	}

	@Override
	public Traversal select(Collection stepNames, TraversalFunction... columnFunctions) {
		pipeline().select(stepNames, wrap(columnFunctions));
		return castToTraversal();
	}

	@Override
	public Traversal select(TraversalFunction... columnFunctions) {
		pipeline().select(wrap(columnFunctions));
		return castToTraversal();
	}

	@Override
	public Traversal select() {
		pipeline().select(new FramingTraversalFunction<>(graph()));
		return castToTraversal();
	}

	@Override
	public Traversal shuffle() {
		pipeline().shuffle();
		return this;
	}

	@Override
	public Cap cap() {
		pipeline().cap();
		Object next = castToTraversal().next();
		if (next instanceof FramingMap) {
			next = ((FramingMap) next).getDelegate();
		}
		if (next instanceof FramingCollection) {
			next = ((FramingCollection) next).getDelegate();
		}
		return (Cap) next;
	}

	@Override
	public Traversal divert(final SideEffectFunction sideEffectFunction) {

		final FramingSideEffectFunction framingSideEffectFunction = new FramingSideEffectFunction(sideEffectFunction, graph());
		pipeline().add(
				new DivertPipe((SideEffectPipe) FluentUtility.removePreviousPipes(pipeline(), 1).get(0), new TraversalFunction() {

					@Override
					public Object compute(Object argument) {
						framingSideEffectFunction.execute(argument);
						return null;
					}

				}));
		return this;
	}

	@Override
	public Traversal transform(TraversalFunction function) {
		pipeline().transform(new FramingTraversalFunction(function, graph()));
		return castToTraversal();
	}

	@Override
	public Traversal start(Object object) {
		pipeline().start(object);
		return this;
	}

	@Override
	public List next(int number) {
		return Lists.transform(pipeline().next(number), new Function() {

			public Object apply(Object e) {
				if (e instanceof Edge) {
					return graph().frameElement((Element) e, TEdge.class);
				} else if (e instanceof Vertex) {
					return graph().frameElement((Element) e, TVertex.class);
				}
				return e;
			}
		});

	}

	@Override
	public List toList() {

		return Lists.transform(pipeline().toList(), new Function() {

			public Object apply(Object e) {
				if (e instanceof Edge) {
					return graph().frameElement((Element) e, TEdge.class);
				} else if (e instanceof Vertex) {
					return graph().frameElement((Element) e, TVertex.class);
				}
				return e;
			}
		});

	}
	
	@Override
	public Set toSet() {

		return Sets.newHashSet(toList());

	}

	@Override
	public Collection fill(Collection collection) {
		return pipeline().fill(new FramingCollection<>(collection, graph()));
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
	public T next() {
		Object e = pipeline().next();
		if (e instanceof Edge) {
			return (T) graph().frameElement((Element) e, TEdge.class);
		} else if (e instanceof Vertex) {
			return (T) graph().frameElement((Element) e, TVertex.class);
		}
		return (T) e;
	}

	@Override
	public T nextOrDefault(T defaultValue) {
		if (pipeline().hasNext()) {
			return next();
		} else {
			return defaultValue;
		}
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

	public Traversal property(String key) {
		pipeline().property(key);
		return castToTraversal();
	}

	public Traversal property(String key, Class type) {
		return property(key);

	}

	protected abstract Traversal castToTraversal();

	@Override
	public boolean hasNext() {
		return pipeline().hasNext();
	}

	@Override
	public Iterator iterator() {
		return Iterators.transform(pipeline(), new Function() {

			public Object apply(Object e) {
				if (e instanceof Element) {
					return graph().frameElement((Element) e, TVertex.class);
				}

				return e;
			}
		});
	}

	private HashSet unwrap(Collection collection) {
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
	public Traversal mark() {
		MarkId mark = pushMark();
		pipeline().as(mark.id);

		return this;
	}

	@Override
	public Mark back() {
		MarkId mark = popMark();
		pipeline().back(mark.id);
		return (Mark) mark.traversal;
	}

	@Override
	public Mark optional() {
		MarkId mark = popMark();
		pipeline().optional(mark.id);
		return (Mark) mark.traversal;
	}

	/**
	 * Cast the traversal as a split traversal
	 * 
	 * @return
	 */
	protected abstract <N> SplitTraversal<N> castToSplit();

	/**
	 * Cast the traversal as a vertex traversal
	 * 
	 * @return
	 */
	protected abstract VertexTraversal<Cap, SideEffect, Mark> castToVertices();

	/**
	 * Cast the traversal to an edge traversalT
	 * 
	 * @return
	 */
	protected abstract EdgeTraversal<Cap, SideEffect, Mark> castToEdges();

	public abstract MarkId pushMark();

	public abstract MarkId popMark();

	protected static class MarkId {
		Traversal traversal;
		String id;
	}

	@Override
	public VertexTraversal<?, ?, Mark> v(Collection<?> ids) {
		return (VertexTraversal) graph().v(ids);
	}

	@Override
	public EdgeTraversal<?, ?, Mark> e(Collection<?> ids) {
		return (EdgeTraversal) graph().e(ids);
	}

	public Traversal loop(TraversalFunction input) {
		GremlinPipeline pipeline = ((TraversalBase) input.compute(new TVertex())).pipeline();
		pipeline().add(new LoopPipe(pipeline, LoopPipe.createTrueFunction(), null));

		return this;

	}

	public Traversal loop(TraversalFunction input, final int depth) {
		GremlinPipeline pipeline = ((TraversalBase) input.compute(new TVertex())).pipeline();
		pipeline().add(new LoopPipe(pipeline, LoopPipe.createLoopsFunction(depth), null));

		return this;

	}

	@Override
	public Traversal<T, ?, ?, Mark> limit(int limit) {
		return range(0, limit - 1);
	}
	
	@Override
	public void remove() {
		pipeline().remove();
	}
}
