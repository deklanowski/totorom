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
package org.jglue.totorom.internal;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.tinkerpop.pipes.Pipe;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.sideeffect.SideEffectPipe;
import com.tinkerpop.pipes.util.AbstractMetaPipe;
import com.tinkerpop.pipes.util.FastNoSuchElementException;
import com.tinkerpop.pipes.util.MetaPipe;
import com.tinkerpop.pipes.util.PipeHelper;

public class DivertPipe<S, T> extends AbstractMetaPipe<S, S> implements MetaPipe {

	private final SideEffectPipe<S, T> pipeToCap;
	
	private PipeFunction<T, ?> sideEffectFunction;

	public DivertPipe(final SideEffectPipe<S, T> pipeToCap, final PipeFunction<T, ?> sideEffectFunction) {
		this.pipeToCap = pipeToCap;
		this.sideEffectFunction = sideEffectFunction;
	}

	public void setStarts(final Iterator<S> starts) {
		this.pipeToCap.setStarts(starts);
	}

	protected S processNextStart() {
		if (this.pipeToCap instanceof SideEffectPipe.LazySideEffectPipe) {
			S next = this.pipeToCap.next();
			sideEffectFunction.compute(this.pipeToCap.getSideEffect());
			return next;
		} else {

			try {
				return this.pipeToCap.next();
			} catch (final NoSuchElementException e) {
				sideEffectFunction.compute(this.pipeToCap.getSideEffect());
				throw FastNoSuchElementException.instance();
			}
		
		}
	}

	public List getCurrentPath() {
		if (this.pathEnabled) {
			final List list = this.pipeToCap.getCurrentPath();
			list.add(this.currentEnd);
			return list;
		} else {
			throw new RuntimeException(Pipe.NO_PATH_MESSAGE);
		}
	}

	public String toString() {
		return PipeHelper.makePipeString(this, this.pipeToCap);
	}

	public List<Pipe> getPipes() {
		return Arrays.asList((Pipe) this.pipeToCap);
	}

	public void reset() {
		super.reset();
	}
}