package tddc17;


import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.util.*;
import java.lang.Math;
import java.util.Random;

class MyAgentState
{
	public int[][] world = new int[30][30];
	public int initialized = 0;
	final int UNKNOWN 	= 0;
	final int WALL 		= 1;
	final int CLEAR 	= 2;
	final int DIRT		= 3;
	final int HOME		= 4;
	final int ACTION_NONE 			= 0;
	final int ACTION_MOVE_FORWARD 	= 1;
	final int ACTION_TURN_RIGHT 	= 2;
	final int ACTION_TURN_LEFT 		= 3;
	final int ACTION_SUCK	 		= 4;
	
	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public int agent_direction = EAST;
	



	MyAgentState()
	{
		for (int i=0; i < world.length; i++)
			for (int j=0; j < world[i].length ; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = HOME;
		agent_last_action = ACTION_NONE;
	}
	// Based on the last action and the received percept updates the x & y agent position
	public void updatePosition(DynamicPercept p)
	{
		Boolean bump = (Boolean)p.getAttribute("bump");

		if (agent_last_action==ACTION_MOVE_FORWARD && !bump)
	    {
			switch (agent_direction) {
			case MyAgentState.NORTH:
				agent_y_position--;
				break;
			case MyAgentState.EAST:
				agent_x_position++;
				break;
			case MyAgentState.SOUTH:
				agent_y_position++;
				break;
			case MyAgentState.WEST:
				agent_x_position--;
				break;
			}
	    }
		
	}
	
	public void updateWorld(int x_position, int y_position, int info)
	{
		world[x_position][y_position] = info;
	}
	
	public void printWorldDebug()
	{
		for (int i=0; i < world.length; i++)
		{
			for (int j=0; j < world[i].length ; j++)
			{
				if (world[j][i]==UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i]==WALL)
					System.out.print(" # ");
				if (world[j][i]==CLEAR)
					System.out.print(" . ");
				if (world[j][i]==DIRT)
					System.out.print(" D ");
				if (world[j][i]==HOME)
					System.out.print(" H ");
			}
			System.out.println("");
		}
	}
}

class MyAgentProgram implements AgentProgram {

	private int initnialRandomActions = 10;
	private Random random_generator = new Random();
	
	// Here you can define your variables!
	public MyAgentState state = new MyAgentState();
	public Queue<Integer> queuedActions = new LinkedList<Integer>();
	public ArrayList<Point> path  = new ArrayList<Point>();

	public static final int FORWARD = 0;  
	public static final int RIGHT = 1;
	public static final int LEFT = 2;

	public int iterationCounter = state.world.length * state.world[0].length * 2 ;

// Class to handle x,y positions of the agent. We have some overrides over compare function so that methods like "contains" work properly for us.
	public class Point {
		public final int x;
		public final int y;

		public Point(int x, int y){
   			this.x=x;
    		this.y=y;
		}

	@Override
	public int hashCode() {
		return 256000*this.x + this.y;
	}

	@Override
	public boolean equals(Object obj) {
		return this.x == ((Point)obj).x && this.y == ((Point)obj).y;
	}
}

	// moves the Agent to a random start position
	// uses percepts to update the Agent position - only the position, other percepts are ignored
	// returns a random action
	private Action moveToRandomStartPosition(DynamicPercept percept) {
		int action = random_generator.nextInt(6);
		initnialRandomActions--;
		state.updatePosition(percept);
		if(action==0) {
		    state.agent_direction = ((state.agent_direction-1) % 4);
		    if (state.agent_direction<0) 
		    	state.agent_direction +=4;
		    state.agent_last_action = state.ACTION_TURN_LEFT;
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		} else if (action==1) {
			state.agent_direction = ((state.agent_direction+1) % 4);
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		} 
		state.agent_last_action=state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}
	
	public Action rightTurn(){
		state.agent_last_action = state.ACTION_TURN_RIGHT;
	    state.agent_direction = ((state.agent_direction + 1) % 4);
		return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	}

