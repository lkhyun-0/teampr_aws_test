package com.aws.carepoint.mapper;

import com.aws.carepoint.domain.Food;
import com.aws.carepoint.domain.FoodList;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FoodMapper {

    // 식단(food) 기록 추가 (식사 기록 저장)
    @Insert("INSERT INTO food (select_date, foodtype, user_pk) VALUES (#{selectDate}, #{foodType}, #{userPk})")
    @Options(useGeneratedKeys = true, keyProperty = "foodPk")
    public int insertFood(Food food);

    // 개별 음식(foodlist) 기록 추가
    @Insert("INSERT INTO foodlist (menu, protein, carbohydrate, fat, kcal, food_pk) " +
            "VALUES (#{menu}, #{protein}, #{carbohydrate}, #{fat}, #{kcal}, #{foodPk})")
    public void insertFoodList(FoodList foodList);


    // 특정 날짜의 식단 기록 조회
    @Select("SELECT * FROM food f JOIN foodlist fl ON f.food_pk = fl.food_pk WHERE f.user_pk = #{userPk} AND f.select_date = #{selectDate}")
    @Results({
            @Result(property = "foodPk", column = "food_pk"),
            @Result(property = "selectDate", column = "select_date"),
            @Result(property = "foodType", column = "foodtype"),
            @Result(property = "userPk", column = "user_pk"),
            @Result(property = "foodListPk", column = "foodlist_pk"),
            @Result(property = "menu", column = "menu"),
            @Result(property = "kcal", column = "kcal"),
            @Result(property = "protein", column = "protein"),
            @Result(property = "carbohydrate", column = "carbohydrate"),
            @Result(property = "fat", column = "fat")
    })
    List<FoodList> getFoodByDate(@Param("userPk") int userPk, @Param("selectDate") String selectDate);

    // 개별 음식 삭제
    @Delete("DELETE FROM foodlist WHERE foodlist_pk = #{foodListPk}")
    void deleteFood(@Param("foodListPk") int foodListPk);

    @Update("UPDATE foodlist SET menu = #{menu}, kcal = #{kcal}, protein = #{protein}, carbohydrate = #{carbohydrate}, fat = #{fat} WHERE foodlist_pk = #{foodListPk}")
    void updateFood(FoodList foodList);

    @Select("SELECT * FROM food f JOIN foodlist fl ON f.food_pk = fl.food_pk " +
            "WHERE f.user_pk = #{userPk} AND f.select_date = #{selectDate} AND f.foodtype = #{foodType}")
    @Results({
            @Result(property = "foodPk", column = "food_pk"),
            @Result(property = "selectDate", column = "select_date"),
            @Result(property = "foodType", column = "foodtype"),
            @Result(property = "userPk", column = "user_pk"),
            @Result(property = "foodListPk", column = "foodlist_pk"),
            @Result(property = "menu", column = "menu"),
            @Result(property = "kcal", column = "kcal"),
            @Result(property = "protein", column = "protein"),
            @Result(property = "carbohydrate", column = "carbohydrate"),
            @Result(property = "fat", column = "fat")
    })
    List<FoodList> getFoodByDateAndType(@Param("userPk") int userPk, @Param("selectDate") String selectDate, @Param("foodType") String foodType);








}
