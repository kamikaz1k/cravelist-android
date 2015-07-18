package com.imperialtechnologies.theeatlist_3;

/**
 * Created by kdandang on 7/17/2015.
 */
public class UpdateFoodListEvent {

    public String foodId;

    public void setFoodId(String id){

        foodId = id;

    }

    public String getFoodId(){

        return foodId;

    }
}
