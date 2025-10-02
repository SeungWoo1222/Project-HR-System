-- V2: sample_post 테이블 생성
CREATE TABLE sample_post (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    title       VARCHAR(200) NOT NULL,
    content     TEXT         NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    author_id   BIGINT       NULL
);

-- 조회 자주 쓰는 컬럼 인덱스
CREATE INDEX idx_sample_post_created_at ON sample_post(created_at);
