package com.alex.structure.data_structure.graph.sample;

import com.alex.structure.data_structure.datamodel.GraphInterface;
import org.alex.util.LogTrack;

import java.util.Queue;
import java.util.Stack;

/**
 * 作者：Alex
 * 时间：2017/11/2217:03
 * 简述：
 */
public class TestGraph {
    public static void main(String[] args) {
        GraphInterface<String> graph = new DirectedGraph<>();
        LogTrack.w("Graph is empty? " + graph.isEmpty());

        LogTrack.w("Adding vertexs...");
        graph.addVertex("A");graph.addVertex("B");
        graph.addVertex("C");graph.addVertex("D");
        graph.addVertex("E");
        LogTrack.w("Number of graph's vertex = " + graph.getNumberOfVertices());//5
		
		/*
		 *   <A,D>  <A,C>  <A,B>  <D,C>  <C,E>
		 */
        LogTrack.w("Adding edges...");
        graph.addEdge("A", "D");graph.addEdge("A", "C");
        graph.addEdge("A", "B");graph.addEdge("D", "C");
        graph.addEdge("C", "E");
        LogTrack.w("Number of graph's edge = " + graph.getNumberOfEdges());//5

        LogTrack.w("vertexs between B and C has Edges? " + graph.hasEdge("B", "C"));//false
        LogTrack.w("vertex between D and C has Edges? " + graph.hasEdge("D", "C"));//true

        LogTrack.w("Breadth First traverse graph with initial vertex 'A'...");
        Queue<String> bfsTraversalOrder = graph.getBreadthFirstTraversal("A");//A D C B E
        while(!bfsTraversalOrder.isEmpty())
            System.out.print(bfsTraversalOrder.poll() + " ");

        LogTrack.w("\nDFS traverse graph with inital vertex 'A'...");
        Queue<String> dfsTraversalOrder = graph.getDepthFirstTraversal("A");
        while(!dfsTraversalOrder.isEmpty())
            System.out.print(dfsTraversalOrder.poll() + " ");

        LogTrack.w("\nTopological Order");
        Stack<String> stack = graph.getTopologicalSort();
        while(!stack.isEmpty())
            System.out.print(stack.pop() + " ");

        LogTrack.w("\ncleanning graph");
        graph.clear();
        LogTrack.w("Now, number of vertexs = " + graph.getNumberOfVertices());


    }
}