	public Action goForward(){
		state.agent_last_action = state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}


	public Action leftTurn(){
		state.agent_last_action = state.ACTION_TURN_LEFT;
	    state.agent_direction = ((state.agent_direction - 1 + 4) % 4);
		return LIUVacuumEnvironment.ACTION_TURN_LEFT;
	}

// This method will return one action and call the right method based on what was popped from the queue.
	public Action doQueuedActions(){
		switch (queuedActions.remove()) 
		{
			case FORWARD:
				return goForward();
			case LEFT:
				return leftTurn();
			case RIGHT:
				return rightTurn();
			default:
				return null;
		}
	}


  	// This method will based on the goal location (end), build a path with the help of saved points in the hashmap to the location from where we are to the goal. It will return an Arraylist with the first element being the goal. 
	public ArrayList<Point> buildPathToChild(Point end, HashMap<Point, 
		Point> childParentLinks){
		path.add(end);
		Point parent = childParentLinks.get(end);
		while(parent != null){
			path.add(parent);
			end = parent;
			parent = childParentLinks.get(end);
		}
		path.remove(path.size()-1);
		return path;
	}

	// breadth-first search to find places that we can go with our agent. We search for UNKNOWN places that we can go to.
	public ArrayList<Point> breadthFirstSearch(){

		HashMap<Point, Point> childParentLinks = new HashMap<Point, Point>();
		Queue<Point> frontier = new LinkedList<Point>();
		Point startPoint = new Point(state.agent_x_position, state.agent_y_position);
		frontier.add(startPoint);
		childParentLinks.put(startPoint, null);
	    while(!frontier.isEmpty())
	    {
	    	Point parent = frontier.remove();
	    	if(state.world[parent.x][parent.y] == state.UNKNOWN){
	    		return buildPathToChild(parent, childParentLinks);
	    	}
	    	for(int i=-1; i<=1; i++){
	    		for(int j =-1; j<=1; j++){
	    			Point child = new Point(parent.x + i, parent.y + j);
	    			// make sure we only adjacent places that are south, east. north or west and that we havent already explored the child
	    			if((Math.abs(j) != Math.abs(i)) && !childParentLinks.containsKey(child))
	    			{
	    				if(state.world[child.x][child.y] != state.WALL){
	    					childParentLinks.put(child, parent);
	    					frontier.add(child);
	    				}
	    			}
	    		}
	    	}	
	    }
	    return null;
	}

// Takes the path as an argument and looks at the next step to make in the path.
// Based on the next steps coordination and the agents current point, different methods are called.
	public void prepareActions(ArrayList<Point> path){
		
			Point nextStep = path.remove(path.size() -1);
			if(nextStep.x > state.agent_x_position)
			{
				turnEast();
			}
			else if(nextStep.x < state.agent_x_position)
			{
				turnWest();
			}
			else if(nextStep.y < state.agent_y_position)
			{
				turnNorth();
			}
			else if(nextStep.y > state.agent_y_position)
			{
				turnSouth();
			}
		}
	

