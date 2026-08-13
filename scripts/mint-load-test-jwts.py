#!/usr/bin/env python3
"""[미사용/보관용] TraceK 부하테스트용 JWT 오프라인 서명 스크립트.

현재 JMeter 시나리오는 이 스크립트 대신 서버의 POST /api/auth/dev/token/{userId}
(dev/local 프로필 전용)을 매 반복마다 호출해 토큰을 실시간 발급받는다. 이 스크립트는
그 API가 없던 시절에 쓰던 방식으로, 참고/보관 목적으로만 남겨둔다.

배포 서버의 JwtTokenProvider(HS256, subject=userId, claim role/name)와
동일한 형식으로 액세스 토큰을 만들어 CSV로 저장한다.
실제 카카오 로그인을 거치지 않고, 이미 로그인된 사용자를 흉내내는 용도.
표준 라이브러리만 사용 (pip install 불필요).

사용 예 (scripts/ 디렉토리에서 실행, JWT_SECRET 환경변수로 전달해 커맨드라인/셸
히스토리에 시크릿 노출 방지):
  cd scripts
  JWT_SECRET="<JWT_SECRET>" python mint-load-test-jwts.py \
      --start-id 900001 --count 100 --hours 24 --out jmeter/users_flow.csv
  JWT_SECRET="<JWT_SECRET>" python mint-load-test-jwts.py \
      --start-id 900101 --count 100 --hours 24 --out jmeter/users_hotspot.csv

주의: 여기 쓰는 시크릿이 실제 배포 서버의 JWT_SECRET과 같으면, 이 스크립트로 만든
토큰이 진짜 로그인 세션과 위조 불가능하게 구별이 안 된다. 가능하면 로컬 개발용
JWT_SECRET(운영과 분리된 값)으로만 실행하고, 정말 배포 서버 대상으로 테스트해야
한다면 결과 CSV 유출에 각별히 주의할 것.
"""

import argparse
import base64
import csv
import hashlib
import hmac
import json
import math
import os
import time


def b64url(data: bytes) -> str:
    return base64.urlsafe_b64encode(data).rstrip(b"=").decode("ascii")


def sign_hs256(secret: str, payload: dict) -> str:
    header = {"alg": "HS256", "typ": "JWT"}
    header_b64 = b64url(json.dumps(header, separators=(",", ":")).encode("utf-8"))
    payload_b64 = b64url(json.dumps(payload, separators=(",", ":")).encode("utf-8"))
    signing_input = f"{header_b64}.{payload_b64}".encode("ascii")
    signature = hmac.new(secret.encode("utf-8"), signing_input, hashlib.sha256).digest()
    return f"{header_b64}.{payload_b64}.{b64url(signature)}"


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--secret",
        default=os.environ.get("JWT_SECRET"),
        help="배포 서버 .env의 JWT_SECRET 값 (기본값: JWT_SECRET 환경변수)",
    )
    parser.add_argument("--start-id", type=int, default=900001, help="시작 userId")
    parser.add_argument("--count", type=int, default=100, help="발급할 토큰 개수")
    parser.add_argument("--role", default="USER")
    parser.add_argument("--hours", type=float, default=24, help="토큰 유효시간(시간)")
    parser.add_argument("--out", default="users.csv")
    args = parser.parse_args()
    if not args.secret:
        parser.error("JWT_SECRET을 환경변수로 넘기거나 --secret으로 지정하세요.")
    if args.count <= 0:
        parser.error("--count는 1 이상이어야 합니다.")
    if not math.isfinite(args.hours) or args.hours < (1 / 3600):
        parser.error("--hours는 최소 1초(1/3600시간) 이상의 유한한 값이어야 합니다.")

    now = int(time.time())
    exp = now + int(args.hours * 3600)

    with open(args.out, "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(["userId", "jwt"])
        for i in range(args.count):
            user_id = args.start_id + i
            payload = {
                "sub": str(user_id),
                "role": args.role,
                "name": f"loadtest{user_id}",
                "iat": now,
                "exp": exp,
            }
            token = sign_hs256(args.secret, payload)
            writer.writerow([user_id, token])

    print(
        f"{args.count}개 토큰을 '{args.out}'에 저장했습니다. "
        f"(userId {args.start_id}~{args.start_id + args.count - 1}, {args.hours}시간 유효)"
    )


if __name__ == "__main__":
    main()
