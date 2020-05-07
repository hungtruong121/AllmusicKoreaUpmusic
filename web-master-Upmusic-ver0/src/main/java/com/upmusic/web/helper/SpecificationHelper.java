package com.upmusic.web.helper;

import com.upmusic.web.domain.CrowdFunding;
import com.upmusic.web.domain.Member;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.xml.crypto.Data;
import java.sql.Date;

//관리자페이지의 각종 검색을 위한 클래스.
public class SpecificationHelper {
    private SpecificationHelper(){}

    //회원 검색을 위한 Specification 정의 (SELECT * FROM member WHERE $column LIKE '%' + $value + '%')
    public static Specification<Member> searchMember(String column, String value){
        return new Specification<Member>() {
            @Override
            public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.like(root.<String>get(column).as(String.class), "%" + value + "%");
            }
        };
    }

    public static Specification<CrowdFunding> searchProjectByValue(String column, String value){
        return new Specification<CrowdFunding>() {
            @Override
            public Predicate toPredicate(Root<CrowdFunding> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.like(root.<String>get(column).as(String.class), "%" + value + "%");
            }
        };
    }
}
