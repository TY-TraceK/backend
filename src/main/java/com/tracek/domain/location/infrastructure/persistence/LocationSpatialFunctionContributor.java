package com.tracek.domain.location.infrastructure.persistence;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

// 지도 bounds 조회를 location.geo_point SPATIAL INDEX(R-Tree)로 처리하기 위한 함수.
// geo_point는 scripts/add-location-spatial-index.sql 로 추가한 generated column(SRID 0, x=경도, y=위도)이며
// 테스트(H2 create-drop)가 POINT 타입을 만들 수 없어 엔티티에 매핑하지 않는다. 그래서 패턴에 컬럼명을 직접 쓴다.
// 주의: 테이블 별칭 없이 geo_point를 참조하므로 location 단독 조회에서만 사용할 것 (같은 이름 컬럼이 있는 테이블과 조인 금지).
//
// META-INF/services/org.hibernate.boot.model.FunctionContributor 에 이 클래스가 등록돼 있어야 적용됨.
public class LocationSpatialFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        // ?1 서쪽 경도, ?2 남쪽 위도, ?3 동쪽 경도, ?4 북쪽 위도 (SRID 0 평면: x=경도, y=위도)
        // MBRCovers(경계 포함)는 MySQL 8.4에서 SPATIAL INDEX를 타지 않아 MBRContains를 사용한다.
        // 그래서 BETWEEN과 달리 bounds 경계선 위에 정확히 놓인 점은 제외됨 (화면 가장자리라 실사용 영향 없음)
        functionContributions
                .getFunctionRegistry()
                .registerPattern(
                        "mbr_contains_location",
                        "MBRContains(ST_MakeEnvelope(POINT(?1, ?2), POINT(?3, ?4)), geo_point)",
                        functionContributions
                                .getTypeConfiguration()
                                .getBasicTypeRegistry()
                                .resolve(StandardBasicTypes.BOOLEAN));
    }
}
