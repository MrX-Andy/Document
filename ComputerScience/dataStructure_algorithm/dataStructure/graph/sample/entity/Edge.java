package com.alex.structure.data_structure.graph.sample.entity;

import com.alex.structure.data_structure.datamodel.VertexInterface;

/**
 * 作者：Alex
 * 时间：2017/11/2215:20
 * 简述：边
 */
@SuppressWarnings("WeakerAccess")
public class Edge<T> {
    private VertexInterface<T> vertex;// 终点
    private double weight;//权值

    //Vertex 类本身就代表顶点对象,因此在这里只需提供 endVertex，就可以表示一条边了
    protected Edge(VertexInterface<T> endVertex, double edgeWeight){
        vertex = endVertex;
        weight = edgeWeight;
    }

    protected VertexInterface<T> getEndVertex(){
        return vertex;
    }
    protected double getWeight(){
        return weight;
    }
}
