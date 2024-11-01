package com.woosan.hr_system.employee.model;

public enum Position {
    사원(1),
    대리(2),
    과장(3),
    차장(4),
    부장(5),
    사장(6);

    private final int rank;

    Position(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    // fromRank 메소드: 주어진 rank 값을 이용해 해당하는 Position enum 값을 반환합니다.
    public static Position fromRank(int rank) {
        for (Position position : Position.values()) {
            if (position.getRank() == rank) {
                return position;
            }
        }
        throw new IllegalArgumentException("Unknown rank: " + rank);
    }

    // 직급 이름을 이용해 해당하는 rank 값을 반환하는 메소드
    public static int getRankByPositionName(String name) {
        for (Position position : Position.values()) {
            if (position.name().equals(name)) {
                return position.getRank();
            }
        }
        throw new IllegalArgumentException("Unknown position name: " + name);
    }
}
