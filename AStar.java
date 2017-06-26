import java.util.*;
//cell information in Cell class
class Cell{
		//to hold the parent of current cell
		int parent_i,parent_j;
		double f_value,g_value,h_value;
		Cell(double f,double g,double h,int pi,int pj){
			this.f_value = f;
			this.g_value = g;
			this.h_value = h;
			this.parent_i = pi;
			this.parent_j = pj;
		}
}

//class to store the f value and the indices in the stack
class pPair{
	int i,j;
	double f;
	pPair(double f_value,int i_value,int j_value){
		this.f = f_value;
		this.i = i_value;
		this.j = j_value;
	}
}
	
//class to store cell indices
class pair{
		int i,j;
		pair(int i_value,int j_value){
			this.i = i_value;
			this.j = j_value;
		}
}

public class AStar {
	public static boolean foundDestination = false;
	public boolean isValid(pair obj,int grid[][]){
		int i_value = obj.i,j_value = obj.j;
		int row = grid.length,col = grid[0].length;
		if(i_value<0 || i_value>=row || j_value<0 || j_value >=col)	return false;
		return true;
	}
	
	public boolean isValid(int i,int j,int grid[][]){
		int i_value = i,j_value = j;
		int row = grid.length,col = grid[0].length;
		if(i_value<0 || i_value>=row || j_value<0 || j_value >=col)	return false;
		return true;
	}
	
	public boolean isDestination(int i,int j,pair destination){
		return (i==destination.i && j==destination.j);
	}
	
	public double euclideanDistance(int row,int col,pair dest){
		  return ((double)Math.sqrt ((row-dest.i)*(row-dest.i)+ (col-dest.j)*(col-dest.j)));
	}
	
