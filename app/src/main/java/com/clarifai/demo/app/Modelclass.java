package com.clarifai.demo.app;

import java.io.Serializable;

/**
 * Created by sunny on 1/11/2018.
 */

public class Modelclass implements Serializable{
    private String Name;
    private int Link;
    private String Description;

    public Modelclass(String Name,int Link,String Description){
        this.Name=Name;
        this.Link=Link;
        this.Description=Description;
    }

    public String getName(){
        return Name;
    }
    public int getLink(){
        return Link;
    }
    public String getDescription(){
        return Description;
    }
}
