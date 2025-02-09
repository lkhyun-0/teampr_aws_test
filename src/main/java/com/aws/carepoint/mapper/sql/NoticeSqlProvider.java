package com.aws.carepoint.mapper.sql;

import com.aws.carepoint.dto.NoticeDto;
import org.apache.ibatis.jdbc.SQL;

//public class NoticeSqlProvider {
//    public static String updateDelStatus(NoticeDto notice) {
//        return new SQL() {{
//            UPDATE("article");
//            SET("del_status = 1");
//
//
//            if (notice.getOriginNum().equals(notice.getArticlePk())) {
//                WHERE("origin_num = #{originNum}");
//            } else {
//                WHERE("article_pk = #{articlePk}");
//            }
//        }}.toString();
//    }
//}
