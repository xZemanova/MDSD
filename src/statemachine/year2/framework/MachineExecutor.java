/*
Copyright (c) 2012, Ulrik Pagh Schultz, University of Southern Denmark
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met: 

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer. 
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies, 
either expressed or implied, of the University of Southern Denmark.
*/

package statemachine.year2.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import statemachine.year1.library.Event;
import statemachine.year1.library.IMachine;

/**
 * State machine executor: executes a state machine description,
 * keeping track of current state, stores a map from state name
 * to state object (used to perform state transitions).
 * @author ups
 */
public class MachineExecutor<T extends AbstractRuntime<T>> extends Observable implements IMachine<T> {

    /**
     * Initial state
     */
    private String initialStateName;
    /**
     * Map from state name to state object
     */
    private Map<String,State<T>> states = new HashMap<String,State<T>>();
    /**
     * Runtime, including extended state
     */
    private T runtime;
    
    /**
     * Initialize the state machine based on the machine description
     */
    public MachineExecutor(MachineDescription<T> description) {
        List<State<T>> allStates = description.getAllStates();
        for(State<T> state: allStates)
            states.put(state.getName(), state);
        initialStateName = allStates.get(0).getName();
        runtime = description.createExtendedState();
    }
    
    /**
     * Reset the state machine 
     */
    public void initialize() {
    	runtime.reset();
        setState(initialStateName);
        setChanged();
        notifyObservers();
    }
    
    /**
     * Set the current active state
     * @param stateid the ID of the active state
     */
    public void setState(String stateid) {
        State<T> state = states.get(stateid);
        if(state==null) throw new Error("Illegal state identifier: "+stateid);
        runtime.setState(state);
    }

    /**
     * Get the name of the currently active state
     */
    public String getStateName() {
        return runtime.getStateName();
    }

    /**
     * Process an incoming event based on the current state
     */
    public void processEvent(Event event) {
        if(runtime.getState()==null) throw new Error("State machine not initialized");
        runtime.getState().processEvent(this,event);
        setChanged();
        notifyObservers();
    }

    /**
     * Get the current runtime state of the machine
     */
	@Override
	public T getRuntimeState() {
		return runtime;
	}

}
