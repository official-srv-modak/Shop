package com.sourav.kisara;

import java.util.ArrayList;

public class FeedElementsQueue {
    int max = 100;
    ArrayList <String> pool = new ArrayList<String>();
    public FeedElementsQueue()
    {

    }
    public FeedElementsQueue(String element)
    {
        push(element);
    }
    public void push(String element)
    {
        if(pool.size()<max)
        {
            pool.add(element);
        }
        else
        {
            pool.remove(0);
            pool.add(element);
        }
    }
    public String pop()
    {
        if(pool.size()>0)
        {
            return pool.get(pool.size()-1);
        }
        return null;
    }
    
}
