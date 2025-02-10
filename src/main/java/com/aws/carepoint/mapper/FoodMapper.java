package com.aws.carepoint.mapper;

import com.aws.carepoint.domain.Food;
import com.aws.carepoint.domain.FoodList;
import com.aws.carepoint.dto.FoodDto;
import com.aws.carepoint.dto.FoodListDto;
import com.aws.carepoint.dto.WeeklyFoodStatsDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FoodMapper {

    // 식단(food) 기록 추가 (식사 기록 저장)
    @Insert("INSERT INTO food (select_date, foodtype, user_pk) VALUES (#{selectDate}, #{foodType}, #{userPk})")
    @Options(useGeneratedKeys = true, keyProperty = "foodPk")
    public int insertFood(Food food);

    // 개별 음식(foodlist) 기록 추가
    @Insert("INSERT INTO foodlist (menu, protein, carbohydrate, fat, kcal, amount, food_pk) " +
            "VALUES (#{menu}, #{protein}, #{carbohydrate}, #{fat}, #{kcal}, #{amount}, #{foodPk})")
    void insertFoodList(FoodList foodList);



    // 특정 날짜의 식단 기록 조회
    @Select("SELECT * FROM food f JOIN foodlist fl ON f.food_pk = fl.food_pk " +
            "WHERE f.user_pk = #{userPk} AND f.select_date = #{selectDate}")
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
            @Result(property = "fat", column = "fat"),
            @Result(property = "amount", column = "amount")  //
    })
    List<FoodList> getFoodByDate(@Param("userPk") int userPk, @Param("selectDate") String selectDate);


    // 개별 음식 삭제 (foodlist 테이블에서 삭제)
    @Delete("DELETE FROM foodlist WHERE foodlist_pk = #{foodListPk}")
    void deleteFood(@Param("foodListPk") int foodListPk);

    // 특정 foodListPk에 해당하는 food_pk 가져오기
    @Select("SELECT food_pk FROM foodlist WHERE foodlist_pk = #{foodListPk}")
    Integer getFoodPkByFoodListPk(@Param("foodListPk") int foodListPk);

    // 특정 food_pk에 남은 음식 개수 확인 (이 부분 추가!)
    @Select("SELECT COUNT(*) FROM foodlist WHERE food_pk = #{foodPk}")
    int countFoodListByFoodPk(@Param("foodPk") int foodPk);

    // 특정 날짜와 식사 타입(B/L/D)의 모든 음식이 삭제되었을 경우 food 테이블에서도 삭제
    @Delete("DELETE FROM food WHERE food_pk = #{foodPk} AND " +
            "NOT EXISTS (SELECT 1 FROM foodlist WHERE foodlist.food_pk = food.food_pk)")
    void deleteEmptyFood(@Param("foodPk") int foodPk);


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
            @Result(property = "fat", column = "fat"),
            @Result(property = "amount", column = "amount")  // 🔥 `amount` 추가
    })
    List<FoodList> getFoodByDateAndType(@Param("userPk") int userPk, @Param("selectDate") String selectDate, @Param("foodType") String foodType);



    // 기존 음식 수정 (칼로리, 영양소 변경)
    @Update("UPDATE foodlist SET menu = #{menu}, kcal = #{kcal}, protein = #{protein}, " +
            "carbohydrate = #{carbohydrate}, fat = #{fat}, amount = #{amount} WHERE foodlist_pk = #{foodListPk}")
    void updateFood(FoodList foodList);



    @Select("SELECT DISTINCT select_date, " +
            "MAX(CASE WHEN foodtype = 'B' THEN 1 ELSE 0 END) AS breakfast, " +
            "MAX(CASE WHEN foodtype = 'L' THEN 1 ELSE 0 END) AS lunch, " +
            "MAX(CASE WHEN foodtype = 'D' THEN 1 ELSE 0 END) AS dinner " +
            "FROM food " +
            "WHERE user_pk = #{userPk} " +
            "GROUP BY select_date " +
            "ORDER BY select_date DESC")
    @Results({
            @Result(property = "selectDate", column = "select_date"),
            @Result(property = "breakfast", column = "breakfast"),
            @Result(property = "lunch", column = "lunch"),
            @Result(property = "dinner", column = "dinner")
    })
    List<FoodListDto> getFoodList(@Param("userPk") int userPk);


    @Select("""
        SELECT f.select_date,
               COALESCE(SUM(fl.kcal), 0) AS total_calories,
               COALESCE(SUM(fl.protein), 0) AS total_protein,
               COALESCE(SUM(fl.carbohydrate), 0) AS total_carbohydrates,
               COALESCE(SUM(fl.fat), 0) AS total_fat
        FROM (SELECT DISTINCT select_date
              FROM food
              WHERE user_pk = #{userPk} 
              AND select_date >= DATE_SUB(CURDATE(), INTERVAL 6 DAY)
             ) f
        LEFT JOIN food fd ON f.select_date = fd.select_date AND fd.user_pk = #{userPk}
        LEFT JOIN foodlist fl ON fd.food_pk = fl.food_pk
        GROUP BY f.select_date
        ORDER BY f.select_date ASC
        """)
    @Results({
            @Result(property = "selectDate", column = "select_date"),
            @Result(property = "totalCalories", column = "total_calories"),
            @Result(property = "totalProtein", column = "total_protein"),
            @Result(property = "totalCarbohydrates", column = "total_carbohydrates"),
            @Result(property = "totalFat", column = "total_fat")
    })
    List<WeeklyFoodStatsDto> getWeeklyFoodStats(@Param("userPk") int userPk);



}
