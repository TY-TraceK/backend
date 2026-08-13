#!/usr/bin/env python3
"""TraceK 부하테스트용 JWT 오프라인 서명 스크립트.

배포 서버의 JwtTokenProvider(HS256, subject=userId, claim role/name)와
동일한 형식으로 액세스 토큰을 만들어 CSV로 저장한다.
실제 카카오 로그인을 거치지 않고, 이미 로그인된 사용자를 흉내내는 용도.
표준 라이브러리만 사용 (pip install 불필요).

사용 예:
  python mint-load-test-jwts.py --secret "<배포서버 JWT_SECRET>" \
      --start-id 900001 --count 100 --hours 24 --out jmeter/users_flow.csv
  python mint-load-test-jwts.py --secret "<배포서버 JWT_SECRET>" \
      --start-id 900101 --count 100 --hours 24 --out jmeter/users_hotspot.csv

--secret 는 배포 서버 .env의 JWT_SECRET 값을 그대로 넣어야 한다.
로컬 default 값과 다를 수 있으니 반드시 배포 서버에서 확인할 것.
"""

import argparse
import base64
import csv
import hashlib
import hmac
import json
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
    parser.add_argument("--secret", required=True, help="배포 서버 .env의 JWT_SECRET 값")
    parser.add_argument("--start-id", type=int, default=900001, help="시작 userId")
    parser.add_argument("--count", type=int, default=100, help="발급할 토큰 개수")
    parser.add_argument("--role", default="USER")
    parser.add_argument("--hours", type=float, default=24, help="토큰 유효시간(시간)")
    parser.add_argument("--out", default="users.csv")
    args = parser.parse_args()

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