	public void cellDetailsPrint(Cell cellDetails[][]){
		int row = cellDetails.length;
		int col = cellDetails[0].length;
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++){
				System.out.print(cellDetails[i][j].f_value+"\t");
			}
			System.out.println();
		}
	}
	
	public void tracePath(Cell cellDetails[][],pair destination){
		//tracing the path
		int row = destination.i;
		int col = destination.j;
		System.out.println("The path is");
		Stack<pair> tracePath = new Stack<pair>();
		while(!(cellDetails[row][col].parent_i==row && cellDetails[row][col].parent_j==col)){
			tracePath.push(new pair(row,col));
			int temp_row = cellDetails[row][col].parent_i;
			int temp_col = cellDetails[row][col].parent_j;
			row = temp_row;
			col = temp_col;
		}
		//push the starting cell's i and j
		tracePath.push(new pair(row,col));
		//print the stack trace
		while(!tracePath.isEmpty()){
			pair p = tracePath.pop();
			System.out.println("->("+p.i+" "+p.j+")");
		}
		
	}
	
	public boolean isUnblocked(int [][]grid,int i,int j){
		return (grid[i][j]==1);
	}
	
	public void successorTrace(double addDistance, int original_i,int original_j,Set<pPair> openList,int i,int j,int grid[][],pair destination,Cell [][] cellDetails,boolean [][] closedList, double gNew,double hNew,double fNew){
		//check for the valid i j and cell blocker
		if(isValid(i,j,grid)){
			//if destination is same as this cell
			if(isDestination(i,j,destination)){
				//set the parent of this cell to be the prev one
				cellDetails[i][j].parent_i = original_i;
				cellDetails[i][j].parent_j = original_j;
				System.out.println("Destination is found");
				//cellDetailsPrint(cellDetails);
				tracePath(cellDetails, destination);
				foundDestination = true;
				return;
			}
			//check if the successor is already chosen in some other path
			else if(!closedList[i][j] && isUnblocked(grid,i,j)){
				gNew = cellDetails[original_i][original_j].g_value + addDistance; //default cell movement distance is 1.0
				hNew = euclideanDistance(i,j,destination);
				fNew = gNew+hNew;
				// if the cell is not in the openList, add it with appropriate cellDetails set
				//OR if it is already in the openList, check if the new fvalue is smaller
				
				if(cellDetails[i][j].f_value == Float.MAX_VALUE || fNew < cellDetails[i][j].f_value){
					openList.add(new pPair(fNew,i,j));
					
					//update the cell details
					cellDetails[i][j].f_value = fNew;
					cellDetails[i][j].g_value = gNew;
					cellDetails[i][j].h_value = hNew;
					cellDetails[i][j].parent_i = original_i;
					cellDetails[i][j].parent_j = original_j;
				}
			}
		}
		
	}
	
	public void aStarSearch(int grid[][],pair source,pair destination){
		//checking the corner cases
		//1. source or destination is outside the given range
		if(!isValid(source,grid)||!isValid(destination,grid) || !isUnblocked(grid,source.i,source.j) || !isUnblocked(grid,destination.i,destination.j)){
			System.out.println("Out of range or Inacessible");
		}
		// if destination is same as source
		if(isDestination(source.i,source.j,destination)){
			System.out.print("source and destination ar same");
		}
		
		int row = grid.length;
		int col = grid[0].length;
		
		//graph traversal visited matrix
		boolean [][]closedList = new boolean[row][col];
		
		//2d array of cell details, assign to Float MAX value and parents to -1 initially
		
		Cell [][] cellDetails = new Cell[row][col];
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++){
				cellDetails[i][j] = new Cell(Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE,-1,-1);
			}
		}
		
		//Initialising the parameter of the starting node
		
		int i = source.i;
		int j = source.j;
		cellDetails[i][j].f_value = 0.0;
		cellDetails[i][j].g_value = 0.0;
		cellDetails[i][j].h_value = 0.0;
		cellDetails[i][j].parent_i = i;
		cellDetails[i][j].parent_j = j;
		
		//An open list, act as stack to insert the nodes that trace the path
		Set<pPair> openList = new LinkedHashSet<pPair>();
		openList.add(new pPair(0.0,i,j));
		
		
		while(!openList.isEmpty()){
			pPair node = openList.iterator().next();
			openList.remove(node);
			
			//get the i and j and make this cell, a visited one
			i = node.i;
			j = node.j;
			closedList[i][j] = true;
			double gNew=0.0,hNew=0.0,fNew=0.0;
			//8 successors check
			//north
			if(!foundDestination) 	successorTrace(1.0,i,j,openList,i-1,j,grid,destination,cellDetails,closedList,gNew,hNew,fNew);//North
			if(!foundDestination)	successorTrace(1.0,i,j,openList,i+1,j,grid,destination,cellDetails,closedList,gNew,hNew,fNew);//south
			if(!foundDestination)	successorTrace(1.0,i,j,openList,i,j+1,grid,destination,cellDetails,closedList,gNew,hNew,fNew);//east
			if(!foundDestination)	successorTrace(1.0,i,j,openList,i,j-1,grid,destination,cellDetails,closedList,gNew,hNew,fNew);//west
			if(!foundDestination)	successorTrace(1.414,i,j,openList,i-1,j+1,grid,destination,cellDetails,closedList,gNew,hNew,fNew);//North-East
			if(!foundDestination)	successorTrace(1.414,i,j,openList,i-1,j-1,grid,destination,cellDetails,closedList,gNew,hNew,fNew);//North-West
			if(!foundDestination)	successorTrace(1.414,i,j,openList,i+1,j+1,grid,destination,cellDetails,closedList,gNew,hNew,fNew);//south-east
			if(!foundDestination)	successorTrace(1.414,i,j,openList,i+1,j-1,grid,destination,cellDetails,closedList,gNew,hNew,fNew);//south-west
			if(foundDestination)	break;
			
			
			
			
			
			
			
		}
		
		
		
		if(!foundDestination){
			System.out.println("Failed to find the destination");
		}
	}
	public static void main(String [] args){
		AStar object = new AStar();
		//0 - stands for blocked path
		//1 - stands for free path
		int grid[][] = new int[][]{
	        { 1, 0, 1, 1, 1, 1, 0, 1, 1, 1 },
	        { 1, 1, 1, 0, 1, 1, 1, 0, 1, 1 },
	        { 1, 1, 1, 0, 1, 1, 0, 1, 0, 1 },
	        { 0, 0, 1, 0, 1, 0, 0, 0, 0, 1 },
	        { 1, 1, 1, 0, 1, 1, 1, 0, 1, 0 },
	        { 1, 0, 1, 1, 1, 1, 0, 1, 0, 0 },
	        { 1, 0, 0, 0, 0, 1, 0, 0, 0, 1 },
	        { 1, 0, 1, 1, 1, 1, 0, 1, 1, 1 },
	        { 1, 1, 1, 0, 0, 0, 1, 0, 0, 1 }
	    };
	    pair source = new pair(8,0);
	    pair destination = new pair(0,0);
	    object.aStarSearch(grid,source,destination);
	    
	}
}
