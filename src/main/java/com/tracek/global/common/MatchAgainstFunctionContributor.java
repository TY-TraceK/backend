package com.tracek.global.common;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

// META-INF/services/org.hibernate.boot.model.FunctionContributor에 이 클래스가
// 등록돼 있어야 ServiceLoader가 읽어서 실제로 적용됨.
public class MatchAgainstFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        // ?1~?3은 컬럼 3개(name, city, district) 고정 - registerPattern은 가변 인자를 못 받아서
        // location의 FULLTEXT(name, city, district) 인덱스에 맞춰 3개로 박아둠.
        functionContributions
                .getFunctionRegistry()
                .registerPattern(
                        "match_against",
                        "MATCH (?1, ?2, ?3) AGAINST (?4 IN BOOLEAN MODE)",
                        functionContributions
                                .getTypeConfiguration()
                                .getBasicTypeRegistry()
                                .resolve(StandardBasicTypes.BOOLEAN));

        // 컬럼 2개짜리(name, alias) FULLTEXT 인덱스용 - artist의 idx_artist_search에 맞춰 2개로 박아둠.
        functionContributions
                .getFunctionRegistry()
                .registerPattern(
                        "match_against2",
                        "MATCH (?1, ?2) AGAINST (?3 IN BOOLEAN MODE)",
                        functionContributions
                                .getTypeConfiguration()
                                .getBasicTypeRegistry()
                                .resolve(StandardBasicTypes.BOOLEAN));
    }
}
