package com.personal.peterpoon;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/*
 * -Directed acyclic graph-
 * Given an array of integers with values greater than or equal to 0, for example:
 * [5, 6, 0, 4, 2, 4, 1, 0, 0, 4]
 * Develop and implement an algorithm to traverse the array in the shortest number of “hops” starting at index 0, where traversal is defined as follows:
 * -Start at the first (0th) index of the array, look at the array value there, and you can hop forward to any array index that is no farther than that value away.
 *  So in this example, you start at index 0 containing the value 5 and can now consider hopping to any of indices 1 through 5 inclusive.
 * -If you choose to hop to index 3, it contains the value 4 and you can next hop up to 4 more spots from your current index (3)—so you now consider indices 4 through 7
 *  as next steps in your sequence.
 * -Once you can legally hop beyond the last array element, you have successfully traversed the array.
 *  
 * Compute the minimum-length sequence of hops that successfully traverses the array starting from index 0, or determine that there is no such sequence.   
 * The algorithm must identify a minimum-hops solution, but can choose arbitrarily among solutions with the same number of hops.  
 * The program will implement the algorithm and write a solution to the standard output stream on a single line identifying the array indices that comprise
 * a solution path, with the indices separated by commas.
 * 
 * For this example, the following would be valid output:
 * 0, 5, 9, out
 * (Note that your output is a sequence of array indices, not a sequence of hop lengths.)  
 * -The program must accept a single command-line argument, which is the path to a file containing the input data (the array of integers). It must not read that path
 *  from STDIN or the console.
 * -The input file will contain the array for a single problem.
 *  *One integer per line, with no brackets or commas.
 *  *The input array will likely be large in our testing, so performance is important.
 * -If there is no solution, the program should write the string “failure” to the standard output stream, followed by a newline character. 
 */
public class ArrayJumper {
	enum State {UNVISITED, VISITING, VISITED};

	public static class Node{
		public int value;
		public int currentIndex;
		public int parentIndex;
		public State state;

		public Node(int value, int currentIndex, int parentIndex){
			this.value = value;
			this.currentIndex = currentIndex;
			this.parentIndex = parentIndex;
			this.state = State.UNVISITED;
		}
	}

	public static String computeShortestPath(Integer[] integerArray){
		String retVal = "";
		if(integerArray != null && integerArray.length > 0){
			//BFS tracking queue
			Queue<Node> visitingQueue = new LinkedList<>();

			//integerArray has insufficient data, use this for additional data
			Node[] lazyLoadGraghNodeArray = new Node[integerArray.length];

			//very first node has no parent so use -1 as it's parent index
			Node firstNode = new Node(integerArray[0], 0, -1);
			firstNode.state = State.VISITING;
			visitingQueue.add(firstNode);
			lazyLoadGraghNodeArray[0] = firstNode;

			retVal = computeShortestPath(integerArray, lazyLoadGraghNodeArray, visitingQueue);
		}

		return retVal.length() == 0? "failure" : retVal;
	}

	private static String computeShortestPath(Integer[] integerArray, Node[] lazyLoadGraghNodeArray, Queue<Node> visitingQueue){
		if(!visitingQueue.isEmpty()){
			//get current "level" queue size before adding next "level" "children" nodes
			int elementsToLoop = visitingQueue.size();
			Node currentNode;

			//loop the current "level" only
			while(elementsToLoop-- > 0){
				currentNode = visitingQueue.poll();
				currentNode.state = State.VISITED;

				//We've gotten a winner
				if(currentNode.value + currentNode.currentIndex >= integerArray.length){
					StringBuilder sb = new StringBuilder();

					//use parentIndex as a pointer to go back up; just like a linked list
					genPathString(currentNode, lazyLoadGraghNodeArray, sb);
					sb.append("out");

					return sb.toString();
				}else{
					queueUpNextToVisit(currentNode, integerArray, lazyLoadGraghNodeArray, visitingQueue);
				}
			}

			//No winners yet, go to the next "level"
			return computeShortestPath(integerArray, lazyLoadGraghNodeArray, visitingQueue);
		}else {
			return "";
		}
	}

	private static void queueUpNextToVisit(Node currentNode, Integer[] integerArray, Node[] lazyLoadGraghNodeArray, Queue<Node> visitingQueue){
		//ignore all the "deadends": (< 1)
		if(currentNode.value > 0){
			for(int i = 1; i <= currentNode.value; i++){
				int nextIndexToVisit = i + currentNode.currentIndex;
				int nextVal = integerArray[nextIndexToVisit];
				Node nextNode = new Node(nextVal, nextIndexToVisit, currentNode.currentIndex);

				setState(nextVal, nextIndexToVisit, nextNode, lazyLoadGraghNodeArray, visitingQueue);
			}
		}
	}

	private static void setState(int nextVal, int nextIndexToVisit, Node nextNode, Node[] lazyLoadGraghNodeArray, Queue<Node> visitingQueue){
		if(lazyLoadGraghNodeArray[nextIndexToVisit] == null){
			lazyLoadGraghNodeArray[nextIndexToVisit] = nextNode;
		}

		//mark it as visited if it is a "deadend": (< 1)
		if(nextVal < 1){
			lazyLoadGraghNodeArray[nextIndexToVisit].state = State.VISITED;
		}

		if(lazyLoadGraghNodeArray[nextIndexToVisit].state == State.UNVISITED){
			visitingQueue.add(nextNode);
			lazyLoadGraghNodeArray[nextIndexToVisit].state = State.VISITING;			
		}
	}

	private static void genPathString(Node currentNode, Node[] lazyLoadGraghNodeArray, StringBuilder sb){
		if(currentNode.parentIndex != -1){
			genPathString(lazyLoadGraghNodeArray[currentNode.parentIndex], lazyLoadGraghNodeArray, sb);

		}

		sb.append(currentNode.currentIndex);
		sb.append(", ");
	}

	private static Integer[] prepareInputFromFile(String filePath) throws Exception {
		String row = null;
		ArrayList<Integer> integerList = new ArrayList<>();

		BufferedReader bufferedReader = null;

		try {
			bufferedReader = new BufferedReader(new FileReader(filePath));
			row = bufferedReader.readLine();

			if(row == null){
				throw new IllegalArgumentException("File provided cannot be empty.");

			}else{
				while ((row != null)) {
					row = row.trim();

					if (row.length()>0) {
						integerList.add(Integer.parseInt(row));

					}else{
						throw new IllegalArgumentException("File provided cannot contain zero length or empty string as array an element.");
					}

					row = bufferedReader.readLine();
				}
			}

			return integerList.toArray(new Integer[0]);
		} catch (FileNotFoundException e) {
			throw new Exception(e);
		} catch (IOException e) {
			throw new Exception(e);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					throw new Exception(e);
				}
			}
		}
	}

	public static void main(String[] args) {
		if (args == null || args.length < 1) {
			System.out.println("File path provided cannot be null");
		} else {
			try {
				System.out.println(computeShortestPath(prepareInputFromFile(args[0])));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
