package com.example.carl.orderdishes.adapter;

import com.example.carl.orderdishes.entity.Food;
import com.example.carl.orderdishes.entity.FoodType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carl on 2017/11/2.
 */

public class FoodAllInfo {
    private FoodType foodType = new FoodType();

    public List<Food> getFoolist() {
        return foolist;
    }

    public void setFoolist(List<Food> foolist) {
        this.foolist = foolist;
    }

    private List<Food> foolist = new ArrayList<>();

    @Override
    public String toString() {
        return "FoodAllInfo{" +
                "foodType=" + foodType +
                ", foolist=" + foolist +
                '}';
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(FoodType foodType) {
        this.foodType = foodType;
    }
}