	@Override
	public Action execute(Percept percept) {
		
		// DO NOT REMOVE this if condition!!!
    	if (initnialRandomActions>0) {
    		return moveToRandomStartPosition((DynamicPercept) percept);
    	} else if (initnialRandomActions==0) {
    		// process percept for the last step of the initial random actions
    		initnialRandomActions--;
    		state.updatePosition((DynamicPercept) percept);
			System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
			state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
    	}
		
    	// This example agent program will update the internal agent state while only moving forward.
    	// START HERE - code below should be modified!
    	    	
    	System.out.println("x=" + state.agent_x_position);
    	System.out.println("y=" + state.agent_y_position);
    	System.out.println("dir=" + state.agent_direction);
    	
		
	    iterationCounter--;
	    if (iterationCounter==0)
	    	return NoOpAction.NO_OP;

	    DynamicPercept p = (DynamicPercept) percept;
	    Boolean bump = (Boolean)p.getAttribute("bump");
	    Boolean dirt = (Boolean)p.getAttribute("dirt");
	    Boolean home = (Boolean)p.getAttribute("home");
	    System.out.println("percept: " + p);
	    
	    // State update based on the percept value and the last action
	    state.updatePosition((DynamicPercept)percept);
	    if (bump) {
			switch (state.agent_direction) {
			case MyAgentState.NORTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position-1,state.WALL);
				break;
			case MyAgentState.EAST:
				state.updateWorld(state.agent_x_position+1,state.agent_y_position,state.WALL);
				break;
			case MyAgentState.SOUTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position+1,state.WALL);
				break;
			case MyAgentState.WEST:
				state.updateWorld(state.agent_x_position-1,state.agent_y_position,state.WALL);
				break;
			}
	    }

	    if (dirt){
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
	    }
	    else if (!home){
		    state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
			}
	    state.printWorldDebug();


	      // Next action selection based on the percept value
	  	if (dirt)
	    {
	    	System.out.println("DIRT -> choosing SUCK action!");
	    	state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
	    } 


	    if(queuedActions.size() == 0 && path.size() == 0){
	    	path = breadthFirstSearch();
	    	if(path == null){
	    		return NoOpAction.NO_OP;
	    	}
	    }

	    if (queuedActions.size() == 0){
	    	prepareActions(path);
	    }
	    if(queuedActions.size() > 0){
	    	return doQueuedActions();
	    } 
	    return NoOpAction.NO_OP;
	}


	public void turnEast(){
		switch(state.agent_direction){
			case MyAgentState.EAST:
				queuedActions.add(FORWARD);
				break;
			case MyAgentState.SOUTH:
				queuedActions.add(LEFT);
				queuedActions.add(FORWARD);
				break;
			case MyAgentState.NORTH:
				queuedActions.add(RIGHT);
				queuedActions.add(FORWARD);
				break;
			case MyAgentState.WEST:
				queuedActions.add(LEFT);
				queuedActions.add(LEFT);
				queuedActions.add(FORWARD);
				break;
		}
	}

	public void turnWest(){
		switch(state.agent_direction){
			case MyAgentState.EAST:
				queuedActions.add(RIGHT);
				queuedActions.add(RIGHT);
				queuedActions.add(FORWARD);
				break;
			case MyAgentState.SOUTH:
				queuedActions.add(RIGHT);
				queuedActions.add(FORWARD);
				break;
			case MyAgentState.NORTH:
				queuedActions.add(LEFT);
				queuedActions.add(FORWARD);
				break;
			case MyAgentState.WEST:
				queuedActions.add(FORWARD);
				break;
		}
	}

	public void turnSouth(){
		switch(state.agent_direction){
			case MyAgentState.EAST:
				queuedActions.add(RIGHT);
				queuedActions.add(FORWARD);
				break;
			case MyAgentState.SOUTH:
				queuedActions.add(FORWARD);
				break;
			case MyAgentState.NORTH:
				queuedActions.add(RIGHT);
				queuedActions.add(RIGHT);
				queuedActions.add(FORWARD);
				break;
			case MyAgentState.WEST:
				queuedActions.add(LEFT);
				queuedActions.add(FORWARD);
				break;
		}
	}

	public void turnNorth(){
		switch(state.agent_direction){
			case MyAgentState.EAST:
				queuedActions.add(LEFT);
				queuedActions.add(FORWARD);
				break;
			case MyAgentState.SOUTH:
				queuedActions.add(LEFT);
				queuedActions.add(LEFT);
				queuedActions.add(FORWARD);
				break;
			case MyAgentState.NORTH:
				queuedActions.add(FORWARD);
				break;
			case MyAgentState.WEST:
				queuedActions.add(RIGHT);
				queuedActions.add(FORWARD);
				break;
		}
	}
}

public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram());
	}
}
