package com.aws.carepoint.mapper.sql;

import com.aws.carepoint.dto.QnaDto;
import org.apache.ibatis.jdbc.SQL;

public class QnaSqlProvider {
    public static String updateDelStatus(QnaDto qna, int userPk) {
        return new SQL() {{
            UPDATE("article");
            SET("del_status = 1");


            if (qna.getOriginNum().equals(qna.getArticlePk())) {
                WHERE("origin_num = #{originNum}");
            } else {
                WHERE("article_pk = #{articlePk}");
            }
        }}.toString();
    }
}
